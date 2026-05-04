SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- ------------------------------------------------------------
-- Schema: Data Visualizer
-- ------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `data_visualizer`;
USE `data_visualizer`;

-- ------------------------------------------------------------
-- Table `authors`
-- ------------------------------------------------------------
CREATE TABLE `authors` (
    `author_id`   INT NOT NULL AUTO_INCREMENT,
    `author_name` VARCHAR(300) NOT NULL,
    PRIMARY KEY (`author_id`),
    INDEX `index_author_name` (`author_name`)
);

-- ------------------------------------------------------------
-- Table `journals`
-- ------------------------------------------------------------
CREATE TABLE `journals` (
    `journal_id`         INT NOT NULL AUTO_INCREMENT,
    `rank`               VARCHAR(100) NULL,
    `title`              VARCHAR(500) NOT NULL,
    `oa_status`          VARCHAR(100) NULL,
    `country`            VARCHAR(100) NULL,
    `sjr_index`          VARCHAR(200) NULL,
    `citescore`          VARCHAR(200) NULL,
    `h_index`            VARCHAR(100) NULL,
    `total_docs`         VARCHAR(100) NULL,
    `total_docs_3y`      VARCHAR(100) NULL,
    `total_refs`         VARCHAR(100) NULL,
    `total_cites_3y`     VARCHAR(100) NULL,
    `citable_docs_3y`    VARCHAR(100) NULL,
    `cites_doc_2y`   VARCHAR(200) NULL,
    `refs_doc`       VARCHAR(200) NULL,
    `best_quartile`      VARCHAR(100) NULL,
    `best_categories`    VARCHAR(500) NULL,
    `best_subject_area`  VARCHAR(200) NULL,
    `best_subject_rank`  VARCHAR(100) NULL,
    `publisher`          VARCHAR(300) NULL,
    `core_collection`    VARCHAR(100) NULL,
    `coverage`           VARCHAR(100) NULL,
    `active`             VARCHAR(100) NULL,
    `in_press`           VARCHAR(100) NULL,
    `iso_language_code`  VARCHAR(100) NULL,
    PRIMARY KEY (`journal_id`),
    INDEX `index_journal_title` (`title`(500))
);

-- ------------------------------------------------------------
-- Table `conferences`
-- ------------------------------------------------------------
CREATE TABLE `conferences` (
    `conference_id` INT NOT NULL AUTO_INCREMENT,
    `title`         VARCHAR(500) NOT NULL,
    `acronym`       VARCHAR(100) NULL,
    `rank`          VARCHAR(100) NULL,
    `primary_for`   VARCHAR(100) NULL,
    PRIMARY KEY (`conference_id`),
    INDEX `index_conference_title`  (`title`(500)),
    INDEX `index_conference_acronym` (`acronym`)
);

-- ------------------------------------------------------------
-- Table `publications`
-- ------------------------------------------------------------
CREATE TABLE `publications` (
    `publication_id` INT NOT NULL,
    `title`          TEXT NULL,
    `year`           VARCHAR(100) NULL,
    `pages`          VARCHAR(500) NULL,
    `ee`             VARCHAR(800) NULL,
    `journal_id`     INT NULL,
    `conference_id`  INT NULL,
    `type`           ENUM('journal','conference') NOT NULL,
    PRIMARY KEY (`publication_id`),
    CONSTRAINT `journal_fk`    FOREIGN KEY (`journal_id`)    REFERENCES `journals`(`journal_id`),
    CONSTRAINT `conference_fk` FOREIGN KEY (`conference_id`) REFERENCES `conferences`(`conference_id`)
);

-- ------------------------------------------------------------
-- Table `publications_authors`
-- ------------------------------------------------------------
CREATE TABLE `publications_authors` (
    `publication_id` INT NOT NULL,
    `author_id`      INT NOT NULL,
    PRIMARY KEY (`publication_id`, `author_id`),
    CONSTRAINT `publication_fk` FOREIGN KEY (`publication_id`) REFERENCES `publications`(`publication_id`),
    CONSTRAINT `author_fk`      FOREIGN KEY (`author_id`)      REFERENCES `authors`(`author_id`)
);

-- ------------------------------------------------------------
-- Staging Table: `staging_publications`
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `staging_publications` (
    `id`        INT,
    `title`     TEXT,
    `year`      TEXT,
    `pages`     TEXT,
    `ee`        TEXT,
    `journal`   VARCHAR(500),
    `booktitle` VARCHAR(500),
    `type`      TEXT
);

-- ------------------------------------------------------------
-- Staging Table: `staging_publications_authors`
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `staging_publications_authors` (
    `publication_id` INT,
    `author_name`    VARCHAR(300)
);

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;