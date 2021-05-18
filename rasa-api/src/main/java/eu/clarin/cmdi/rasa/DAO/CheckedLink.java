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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

/**
 * Corresponds to a tuple in the status table
 */

public class CheckedLink{

	private String url;
	private String method;
	private Integer status;
	private String contentType;
	private Integer byteSize;
	private Integer duration;
	private Timestamp checkingTime;
	private String message;
	private Integer redirectCount;
	private Category category;

	private List<Context> contexts;

	public CheckedLink() {
		this.contexts = new ArrayList<Context>(); 
	}

	@Deprecated
	public CheckedLink(String url, String method, Integer status, String contentType, Integer byteSize,
			Integer duration, Timestamp timestamp, String message, String collection, Integer redirectCount,
			String record, String expectedMimeType, Category category) {
		this();

		this.url = url;
		this.method = method;
		this.status = status;
		this.contentType = contentType;
		this.byteSize = byteSize;
		this.duration = duration;
		this.checkingTime= timestamp;
		this.message = message;
		setCollection(collection);
		this.redirectCount = redirectCount;
		this.setRecord(record);
		this.setExpectedMimeType(expectedMimeType);
		this.category = category;
	}

	public CheckedLink(String url, String method, Integer status, String contentType, Integer byteSize,
			Integer duration, Timestamp checkingTime, String message, Integer redirectCount,
			Category category) {
		this();

		this.url = url;
		this.method = method;
		this.status = status;
		this.contentType = contentType;
		this.byteSize = byteSize;
		this.duration = duration;
		this.checkingTime = checkingTime;
		this.message = message;
		this.redirectCount = redirectCount;
		this.category = category;
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
	@Deprecated
	public Timestamp getTimestamp() {
		return checkingTime;
	}
	@Deprecated
	public void setTimestamp(Timestamp timestamp) {
		this.checkingTime = timestamp;
	}

	public Integer getRedirectCount() {
		return redirectCount;
	}

	public void setRedirectCount(Integer redirectCount) {
		this.redirectCount = redirectCount;
	}

	@Deprecated
	public String getCollection() {
		return this.contexts.size() > 0? this.contexts.get(0).getCollection():null;
	}
	@Deprecated
	public void setCollection(String collection) {
		if(this.contexts.size() == 0) 
			this.contexts.add(new Context());
		
		this.contexts.get(0).setCollection(collection);
	}
	@Deprecated
	public String getRecord() {
		return this.contexts.size() > 0?this.getContexts().get(0).getRecord():null;
	}
	@Deprecated
	public void setRecord(String record) {
		if(this.contexts.size() == 0) 
			this.contexts.add(new Context());
		
		this.contexts.get(0).setRecord(record);
	}
	@Deprecated
	public String getExpectedMimeType() {
		return this.contexts.size() > 0?this.getContexts().get(0).getExpectedMimeType():null;
	}
	@Deprecated
	public void setExpectedMimeType(String expectedMimeType) {
		if(this.contexts.size() == 0) 
		this.contexts.add(new Context());
	
	this.contexts.get(0).setExpectedMimeType(expectedMimeType);
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
	
	public void addContext(String record, String collection, String expectedMimeType, Timestamp harvestDate) {
		this.contexts.add(new Context(record, collection, expectedMimeType, harvestDate));
	}
	
	public List<Context> getContexts(){
		return this.contexts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CheckedLink that = (CheckedLink) o;
		return url.equals(that.url) && Objects.equals(method, that.method) && Objects.equals(status, that.status)
				&& Objects.equals(contentType, that.contentType) && Objects.equals(byteSize, that.byteSize)
				&& Objects.equals(duration, that.duration) && Objects.equals(checkingTime, that.checkingTime)
				&& Objects.equals(message, that.message) && Objects.equals(contexts, that.contexts)
				&& Objects.equals(redirectCount, that.redirectCount) && Objects.equals(category, that.category);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, method, status, contentType, byteSize, duration, checkingTime, message, contexts,
				redirectCount, category);
	}

	@Override
	public String toString() {
		

		
		this.contexts.stream().map(context -> {
			return "{collection=\'" + context.getCollection() + "\', record='" + context.getRecord() + "\', expectedMimeType='"
			+ context.getExpectedMimeType() + "\'}";
		}).collect(Collectors.joining(", "));
		
		return "CheckedLink{" + "url='" + url + '\'' + ", method='" + method + '\'' + ", status=" + status
				+ ", contentType='" + contentType + '\'' + ", byteSize=" + byteSize + ", duration=" + duration
				+ ", timestamp=" + checkingTime + ", message='" + message + '\'' 
				+ ", redirectCount=" + redirectCount + ", category='" + category + '\'' + '}';
		
		

	}

	public class Context {



		private String record;
		private String collection;
		private String expectedMimeType;
		private Timestamp harvestDate;
		
		public Context() {
			
		}
		
		public Context(String record, String collection, String expectedMimeType, Timestamp harvestDate) {

			this.record = record;
			this.collection = collection;
			this.expectedMimeType = expectedMimeType;
			this.harvestDate = harvestDate;
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

		public Timestamp getHarvestDate() {
			return harvestDate;
		}

		public void setHarvestDate(Timestamp harvestDate) {
			this.harvestDate = harvestDate;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Context that = (Context) obj;
			return Objects.equals(this.collection, that.collection) 
					&& Objects.equals(this.record,that.record)
					&& Objects.equals(this.expectedMimeType,that.expectedMimeType)
					&& Objects.equals(this.harvestDate,that.harvestDate);
		}
		
		
	}
}
