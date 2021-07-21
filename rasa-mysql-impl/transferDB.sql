# the script creates a relational linkchecker database
# and transfers data from the former stomychecker datase to the linkchecker database
# author: Wolfgang Walter SAUER (wolfgang.sauer@oeaw.ac.at)

DROP DATABASE  IF EXISTS `linkchecker`;
CREATE DATABASE  IF NOT EXISTS `linkchecker` CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `linkchecker`;

CREATE TABLE `providerGroup` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
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
  `url` varchar(1024) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url` (`url`)
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
  `checkingDate` datetime NOT NULL,
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
  `checkingDate` datetime NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url_id_ceckingDate` (`url_id`,`checkingDate`)
);

# transfer urls
INSERT IGNORE INTO linkchecker.url(url)
SELECT url FROM stormychecker.urls;

# transfer provider groups
INSERT INTO linkchecker.providerGroup(name)
SELECT DISTINCT collection FROM stormychecker.urls;

# transfer contexts
INSERT INTO linkchecker.context(record, providerGroup_id, expectedMimeType)
SELECT DISTINCT u.record, p.id, u.expectedMimeType FROM stormychecker.urls u, linkchecker.providerGroup p
WHERE u.collection=p.name;

# link contexts to urls
INSERT INTO url_context(url_id, context_id)
SELECT lu.id, lc.id
FROM linkchecker.url lu, linkchecker.context lc, linkchecker.providerGroup lp, stormychecker.urls su
WHERE su.url=lu.url
AND su.record=lc.record
AND su.collection=lp.name
AND lc.providerGroup_id=lp.id
AND su.expectedMimeType=lc.expectedMimeType;

# transfer status
INSERT INTO linkchecker.status(url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT u.id, s.statusCode, s.message, s.category, s.method, s.contentType, s.byteSize, s.duration, s.timestamp, s.redirectCount FROM stormychecker.status s, linkchecker.url u
WHERE s.url=u.url;

# transfer history
CREATE INDEX IF NOT EXISTS idx_history_url ON stormychecker.history(url);
INSERT IGNORE INTO linkchecker.history(url_id, status_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT u.id, s.id, h.statusCode, h.message, h.category, h.method, h.contentType, h.byteSize, h.duration, h.timestamp, h.redirectCount
FROM stormychecker.history h, linkchecker.url u, linkchecker.status s
WHERE h.category IS NOT NULL
AND h.url=u.url
AND u.id=s.url_id;