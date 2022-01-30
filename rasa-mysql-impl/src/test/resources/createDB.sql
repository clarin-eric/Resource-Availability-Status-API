# This script creates a linkchecker database as it is assumed by this mysql implementation of the rasa-api
# author: Wolfgang Walter SAUER (wowasa) <clarin@wowasa.com>
# date: July 2021

DROP DATABASE  IF EXISTS `linkchecker`;
CREATE DATABASE  IF NOT EXISTS `linkchecker` CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `linkchecker`;

CREATE TABLE `providerGroup` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_providerGroup_name` (`name`)
);


CREATE TABLE `context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `source` varchar(256) DEFAULT NULL,
  `record` varchar(256) DEFAULT NULL,
  `providerGroup_id` int DEFAULT NULL,
  `expectedMimeType` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_context_record_providerGroup_id_expectedMimeType_source` (`record`, `providerGroup_id`, `expectedMimeType`, `source`),
  KEY `key_providerGroup_id` (`providerGroup_id`),
  CONSTRAINT `fkey_context_providerGroup_id` FOREIGN KEY `key_providerGroup_id` (`providerGroup_id`) REFERENCES `providerGroup` (`id`)
);



CREATE TABLE `url` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `groupKey` varchar(128) DEFAULT NULL,
  `valid` boolean DEFAULT NULL, 
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_url_url` (`url`),
  KEY `key_url_host` (`groupKey`)
);


CREATE TABLE `url_context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int NOT NULL,
  `context_id` int NOT NULL,
  `ingestionDate` datetime NOT NULL DEFAULT NOW(),
  `active` boolean NOT NULL DEFAULT false,
  PRIMARY KEY (`id`),
  KEY `key_url_context_url_id_active_context_id` (`url_id`, `active`, `context_id`),
  KEY `key_url_context_context_id_active_url_id` (`context_id`, `active`, `url_id`),
  UNIQUE KEY `ukey_url_context_url_id_context_id` (`url_id`, `context_id`),
  CONSTRAINT `fkey_url_context_url_id` FOREIGN KEY `key_url_context_url_id_active_context_id` (`url_id`) REFERENCES `url` (`id`),
  CONSTRAINT `fkey_url_context_context_id` FOREIGN KEY `key_url_context_context_id_active_url_id` (`context_id`) REFERENCES `context` (`id`)
);


CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int NOT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(1024) NOT NULL,
  `category` varchar(25) NOT NULL,
  `method` varchar(10) DEFAULT NULL,
  `contentType` varchar(256) DEFAULT NULL,
  `byteSize` bigint DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `checkingDate` datetime NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_status_url_id` (`url_id`),
  KEY `key_status_statusCode` (`statusCode`),
  CONSTRAINT `fkey_status_url_id` FOREIGN KEY `ukey_status_url_id` (`url_id`) REFERENCES `url` (`id`)
);


CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status_id` int NOT NULL,
  `url_id` int DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(256),
  `category` varchar(25) NOT NULL,
  `method` varchar(10) NOT NULL,
  `contentType` varchar(256) DEFAULT NULL,
  `byteSize` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `checkingDate` datetime NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_history_url_id_ceckingDate` (`url_id`,`checkingDate`)
);

CREATE TABLE `nextCheck` (
  `url_id` int NOT NULL,
  PRIMARY KEY (`url_id`)
);
