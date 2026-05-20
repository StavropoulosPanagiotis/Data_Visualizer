# Data Visualizer

## Overview

A desktop app for exploring academic publication data from DBLP. We clean and load the data into MySQL using Pentaho, then browse it through a JavaFX interface with search, filters, stats, and charts.

## Technologies used

| Layer | Technology |
|-------|------------|
| ETL | Pentaho Data Integration (Spoon) |
| Database | MySQL 8.0 — schema `data_visualizer` |
| App | Java + JavaFX 26 |
| JDBC Driver | MySQL Connector/J 9.7.0 |
| IDE | IntelliJ IDEA |

## Cleaned Data

The raw and cleaned data files are available on Google Drive:

**[Download Data](https://drive.google.com/drive/folders/1yMCPb7EATtTbE6tz32f9m2kXQvAtDUxp?usp=drive_link)**

The folder contains two subfolders:
- `raw_data/` — original source files
- `cleaned_data/` — CSV files output by Pentaho, ready for loading into MySQL

> After downloading, copy all 5 files from `cleaned_data/` to the MySQL upload directory as described in Step 4.

---

## Database Backup

A full dump of the `data_visualizer` database is available on Google Drive:

**[Download Backup](https://drive.google.com/file/d/1EGJHzhVsR38uhY-eCL54SuKuIHcgDKTt/view?usp=drive_link)**

It includes the schema, all data, views, stored procedures, and the `normalize_journal()` function. To restore it, run:

```bash
mysql -u root -p < data_visualizer_backup.sql
```

---

## Project Structure

```
Data_Visualizer/
├── app/                Java + JavaFX application
│   ├── lib/            JavaFX SDK + MySQL connector
│   └── src/
│       ├── application/    Entry point
│       ├── db/             JDBC connection
│       ├── model/          DTO classes
│       ├── repository/     DB access (stored procedures)
│       ├── service/        Business logic
│       ├── viewmodel/      JavaFX Tasks
│       └── gui/            Controllers + FXML views
├── scripts/            SQL scripts (schema, load, views, procedures)
├── transformations/    Pentaho ETL (.ktr / .kjb)
├── backup/             Google Drive link to full DB backup
└── deliverables/       Report + video
```

---

## Database Setup

Follow these steps in order to setup the DB correctly. All scripts are in the `scripts/` folder.

### Prerequisites

- MySQL 8.0 installed and running
- MySQL Workbench
- The 5 cleaned CSV files from the `data/` folder

---

### Step 1 — Create the database user

**Connection:** root  
**Script:** `scripts/create_user.sql`

This creates the application user `DataVisualizerUser` with password `DataVisualizer` and grants it all required privileges.

```sql
CREATE USER IF NOT EXISTS 'DataVisualizerUser'@'localhost' IDENTIFIED BY 'DataVisualizer';
GRANT ALL PRIVILEGES ON `data_visualizer`.* TO 'DataVisualizerUser'@'localhost';
GRANT SYSTEM_VARIABLES_ADMIN ON *.* TO 'DataVisualizerUser'@'localhost';
GRANT FILE ON *.* TO 'DataVisualizerUser'@'localhost';
FLUSH PRIVILEGES;
```

> `SYSTEM_VARIABLES_ADMIN` is needed to set `log_bin_trust_function_creators` in Step 3.  
> `FILE` is needed to use `LOAD DATA INFILE` in Step 4.

---

### Step 2 — Create a new connection for DataVisualizerUser

In MySQL Workbench, create a new connection:

| Field | Value                                  |
|-------|----------------------------------------|
| Connection Name | DataVisualizerConnection (or any name) |
| Hostname | 127.0.0.1                              |
| Port | 3306                                   |
| Username | DataVisualizerUser                     |
| Password | DataVisualizer                         |

All remaining steps run under this connection.

---

### Step 3 — Create the schema and tables

**Connection:** DataVisualizerConnection
**Script:** `scripts/create_schema.sql`

Creates the `data_visualizer` database and all tables:

| Table | Description                                                                                                 |
|-------|-------------------------------------------------------------------------------------------------------------|
| `authors` | Author dimension — `author_id`, `author_name`                                                               |
| `journals` | Journal dimension — 25 columns including fields like (SJR, CiteScore, h-index, quartile, etc.)              |
| `conferences` | Conference dimension — `conference_id`, `title`, `acronym`, `rank`, `primary_for`                           |
| `publications` | Fact table — `publication_id`, `title`, `year`, `type` (journal/conference), FK to journals and conferences |
| `publications_authors` | N:M bridge table — `publication_id`, `author_id`                                                            |
| `staging_publications` | Temporary staging table used during loading only                                                            |
| `staging_publications_authors` | Temporary staging table used during loading only                                                            |

Indexes created at this stage (dimension tables):

| Index | Table | Column |
|-------|-------|--------|
| `idx_author_name` | `authors` | `author_name` |
| `idx_journal_title` | `journals` | `title` |
| `idx_conference_title` | `conferences` | `title` |
| `idx_conference_acronym` | `conferences` | `acronym` |

---

### Step 4 — Copy cleaned CSV files to the MySQL upload directory

Before running the load script, copy all 5 CSV files from the `data/cleaned_data/` folder to:

```
C:\ProgramData\MySQL\MySQL Server 8.0\Uploads\
```

The exact files expected:

| File | Loaded into |
|------|-------------|
| `author_dim.csv` | `authors` |
| `conference_dim.csv` | `conferences` |
| `journal_dim.csv` | `journals` |
| `publication_fact.csv` | `staging_publications` |
| `publication_author.csv` | `staging_publications_authors` |

> This directory is the `secure_file_priv` upload path configured by MySQL on Windows. The `FILE` privilege granted in Step 1 is required for this to work.

---

### Step 5 — Define the journal normalization function

**Connection:** DataVisualizerConnection
**Script:** `scripts/handle_journal_abbreviations.sql`

Creates the `normalize_journal()` function that expands common abbreviations in journal titles (e.g. `J.` → `Journal of`, `Trans.` → `Transactions on`). This is needed so that abbreviated journal names from DBLP can be matched against the full names in the journal dimension.

The script temporarily sets `log_bin_trust_function_creators = 1` (allowed by the `SYSTEM_VARIABLES_ADMIN` grant) and restores it to `0` at the end.

---

### Step 6 — Increase InnoDB buffer pool size !!!ONLY IF YOUR MACHINE CAN SUPPORT IT

Before loading, increase MySQL's InnoDB buffer pool to 8 GB to reduce disk I/O during the bulk insert. Without this, MySQL would frequently flush dirty pages to disk while inserting the 7M+ row `publications_authors` table, making the load significantly slower.

Run the following as `DataVisualizerUser`:

```sql
SET GLOBAL innodb_buffer_pool_size = 8589934592;
```

> This change is temporary and resets when MySQL restarts. It only needs to be active during the load.

---

### Step 7 — Load data

**Connection:** DataVisualizerConnection
**Script:** `scripts/load_data.sql`

This script:

1. Bulk-loads all 5 CSVs into the dimension and staging tables via `LOAD DATA INFILE`
2. Runs `normalize_journal()` on all journal names in staging to expand abbreviations
3. Adds temporary indexes on the staging tables to speed up the joins
4. Inserts into `publications` by joining staging data against the dimension tables — matching journals by title and conferences by title or acronym (using `COALESCE`)
5. Inserts into `publications_authors` by joining staging author names against the `authors` dimension
6. Drops both staging tables
7. Creates fact table indexes:

| Index | Table | Column |
|-------|-------|--------|
| `idx_pub_year` | `publications` | `year` |
| `idx_pub_jid` | `publications` | `journal_id` |
| `idx_pub_cid` | `publications` | `conference_id` |
| `idx_pa_author` | `publications_authors` | `author_id` |

> `publications_authors` will have ~7 million rows after loading. The script may take several minutes.

---

### Step 8 — Create views

**Connection:** DataVisualizerConnection 
**Script:** `scripts/db_views.sql`

Creates 9 views to simplify procedure queries:

| View | Purpose |
|------|---------|
| `author_publications_view` | All publications per author with venue name |
| `venue_publications_view` | All publications per venue  |
| `venue_author_publications_view` | Publications per venue including author |
| `valid_publications_view` | Publications where year is not null |
| `publisher_stats_view` | Per-publisher journal count and Q1–Q4 breakdown |
| `journal_metrics_view` | Journal fields (SJR, CiteScore, h-index, etc.) |
| `year_publication_details_view` | Full publication detail per year including author names |
| `journal_category_year_view` | Publications per journal per year with subject area |
| `conference_category_year_view` | Publications per conference per year with primary field |

> Run this script before Step 9 — procedures depend on these views.

---

### Step 9 — Create stored procedures

**Connection:** DataVisualizerConnection  
**Script:** `scripts/db_procedures.sql`

Creates 15 stored procedures. All application queries go through these

| Procedure | Used by |
|-----------|---------|
| `search_authors_procedure` | Authors tab — search |
| `author_stats_procedure` | Authors tab — profile stats |
| `author_year_stats_procedure` | Authors tab — LineChart data |
| `author_publications_procedure` | Authors tab — publications table |
| `search_venues_procedure` | Venues tab — search |
| `venue_stats_procedure` | Venues tab — profile stats |
| `venue_year_detail_procedure` | Venues tab — LineChart data |
| `venue_publications_procedure` | Venues tab — publications table |
| `venue_year_stats_procedure` | Venues tab — year stats |
| `publications_per_year_procedure` | Years tab — overview list |
| `year_profile_procedure` | Years tab — year stats |
| `year_publications_procedure` | Years tab — filtered publications table |
| `publisher_stats_procedure` | Charts tab — StackedBarChart |
| `category_year_stats_procedure` | Charts tab — LineChart |
| `journal_scatter_procedure` | Charts tab — ScatterChart |

---

## Running the Application

### Prerequisites

- Java 21+ JDK
- JavaFX SDK 26 (included in `lib/`)
- MySQL 8.0 with the database set up as above

### Connection Configuration

The file `app/src/db.ini` is included in the repository and pre-configured to connect using the user created in Step 1:

```ini
db.url=jdbc:mysql://localhost:3306/data_visualizer
db.user=DataVisualizerUser
db.password=DataVisualizer
```

No changes are needed if you followed the database setup steps above.

### IntelliJ Run Configuration

**1. Add the MySQL connector to the classpath:**

Go to **File → Project Structure → Modules → Dependencies tab**, click **+** → **JARs or directories**, and select:

```
app\lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar
```

**2. Add the following VM options to the run configuration for `application.Main`:**

```
--module-path "C:\...\Data_Visualizer\app\lib\javafx-sdk-26\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics
```

Replace `C:\...\Data_Visualizer` with the actual path to the project on your machine.

**Main class:** `application.Main`

---

## Deliverables

See the `deliverables/` folder for:
- Final report PDF
- Video link (system walkthrough)
