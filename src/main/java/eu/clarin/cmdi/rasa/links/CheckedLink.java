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

import java.util.Objects;

public class CheckedLink {

    private String url;
    private String method;
    private String message;
    private int status;
    private String contentType;
    private String byteSize;
    private long duration;
    private long timestamp;

    private String collection;
    private int redirectCount;
    private String record;
    private String expectedMimeType;

    public CheckedLink() {

    }

    public CheckedLink(String url, String method, String message, int status, String contentType, String byteSize, long duration, long timestamp, String collection, int redirectCount, String record, String expectedMimeType) {
        this.url = url;
        this.method = method;
        this.message = message;
        this.status = status;
        this.contentType = contentType;
        this.byteSize = byteSize;
        this.duration = duration;
        this.timestamp = timestamp;
        this.collection = collection;
        this.redirectCount = redirectCount;
        this.record = record;
        this.expectedMimeType = expectedMimeType;
    }

    public CheckedLink(Document document) {
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

    //    if you add a new parameter dont forget to add it to this method
    public Document getMongoDocument() {
        Document document = new Document("url", url)
                .append("method", method)
                .append("message", message)
                .append("status", status)
                .append("contentType", contentType)
                .append("byteSize", byteSize)
                .append("duration", duration)
                .append("timestamp", timestamp)
                .append("redirectCount", redirectCount)
                .append("collection", collection)
                .append("record", record)
                .append("expectedMimeType", expectedMimeType);

        return document;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckedLink that = (CheckedLink) o;
        return status == that.status &&
                duration == that.duration &&
                timestamp == that.timestamp &&
                redirectCount == that.redirectCount &&
                url.equals(that.url) &&
                Objects.equals(method, that.method) &&
                Objects.equals(message, that.message) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(byteSize, that.byteSize) &&
                Objects.equals(collection, that.collection) &&
                Objects.equals(record, that.record) &&
                Objects.equals(expectedMimeType, that.expectedMimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, message, status, contentType, byteSize, duration, timestamp, redirectCount, collection, record, expectedMimeType);
    }

    @Override
    public String toString() {
        return  url +
                ", \"" + method +
                "\", \"" + message +
                "\", " + status +
                ", \"" + contentType +
                "\", " + byteSize +
                ", " + duration +
                ", " + timestamp +
                ", \"" + collection +
                "\", " + redirectCount +
                ", \"" + record +
                "\", \"" + expectedMimeType+"\"" ;
    }
}
