# the script creates a new unique index on context table and 
# drops the old index
# author: Wolfgang Walter SAUER (wowasa) <clarin@wowasa.com>
# date: November 2021
CREATE UNIQUE INDEX ukey_context_source_record_providerGroup_id_expectedMimeType ON context(source, record, providerGroup_id, expectedMimeType); 
DROP INDEX ukey_context_record_providerGroup_id_expectedMimeType ON context;