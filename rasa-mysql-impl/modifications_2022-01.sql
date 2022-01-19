# the script creates a new unique index on context table and 
# drops the old index
# author: Wolfgang Walter SAUER (wowasa) <clarin@wowasa.com>
# date: January 2022
ALTER TABLE status CHANGE COLUMN url_id url_id INT(11) NOT NULL;
ALTER TABLE status CHANGE COLUMN message varchar(1024) NOT NULL;
ALTER TABLE status CHANGE COLUMN method varchar(10) DEFAULT NULL
DROP INDEX ukey_context_record_providerGroup_id_expectedMimeType ON context;
CREATE UNIQUE INDEX ukey_context_record_providerGroup_id_expectedMimeType_source ON context(record, providerGroup_id, expectedMimeType, source); 
CREATE INDEX key_providerGroup_id ON context(providerGroup_id);
ALTER TABLE context ADD CONSTRAINT fkey_context_providerGroup_id FOREIGN KEY key_providerGroup_id (providerGroup_id) REFERENCES providerGroup(id);
ALTER TABLE url_context DROP FOREIGN KEY fkey_url_context_url_id;
ALTER TABLE url_context DROP FOREIGN KEY fkey_url_context_context_id;
DROP INDEX key_url_context_url_id ON url_context;
DROP INDEX key_url_context_context_id ON url_context;
CREATE INDEX ukey_url_context_url_id_active_context_id ON url_context(url_id, active, context_id); 
CREATE INDEX ukey_url_context_context_id_active_url_id ON url_context(context_id, active, url_id);
CREATE UNIQUE INDEX key_url_context_url_id_context_id ON url_context(url_id, context_id);
ALTER TABLE url_context ADD CONSTRAINT fkey_url_context_url_id FOREIGN KEY ukey_url_context_url_id_active_context_id (url_id) REFERENCES url(id);
ALTER TABLE url_context ADD CONSTRAINT fkey_url_context_context_id FOREIGN KEY ukey_url_context_context_id_active_url_id (context_id) REFERENCES context(id);
ALTER TABLE url ADD COLUMN host varchar(256) DEFAULT NULL;
ALTER TABLE url ADD COLUMN boolean NOT NULL DEFAULT false;
CREATE INDEX key_url_host ON url(host);
UPDATE context SET source='CMD';