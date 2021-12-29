# the script creates a new unique index on context table and 
# drops the old index
# author: Wolfgang Walter SAUER (wowasa) <clarin@wowasa.com>
# date: November 2021
DROP INDEX ukey_context_record_providerGroup_id_expectedMimeType ON context;
CREATE UNIQUE INDEX ukey_context_source_record_providerGroup_id_expectedMimeType ON context(source, record, providerGroup_id, expectedMimeType); 
CREATE UNIQUE INDEX ukey_url_context_url_id_context_id ON url_context(url_id, context_id); 
CREATE INDEX key_status_category ON status(category); 
CREATE INDEX key_context_providerGroup_id ON context(providerGroup_id);
#CREATE INDEX key_url_context_active ON url_context(active);
update context set source='CMD';