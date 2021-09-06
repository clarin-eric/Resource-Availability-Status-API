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
   private Long urlId;
   private String url;

   private String record;
   private String providerGroup;
   private Timestamp ingestionDate;
   private String expectedMimeType;

   public LinkToBeChecked() {
   }

   public LinkToBeChecked(String url) {
      this.url = url;
   }

   public LinkToBeChecked(Long urlId, String url) {
      this(url);
      this.urlId = urlId;

   }

   public LinkToBeChecked(String url, String record, String providerGroup, String expectedMimeType,
         Timestamp ingestionDate) {
      this(url);
      this.record = record;
      this.providerGroup = providerGroup;
      this.expectedMimeType = expectedMimeType;
      this.ingestionDate = ingestionDate;
   }

   public LinkToBeChecked(Long urlId, String url, String record, String providerGroup, String expectedMimeType,
         Timestamp ingestionDate) {
      this(url, record, providerGroup, expectedMimeType, ingestionDate);
      this.urlId = urlId;

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

   public Timestamp getIngestionDate() {
      return ingestionDate;
   }

   public void setInjectionDate(Timestamp ingestionDate) {
      this.ingestionDate = ingestionDate;
   }

   public Long getUrlId() {
      return urlId;
   }

   public void setLinkId(Long linkId) {
      this.urlId = linkId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      LinkToBeChecked that = (LinkToBeChecked) o;
      return url.equals(that.url);
   }

   @Override
   public int hashCode() {
      return Objects.hash(url);
   }

   @Override
   public String toString() {
      return "LinkToBeChecked{urlId=" + urlId + ", url='" + url + '\'' + ", record='" + record + '\'' + ", providerGroup='"
            + providerGroup + '\'' + ", expectedMimeType='" + expectedMimeType + '\'' + ", injectionDate="
            + ingestionDate + '}';
   }
}
