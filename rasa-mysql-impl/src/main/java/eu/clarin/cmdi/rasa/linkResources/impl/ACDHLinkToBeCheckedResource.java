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
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;

import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ACDHLinkToBeCheckedResource implements LinkToBeCheckedResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHLinkToBeCheckedResource.class);

    private final ConnectionProvider connectionProvider;

    public ACDHLinkToBeCheckedResource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

    }

    @Override
    public Optional<LinkToBeChecked> get(String url) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            String query = "SELECT * FROM link WHERE url_hash = MD5(?)";
            try (PreparedStatement statement = con.prepareStatement(query)) {

                statement.setString(1, url);

                try (ResultSet rs = statement.executeQuery()) {
                	
                	return rs.next()?Optional.of(getLinkToBeChecked(rs)):Optional.empty();
                }
            }
        }
    }

    //call this method in a try with resources so that the underlying resources are closed after use
    @Override
    public Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        final Connection con = connectionProvider.getConnection();
        final String defaultQuery = "SELECT * FROM link ORDER BY nextFetchDate DESC";
        final PreparedStatement statement = con.prepareStatement(defaultQuery);
        final ResultSet rs = statement.executeQuery();

        Stream<Record> recordStream = DSL.using(con).fetchStream(rs);
        recordStream.onClose(() -> {
            try {
                rs.close();
                statement.close();
                con.close();
            } catch (SQLException e) {
                _logger.error("Can't close prepared statement or resultset or connection.");
            }
        });

        return recordStream.map(this::getLinkToBeChecked);
    }


    @Override
    public List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        try (Stream<LinkToBeChecked> linkToBeCheckedStream = get(filter)) {
            return linkToBeCheckedStream.collect(Collectors.toList());
        }
    }

    @Override
    public Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException {
    	String query = null;
    	
	        try (Connection con = connectionProvider.getConnection()) {
	        	query = "SELECT id from link where url_hash=MD5(?)";
	        	
	        	
	        	try (PreparedStatement statement = con.prepareStatement(query)){
	        		statement.setString(1, linkToBeChecked.getUrl());
	        		
	        		try(ResultSet rs = statement.executeQuery()){
	        			if(rs.next())
	        				linkToBeChecked.setLinkId(rs.getLong("id"));
	        		}
	        	}
	        	if(linkToBeChecked.getLinkId() == null) {//insert new link
	        		query = "INSERT INTO link(url, url_hash, nextFetchDate)"
		        			+ " VALUES(?,MD5(?),?)";
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		                statement.setString(1, linkToBeChecked.getUrl());
		                statement.setString(2, linkToBeChecked.getUrl());
		                statement.setTimestamp(3, linkToBeChecked.getNextFetchDate());
		                
		                statement.execute();
	        		
		            }	
		            
	        		query = "SELECT LAST_INSERT_ID() AS id";
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		            	try(ResultSet rs = statement.executeQuery()){
		        			if(rs.next())
		        				linkToBeChecked.setLinkId(rs.getLong("id"));
		        		}
		            }	
	            } //end insert link
	        	
	        	Long providerGroupId = null;
	        	
	        	query = "SELECT id FROM providerGroup where name_hash=MD5(?)";

	            
	            if(providerGroupId == null) {//insert new providerGroup
	            	query = "INSERT INTO providerGroup(name, name_hash) VALUES(?,?)";
	            	
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		                statement.setString(1, linkToBeChecked.getProviderGroup());
		                statement.setString(2, linkToBeChecked.getProviderGroup());
		                
		                statement.execute();	        		
		            }	
		            
	        		query = "SELECT LAST_INSERT_ID() AS id";
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		            	try(ResultSet rs = statement.executeQuery()){
		        			if(rs.next())
		        				providerGroupId = rs.getLong("id");
		        		}
		            }	
	            }// end insert providerGroup
	            
	            Long contextId = null;
	            
	            query = "SELECT id FROM context WHERE providerGroup_id=? AND record=? AND expectedMimeType=?";
	            
	            try (PreparedStatement statement = con.prepareStatement(query)) {
	            	statement.setLong(1, providerGroupId);
	            	statement.setString(2, linkToBeChecked.getRecord());
	            	statement.setString(3, linkToBeChecked.getExpectedMimeType());
	            	
	        		try(ResultSet rs = statement.executeQuery()){
	        			if(rs.next())
	        				contextId = rs.getLong("id");
	        		}        	
	            }
	            
	            if(contextId == null) {//insert new context
	            	query = "INSERT INTO context(providerGroup_id, record, expectedMimeType) VALUES(?,?,?)";
	            	
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		            	statement.setLong(1, providerGroupId);
		            	statement.setString(2, linkToBeChecked.getRecord());
		            	statement.setString(3, linkToBeChecked.getExpectedMimeType());
		                
		                statement.execute();	        		
		            }	
		            
	        		query = "SELECT LAST_INSERT_ID() AS id";
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		            	try(ResultSet rs = statement.executeQuery()){
		        			if(rs.next())
		        				contextId = rs.getLong("id");
		        		}
		            }	
	            }// end insert context
	            
	            Long linkContextId = null;
	            
	            query = "SELECT id from link_context WHERE link_id=? AND context_id=?";
	            
	            try (PreparedStatement statement = con.prepareStatement(query)) {
	                statement.setLong(1, linkToBeChecked.getLinkId());
	                statement.setLong(2, contextId);
	                
	                try(ResultSet rs = statement.executeQuery()){
	        			if(rs.next()) {
	        				linkContextId = rs.getLong("id");
	        			}	
	        		}	
	            }
	            
	            if(linkContextId == null) {
	            	query = "INSERT INTO link_context(link_id, context_id, harvestDate) VALUES (?,?,?)";
	            	
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		                statement.setLong(1, linkToBeChecked.getLinkId());
		                statement.setLong(2, contextId);
		                statement.setTimestamp(3, linkToBeChecked.getHarvestDate2());
		                
		                statement.execute();
		            }
	            }
	            else {
	            	query = "UPDATE link_context SET harvestDate=? WHERE id=?";
	            	
		            try (PreparedStatement statement = con.prepareStatement(query)) {
		                statement.setTimestamp(1, linkToBeChecked.getHarvestDate2());
		                statement.setLong(2, linkContextId);
		                
		                statement.execute();
		            }	            	
	            }
        }
    	
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
    	_logger.error("method \"delete(String url)\" not implemented");
    	return false;
    }

    @Override
    public Boolean delete(List<String> urls) throws SQLException {
    	_logger.error("method \"delete(List<String> urls)\" not implemented");
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
    	_logger.error("method \"deleteOldLinks(Long date)\" not implemented");
    	return -1;
    }

    @Override
    public int deleteOldLinks(Long date, String collection) throws SQLException {
    	_logger.error("method \"deleteOldLinks(Long date, String collection)\" not implemented");
    	return -1;
    }
    
    @Override
    public Boolean updateNextFetchDate(Long linkId, Timestamp nextFetchDate) throws SQLException {
    	try(Connection con = connectionProvider.getConnection()){
    		String query = "UPDATE link SET nextFectDate=? WHERE id=?";
    		try(PreparedStatement statement = con.prepareStatement(query)){
    			statement.setTimestamp(1, nextFetchDate);
    			statement.setLong(2, linkId);
    			
    			return statement.execute();
    		}
    	}
    }
    
    private LinkToBeChecked getLinkToBeChecked(ResultSet rs) throws SQLException {
    	return new LinkToBeChecked(
				rs.getLong("id"),
		        rs.getString("url"),
		        rs.getTimestamp("nextFetchDate")
			);
    }
    private LinkToBeChecked getLinkToBeChecked(Record rec){
    	return new LinkToBeChecked(
				(Long) rec.get("id"),
		        (String) rec.get("url"),
		        (Timestamp) rec.get("nextFetchDate")
			);
    }
}
