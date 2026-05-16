USE `data_visualizer`;

-- ------------------------------------------------------------
-- Load Dimension Tables
-- ------------------------------------------------------------

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/author_dim.csv'
INTO TABLE `authors`
FIELDS OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(`author_name`);


LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/conference_dim.csv'
INTO TABLE `conferences`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(`title`, `acronym`, `rank`, `primary_for`);


LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/journal_dim.csv'
INTO TABLE `journals`
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(`rank`, `title`, `oa_status`, `country`, `sjr_index`, `citescore`,
 `h_index`, `best_quartile`, `best_categories`, `best_subject_area`,
 `best_subject_rank`, `publisher`, `core_collection`, `coverage`,
 `active`, `in_press`, `iso_language_code`,
 `total_docs`, `total_docs_3y`, `total_refs`,
 `total_cites_3y`, `citable_docs_3y`, `cites_doc_2y`, `refs_doc`,
 @skip);


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
-- Normalize Journal Names in Staging
-- ------------------------------------------------------------

SET SQL_SAFE_UPDATES = 0;
UPDATE `staging_publications`
SET `journal` = normalize_journal(`journal`)
WHERE `type` = 'journal';
SET SQL_SAFE_UPDATES = 1;


-- ------------------------------------------------------------
-- Build Staging Indexes
-- ------------------------------------------------------------

ALTER TABLE `staging_publications_authors`
    ADD INDEX `staging_publication_id_idx` (`publication_id`),
    ADD INDEX `staging_author_name_idx` (`author_name`);

ALTER TABLE `staging_publications`
    ADD INDEX `staging_journal_idx` (`journal`),
    ADD INDEX `staging_booktitle_idx` (`booktitle`);


-- ------------------------------------------------------------
-- Insert into publications
-- ------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

INSERT INTO `publications` (`publication_id`, `type`, `title`, `year`, `pages`, `ee`, `journal_id`, `conference_id`)
WITH
    journal_lookup AS (
        SELECT MIN(`journal_id`) AS `journal_id`, `title`
        FROM `journals`
        GROUP BY `title`
    ),
    conference_title_lookup AS (
        SELECT MIN(`conference_id`) AS `conference_id`, `title`
        FROM `conferences`
        GROUP BY `title`
    ),
    conference_acronym_lookup AS (
        SELECT MIN(`conference_id`) AS `conference_id`, `acronym`
        FROM `conferences`
        GROUP BY `acronym`
    )
SELECT
    `staging_publications`.`id`,
    `staging_publications`.`type`,
    `staging_publications`.`title`,
    `staging_publications`.`year`,
    `staging_publications`.`pages`,
    `staging_publications`.`ee`,
    journal_lookup.`journal_id`,
    COALESCE(conference_title_lookup.`conference_id`, conference_acronym_lookup.`conference_id`)
FROM `staging_publications`
LEFT JOIN journal_lookup
    ON `staging_publications`.`journal` = journal_lookup.`title` AND `staging_publications`.`type` = 'journal'
LEFT JOIN conference_title_lookup
    ON `staging_publications`.`booktitle` = conference_title_lookup.`title` AND `staging_publications`.`type` = 'conference'
LEFT JOIN conference_acronym_lookup
    ON `staging_publications`.`booktitle` = conference_acronym_lookup.`acronym` AND `staging_publications`.`type` = 'conference';


-- ------------------------------------------------------------
-- Insert into publications_authors
-- ------------------------------------------------------------

INSERT INTO `publications_authors` (`publication_id`, `author_id`)
SELECT DISTINCT
    `staging_publications_authors`.`publication_id`,
    `authors`.`author_id`
FROM `staging_publications_authors`
JOIN `publications`
    ON `staging_publications_authors`.`publication_id` = `publications`.`publication_id`
JOIN `authors`
    ON `staging_publications_authors`.`author_name` = `authors`.`author_name`;

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

CREATE INDEX `idx_pub_year` ON `publications` (`year`);
CREATE INDEX `idx_pub_jid` ON `publications` (`journal_id`);
CREATE INDEX `idx_pub_cid` ON `publications` (`conference_id`);
CREATE INDEX `idx_pa_author` ON `publications_authors` (`author_id`);
