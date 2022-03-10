package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFilter {

	protected String from;
	protected Set<String> join = new LinkedHashSet<String>();
	protected List<Tuple> where = new ArrayList<Tuple>();
	protected String groupBy;
	protected String orderBy;
	protected String limit;
	

   protected Object setUrlIs(String url) {
      this.where.add(new Tuple("u.url = ?", java.sql.Types.CHAR, url));
      
      return this;
   }

   protected Object setUrlIn(String... urls) {      
      this.where.add(new Tuple(
            Arrays.stream(urls).map(u -> "?").collect(Collectors.joining(",", "u.url IN(", ")")), 
            java.sql.Types.CHAR, 
            (Object[]) urls));
      
      return this;

   }  
   
   public Object setIngestionDateIs(Timestamp ingestionDate) {     
      this.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      this.where.add(new Tuple("uc.ingestionDate = ?", java.sql.Types.TIMESTAMP, ingestionDate));
      
      return this; 
   }
   
   public Object setIsActive(boolean active) {    
      this.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      this.where.add(new Tuple("uc.active = ?", java.sql.Types.BOOLEAN, active));
      
      return this;
   }

   protected Object setProviderGroupIs(String providerGroup) {     
      if(!providerGroup.equals("Overall")) {
         this.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
         this.join.add("INNER JOIN context c ON uc.context_id=c.id");
         this.join.add("INNER JOIN providerGroup p ON c.providerGroup_id=p.id");
         this.where.add(new Tuple("p.name = ?", java.sql.Types.CHAR, providerGroup));
      }
      
      return this;
   }
   
   protected Object setSourceIs(String source) {      
      this.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      this.join.add("INNER JOIN context c ON uc.context_id=c.id");
      this.where.add(new Tuple("c.source = ?", java.sql.Types.CHAR, source));

      return this;
   }

   protected Object setRecordIs(String record) {      
      this.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      this.join.add("INNER JOIN context c ON uc.context_id=c.id");
      this.where.add(new Tuple("c.record = ?", java.sql.Types.CHAR, record));

      return this;
   }

   public Object setLimit(int offset, int limit) {      
      this.limit = offset + ", " + limit;
      
      return this;
   }

   public PreparedStatement getPreparedStatement(Connection con, String select) throws SQLException {
	   final PreparedStatement stmt = con.prepareStatement(select + " FROM " + from
            + (join.size() == 0?"": join.stream().collect(Collectors.joining(" ", " ", "")))
            + (where.size() == 0? "":" WHERE " + where.stream().map(tuple -> tuple.sqlString).collect(Collectors.joining(" AND "))) 
            + (groupBy == null?"":" GROUP BY " + groupBy)
            + (orderBy == null?"":" ORDER BY " + orderBy)
            + (limit == null?"": " LIMIT " + limit));
	   
	   final AtomicInteger valueNr = new AtomicInteger(0);
	   
	   where.stream().forEach(tuple -> {
	      Arrays.stream(tuple.values).forEach(value -> {
            try {
               stmt.setObject(valueNr.incrementAndGet(), value, tuple.sqlType);
            } catch (SQLException e) {
               log.error("can't set value number {} of SQL type {} with value {}", valueNr.get(), tuple.sqlType, value);
            }
         });
	   });
	   
	   log.debug("prepared statement:\n{}", stmt.toString());
	   
	   return stmt;
	};
	
	protected class Tuple {
	   private String sqlString;
	   private int sqlType;
	   private Object[] values;
	   
	   public Tuple(String sqlString, int sqlType, Object... values) {
	      this.sqlString = sqlString;
	      this.sqlType = sqlType;
	      this.values = values;
	   }	   
	}
}
