SET @@global.time_zone = '+00:00';

CREATE TABLE `providerGroup` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `name_hash` char(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name_hash` (`name_hash`)
);


CREATE TABLE `context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `record` varchar(256) DEFAULT NULL,
  `providerGroup_id` int DEFAULT NULL,
  `expectedMimeType` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_providerGroup_id_record` (`record`,`providerGroup_id`)
);



CREATE TABLE `link` (
  `id` int NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) NOT NULL,
  `url_hash` char(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `host` varchar(128) NOT NULL,
  `nextFetchDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url_hash` (`url_hash`)
);


CREATE TABLE `link_context` (
  `id` int NOT NULL AUTO_INCREMENT,
  `link_id` int NOT NULL,
  `context_id` int NOT NULL,
  `harvestDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_link_context_1_idx` (`link_id`),
  KEY `fk_link_context_2_idx` (`context_id`),
  CONSTRAINT `fk_link_context_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`),
  CONSTRAINT `fk_link_context_2` FOREIGN KEY (`context_id`) REFERENCES `context` (`id`)
);


CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `link_id` int DEFAULT NULL,
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
  UNIQUE KEY `idx_link_id` (`link_id`),
  KEY `fk_status_1_idx` (`link_id`),
  CONSTRAINT `fk_status_1` FOREIGN KEY (`link_id`) REFERENCES `link` (`id`)
);


CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `status_id` int NOT NULL,
  `link_id` int DEFAULT NULL,
  `statusCode` int DEFAULT NULL,
  `message` varchar(256),
  `category` varchar(25) NOT NULL DEFAULT 'Undeterminded',
  `method` varchar(10) NOT NULL,
  `contentType` varchar(256) DEFAULT NULL,
  `byteSize` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `checkingDate` timestamp NOT NULL,
  `redirectCount` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_link_id_ceckingDate` (`link_id`,`checkingDate`)
);


INSERT INTO providerGroup(name, name_hash)
VALUES 
('NotGoogle', MD5('NotGoogle')),
('Google', MD5('Google'));

INSERT INTO context(record, providerGroup_id) 
VALUES 
('record', (SELECT id FROM providerGroup WHERE name_hash = MD5('NotGoogle'))),
('GoogleRecord', (SELECT id FROM providerGroup WHERE name_hash = MD5('Google')));

INSERT INTO link(url, url_hash, host, nextFetchDate)
VALUES
('http://www.ailla.org/waiting.html', MD5('http://www.ailla.org/waiting.html'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/audio_files/EMP1M1B1.mp3', MD5('http://www.ailla.org/audio_files/EMP1M1B1.mp3'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/audio_files/WBA1M3A2.mp3', MD5('http://www.ailla.org/audio_files/WBA1M3A2.mp3'), '/www.ailla.org', '2000-01-01'),
('http://www.ailla.org/text_files/WBA1M1A2a.mp3', MD5('http://www.ailla.org/text_files/WBA1M1A2a.mp3'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/audio_files/KUA2M1A1.mp3', MD5('http://www.ailla.org/audio_files/KUA2M1A1.mp3'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/text_files/KUA2M1.pdf', MD5('http://www.ailla.org/text_files/KUA2M1.pdf'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/audio_files/sarixojani.mp3', MD5('http://www.ailla.org/audio_files/sarixojani.mp3'), '/www.ailla.org', '2000-01-01'),
('http://www.ailla.org/audio_files/TEH11M7A1sa.mp3', MD5('http://www.ailla.org/audio_files/TEH11M7A1sa.mp3'), 'www.ailla.org', '2000-01-01'),
('http://www.ailla.org/text_files/TEH11M7.pdf', MD5('http://www.ailla.org/text_files/TEH11M7.pdf'), 'www.ailla.org', '2000-01-01'),
('http://dspin.dwds.de:8088/ddc-sru/dta/', MD5('http://dspin.dwds.de:8088/ddc-sru/dta/'), 'dspin.dwds.de', '2000-01-01'),
('http://dspin.dwds.de:8088/ddc-sru/grenzboten/', MD5('http://dspin.dwds.de:8088/ddc-sru/grenzboten/'), 'dspin.dwds.de', '2000-01-01'),
('http://dspin.dwds.de:8088/ddc-sru/rem/', MD5('http://dspin.dwds.de:8088/ddc-sru/rem/'), 'dspin.dwds.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml'), '/www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml', MD5('http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml'), 'www.deutschestextarchiv.de', '2000-01-01'),
('https://www.google.com', MD5('https://www.google.com'), 'www.google.com', '2000-01-01'),
('https://maps.google.com', MD5('https://maps.google.com'), 'maps.google.com', '2000-01-01'),
('https://drive.google.com', MD5('https://drive.google.com'), 'drive.google.com', '2000-01-01');

INSERT INTO link_context(link_id, context_id, harvestDate)
VALUES
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/waiting.html')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/EMP1M1B1.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/WBA1M3A2.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/WBA1M1A2a.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/KUA2M1A1.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/KUA2M1.pdf')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/sarixojani.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/TEH11M7A1sa.mp3')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/TEH11M7.pdf')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/dta/')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/grenzboten/')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/rem/')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml')), (SELECT id FROM context WHERE record = 'record'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('https://www.google.com')), (SELECT id FROM context WHERE record = 'GoogleRecord'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('https://maps.google.com')), (SELECT id FROM context WHERE record = 'GoogleRecord'), '2000-01-01'),
((SELECT id FROM link WHERE url_hash=MD5('https://drive.google.com')), (SELECT id FROM context WHERE record = 'GoogleRecord'), '2000-01-01');

INSERT INTO status(link_id,method,statusCode,contentType,byteSize,duration,checkingDate,message,redirectCount,category)
VALUES
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/waiting.html')), 'HEAD', 200, 'text/html; charset=UTF-8', 100, 132, '2019-10-11 00:00:00', 'Ok', 0, 'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/EMP1M1B1.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 46, '2019-10-11 00:00:00', 'Broken', 0, 'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/WBA1M3A2.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 46, '2019-10-11 00:00:00', 'Broken', 0, 'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/WBA1M1A2a.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 46, '2019-10-11 00:00:00', 'Broken', 0,  'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/KUA2M1A1.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 56, '2019-10-11 00:00:00', 'Broken', 0, 'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/KUA2M1.pdf')), 'HEAD',  200, 'text/html; charset=UTF-8', 0, 51, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/sarixojani.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 48, '2019-10-11 00:00:00', 'Broken', 0, 'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/audio_files/TEH11M7A1sa.mp3')), 'GET',  400, 'text/html; charset=UTF-8', 0, 48, '2019-10-11 00:00:00', 'Broken', 0, 'Broken'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.ailla.org/text_files/TEH11M7.pdf')), 'HEAD',  200, 'text/html; charset=UTF-8', 0, 57, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/dta/')), 'HEAD',  200, 'application/xml;charset=utf-8', 2094, 67, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/grenzboten/')), 'HEAD',  200, 'application/xml;charset=utf-8', 2273, 57, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://dspin.dwds.de:8088/ddc-sru/rem/')), 'HEAD',  200, 'application/xml;charset=utf-8', 2497, 58, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 591, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 592, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 602, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 613, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 605, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 599, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml')), 'HEAD',  200, 'text/html; charset=utf-8', 0, 591, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('https://www.google.com')), 'HEAD',  200, 'text/html; charset=ISO-8859-1', 0, 222, '2019-10-11 00:00:00', 'Ok', 0,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('https://maps.google.com')), 'HEAD',  200, 'text/html; charset=UTF-8', 0, 440, '2019-10-11 00:00:00', 'Ok', 2,  'Ok'),
((SELECT id FROM link WHERE url_hash=MD5('https://drive.google.com')), 'HEAD',  200, 'text/html; charset=UTF-8', 73232, 413, '2019-10-11 00:00:00', 'Ok', 1, 'Ok');