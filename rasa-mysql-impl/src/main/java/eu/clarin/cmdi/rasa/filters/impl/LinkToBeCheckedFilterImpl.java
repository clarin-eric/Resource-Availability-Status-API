package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;

public class LinkToBeCheckedFilterImpl extends AbstractFilter implements LinkToBeCheckedFilter {

   public LinkToBeCheckedFilterImpl() {
      super.from = "url u";
   }

   @Override
   public LinkToBeCheckedFilter setUrlIs(String url) {
      super.where.put("u.url", "u.url = '" + url + "'");
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setUrlIn(String... urls) {
      super.where.put("u.url", "u.url IN " + Arrays.stream(urls).collect(Collectors.joining("', '", "('", "')")));
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setProviderGroupIs(String providerGroup) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      super.join.add("INNER JOIN context c ON uc.context_id=c.id");
      super.join.add("INNER JOIN providerGroup p ON c.providerGroup_id=p.id");
      super.where.put("p.name", "p.name = '" + providerGroup + "'");
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setRecordIs(String record) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      super.join.add("INNER JOIN context c ON uc.context_id=c.id");
      super.where.put("c.record", "c.record = '" + record + "'");

      return this;
   }

   @Override
   public LinkToBeCheckedFilter setIngestionDateIs(Timestamp ingestionDate) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      super.where.put("uc.ingestionDate", "uc.ingestionDate = '" + ingestionDate + "'");

      return this;
   }

   @Override
   public LinkToBeCheckedFilter setLimit(int offset, int limit) {
      this.limit = offset + ", " + limit;
      return this;
   }

   @Override
   public LinkToBeCheckedFilter setIsActive(boolean active) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      super.where.put("uc.active", "uc.active = " + active);

      return this;
   }

   @Override
   public LinkToBeCheckedFilter setDoOrder(boolean doOrder) {
      if (doOrder) {
         this.join.add("LEFT JOIN status s ON u.id=s.url_id");
         this.orderBy = "s.lastCheckDate";
      } else {
         this.join.remove("LEFT JOIN status s ON u.id=s.url_id");
         this.orderBy = null;
      }
      return this;
   }
}
