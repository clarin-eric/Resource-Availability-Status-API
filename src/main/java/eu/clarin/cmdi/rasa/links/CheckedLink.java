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

public class CheckedLink {
    private String url;
    private String method;
    private String message;
    private int status;
    private String contentType;
    private String byteSize;
    private long duration;
    private long timestamp;
    private int redirectCount;
    private String collection;
    private String record;
    private String expectedMimeType;

    public CheckedLink(Document document){
        this.url = document.getString("url");
        this.method = document.getString("method");
        this.message = document.getString("message");
        this.status = document.getInteger("status");
        this.contentType = document.getString("contentType");
        this.byteSize = document.getString("byteSize");
        this.duration = document.getLong("duration");
        this.timestamp = document.getLong("timestamp");
        this.collection = document.getString("collection");
        this.record = document.getString("record");
        this.redirectCount = document.getInteger("redirectCount");
        this.expectedMimeType = document.getString("expectedMimeType");
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getByteSize() {
        return byteSize;
    }

    public void setByteSize(String byteSize) {
        this.byteSize = byteSize;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getExpectedMimeType() {
        return expectedMimeType;
    }

    public void setExpectedMimeType(String expectedMimeType) {
        this.expectedMimeType = expectedMimeType;
    }
}
