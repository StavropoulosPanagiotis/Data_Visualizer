-- ============================================================
-- RUN AFTER db_views.sql
-- ============================================================

USE data_visualizer;

DROP PROCEDURE IF EXISTS search_authors_procedure;
DROP PROCEDURE IF EXISTS author_year_stats_procedure;
DROP PROCEDURE IF EXISTS author_publications_procedure;
DROP PROCEDURE IF EXISTS author_stats_procedure;
DROP PROCEDURE IF EXISTS search_venues_procedure;
DROP PROCEDURE IF EXISTS venue_year_stats_procedure;
DROP PROCEDURE IF EXISTS venue_stats_procedure;
DROP PROCEDURE IF EXISTS venue_year_detail_procedure;
DROP PROCEDURE IF EXISTS venue_publications_procedure;
DROP PROCEDURE IF EXISTS publications_per_year_procedure;
DROP PROCEDURE IF EXISTS year_profile_procedure;
DROP PROCEDURE IF EXISTS year_publications_procedure;
DROP PROCEDURE IF EXISTS publisher_stats_procedure;
DROP PROCEDURE IF EXISTS category_year_stats_procedure;
DROP PROCEDURE IF EXISTS journal_scatter_procedure;

DELIMITER $$

-- ------------------------------------------------------------
-- search_authors_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE search_authors_procedure(
	IN param_name VARCHAR(300),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT author_id, author_name, COUNT(publication_id) AS publication_count
	FROM author_publications_view
	WHERE author_name LIKE CONCAT('%', param_name, '%') AND year BETWEEN param_from_year AND param_to_year
	GROUP BY author_id, author_name
	ORDER BY publication_count DESC;
END$$

-- ------------------------------------------------------------
-- author_year_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE author_year_stats_procedure(
	IN param_author_id INT,
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		year,
		COUNT(*) AS publication_count,
		SUM(type = 'journal') AS journal_count,
		SUM(type = 'conference') AS conference_count
	FROM author_publications_view
	WHERE author_id = param_author_id AND year BETWEEN param_from_year AND param_to_year
	GROUP BY year
	ORDER BY year;
END$$

-- ------------------------------------------------------------
-- author_publications_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE author_publications_procedure(
	IN param_author_id INT,
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT publication_id, title, year, type, venue
	FROM author_publications_view
	WHERE author_id = param_author_id AND year BETWEEN param_from_year AND param_to_year
	ORDER BY year DESC, title;
END$$

-- ------------------------------------------------------------
-- author_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE author_stats_procedure(
	IN param_author_id INT
)
BEGIN
	SELECT
		author_name,
		MIN(year) AS first_year,
		MAX(year) AS last_year,
		COUNT(DISTINCT publication_id) AS total_publications,
		SUM(type = 'journal') AS journal_count,
		SUM(type = 'conference') AS conf_count,
		ROUND(COUNT(DISTINCT publication_id) / COUNT(DISTINCT year), 2) AS avg_per_year
	FROM author_publications_view
	WHERE author_id = param_author_id
	GROUP BY author_id, author_name;
END$$

-- ------------------------------------------------------------
-- search_venues_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE search_venues_procedure(
	IN param_name VARCHAR(500),
	IN param_type VARCHAR(20),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		venue_id,
		venue_title AS title,
		venue_type AS type,
		COALESCE(`rank`, 'N/A') AS `rank`,
		COUNT(publication_id) AS publication_count
	FROM venue_publications_view
	WHERE venue_title LIKE CONCAT('%', param_name, '%')
		AND (param_type = '' OR venue_type = param_type)
		AND year BETWEEN param_from_year AND param_to_year
	GROUP BY venue_id, venue_title, venue_type, `rank`
	ORDER BY publication_count DESC;
END$$

-- ------------------------------------------------------------
-- venue_year_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE venue_year_stats_procedure(
	IN param_venue_id INT,
	IN param_type VARCHAR(20),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT year, COUNT(publication_id) AS publication_count
	FROM venue_publications_view
	WHERE venue_id = param_venue_id
	AND venue_type = param_type
	AND year BETWEEN param_from_year AND param_to_year
	GROUP BY year
	ORDER BY year;
END$$

-- ------------------------------------------------------------
-- venue_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE venue_stats_procedure(
	IN param_venue_id INT,
	IN param_type VARCHAR(20),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		MIN(year) AS first_year,
		MAX(year) AS last_year,
		COUNT(DISTINCT publication_id) AS total_publications,
		COUNT(author_id) AS total_authors,
		COUNT(DISTINCT author_id) AS distinct_authors,
		ROUND(COUNT(author_id) / COUNT(DISTINCT publication_id), 2) AS avg_authors_per_article,
		ROUND(COUNT(DISTINCT publication_id) / COUNT(DISTINCT year), 2) AS avg_articles_per_year
	FROM venue_author_publications_view
	WHERE venue_id = param_venue_id AND venue_type = param_type AND year BETWEEN param_from_year AND param_to_year;
END$$

-- ------------------------------------------------------------
-- venue_year_detail_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE venue_year_detail_procedure(
	IN param_venue_id INT,
	IN param_type VARCHAR(20),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		year,
		COUNT(DISTINCT publication_id) AS publication_count,
		COUNT(author_id) AS total_authors,
		COUNT(DISTINCT author_id) AS distinct_authors
	FROM venue_author_publications_view
	WHERE venue_id = param_venue_id
		AND venue_type = param_type
		AND year BETWEEN param_from_year AND param_to_year
	GROUP BY year
	ORDER BY year;
END$$

-- ------------------------------------------------------------
-- venue_publications_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE venue_publications_procedure(
	IN param_venue_id INT,
	IN param_type VARCHAR(20),
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		venue_publications_view.publication_id,
		venue_publications_view.pub_title AS title,
		venue_publications_view.year,
		COUNT(publications_authors.author_id) AS author_count
	FROM venue_publications_view
	LEFT JOIN publications_authors ON venue_publications_view.publication_id = publications_authors.publication_id
	WHERE venue_publications_view.venue_id = param_venue_id
		AND venue_publications_view.venue_type = param_type
		AND venue_publications_view.year BETWEEN param_from_year AND param_to_year
	GROUP BY venue_publications_view.publication_id, venue_publications_view.pub_title, venue_publications_view.year
	ORDER BY venue_publications_view.year DESC, venue_publications_view.pub_title;
END$$

-- ------------------------------------------------------------
-- publications_per_year_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE publications_per_year_procedure(
	IN param_from_year INT,
	IN param_to_year INT
)
BEGIN
	SELECT
		year,
		COUNT(*) AS total,
		SUM(type = 'journal') AS journal_count,
		SUM(type = 'conference') AS conference_count
	FROM valid_publications_view
	WHERE year BETWEEN param_from_year AND param_to_year
	GROUP BY year
	ORDER BY year;
END$$

-- ------------------------------------------------------------
-- year_profile_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE year_profile_procedure(
	IN param_year INT
)
BEGIN
	SELECT
		COUNT(DISTINCT valid_publications_view.publication_id) AS total_publications,
		SUM(valid_publications_view.type = 'journal') AS distinct_journals,
		SUM(valid_publications_view.type = 'conference') AS distinct_conferences,
		COUNT(publications_authors.author_id) AS total_authors,
		COUNT(DISTINCT publications_authors.author_id) AS distinct_authors
	FROM valid_publications_view
	LEFT JOIN publications_authors ON valid_publications_view.publication_id = publications_authors.publication_id
	WHERE valid_publications_view.year = param_year;
END$$

-- ------------------------------------------------------------
-- year_publications_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE year_publications_procedure(
	IN param_year INT,
	IN param_type_filter VARCHAR(20),
	IN param_venue_name VARCHAR(500),
	IN param_author_name VARCHAR(300)
)
BEGIN
	SELECT DISTINCT publication_id, title, type, venue
	FROM year_publication_details_view
	WHERE year = param_year
		AND (param_type_filter = '' OR type = param_type_filter)
		AND (param_venue_name = '' OR journal_title LIKE CONCAT('%', param_venue_name, '%') 
        OR conference_title LIKE CONCAT('%', param_venue_name, '%'))
		AND (param_author_name = '' OR author_name LIKE CONCAT('%', param_author_name, '%'))
	ORDER BY title;
END$$

-- ------------------------------------------------------------
-- publisher_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE publisher_stats_procedure()
BEGIN
	SELECT publisher, journal_count, q1_count, q2_count, q3_count, q4_count
	FROM publisher_stats_view
	ORDER BY journal_count DESC;
END$$

-- ------------------------------------------------------------
-- category_year_stats_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE category_year_stats_procedure(
	IN param_venue_type VARCHAR(20),
	IN param_category VARCHAR(200)
)
BEGIN
	IF param_venue_type = 'journal' THEN
		SELECT year, COUNT(DISTINCT venue_id) AS venue_count, COUNT(DISTINCT publication_id) AS publication_count
		FROM journal_category_year_view
		WHERE (param_category = '' OR category LIKE CONCAT('%', param_category, '%'))
		GROUP BY year ORDER BY year;
	ELSE
		SELECT year, COUNT(DISTINCT venue_id) AS venue_count, COUNT(DISTINCT publication_id) AS publication_count
		FROM conference_category_year_view
		WHERE (param_category = '' OR category LIKE CONCAT('%', param_category, '%'))
		GROUP BY year ORDER BY year;
	END IF;
END$$

-- ------------------------------------------------------------
-- journal_scatter_procedure
-- ------------------------------------------------------------
CREATE PROCEDURE journal_scatter_procedure(
	IN param_subject_area VARCHAR(200)
)
BEGIN
	SELECT title, quartile, subject_area, sjr_index, citescore, h_index,
		total_docs, total_docs_3y, total_refs, total_cites_3y, citable_docs_3y, cites_doc_2y, refs_doc
	FROM journal_metrics_view
	WHERE (param_subject_area = '' OR subject_area LIKE CONCAT('%', param_subject_area, '%'));
END$$

DELIMITER ;
