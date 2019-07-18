/*
 * Copyright (C) 2019 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package eu.clarin.cmdi.rasa.links;

import org.bson.Document;

public class LinkToBeChecked {
    private String url;
    private String record;
    private String collection;
    private String expectedMimeType;

    public LinkToBeChecked() {
    }

    public LinkToBeChecked(String url, String record, String collection, String expectedMimeType) {
        this.url = url;
        this.record = record;
        this.collection = collection;
        this.expectedMimeType = expectedMimeType;
    }

    public LinkToBeChecked(CheckedLink checkedLink) {
        this.url = checkedLink.getUrl();
        this.record = checkedLink.getRecord();
        this.collection = checkedLink.getCollection();
        this.expectedMimeType = checkedLink.getExpectedMimeType();
    }

    public LinkToBeChecked(Document document) {
        this.url = document.getString("url");
        this.collection = document.getString("collection");
        this.record = document.getString("record");
        this.expectedMimeType = document.getString("expectedMimeType");
    }

    public Document getMongoDocument() {
        return new Document("url", url)
                .append("record", record)
                .append("collection", collection)
                .append("expectedMimeType", expectedMimeType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getExpectedMimeType() {
        return expectedMimeType;
    }

    public void setExpectedMimeType(String expectedMimeType) {
        this.expectedMimeType = expectedMimeType;
    }
}
