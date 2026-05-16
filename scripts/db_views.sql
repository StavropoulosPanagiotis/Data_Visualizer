-- ============================================================
-- The according procedures will query these views
-- RUN BEFORE db_procedures.sql
-- ============================================================

USE data_visualizer;

DROP VIEW IF EXISTS conference_category_year_view;
DROP VIEW IF EXISTS journal_category_year_view;
DROP VIEW IF EXISTS year_publication_details_view;
DROP VIEW IF EXISTS journal_metrics_view;
DROP VIEW IF EXISTS publisher_stats_view;
DROP VIEW IF EXISTS valid_publications_view;
DROP VIEW IF EXISTS venue_author_publications_view;
DROP VIEW IF EXISTS venue_publications_view;
DROP VIEW IF EXISTS author_publications_view;

-- ------------------------------------------------------------
-- author_publications_view
-- ------------------------------------------------------------
CREATE VIEW author_publications_view AS
	SELECT
		authors.author_id,
		authors.author_name,
		publications.publication_id,
		publications.title,
		publications.year,
		publications.type,
		COALESCE(journals.title, conferences.title, 'Unknown') AS venue
	FROM publications
	JOIN publications_authors ON publications.publication_id = publications_authors.publication_id
	JOIN authors ON publications_authors.author_id = authors.author_id
	LEFT JOIN journals ON publications.journal_id = journals.journal_id
	LEFT JOIN conferences ON publications.conference_id = conferences.conference_id;

-- ------------------------------------------------------------
-- venue_publications_view
-- ------------------------------------------------------------
CREATE VIEW venue_publications_view AS
	SELECT
		'journal' AS venue_type,
		journals.journal_id AS venue_id,
		journals.title AS venue_title,
		journals.`rank`,
		publications.publication_id,
		publications.title AS pub_title,
		publications.year
	FROM publications
	JOIN journals ON publications.journal_id = journals.journal_id
UNION ALL
	SELECT
		'conference' AS venue_type,
		conferences.conference_id AS venue_id,
		conferences.title AS venue_title,
		conferences.`rank`,
		publications.publication_id,
		publications.title AS pub_title,
		publications.year
	FROM publications
	JOIN conferences ON publications.conference_id = conferences.conference_id;

-- ------------------------------------------------------------
-- venue_author_publications_view
-- ------------------------------------------------------------
CREATE VIEW venue_author_publications_view AS
	SELECT
		'journal' AS venue_type,
		publications.journal_id AS venue_id,
		publications.publication_id,
		publications.year,
		publications_authors.author_id
	FROM publications
	JOIN publications_authors ON publications.publication_id = publications_authors.publication_id
	WHERE publications.journal_id IS NOT NULL
UNION ALL
	SELECT
		'conference' AS venue_type,
		publications.conference_id AS venue_id,
		publications.publication_id,
		publications.year,
		publications_authors.author_id
	FROM publications
	JOIN publications_authors ON publications.publication_id = publications_authors.publication_id
	WHERE publications.conference_id IS NOT NULL;

-- ------------------------------------------------------------
-- valid_publications_view
-- ------------------------------------------------------------
CREATE VIEW valid_publications_view AS
	SELECT publication_id, title, year, type, journal_id, conference_id
	FROM publications
	WHERE year IS NOT NULL;

-- ------------------------------------------------------------
-- publisher_stats_view
-- ------------------------------------------------------------
CREATE VIEW publisher_stats_view AS
	SELECT
		journals.publisher,
		COUNT(*) AS journal_count,
		SUM(journals.best_quartile = 'Q1') AS q1_count,
		SUM(journals.best_quartile = 'Q2') AS q2_count,
		SUM(journals.best_quartile = 'Q3') AS q3_count,
		SUM(journals.best_quartile = 'Q4') AS q4_count
	FROM journals
	WHERE journals.publisher IS NOT NULL AND journals.publisher != ''
	GROUP BY journals.publisher;

-- ------------------------------------------------------------
-- journal_metrics_view
-- ------------------------------------------------------------
CREATE VIEW journal_metrics_view AS
	SELECT
		journals.title,
		journals.best_quartile AS quartile,
		journals.best_subject_area AS subject_area,
		journals.sjr_index,
		journals.citescore,
		NULLIF(journals.h_index, '') AS h_index,
		NULLIF(journals.total_docs, '') AS total_docs,
		NULLIF(journals.total_docs_3y, '') AS total_docs_3y,
		NULLIF(journals.total_refs, '') AS total_refs,
		NULLIF(journals.total_cites_3y, '') AS total_cites_3y,
		NULLIF(journals.citable_docs_3y, '') AS citable_docs_3y,
		NULLIF(journals.cites_doc_2y, '') AS cites_doc_2y,
		NULLIF(journals.refs_doc, '') AS refs_doc
	FROM journals
	WHERE journals.sjr_index IS NOT NULL AND journals.sjr_index != '' AND journals.citescore IS NOT NULL AND journals.citescore != '';

-- ------------------------------------------------------------
-- year_publication_details_view
-- ------------------------------------------------------------
CREATE VIEW year_publication_details_view AS
	SELECT
		publications.publication_id,
		publications.title,
		publications.year,
		publications.type,
		COALESCE(journals.title, conferences.title, 'Unknown') AS venue,
		journals.title AS journal_title,
		conferences.title AS conference_title,
		authors.author_name
	FROM publications
	LEFT JOIN journals ON publications.journal_id = journals.journal_id
	LEFT JOIN conferences ON publications.conference_id = conferences.conference_id
	LEFT JOIN publications_authors ON publications.publication_id = publications_authors.publication_id
	LEFT JOIN authors ON publications_authors.author_id = authors.author_id;

-- ------------------------------------------------------------
-- journal_category_year_view
-- ------------------------------------------------------------
CREATE VIEW journal_category_year_view AS
	SELECT
		publications.year,
		publications.journal_id AS venue_id,
		publications.publication_id,
		journals.best_subject_area AS category
	FROM publications
	JOIN journals ON publications.journal_id = journals.journal_id
	WHERE publications.year IS NOT NULL;

-- ------------------------------------------------------------
-- conference_category_year_view
-- ------------------------------------------------------------
CREATE VIEW conference_category_year_view AS
	SELECT
		publications.year,
		publications.conference_id AS venue_id,
		publications.publication_id,
		conferences.primary_for AS category
	FROM publications
	JOIN conferences ON publications.conference_id = conferences.conference_id
	WHERE publications.year IS NOT NULL;
