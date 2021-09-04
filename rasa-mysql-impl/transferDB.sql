# the script creates a relational linkchecker database
# and transfers data from the former stomychecker datase to the linkchecker database
# author: Wolfgang Walter SAUER (wowasa) <wolfgang.sauer@oeaw.ac.at>

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
  UNIQUE KEY `ukey_context_record_providerGroup_id_expectedMimeType` (`record`,`providerGroup_id`, `expectedMimeType`)
);



CREATE TABLE `url` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ukey_url_url` (`url`)
);


CREATE TABLE `url_context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int NOT NULL,
  `context_id` int NOT NULL,
  `ingestionDate` datetime NOT NULL DEFAULT NOW(),
  `active` boolean NOT NULL DEFAULT false,
  PRIMARY KEY (`id`),
  KEY `key_url_context_url_id` (`url_id`),
  KEY `key_url_context_context_id` (`context_id`),
  KEY `key_url_context_url_id_active` (`url_id`, `active`),
  CONSTRAINT `fkey_url_context_url_id` FOREIGN KEY `key_url_context_url_id` (`url_id`) REFERENCES `url` (`id`),
  CONSTRAINT `fkey_url_context_context_id` FOREIGN KEY `key_url_context_context_id` (`context_id`) REFERENCES `context` (`id`)
);


CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url_id` int DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(1024),
  `category` varchar(25) NOT NULL,
  `method` varchar(10) NOT NULL,
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

# create temporary table tmp_history for corrections
CREATE TEMPORARY TABLE `tmp_history` (
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
  UNIQUE KEY `ukey_history_url_id_ceckingDate` (`url_id`,`checkingDate`),
  KEY `key_tmp_history_url_id` (`url_id`),
  KEY `key_tmp_history_checkingDate` (`checkingDate`)
);

CREATE INDEX IF NOT EXISTS idx_history_url ON stormychecker.history(url);

INSERT IGNORE INTO linkchecker.tmp_history(url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT u.id, s.statusCode, s.message, s.category, s.method, s.contentType, s.byteSize, s.duration, s.timestamp, s.redirectCount FROM stormychecker.status s, linkchecker.url u
WHERE s.url=u.url;

INSERT IGNORE INTO linkchecker.tmp_history(url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT u.id, h.statusCode, h.message, h.category, h.method, h.contentType, h.byteSize, h.duration, h.timestamp, h.redirectCount FROM stormychecker.history h, linkchecker.url u
WHERE h.url=u.url;

# delete records where the switch from head to get request might not have worked
DELETE FROM tmp_history WHERE category != 'Ok' AND method = 'HEAD';

CREATE TEMPORARY TABLE `tmp_latest_history` (
   `url_id` int,
   `checkingDate` datetime NOT NULL,
   KEY `key_tmp_latest_history_url_id` (`url_id`),
   KEY `key_tmp_latest_history_checkingDate` (`checkingDate`)
);

INSERT INTO tmp_latest_history
SELECT t.url_id, MAX(t.checkingDate) AS maxtime FROM tmp_history t GROUP BY t.url_id;

# insert latest record for each url into status table
INSERT INTO status(url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT t.url_id, t.statusCode, t.message, t.category, t.method, t.contentType, t.byteSize, t.duration, t.checkingDate, t.redirectCount
FROM tmp_history t, tmp_latest_history m
WHERE t.url_id=m.url_id
AND t.checkingDate=m.checkingDate;


# delete these records from table tmp_history
DELETE t.* FROM tmp_history t, tmp_latest_history m
WHERE t.url_id=m.url_id
AND t.checkingDate=m.checkingDate;

# insert the remaining records from table tmp_history into table history
INSERT INTO history(url_id, status_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)
SELECT t.url_id, s.id, t.statusCode, t.message, t.category, t.method, t.contentType, t.byteSize, t.duration, t.checkingDate, t.redirectCount
FROM tmp_history t, status s
WHERE t.url_id=s.url_id;