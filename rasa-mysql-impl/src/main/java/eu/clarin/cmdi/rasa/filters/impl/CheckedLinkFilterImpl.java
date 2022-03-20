package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

public class CheckedLinkFilterImpl extends AbstractFilter implements CheckedLinkFilter {

   public CheckedLinkFilterImpl() {
      super.from = "status s";
      super.join.add("INNER JOIN url u ON s.url_id=u.id");
   }

   @Override
   public CheckedLinkFilter setUrlIs(String url) {
      
      super.where.add(new Tuple("u.url = ?", java.sql.Types.VARCHAR, url==null?null:url.trim()));
      return this;
   }

   @Override
   public CheckedLinkFilter setUrlIn(String... urls) {
      
      super.where.add(new Tuple(Arrays.stream(urls).map(u -> "?").collect(Collectors.joining(",", "u.url IN(", ")")),
            java.sql.Types.VARCHAR, Arrays.asList(urls).stream().map(url -> url==null?null:url.trim()).toArray(Object[]::new)));
      return this;
   }

   @Override
   public CheckedLinkFilter setStatusIs(Integer status) {
      super.where.add(new Tuple("s.statusCode = ?", java.sql.Types.INTEGER, status));

      return this;
   }

   @Override
   public CheckedLinkFilter setStatusBetween(Integer statusFrom, Integer statusTo) {
      super.where
            .add(new Tuple("s.statusCode >= ? AND s.statusCode <= ?", java.sql.Types.INTEGER, statusFrom, statusTo));

      return this;
   }

   @Override
   public CheckedLinkFilter setCheckedBetween(Timestamp checkedAfter, Timestamp checkedBefore) {
      super.where
            .add(new Tuple("s.checkingDate BETWEEN ? AND ?", java.sql.Types.TIMESTAMP, checkedAfter, checkedBefore));

      return this;
   }

   @Override
   public CheckedLinkFilter setProviderGroupIs(String providerGroup) {
      super.setProviderGroupIs(providerGroup);

      return this;
   }
   
   @Override
   public CheckedLinkFilter setSourceIs(String source) {
      super.setRecordIs(source);

      return this;
   }

   @Override
   public CheckedLinkFilter setRecordIs(String record) {
      super.setRecordIs(record);

      return this;
   }

   @Override
   public CheckedLinkFilter setIngestionDateIs(Timestamp ingestionDate) {
      super.setIngestionDateIs(ingestionDate);

      return this;
   }

   @Override
   public CheckedLinkFilter setLimit(int offset, int limit) {
      this.limit = offset + ", " + limit;

      return this;
   }

   @Override
   public CheckedLinkFilter setIsActive(boolean active) {
      super.setIsActive(active);

      return this;
   }

   @Override
   public CheckedLinkFilter setCategoryIs(Category category) {
      this.where.add(new Tuple("s.category = ?", java.sql.Types.VARCHAR, category.name()));

      return this;
   }

   @Override
   public CheckedLinkFilter setCategoryIn(Category... categories) {
      this.where.add(
            new Tuple(Arrays.stream(categories).map(c -> "?").collect(Collectors.joining(",", "category IN (", ")")),
                  java.sql.Types.VARCHAR,
                  Arrays.stream(categories).map(Category::name).collect(Collectors.toList()).toArray()));

      return this;
   }

   @Override
   public CheckedLinkFilter setOrderByCheckingDate(boolean isAscending) {
      this.orderBy = "s.checkingDate" + (isAscending?"":" DESC"); 
      
      return this;
   }

   @Override
   public CheckedLinkFilter setGroupByCategory() {
      this.groupBy = "s.category";
      
      return this;
   }

   @Override
   public CheckedLinkFilter setOrderByCategory(boolean isAscending) {
      this.orderBy = "s.category" + (isAscending?"":" DESC"); 
      return this;
   }
}
