SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- ------------------------------------------------------------
-- Schema: Data Visualizer
-- ------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `data_visualizer`;
USE `data_visualizer`;

-- ------------------------------------------------------------
-- Table `publication_fact`
-- ------------------------------------------------------------
CREATE TABLE `publication_fact` (
    `publication_id` INT NOT NULL,
    `type` ENUM('article','inproceedings') NOT NULL,
    `title` TEXT NULL,
    `year` VARCHAR(10) NULL,
    `pages` VARCHAR(50) NULL,
    `ee` VARCHAR(500) NULL,
    `journal_id` INT NULL,
    `conference_id` INT NULL,
    PRIMARY KEY (`publication_id`),
    CONSTRAINT `journal_fk` FOREIGN KEY (`journal_id`) REFERENCES `journal_dim`(`journal_id`),
    CONSTRAINT `conference_fk` FOREIGN KEY (`conference_id`) REFERENCES `conference_dim`(`conference_id`)
);

-- ------------------------------------------------------------
-- publication_authors (because we have many-many)
-- ------------------------------------------------------------
CREATE TABLE `publication_authors` (
    `publication_id` INT NOT NULL,
    `author_id` INT NOT NULL,
    PRIMARY KEY (`publication_id`, `author_id`),
    CONSTRAINT `publication_fk` FOREIGN KEY (`publication_id`) REFERENCES `publication_fact`(`publication_id`),
    CONSTRAINT `author_fk` FOREIGN KEY (`author_id`) REFERENCES `author_dim`(`author_id`)
);

-- ------------------------------------------------------------
-- author_dim
-- ------------------------------------------------------------
CREATE TABLE `author_dim` (
    `author_id` INT NOT NULL AUTO_INCREMENT,
    `author_name` VARCHAR(300) NOT NULL,
    PRIMARY KEY (`author_id`)
);

-- ------------------------------------------------------------
-- journal_dim
-- ------------------------------------------------------------
CREATE TABLE `journal_dim` (
    `journal_id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(500) NOT NULL,
    `rank` VARCHAR(10) NULL,
    `sjr_index` VARCHAR(20) NULL,
    `citescore` VARCHAR(20) NULL,
    `h_index` VARCHAR(10) NULL,
    `best_quartile` VARCHAR(5) NULL,
    `best_categories` VARCHAR(500) NULL,
    `best_subject_area` VARCHAR(200) NULL,
    `best_subject_rank` VARCHAR(10) NULL,
    `publisher` VARCHAR(300) NULL,
    `country` VARCHAR(100) NULL,
    `coverage` VARCHAR(10) NULL,
    `active` VARCHAR(10) NULL,
    `iso_language_code` VARCHAR(10) NULL,
    PRIMARY KEY (`journal_id`)
);

-- ------------------------------------------------------------
-- conference_dim
-- ------------------------------------------------------------
CREATE TABLE `conference_dim` (
    `conference_id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(500) NOT NULL,
    `acronym` VARCHAR(100) NULL,
    `rank` VARCHAR(10) NULL,
    `primary_for` VARCHAR(100) NULL,
    PRIMARY KEY (`conference_id`)
);

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
