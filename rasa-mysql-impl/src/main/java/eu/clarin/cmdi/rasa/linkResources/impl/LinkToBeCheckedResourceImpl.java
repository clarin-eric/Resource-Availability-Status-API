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
package eu.clarin.cmdi.rasa.linkResources.impl;

import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.filters.impl.AbstractFilter;
import eu.clarin.cmdi.rasa.filters.impl.LinkToBeCheckedFilterImpl;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import lombok.extern.slf4j.Slf4j;

import org.jooq.impl.DSL;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class LinkToBeCheckedResourceImpl implements LinkToBeCheckedResource {
   
   private final static List<String> VALID_PROTOCOLS = Arrays.asList("http", "https", "ftp");

   private final ConnectionProvider connectionProvider;

   private Map<String, Long> providerGroupIdMap; // since the deactivation depends on this we need a map here


   public LinkToBeCheckedResourceImpl(ConnectionProvider connectionProvider) {
      this.connectionProvider = connectionProvider;

      this.providerGroupIdMap = new HashMap<String, Long>();

   }

   @Override
   public Stream<LinkToBeChecked> get(LinkToBeCheckedFilter filter) throws SQLException {
      AbstractFilter aFilter = AbstractFilter.class.cast(filter);
      
      final Connection con = connectionProvider.getConnection();
      
      try {
         final PreparedStatement stmt = aFilter.getPreparedStatement(con, "SELECT DISTINCT u.*");
         final ResultSet rs = stmt.executeQuery();
         
         return DSL.using(con).fetchStream(rs)
               .onClose(() -> {
                  try {
                     rs.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close resultset.");
                  }
                  try {
                     stmt.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close prepared statement.");
                  }
                  try {
                     con.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close connection.");
                  }
               })
               .map(rec -> new LinkToBeChecked(rec.get("id", Long.class), rec.get("url", String.class)));
         
      }
      catch(SQLException ex) {
         try {
            con.close();
         } 
         catch (SQLException e) {
            log.error("Can't close connection.");
         }
      }
      return Stream.empty();
   }

   @Override
   public Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException {
      
      Long providerGroupId = this.providerGroupIdMap.containsKey(linkToBeChecked.getProviderGroup())?
            this.providerGroupIdMap.get(linkToBeChecked.getProviderGroup()):
            getProviderGroupId(linkToBeChecked);
            
      try (Connection con = this.connectionProvider.getConnection()) {
         con.setAutoCommit(false);
         long urlId = getUrlId(con, linkToBeChecked);
         long contextId = getContextId(con, linkToBeChecked, providerGroupId);

         saveUrlContext(con, urlId, contextId, linkToBeChecked.getIngestionDate());

         con.commit();
         con.setAutoCommit(true);
      }

      return true;
   }

   @Override
   public Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException {
      Map.Entry<String, Long> lastContextId  = new AbstractMap.SimpleEntry<String, Long>("", null);
      
      try (Connection con = this.connectionProvider.getConnection()) {
         con.setAutoCommit(false);
         for(LinkToBeChecked linkToBeChecked:linksToBeChecked) {
            long urlId = getUrlId(con, linkToBeChecked);
   
            Long providerGroupId = this.providerGroupIdMap.containsKey(linkToBeChecked.getProviderGroup())?
                  this.providerGroupIdMap.get(linkToBeChecked.getProviderGroup()):
                  getProviderGroupId(linkToBeChecked);
            
            String key = linkToBeChecked.getSource() + "-" + linkToBeChecked.getRecord() + "-" + providerGroupId + "-" + linkToBeChecked.getExpectedMimeType();

            if(!lastContextId.getKey().equals(key)) {
               lastContextId =  new AbstractMap.SimpleEntry<String, Long>(key, getContextId(con, linkToBeChecked, providerGroupId));
            }
   
            saveUrlContext(con, urlId, lastContextId.getValue(), linkToBeChecked.getIngestionDate());
            con.commit();
         }   
         con.setAutoCommit(true);
      }
      
      return true;
   }

   @Override
   public Boolean delete(String url) throws SQLException {
      log.error("method \"delete(String url)\" not implemented");
      return false;
   }

   @Override
   public Boolean delete(List<String> urls) throws SQLException {
      log.error("method \"delete(List<String> urls)\" not implemented");
      return false;
   }

   @Override
   public List<String> getProviderGroupNames() throws SQLException {
      try (Connection con = connectionProvider.getConnection()) {
         List<String> collectionNames = new ArrayList<>();

         String query = "SELECT name from providerGroup";
         try (Statement stmt = con.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery(query)) {

               while (rs.next()) {
                  collectionNames.add(rs.getString("name"));
               }

               return collectionNames;
            }
         }
      }
   }

   @Override
   public int deleteOldLinks(Long date) throws SQLException {
      log.error("method \"deleteOldLinks(Long date)\" not implemented");
      return -1;
   }

   @Override
   public int deleteOldLinks(Long date, String collection) throws SQLException {
      log.error("method \"deleteOldLinks(Long date, String collection)\" not implemented");
      return -1;
   }

   @Override
   public int getCount(LinkToBeCheckedFilter filter) throws SQLException {
      AbstractFilter aFilter = AbstractFilter.class.cast(filter);
      
      try (Connection con = this.connectionProvider.getConnection()) {
         try (PreparedStatement stmt = aFilter.getPreparedStatement(con, "SELECT count(DISTINCT u.id) AS count")) {
            try (ResultSet rs = stmt.executeQuery()) {
               if (rs.next())
                  return rs.getInt("count");
            }
         }
      }
      return 0;
   }

   @Override
   public LinkToBeCheckedFilter getLinkToBeCheckedFilter() {

      return new LinkToBeCheckedFilterImpl();
   }

   private long getUrlId(Connection con, LinkToBeChecked linkToBeChecked) throws SQLException {
      
      String urlString = linkToBeChecked.getUrl().trim();
      
      for(int i=0;;i++) {
         try (PreparedStatement statement = con.prepareStatement("SELECT id from url where url=?")) {
            statement.setString(1, urlString);
   
            try (ResultSet rs = statement.executeQuery()) {
               if (rs.next()) {
                  
                  return rs.getLong("id");
               
               }
            }
         }
         
         URL url;
         
         String host = null;
         boolean isValid = false;
         String message = null;
         
         try{
            
            url = new URL(urlString); 
            
            host = url.getHost();
            
            if(host == null || host.length() < 3 || host.contains("localhost") || host.contains("127.0.0")) {
               throw new MalformedURLException("invalid host"); 
            }
            
            if(!VALID_PROTOCOLS.contains(url.getProtocol())){
               throw new MalformedURLException("invalid protocol"); 
            }
            
            isValid = true;
         }
         catch(MalformedURLException e) {
            
            message = e.getMessage();
            log.error("malformed url '{}'", urlString); 
         
         }
         
         long urlId = -1;
            
         try (PreparedStatement statement = con.prepareStatement("INSERT INTO url(url, groupKey, valid) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, urlString);
            statement.setString(2, host);
            statement.setBoolean(3,  isValid);
            
            statement.execute();
            
            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()) {
            
               urlId = rs.getLong(1);
            }
         }
         catch(SQLException ex2) {
            if(i==1) {
               throw ex2;
            }
         }

         if(!isValid) {   // in case of MalformedURLException we can directly add a record to status table
            
            try(PreparedStatement statement = con.prepareStatement("INSERT INTO status(url_id, message, category, checkingDate) VALUES(?,?,?,?)")){
               statement.setLong(1, urlId);
               statement.setString(2, message);
               statement.setString(3, Category.Invalid_URL.name());
               statement.setTimestamp(4, linkToBeChecked.getIngestionDate());
               
               statement.execute();
            }
            
            return urlId;
         }
      }
   }

   private synchronized Long getProviderGroupId(LinkToBeChecked linkToBeChecked) throws SQLException {
      if (linkToBeChecked.getProviderGroup() == null)
         return null;
      if (!this.providerGroupIdMap.containsKey(linkToBeChecked.getProviderGroup())) {
         
         Long providerGroupId = null;
         
         try(Connection con = this.connectionProvider.getConnection()){
            try (PreparedStatement statement = con.prepareStatement("SELECT id FROM providerGroup where name=?")) {
               statement.setString(1, linkToBeChecked.getProviderGroup());
   
               try (ResultSet rs = statement.executeQuery()) {
                  if (rs.next()) {
                     providerGroupId = rs.getLong("id");
                     
                  }              
               }
            }

            if (providerGroupId == null) {// insert new providerGroup
   
               try (PreparedStatement statement = con.prepareStatement("INSERT INTO providerGroup(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                  statement.setString(1, linkToBeChecked.getProviderGroup());
   
                  statement.execute();
                  
                  ResultSet rs = statement.getGeneratedKeys();
                  rs.next();
                  
                  this.providerGroupIdMap.put(linkToBeChecked.getProviderGroup(), rs.getLong(1));
               }
            } 
            else {// deactivate all links of the provider group

               try (PreparedStatement statement = con
                     .prepareStatement("UPDATE url_context uc, context c SET uc.active = false"
                           + " WHERE c.providerGroup_id = ?" + " AND uc.context_id = c.id")) {
   
                  statement.setLong(1, providerGroupId);
   
                  statement.execute();
                  
                  this.providerGroupIdMap.put(linkToBeChecked.getProviderGroup(), providerGroupId);
               }
            }
         }
      }

      return this.providerGroupIdMap.get(linkToBeChecked.getProviderGroup());
   }

   private long getContextId(Connection con, LinkToBeChecked linkToBeChecked, Long providerGroupId)
         throws SQLException {

      for(int i=0;;i++) {
         String query = "SELECT id FROM context WHERE record "               
               + (linkToBeChecked.getRecord() == null ? "IS NULL" : "= '" + linkToBeChecked.getRecord() + "'")
               + " AND providerGroup_id " + (providerGroupId == null ? "IS NULL" : "= " + providerGroupId)
               + " AND expectedMimeType " + (linkToBeChecked.getExpectedMimeType() == null ? "IS NULL"
                     : "= '" + linkToBeChecked.getExpectedMimeType() + "'")
               + " AND source" + (linkToBeChecked.getSource() == null ? "IS NULL" : "= '" + linkToBeChecked.getSource() + "'");
   
         try (Statement stmt = con.createStatement()) {
   
            try (ResultSet rs = stmt.executeQuery(query)) {
               if (rs.next()) {
                  
                  return rs.getLong("id");
               
               }
            }
         }   
   
         try (PreparedStatement stmt = con
               .prepareStatement("INSERT INTO context(source, record, providerGroup_id, expectedMimeType) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, linkToBeChecked.getSource());
            stmt.setString(2, linkToBeChecked.getRecord());
            stmt.setObject(3, providerGroupId, Types.BIGINT);
            stmt.setString(4, linkToBeChecked.getExpectedMimeType());
   
            stmt.execute();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) {;
            
               return rs.getLong(1);
            
            }
         }
         catch(SQLException ex) { //the insert is failing when another thread has inserted the record already
            if(i==1) {
               throw ex;
            }
         }
      }
   }

   private void saveUrlContext(Connection con, long urlId, long contextId, Timestamp ingestionDate) throws SQLException {


      try (PreparedStatement statement = con.prepareStatement(
            "INSERT INTO url_context(url_id, context_id, ingestionDate, active) VALUES (?,?,?, true)"
            + " ON DUPLICATE KEY UPDATE ingestionDate =?, active = true")) {
         statement.setLong(1, urlId);
         statement.setLong(2, contextId);
         statement.setTimestamp(3, ingestionDate);
         statement.setTimestamp(4, ingestionDate);
         
         statement.execute();
      }
   }

   @Override
   public Stream<LinkToBeChecked> getNextLinksToCheck() throws SQLException {
      final Connection con = connectionProvider.getConnection();
      
      try {
         final PreparedStatement stmt = con.prepareStatement(
               "SELECT tab1.* FROM" 
                  + " (SELECT ROW_NUMBER() OVER (PARTITION BY host ORDER BY s.checkingDate) AS orderNr, u.id, u.url, u.host, s.checkingDate" 
                  + " FROM url u"
                  + " LEFT JOIN status s ON s.url_id = u.id"
                  + " INNER JOIN url_context uc ON u.id = uc.url_id"
                  + " WHERE u.valid=true"
                  + " AND uc.active = true"
                  + " ORDER BY u.host, s.checkingDate) tab1"
               + " WHERE orderNr <=200"
               + " ORDER by tab1.checkingDate"
            );
         
         final ResultSet rs = stmt.executeQuery();
         
         return DSL.using(con).fetchStream(rs)
               .onClose(() -> {
                  try {
                     rs.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close resultset.");
                  }
                  try {
                     stmt.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close prepared statement.");
                  }
                  try {
                     con.close();
                  } 
                  catch (SQLException e) {
                     log.error("Can't close connection.");
                  }
               })
               .map(rec -> new LinkToBeChecked(rec.get("id", Long.class), rec.get("url", String.class)));
      }
      catch(SQLException ex) {
         try {
            con.close();
         } 
         catch (SQLException e) {
            log.error("Can't close connection.");
         }
      }
      return Stream.empty();
   }

   @Override
   public Boolean updateURLs() throws SQLException {
      try(
            Connection readCon = this.connectionProvider.getConnection(); 
            Connection writeCon = this.connectionProvider.getConnection()
         ) {
         try(
               Statement readStmt = readCon.createStatement(); 
               PreparedStatement writeURLStmt = writeCon.prepareStatement("UPDATE url SET url=?, groupKey=?, valid=? where id=?");
               PreparedStatement writeStatusStmt = writeCon.prepareStatement(
                     "INSERT INTO status(url_id, category, message, checkingDate) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE category=?, message=?, checkingDate=?"
                     )
            ){
            try(ResultSet rs = readStmt.executeQuery("select * from url where valid is null")){
               
               int index = 0;
               String urlString = null;
               
               URL url;
               
               String host;
               boolean isValid;
               String message;
                
               while(rs.next()) {
                  
                  urlString = rs.getString("url").trim();
                  
                  host = null;
                  isValid = false;
                  message = null;
                     
                  try { 
                  
                     url = new URL(urlString);
                    
                     host = url.getHost();
                     
                     if(host == null || host.length() < 3 || host.contains("localhost") || host.contains("127.0.0")) {
                        throw new MalformedURLException("invalid host"); 
                     }
                     
                     
                     if(!VALID_PROTOCOLS.contains(url.getProtocol())){
                        throw new MalformedURLException("invalid protocol"); 
                     }
                  }
                  catch(MalformedURLException e) {
                     
                     message = e.getMessage();
                     log.error("malformed url '{}'", rs.getString("url")); 
                  
                  }
                  
                  writeURLStmt.setString(1, urlString); 
                  writeURLStmt.setString(2, host);
                  writeURLStmt.setBoolean(3, isValid);
                  writeURLStmt.setLong(4, rs.getLong("id"));
  
                  writeURLStmt.addBatch();
  
                  if((++index%100)==0) { 
                     
                     writeURLStmt.executeBatch();
                   
                  } 
                  
                  if(!isValid) { //adding a status record immediately 
                     writeStatusStmt.setLong(1, rs.getLong("id"));
                     writeStatusStmt.setString(2, Category.Invalid_URL.name());
                     writeStatusStmt.setString(3, message);
                     writeStatusStmt.setTimestamp(4, Timestamp.from(Instant.now()));
                     writeStatusStmt.setString(5, Category.Invalid_URL.name());
                     writeStatusStmt.setString(6, message);
                     writeStatusStmt.setTimestamp(7, Timestamp.from(Instant.now()));
                     
                     writeStatusStmt.execute();
                  }
               } 
               
               writeURLStmt.executeBatch();
            }
         }  
      }
      return null;
   }
}
