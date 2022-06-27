CREATE TABLE `obsolete` (
  `url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `source` varchar(256) DEFAULT NULL,
  `providerGroupName` varchar(256) DEFAULT NULL,
  `record` varchar(256) DEFAULT NULL,
  `expectedMimeType` varchar(256) DEFAULT NULL,
  `ingestionDate` datetime DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(1024) DEFAULT NULL,
  `category` varchar(25) NOT NULL,
  `method` varchar(10) DEFAULT NULL,
  `contentType` varchar(256) DEFAULT NULL,
  `byteSize` bigint DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `checkingDate` datetime NOT NULL,
  `redirectCount` int DEFAULT NULL,
  `deletionDate` datetime NOT NULL DEFAULT NOW()
);

DROP INDEX key_status_statusCode ON status;
CREATE INDEX key_status_category ON status(category);
UPDATE url SET valid=NULL WHERE valid=False;