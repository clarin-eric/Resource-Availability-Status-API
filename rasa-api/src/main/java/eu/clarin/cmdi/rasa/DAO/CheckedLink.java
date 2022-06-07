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

import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

/**
 * Corresponds to a tuple in the status table
 */

public class CheckedLink {
   private Long urlId;
   private Long statusId;
   private String url;
   private String method;
   private Integer status;
   private String contentType;
   private Long byteSize;
   private Integer duration;
   private Timestamp checkingDate;
   private String message;
   private Integer redirectCount;
   private Category category;
   
   // in fact this doesn't represent the n-n relationship between url and context
   private String providerGroup;
   private String record;
   private String expectedMimeType;



   public CheckedLink() {
   }

   private CheckedLink(String method, Integer status, String contentType, Long byteSize, Integer duration,
         Timestamp checkingDate, String message, Integer redirectCount, Category category) {
      
      this();

      this.method = method;
      this.status = status;
      this.contentType = contentType;
      this.byteSize = byteSize;
      this.duration = duration;
      this.checkingDate = checkingDate;
      this.message = message;
      this.redirectCount = redirectCount;
      this.category = category;
   }

   public CheckedLink(String url, String method, Integer status, String contentType, Long byteSize, Integer duration,
         Timestamp checkingDate, String message, Integer redirectCount, Category category) {

      this(method, status, contentType, byteSize, duration, checkingDate, message, redirectCount, category);

      setUrl(url);

   }
   
   public CheckedLink(Long urlId, String method, Integer status, String contentType, Long byteSize, Integer duration,
         Timestamp checkingDate, String message, Integer redirectCount, Category category) {

      this(method, status, contentType, byteSize, duration, checkingDate, message, redirectCount, category);

      this.urlId = urlId;

   }
   
   public CheckedLink(String url, String method, Integer status, String contentType, Long byteSize, Integer duration,
         Timestamp checkingDate, String message, Integer redirectCount, Category category, 
         String providerGroup, String record, String expectedMimeType) {
      
      
      this(url, method, status, contentType, byteSize, duration, checkingDate, message, redirectCount, category);
      
      this.providerGroup = providerGroup;
      this.record = record;
      this.expectedMimeType = expectedMimeType;
   }


   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url.trim();
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

   public Long getByteSize() {
      return byteSize;
   }

   public void setByteSize(Long byteSize) {
      this.byteSize = byteSize;
   }

   public Integer getDuration() {
      return duration;
   }

   public void setDuration(Integer duration) {
      this.duration = duration;
   }

   public Timestamp getCheckingDate() {
      return checkingDate;
   }

   @Deprecated
   public Timestamp getTimestamp() {
      return checkingDate;
   }

   public void setCheckingDate(Timestamp checkingDate) {
      this.checkingDate = checkingDate;
   }

   public Integer getRedirectCount() {
      return redirectCount;
   }

   public void setRedirectCount(Integer redirectCount) {
      this.redirectCount = redirectCount;
   }

   public String getProviderGroup() {     
      return this.providerGroup;      
   }

   public void setProviderGroup(String providerGroup) {
      this.providerGroup = providerGroup;     
   }

   public String getRecord() {     
      return this.record;
   }

   public void setRecord(String record) {
      this.record = record;
   }

   public String getExpectedMimeType() {     
      return this.expectedMimeType;
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

   public Long getUrlId() {
      return urlId;
   }

   public void setUrlId(Long urlId) {
      this.urlId = urlId;
   }

   public Long getStatusId() {
      return statusId;
   }

   public void setStatusId(Long statusId) {
      this.statusId = statusId;
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
            && Objects.equals(duration, that.duration) && Objects.equals(checkingDate, that.checkingDate)
            && Objects.equals(message, that.message) && Objects.equals(providerGroup, that.providerGroup)
            && Objects.equals(record, that.record) && Objects.equals(expectedMimeType, that.expectedMimeType)
            && Objects.equals(redirectCount, that.redirectCount) && Objects.equals(category, that.category);
   }

   @Override
   public int hashCode() {
      return Objects.hash(url, method, status, contentType, byteSize, duration, checkingDate, message,
            redirectCount, category, providerGroup, record, expectedMimeType);
   }

   @Override
   public String toString() {
      return "CheckedLink{urlId=" + urlId + ", statusId=" + statusId + ", url='" + url + "', method='" + method
            + "', status=" + status + ", contentType='" + contentType + "', byteSize=" + byteSize
            + ", duration=" + duration + ", checkingDate=" + checkingDate + ", message='" + message 
            + "', redirectCount=" + redirectCount + ", category='" + category + "', providerGroup='" + providerGroup
            + "', record='" + record + "', expectedMimeType='" + expectedMimeType + "'}";

   }
}
