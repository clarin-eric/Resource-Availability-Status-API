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

import org.jooq.Record;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Corresponds to a tuple in the urls table
 */
public class LinkToBeChecked {
    private String url;
    private String record;
    private String collection;
    //there is a known bug for dates: https://bugs.mysql.com/bug.php?id=93444
    //so this field became a Long for system.currenttimemillis
    private Long harvestDate;
    private String expectedMimeType;

    public LinkToBeChecked() {
    }

    public LinkToBeChecked(String url, String record, String collection, String expectedMimeType, Long harvestDate) {
        this.url = url;
        this.record = record;
        this.collection = collection;
        this.expectedMimeType = expectedMimeType;
        this.harvestDate = harvestDate;
    }

//    /**
//     * Create linkToBeChecked from the necessary info in a given CheckedLink
//     *
//     * @param checkedLink CheckedLink to be copied
//     */
//    public LinkToBeChecked(CheckedLink checkedLink) {
//        this.url = checkedLink.getUrl();
//        this.record = checkedLink.getRecord();
//        this.collection = checkedLink.getCollection();
//        this.expectedMimeType = checkedLink.getExpectedMimeType();
//    }

    public LinkToBeChecked(Record record) {
        this.url = (String) record.getValue("url");
        this.record = (String) record.getValue("record");
        this.collection = (String) record.getValue("collection");
        this.expectedMimeType = (String) record.getValue("expectedMimeType");
        this.harvestDate = record.getValue("harvestDate",Long.class);
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

    public Long getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(Long harvestDate) {
        this.harvestDate = harvestDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkToBeChecked that = (LinkToBeChecked) o;
        return url.equals(that.url) &&
                Objects.equals(record, that.record) &&
                Objects.equals(collection, that.collection) &&
                Objects.equals(harvestDate, that.harvestDate) &&
                Objects.equals(expectedMimeType, that.expectedMimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, record, collection, harvestDate, expectedMimeType);
    }

    @Override
    public String toString() {
        return "LinkToBeChecked{" +
                "url='" + url + '\'' +
                ", record='" + record + '\'' +
                ", collection='" + collection + '\'' +
                ", harvestDate=" + harvestDate +
                ", expectedMimeType='" + expectedMimeType + '\'' +
                '}';
    }
}
