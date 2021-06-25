SET @@global.time_zone = '+00:00';
CREATE TABLE `providerGroup` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `name_hash` char(32) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name_hash` (`name_hash`)
);


CREATE TABLE `context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `source` varchar(256) DEFAULT NULL,
  `record` varchar(256) DEFAULT NULL,
  `providerGroup_id` int DEFAULT NULL,
  `expectedMimeType` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_record_providerGroup_id_expectedMimeType` (`record`,`providerGroup_id`, `expectedMimeType`)
);



CREATE TABLE `url` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) NOT NULL,
  `url_hash` char(32) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `nextFetchDate` datetime NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url_hash` (`url_hash`)
);


CREATE TABLE `url_context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int NOT NULL,
  `context_id` int NOT NULL,
  `ingestionDate` datetime NOT NULL DEFAULT NOW(),
  `active` boolean NOT NULL DEFAULT false,
  PRIMARY KEY (`id`),
  KEY `fk_url_context_1_idx` (`url_id`),
  KEY `fk_url_context_2_idx` (`context_id`),
  KEY `idx_active` (`active`),
  CONSTRAINT `fk_url_context_1` FOREIGN KEY (`url_id`) REFERENCES `url` (`id`),
  CONSTRAINT `fk_url_context_2` FOREIGN KEY (`context_id`) REFERENCES `context` (`id`)
);


CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(256),
  `category` varchar(25) NOT NULL,
  `method` varchar(10) NOT NULL,
  `contentType` varchar(256) DEFAULT NULL,
  `byteSize` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `checkingDate` timestamp NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url_id` (`url_id`),
  KEY `fk_status_1_idx` (`url_id`),
  CONSTRAINT `fk_status_1` FOREIGN KEY (`url_id`) REFERENCES `url` (`id`)
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
  `checkingDate` timestamp NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url_id_ceckingDate` (`url_id`,`checkingDate`)
);