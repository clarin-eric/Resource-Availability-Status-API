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
package eu.clarin.cmdi.rasa.DAO;

import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.jooq.Record;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Corresponds to a tuple in the status table
 */
public class CheckedLink {

    private String url;
    private String method;
    private Integer status;
    private String contentType;
    private Integer byteSize;
    private Integer duration;
    private Timestamp timestamp;
    private String message;
    private String collection;
    private Integer redirectCount;
    private String record;
    private String expectedMimeType;
    public Category category;

    public CheckedLink() {
    }

    public CheckedLink(String url, String method, Integer status,
                       String contentType, Integer byteSize, Integer duration,
                       Timestamp timestamp, String message, String collection, Integer redirectCount,
                       String record, String expectedMimeType, Category category) {
        this.url = url;
        this.method = method;
        this.status = status;
        this.contentType = contentType;
        this.byteSize = byteSize;
        this.duration = duration;
        this.timestamp = timestamp;
        this.message = message;
        this.collection = collection;
        this.redirectCount = redirectCount;
        this.record = record;
        this.expectedMimeType = expectedMimeType;
        this.category=category;
    }

    public CheckedLink(Record record) {
        this.url = (String) record.getValue("url");
        this.status = (Integer) record.getValue("statusCode");
        this.method = (String) record.getValue("method");
        this.contentType = (String) record.getValue("contentType");
        this.byteSize = (Integer) record.getValue("byteSize");
        this.duration = (Integer) record.getValue("duration");
        this.timestamp = (Timestamp) record.getValue("timestamp");
        this.message = (String) record.getValue("message");
        this.redirectCount = (Integer) record.getValue("redirectCount");
        this.record = (String) record.getValue("record");
        this.collection = (String) record.getValue("collection");
        this.expectedMimeType = (String) record.getValue("expectedMimeType");
        this.category = Category.valueOf((String) record.getValue("category"));
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getByteSize() {
        return byteSize;
    }

    public void setByteSize(Integer byteSize) {
        this.byteSize = byteSize;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(Integer redirectCount) {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckedLink that = (CheckedLink) o;
        return url.equals(that.url) &&
                Objects.equals(method, that.method) &&
                Objects.equals(status, that.status) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(byteSize, that.byteSize) &&
                Objects.equals(duration, that.duration) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(message, that.message) &&
                Objects.equals(collection, that.collection) &&
                Objects.equals(redirectCount, that.redirectCount) &&
                Objects.equals(record, that.record) &&
                Objects.equals(expectedMimeType, that.expectedMimeType) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, status, contentType, byteSize, duration, timestamp, message, collection, redirectCount, record, expectedMimeType, category);
    }

    @Override
    public String toString() {
        return "CheckedLink{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", status=" + status +
                ", contentType='" + contentType + '\'' +
                ", byteSize=" + byteSize +
                ", duration=" + duration +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", collection='" + collection + '\'' +
                ", redirectCount=" + redirectCount +
                ", record='" + record + '\'' +
                ", expectedMimeType='" + expectedMimeType + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
