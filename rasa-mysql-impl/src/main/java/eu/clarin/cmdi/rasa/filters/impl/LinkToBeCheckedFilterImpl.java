package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;

public class LinkToBeCheckedFilterImpl extends AbstractFilter implements LinkToBeCheckedFilter {

   public LinkToBeCheckedFilterImpl() {
      super.from = "url u";
   }

   @Override
   public LinkToBeCheckedFilter setUrlIs(String url) {
      super.setUrlIs(url);
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setUrlIn(String... urls) {
      super.setUrlIn(urls);
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setProviderGroupIs(String providerGroup) {
      super.setProviderGroupIs(providerGroup);
      
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setRecordIs(String record) {
      super.setRecordIs(record);
      
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setIngestionDateIs(Timestamp ingestionDate) {
      super.setIngestionDateIs(ingestionDate);

      return this;
   }

   @Override
   public LinkToBeCheckedFilter setLimit(int offset, int limit) {
      this.limit = offset + ", " + limit;
      
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setIsActive(boolean active) {
      super.setIsActive(active);

      return this;
   }

   @Override
   public LinkToBeCheckedFilter setOrderByCheckingDate(boolean isAscending) {
      this.join.add("LEFT JOIN status s ON u.id=s.url_id");
      this.orderBy = "s.checkingDate" + (isAscending?"":" DESC"); 
      
      return this;
   }
}
