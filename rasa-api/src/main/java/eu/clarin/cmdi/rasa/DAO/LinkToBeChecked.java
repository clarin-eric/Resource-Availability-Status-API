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
	Long linkId;
    private String url;
    private Timestamp nextFetchDate;
    
    private String record;
    private String providerGroup;
    private Timestamp injectionDate;
    private String expectedMimeType;

    public LinkToBeChecked() {
    }
    
    public LinkToBeChecked(String url, Timestamp nextFetchDate) {
    	this.url = url;
    	this.nextFetchDate = nextFetchDate;
    }
    
    public LinkToBeChecked(Long linkId, String url, Timestamp nextFetchDate) {
    	this(url, nextFetchDate);
    	this.linkId = linkId;
    	
    }
    
    public LinkToBeChecked(String url, Timestamp nextFetchDate, String record, String providerGroup, String expectedMimeType, Timestamp injectionDate) {
    	this(url, nextFetchDate);
        this.record = record;
        this.providerGroup = providerGroup;
        this.expectedMimeType = expectedMimeType;
        this.injectionDate = injectionDate;
    }
    

    
    public LinkToBeChecked(Long linkId, String url, Timestamp nextFetchDate, String record, String providerGroup, String expectedMimeType, Timestamp injectionDate) {
        this(url, nextFetchDate, record, providerGroup, expectedMimeType, injectionDate);
    	this.linkId = linkId;

    }    

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }  

	public Timestamp getNextFetchDate() {
		return nextFetchDate;
	}

	public void setNextFetchDate(Timestamp nextFetchDate) {
		this.nextFetchDate = nextFetchDate;
	}

	public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
    @Deprecated
    public String getCollection() {
        return providerGroup;
    }
    @Deprecated
    public void setCollection(String providerGroup) {
        this.providerGroup = providerGroup;
    }
    
    public String getProviderGroup() {
        return providerGroup;
    }

    public void setProviderGroup(String providerGroup) {
        this.providerGroup = providerGroup;
    }

    public String getExpectedMimeType() {
        return expectedMimeType;
    }

    public void setExpectedMimeType(String expectedMimeType) {
        this.expectedMimeType = expectedMimeType;
    }   
    
    public Timestamp getInjectionDate() {
        return injectionDate;
    }

    public void setInjectionDate(Timestamp injectionDate) {
        this.injectionDate = injectionDate;
    }    

    public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkToBeChecked that = (LinkToBeChecked) o;
        return url.equals(that.url) &&
                Objects.equals(nextFetchDate, that.nextFetchDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, nextFetchDate);
    }

    @Override
    public String toString() {
        return "LinkToBeChecked{" +
                "url='" + url + '\'' +
                ", nextFetchDate='" + nextFetchDate + '\'' +
                ", record='" + record + '\'' +
                ", providerGroup='" + providerGroup + '\'' +
                ", expectedMimeType='" + expectedMimeType + '\'' +
                ", injectionDate=" + injectionDate + 
                '}';
    }
}
