# Data Visualizer

## Team

| Name | AM |
|------|----|
| Σταυρόπουλος Παναγιώτης | [ΑΜ] |

## Course

**MYE030 / ΠΛΕ045** — Προχωρημένα Θέματα Τεχνολογίας & Εφαρμογών Βάσεων Δεδομένων  
Τμήμα Μηχανικής Η/Υ & Πληροφορικής, Πανεπιστήμιο Ιωαννίνων  
Εαρινό Εξάμηνο 2025–2026

## Overview

Academic bibliographic data integration and visualization system. Raw publication records from DBLP (journal articles and conference papers) are extracted, transformed, and loaded into a relational MySQL database, then exposed through an interactive JavaFX desktop application with search, filtering, statistical profiles, and charts.

## Technology Stack

| Layer | Technology |
|-------|-----------|
| ETL | Pentaho Data Integration (Spoon) |
| Database | MySQL 8.0 — schema `data_visualizer` |
| Application | Java + JavaFX 26 |
| JDBC Driver | MySQL Connector/J 9.7.0 |
| IDE | IntelliJ IDEA |

## Project Structure

```
Data_Visualizer/
├── app/                        Java + JavaFX application (IntelliJ project)
│   └── src/
│       ├── application/        Main.java — JavaFX entry point
│       ├── db/                 DBConnection.java — singleton JDBC connection
│       ├── model/              13 model classes (one per query result shape)
│       ├── repository/         4 repository classes (CallableStatement wrappers)
│       ├── service/            4 service classes (business logic)
│       ├── viewmodel/          4 ViewModel classes (JavaFX Task + ObservableList)
│       └── gui/
│           ├── MainWindow.java
│           ├── controller/     4 FXML controllers
│           └── view/           5 FXML layout files
├── scripts/                    SQL scripts (run in order — see below)
│   ├── create_user.sql
│   ├── create_schema.sql
│   ├── handle_journal_abbreviations.sql
│   ├── load_data.sql
│   ├── db_views.sql
│   └── db_procedures.sql
├── transformations/            Pentaho .ktr / .kjb transformation files
│   ├── prepare_and_clean_data.kjb          (master job)
│   ├── prepare_data_for_author_dim.ktr
│   ├── prepare_data_for_journal_dim.ktr
│   ├── prepare_data_for_conference_dim.ktr
│   ├── prepare_data_for_publication_fact.ktr
│   └── prepare_data_for_publication_author.ktr
├── lib/                        Runtime dependencies
│   ├── javafx-sdk-26/lib/      JavaFX 26 SDK jars
│   └── mysql-connector-j-9.7.0.jar
└── deliverables/               Final report PDF + video link
```

## Database Setup

Scripts must be run **in the following order**:

| Step | Connection | Script | Purpose |
|------|-----------|--------|---------|
| 1 | root | `create_user.sql` | Creates `DataVisualizerUser` with required privileges |
| 1.5 | — | *(create a new MySQL Workbench connection for `DataVisualizerUser`, password: `DataVisualizer`)* | — |
| 2 | DataVisualizerUser | `create_schema.sql` | Creates all tables with PKs, FKs and indexes |
| 3 | DataVisualizerUser | `handle_journal_abbreviations.sql` | Defines `normalize_journal()` function for title matching |
| 4 | DataVisualizerUser | `load_data.sql` | Bulk-loads CSVs via `LOAD DATA INFILE`, populates fact tables |
| 5 | DataVisualizerUser | `db_views.sql` | Creates 9 SQL views |
| 6 | DataVisualizerUser | `db_procedures.sql` | Creates 15 stored procedures |

> **Note:** `load_data.sql` expects the cleaned CSV files to be placed in `C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/` before execution.

### Database Schema

Five tables: `authors`, `journals`, `conferences`, `publications`, `publications_authors`.  
Two staging tables: `staging_publications`, `staging_publications_authors` (used only during loading).

```
authors ──┐
          ├── publications_authors ──── publications ──┬── journals
conferences ──────────────────────────────────────────┘
```

All tables use **InnoDB** with integer surrogate PKs and explicit FK constraints.

## ETL Process

Raw data (DBLP conference and journal articles + venue ranking files) is cleaned and transformed by a **Pentaho PDI** job (`prepare_and_clean_data.kjb`) that orchestrates five transformations:

1. `prepare_data_for_author_dim.ktr` — splits pipe-delimited author lists, deduplicates authors
2. `prepare_data_for_journal_dim.ktr` — normalises journal titles using regex; matches Kaggle ranking data
3. `prepare_data_for_conference_dim.ktr` — maps conference acronyms from iCore26 data
4. `prepare_data_for_publication_fact.ktr` — merges journal and conference articles into a unified CSV
5. `prepare_data_for_publication_author.ktr` — builds the N:M link between publications and authors

Output CSVs are written to the MySQL upload directory and loaded by `load_data.sql`.

## Running the Application

### Prerequisites

- Java 21+ JDK
- JavaFX SDK 26 (included in `lib/`)
- MySQL 8.0 with the database set up as above

### Connection Configuration

Create the file `app/src/db.ini` (not committed — contains credentials):

```ini
db.url=jdbc:mysql://localhost:3306/data_visualizer
db.user=DataVisualizerUser
db.password=DataVisualizer
```

### IntelliJ Run Configuration

Add the following VM options:

```
--module-path "C:\...\Data_Visualizer\lib\javafx-sdk-26\lib"
--add-modules javafx.controls,javafx.fxml,javafx.graphics
```

Main class: `application.Main`

## Application Features

| Tab | Description |
|-----|-------------|
| **Home** | Landing page with feature overview |
| **Authors** | Search by name / year range → author profile: stats + LineChart (journals vs conferences per year) + publications table |
| **Venues** | Search by name / type / year → venue profile: stats + LineChart (publications per year) + publications table |
| **Years** | Year overview list → year profile: stats + filtered publications table (filter by type / venue / author) |
| **Charts** | Publisher Stats (StackedBarChart Q1–Q4) · Category Trends (LineChart) · Journal Scatter (ScatterChart) |

## Architecture

The application follows **MVVM + Repository** layering:

```
FXML View → Controller (@FXML) → ViewModel (JavaFX Task) → Service → Repository → DBConnection → MySQL
```

- All database calls run on a background thread via `Task<T>`; results are pushed to the FX thread via `setOnSucceeded`
- `DBConnection.get()` is `synchronized` to prevent race conditions across threads
- Every query is executed as a **stored procedure** via `CallableStatement` — no SQL strings in Java
- DB credentials are loaded from an external `db.ini` file at runtime

## Deliverables

See the `deliverables/` folder for:
- Final report PDF
- Video link (system walkthrough, ~15 min)
