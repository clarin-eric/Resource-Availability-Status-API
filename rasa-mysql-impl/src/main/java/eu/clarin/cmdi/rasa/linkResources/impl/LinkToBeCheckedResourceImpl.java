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
import eu.clarin.cmdi.rasa.filters.impl.LinkToBeCheckedFilterImpl;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;

import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LinkToBeCheckedResourceImpl implements LinkToBeCheckedResource {

    private final static Logger LOG = LoggerFactory.getLogger(LinkToBeCheckedResourceImpl.class);

    private final ConnectionProvider connectionProvider;
    
    private Map<String, Long> providerGroupIdMap; //since the deactivation depends on this we need a map here
    private Map.Entry<String, Long> lastContextId;

    public LinkToBeCheckedResourceImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        
        this.providerGroupIdMap = new HashMap<String, Long>();
        this.lastContextId = new AbstractMap.SimpleEntry<String, Long>("", null);
    }

	@Override
	public Stream<LinkToBeChecked> get(LinkToBeCheckedFilter filter) throws SQLException {
        String query = "SELECT DISTINCT u.* " + filter;
        LOG.debug("query: {}", query);
		final Connection con = connectionProvider.getConnection();
        final Statement stmt = con.createStatement();
        
        final ResultSet rs = stmt.executeQuery(query);

        Stream<Record> recordStream = DSL.using(con).fetchStream(rs);
        recordStream.onClose(() -> {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.error("Can't close resultset.");
            }
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error("Can't close prepared statement.");
            }
            try {
                con.close();
            } catch (SQLException e) {
                LOG.error("Can't close connection.");
            }
        });

        return recordStream.map(rec -> new LinkToBeChecked(
				rec.get("id", Long.class),
		        rec.get("url", String.class),
		        rec.get("nextFetchDate", Timestamp.class)
			));
	}

    @Override
    public Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException {
    	saveLink(linkToBeChecked);
    	
    	long providerGroupId = getProviderGroupId(linkToBeChecked);
    	long contextId = getContextId(linkToBeChecked, providerGroupId);
    	
    	saveUrlContext(linkToBeChecked, providerGroupId, contextId);
    	
    	return true;
    }

    @Override
    public Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException {
    	for(LinkToBeChecked linkToBeChecked:linksToBeChecked)
    		save(linkToBeChecked);
    	return true;
    }

    @Override
    public Boolean delete(String url) throws SQLException {
    	LOG.error("method \"delete(String url)\" not implemented");
    	return false;
    }

    @Override
    public Boolean delete(List<String> urls) throws SQLException {
    	LOG.error("method \"delete(List<String> urls)\" not implemented");
    	return false;
    }

    @Override
    public List<String> getCollectionNames() throws SQLException {
    	return getProviderGroupNames();
    }
    

	@Override
	public List<String> getProviderGroupNames() throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            List<String> collectionNames = new ArrayList<>();

            String collectionQuery = "SELECT name from providerGroup";
            try (PreparedStatement statement = con.prepareStatement(collectionQuery)) {
                try (ResultSet rs = statement.executeQuery()) {

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
    	LOG.error("method \"deleteOldLinks(Long date)\" not implemented");
    	return -1;
    }

    @Override
    public int deleteOldLinks(Long date, String collection) throws SQLException {
    	LOG.error("method \"deleteOldLinks(Long date, String collection)\" not implemented");
    	return -1;
    }
    
    @Override
    public Boolean updateNextFetchDate(Long linkId, Timestamp nextFetchDate) throws SQLException {
    	try(Connection con = connectionProvider.getConnection()){
    		String query = "UPDATE url SET nextFetchDate=? WHERE id=?";
    		try(PreparedStatement statement = con.prepareStatement(query)){
    			statement.setTimestamp(1, nextFetchDate);
    			statement.setLong(2, linkId);
    			
    			return statement.execute();
    		}
    	}
    }


	@Override
	public int getCount(LinkToBeCheckedFilter filter) throws SQLException {
		String query = "SELECT count(DISTINCT u.id) AS count " + filter;
		LOG.debug("query: {}", query);
		try(Connection con = this.connectionProvider.getConnection()){
			try(PreparedStatement stmt = con.prepareStatement(query)){
				try(ResultSet rs = stmt.executeQuery()){
					if(rs.next())
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
	
	private synchronized void saveLink(LinkToBeChecked linkToBeChecked) throws SQLException{

		try (Connection con = connectionProvider.getConnection()) {
        	try (PreparedStatement statement = con.prepareStatement("SELECT id from url where url_hash=MD5(?)")){
        		statement.setString(1, linkToBeChecked.getUrl());
        		
        		try(ResultSet rs = statement.executeQuery()){
        			if(rs.next())
        				linkToBeChecked.setLinkId(rs.getLong("id"));
        		}
        	}
        	if(linkToBeChecked.getUrlId() == null) {//insert new link
	            try (PreparedStatement statement = con.prepareStatement("INSERT INTO url(url, url_hash, nextFetchDate) VALUES(?,MD5(?),?)")) {
	                statement.setString(1, linkToBeChecked.getUrl());
	                statement.setString(2, linkToBeChecked.getUrl());
	                statement.setTimestamp(3, linkToBeChecked.getNextFetchDate());
	                
	                statement.execute();
	            }	

	            try (PreparedStatement statement = con.prepareStatement("SELECT LAST_INSERT_ID() AS id")) {
	            	try(ResultSet rs = statement.executeQuery()){
	        			if(rs.next())
	        				linkToBeChecked.setLinkId(rs.getLong("id"));
	        		}
	            }	
            }
		}
	}
	private synchronized Long getProviderGroupId(LinkToBeChecked linkToBeChecked) throws SQLException {
		if(linkToBeChecked.getProviderGroup() == null)
			return null;
		if(!this.providerGroupIdMap.containsKey(linkToBeChecked.getProviderGroup())) {
			
			try (Connection con = connectionProvider.getConnection()) {
	        	try (PreparedStatement statement = con.prepareStatement("SELECT id FROM providerGroup where name_hash=MD5(?)")){
	        		statement.setString(1, linkToBeChecked.getProviderGroup());
	        		
	        		try(ResultSet rs = statement.executeQuery()){
	        			if(rs.next())
	        				this.providerGroupIdMap.put(linkToBeChecked.getProviderGroup(), rs.getLong("id"));
	        		}
	        	}

	            
	            if(!this.providerGroupIdMap.containsKey(linkToBeChecked.getProviderGroup())) {//insert new providerGroup
	            	
		            try (PreparedStatement statement = con.prepareStatement("INSERT INTO providerGroup(name, name_hash) VALUES(?,MD5(?))")) {
		                statement.setString(1, linkToBeChecked.getProviderGroup());
		                statement.setString(2, linkToBeChecked.getProviderGroup());
		                
		                statement.execute();	        		
		            }	

		            try (PreparedStatement statement = con.prepareStatement("SELECT LAST_INSERT_ID() AS id")) {
		            	try(ResultSet rs = statement.executeQuery()){
		        			if(rs.next())
		        				this.providerGroupIdMap.put(linkToBeChecked.getProviderGroup(), rs.getLong("id"));
		        		}
		            }	
	            }
	            else {//deactivate all links of the provider group
	    			try(PreparedStatement statement = con.prepareStatement(
	    					"UPDATE url_context uc, context c SET uc.active = false"
	    					+ " WHERE c.providerGroup_id = ?"
	    					+ " AND uc.context_id = c.id")){
	    				
	    				statement.setLong(1, this.providerGroupIdMap.get(linkToBeChecked.getProviderGroup()));
	    				
	    				statement.execute();
	    			}	            	
	            }				
			}
		}
		
		return this.providerGroupIdMap.get(linkToBeChecked.getProviderGroup());
	}
	private synchronized long getContextId(LinkToBeChecked linkToBeChecked, long providerGroupId) throws SQLException {
		String key = linkToBeChecked.getRecord() + "-" + providerGroupId + "-" + linkToBeChecked.getExpectedMimeType();
		if(!this.lastContextId.getKey().contentEquals(key)) {
			this.lastContextId = new AbstractMap.SimpleEntry<String, Long>(key, null);
			
			try (Connection con = connectionProvider.getConnection()) {
		            try (PreparedStatement statement = con.prepareStatement("SELECT id FROM context WHERE providerGroup_id=? AND record=? AND expectedMimeType=?")) {
		            	statement.setLong(1, providerGroupId);
		            	statement.setString(2, linkToBeChecked.getRecord());
		            	statement.setString(3, linkToBeChecked.getExpectedMimeType());
		            	
		        		try(ResultSet rs = statement.executeQuery()){
		        			if(rs.next())
		        				this.lastContextId.setValue(rs.getLong("id"));
		        		}        	
		            }
		            
		            if(this.lastContextId.getValue() == null) {//insert new context
			            try (PreparedStatement statement = con.prepareStatement("INSERT INTO context(providerGroup_id, record, expectedMimeType) VALUES(?,?,?)")) {
			            	statement.setLong(1, providerGroupId);
			            	statement.setString(2, linkToBeChecked.getRecord());
			            	statement.setString(3, linkToBeChecked.getExpectedMimeType());
			                
			                statement.execute();	        		
			            }	
			            try (PreparedStatement statement = con.prepareStatement("SELECT LAST_INSERT_ID() AS id")) {
			            	try(ResultSet rs = statement.executeQuery()){
			        			if(rs.next())
			        				this.lastContextId.setValue(rs.getLong("id"));
			        		}
			            }	
		            }				
			}	
		}
		
		return this.lastContextId.getValue();
	}
	private synchronized void saveUrlContext(LinkToBeChecked linkToBeChecked, long linkId, long contextId) throws SQLException {
		try (Connection con = connectionProvider.getConnection()) {
            Long urlContextId = null;
            
            try (PreparedStatement statement = con.prepareStatement("SELECT id from url_context WHERE url_id=? AND context_id=?")) {
                statement.setLong(1, linkToBeChecked.getUrlId());
                statement.setLong(2, contextId);
                
                try(ResultSet rs = statement.executeQuery()){
        			if(rs.next()) {
        				urlContextId = rs.getLong("id");
        			}	
        		}	
            }
            
            if(urlContextId == null) {
	            try (PreparedStatement statement = con.prepareStatement("INSERT INTO url_context(url_id, context_id, ingestionDate, active) VALUES (?,?,?, true)")) {
	                statement.setLong(1, linkToBeChecked.getUrlId());
	                statement.setLong(2, contextId);
	                statement.setTimestamp(3, linkToBeChecked.getIngestionDate());
	                
	                statement.execute();
	            }
            }
            else {
	            try (PreparedStatement statement = con.prepareStatement("UPDATE url_context SET ingestionDate=?, active = true WHERE id=?")) {
	                statement.setTimestamp(1, linkToBeChecked.getIngestionDate());
	                statement.setLong(2, urlContextId);
	                
	                statement.execute();
	            }	            	
            }
		}
	}
}
