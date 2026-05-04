USE `data_visualizer`;

-- ------------------------------------------------------------
-- Load Dimension Tables
-- ------------------------------------------------------------

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/author_dim.csv'
INTO TABLE `authors`
FIELDS OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@from_file_author_name)
SET `author_name` = TRIM(@from_file_author_name);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/conference_dim.csv'
INTO TABLE `conferences`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@from_file_title, @from_file_acronym, @from_file_rank, @from_file_primary_for)
SET
    `title`       = TRIM(@from_file_title),
    `acronym`     = TRIM(@from_file_acronym),
    `rank`        = TRIM(@from_file_rank),
    `primary_for` = TRIM(@from_file_primary_for);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/journal_dim.csv'
INTO TABLE `journals`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@from_file_rank, @from_file_title, @from_file_oa_status, @from_file_country, @from_file_sjr_index, @from_file_citescore,
 @from_file_h_index, @from_file_best_quartile, @from_file_best_categories, @from_file_best_subject_area,
 @from_file_best_subject_rank, @from_file_publisher, @from_file_core_collection, @from_file_coverage,
 @from_file_active, @from_file_in_press, @from_file_iso_language_code,
 @from_file_total_docs, @from_file_total_docs_3y, @from_file_total_refs,
 @from_file_total_cites_3y, @from_file_citable_docs_3y, @from_file_cites_doc_2y, @from_file_refs_doc,
 @from_file_asjc_codes)
SET
    `rank`              = TRIM(@from_file_rank),
    `title`             = TRIM(@from_file_title),
    `oa_status`         = TRIM(@from_file_oa_status),
    `country`           = TRIM(@from_file_country),
    `sjr_index`         = TRIM(@from_file_sjr_index),
    `citescore`         = TRIM(@from_file_citescore),
    `h_index`           = TRIM(@from_file_h_index),
    `total_docs`        = TRIM(@from_file_total_docs),
    `total_docs_3y`     = TRIM(@from_file_total_docs_3y),
    `total_refs`        = TRIM(@from_file_total_refs),
    `total_cites_3y`    = TRIM(@from_file_total_cites_3y),
    `citable_docs_3y`   = TRIM(@from_file_citable_docs_3y),
    `cites_doc_2y`  = TRIM(@from_file_cites_doc_2y),
    `refs_doc`      = TRIM(@from_file_refs_doc),
    `best_quartile`     = TRIM(@from_file_best_quartile),
    `best_categories`   = TRIM(@from_file_best_categories),
    `best_subject_area` = TRIM(@from_file_best_subject_area),
    `best_subject_rank` = TRIM(@from_file_best_subject_rank),
    `publisher`         = TRIM(@from_file_publisher),
    `core_collection`   = TRIM(@from_file_core_collection),
    `coverage`          = TRIM(@from_file_coverage),
    `active`            = TRIM(@from_file_active),
    `in_press`          = TRIM(@from_file_in_press),
    `iso_language_code` = TRIM(@from_file_iso_language_code);

-- ------------------------------------------------------------
-- Load Staging Tables
-- ------------------------------------------------------------

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/publication_fact.csv'
INTO TABLE `staging_publications`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(`id`, `title`, `year`, `pages`, `ee`, `journal`, `booktitle`, `type`);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/publication_author.csv'
INTO TABLE `staging_publications_authors`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(`publication_id`, `author_name`);

-- ------------------------------------------------------------
-- Build Staging Indexes
-- ------------------------------------------------------------

ALTER TABLE `staging_publications`
    ADD INDEX `staging_journal_idx`   (`journal`),
    ADD INDEX `staging_booktitle_idx` (`booktitle`);

ALTER TABLE `staging_publications_authors`
    ADD INDEX `staging_publication_id_idx` (`publication_id`),
    ADD INDEX `staging_author_name_idx`    (`author_name`);

-- ------------------------------------------------------------
-- Normalize Journal Names in Staging
-- ------------------------------------------------------------

SET SQL_SAFE_UPDATES = 0;
UPDATE `staging_publications`
SET `journal` = normalize_journal(`journal`)
WHERE `type` = 'journal';
SET SQL_SAFE_UPDATES = 1;

-- ------------------------------------------------------------
-- Build Temp Lookup Tables
-- ------------------------------------------------------------

DROP TEMPORARY TABLE IF EXISTS `tmp_journal_lookup`;
CREATE TEMPORARY TABLE `tmp_journal_lookup` (
    `journal_id` INT,
    `title`      VARCHAR(500),
    INDEX `idx_tmp_journal_title` (`title`(500))
) SELECT MIN(`journal_id`) AS `journal_id`, `title`
  FROM `journals`
  GROUP BY `title`;

DROP TEMPORARY TABLE IF EXISTS `tmp_conference_title_lookup`;
CREATE TEMPORARY TABLE `tmp_conference_title_lookup` (
    `conference_id` INT,
    `title`         VARCHAR(500),
    INDEX `idx_tmp_conf_title` (`title`(500))
) SELECT MIN(`conference_id`) AS `conference_id`, `title`
  FROM `conferences`
  GROUP BY `title`;

DROP TEMPORARY TABLE IF EXISTS `tmp_conference_acronym_lookup`;
CREATE TEMPORARY TABLE `tmp_conference_acronym_lookup` (
    `conference_id` INT,
    `acronym`       VARCHAR(100),
    INDEX `idx_tmp_conf_acronym` (`acronym`)
) SELECT MIN(`conference_id`) AS `conference_id`, `acronym`
  FROM `conferences`
  GROUP BY `acronym`;

-- ------------------------------------------------------------
-- Insert into publications
-- ------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

INSERT INTO `publications` (`publication_id`, `type`, `title`, `year`, `pages`, `ee`, `journal_id`, `conference_id`)
SELECT
    sp.`id`,
    sp.`type`,
    sp.`title`,
    sp.`year`,
    sp.`pages`,
    sp.`ee`,
    jd.`journal_id`,
    COALESCE(cd1.`conference_id`, cd2.`conference_id`)
FROM `staging_publications` sp
LEFT JOIN `tmp_journal_lookup` jd
    ON sp.`journal` = jd.`title` AND sp.`type` = 'journal'
LEFT JOIN `tmp_conference_title_lookup` cd1
    ON sp.`booktitle` = cd1.`title` AND sp.`type` = 'conference'
LEFT JOIN `tmp_conference_acronym_lookup` cd2
    ON sp.`booktitle` = cd2.`acronym` AND sp.`type` = 'conference';

DROP TEMPORARY TABLE `tmp_journal_lookup`;
DROP TEMPORARY TABLE `tmp_conference_title_lookup`;
DROP TEMPORARY TABLE `tmp_conference_acronym_lookup`;

-- ------------------------------------------------------------
-- Insert into publications_authors
-- ------------------------------------------------------------

INSERT INTO `publications_authors` (`publication_id`, `author_id`)
SELECT DISTINCT
    spa.`publication_id`,
    a.`author_id`
FROM `staging_publications_authors` spa
JOIN `publications` p
    ON spa.`publication_id` = p.`publication_id`
JOIN `authors` a
    ON spa.`author_name` = a.`author_name`;

SET UNIQUE_CHECKS = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- Drop Staging Tables
-- ------------------------------------------------------------

DROP TABLE IF EXISTS `staging_publications`;
DROP TABLE IF EXISTS `staging_publications_authors`;

-- ------------------------------------------------------------
-- Build Fact Table Indexes
-- ------------------------------------------------------------

CREATE INDEX `idx_pub_year`  ON `publications`         (`year`);
CREATE INDEX `idx_pub_jid`   ON `publications`         (`journal_id`);
CREATE INDEX `idx_pub_cid`   ON `publications`         (`conference_id`);
CREATE INDEX `idx_pa_author` ON `publications_authors` (`author_id`);
