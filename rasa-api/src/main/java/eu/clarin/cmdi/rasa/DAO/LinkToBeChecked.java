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

import java.sql.Timestamp;
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
    private Timestamp harvestTime;
    private String expectedMimeType;

    public LinkToBeChecked() {
    }

    @Deprecated
    public LinkToBeChecked(String url, String record, String collection, String expectedMimeType, Long harvestTime) {
        this(url, record, collection, expectedMimeType, harvestTime==null?null:new Timestamp(harvestTime));
    }
    
    public LinkToBeChecked(String url, String record, String collection, String expectedMimeType, Timestamp harvestTime) {
        this.url = url;
        this.record = record;
        this.collection = collection;
        this.expectedMimeType = expectedMimeType;
        this.harvestTime = harvestTime;
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
    @Deprecated
    public Long getHarvestDate() {
        return harvestTime.getTime();
    }
    
    @Deprecated
    public void setHarvestDate(Long harvestDate) {
        this.harvestTime = harvestDate==null?null:new Timestamp(harvestDate);
    }
    
    public void setHarvestTime(Timestamp harvestTime) {
        this.harvestTime = harvestTime;
    }
    
    public Timestamp getHarvestTime() {
        return harvestTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkToBeChecked that = (LinkToBeChecked) o;
        return url.equals(that.url) &&
                Objects.equals(record, that.record) &&
                Objects.equals(collection, that.collection) &&
                Objects.equals(harvestTime, that.harvestTime) &&
                Objects.equals(expectedMimeType, that.expectedMimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, record, collection, harvestTime, expectedMimeType);
    }

    @Override
    public String toString() {
        return "LinkToBeChecked{" +
                "url='" + url + '\'' +
                ", record='" + record + '\'' +
                ", collection='" + collection + '\'' +
                ", harvestDate=" + harvestTime +
                ", expectedMimeType='" + expectedMimeType + '\'' +
                '}';
    }
}
