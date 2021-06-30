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

import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.CheckedLinkFilterImpl;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckedLinkResourceImpl implements CheckedLinkResource {

    private final static Logger LOG = LoggerFactory.getLogger(CheckedLinkResourceImpl.class);

    private final ConnectionProvider connectionProvider;

    public CheckedLinkResourceImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    

    @Override
    public Stream<CheckedLink> get(CheckedLinkFilter filter) throws SQLException{   
    	String query = "SELECT DISTINCT s.*, u.url " + filter;
    	LOG.debug("query: {}", query);
	    final Connection con = connectionProvider.getConnection();
	    final Statement stmt = con.createStatement();
	    
	    final ResultSet rs = stmt.executeQuery(query);
	    return DSL.using(con)
	            .fetchStream(rs)
	            .map(rec -> new CheckedLink(   
	    				rec.get("id", Long.class),
	    				rec.get("url_id", Long.class),
	    		        rec.get("url", String.class),
	    		        rec.get("method", String.class),
	    		        rec.get("statusCode", Integer.class),           		        
	    		        rec.get("contentType", String.class),
	    		        rec.get("byteSize", Integer.class),
	    		        rec.get("duration", Integer.class),
	    		        rec.get("checkingDate", Timestamp.class),
	    		        rec.get("message", String.class),
	            		rec.get("redirectCount", Integer.class),
	    		        Category.valueOf(rec.get("category", String.class))		
	    	        ))
	            .onClose(() -> {
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
    }

    /**
     * This method is used to be able to have the prepared statement creation in
     * one line, so that it fits well in to a try-with-resources block Then the
     * caller methods don't need to concern themselves with closing the
     * statement.
     *
     * @param defaultQuery
     * @param filter
     * @param inList
     * @return
     * @throws SQLException
     */

    @Override
    @Deprecated
    public Map<String, CheckedLink> get(Collection<String> urls, Optional<CheckedLinkFilter> optional) throws SQLException {
        if (urls.isEmpty()) {
            return Collections.emptyMap();
        } else {	
        	return getMap(new CheckedLinkFilterImpl().setUrlIn(urls.toArray(new String[urls.size()])));
        }
    }
    
    public Map<String, CheckedLink> getMap(CheckedLinkFilter filter) throws SQLException {
    	Map<String, CheckedLink> map = null;
    	try(Stream<CheckedLink> stream = get(filter)){
    		map = stream.collect(Collectors.toMap(CheckedLink::getUrl, Function.identity()));
    	}
    	return map;
    }

    @Override
    public Boolean save(CheckedLink checkedLink) throws SQLException {
    	String query = null;
    	
    	try (Connection con = connectionProvider.getConnection()) {
    		//look up urlId if not set in checkedLink
    		if(checkedLink.getUrlId() == null) {
	    		query = "SELECT id FROM url where url_hash=MD5(?)";
	    		try(PreparedStatement statement = con.prepareStatement(query)){
	    			statement.setString(1, checkedLink.getUrl());
	    			
	    			try(ResultSet rs = statement.executeQuery()){
	    				if(rs.next()) {
	    					checkedLink.setUrlId(rs.getLong("id"));
	    				}
	    				else {
	    					return false;
	    				}
	    			}
	    		}
    		}
	    	// look up status record for urlId	
    		query = "SELECT id FROM status WHERE url_id=?";
    		try(PreparedStatement statement = con.prepareStatement(query)){
    			statement.setLong(1, checkedLink.getUrlId());
    			
    			try(ResultSet rs = statement.executeQuery()){
    				if(rs.next()) {
    					checkedLink.setStatusId(rs.getLong("id"));
    				}
    			}
    		}
	    	// insert status record if no record exists for the urlId	
    		if(checkedLink.getStatusId() == null) {//insert
    			query = "INSERT INTO status(url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)"
        		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
    			
    	           try (PreparedStatement statement = con.prepareStatement(query)) {
    	                statement.setLong(1, checkedLink.getUrlId());
    	                statement.setInt(2,  checkedLink.getStatus());
    	                statement.setString(3, checkedLink.getMessage());
    	                statement.setString(4, checkedLink.getCategory().toString());
    	                statement.setString(5, checkedLink.getMethod());
    	                statement.setString(6, checkedLink.getContentType());
    	                statement.setInt(7, checkedLink.getByteSize());
    	                statement.setInt(8, checkedLink.getDuration());
    	                statement.setTimestamp(9, checkedLink.getCheckingDate());
    	                statement.setInt(10, checkedLink.getRedirectCount());
    	                
    	                statement.execute();
    	            }
    			
    		}
    		else { // otherwise copy existing status record to history and update status record
    			query = "INSERT IGNORE INTO history(status_id, url_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)"
    					+ " SELECT * FROM status WHERE id=?";	    			
    			try (PreparedStatement statement = con.prepareStatement(query)) {
    				statement.setLong(1, checkedLink.getStatusId());
    				
    				statement.execute();
    			}
    			
    			query = "UPDATE status SET statusCode=?, message=?, category=?, method=?, contentType=?, byteSize=?, duration=?, checkingDate=?, redirectCount=? WHERE id=?";	    			
    			try (PreparedStatement statement = con.prepareStatement(query)) {
    				statement.setInt(1,  checkedLink.getStatus());
	                statement.setString(2, checkedLink.getMessage());
	                statement.setString(3, checkedLink.getCategory().toString());
	                statement.setString(4, checkedLink.getMethod());
	                statement.setString(5, checkedLink.getContentType());
	                statement.setInt(6, checkedLink.getByteSize());
	                statement.setInt(7, checkedLink.getDuration());
	                statement.setTimestamp(8, checkedLink.getCheckingDate());
	                statement.setInt(9, checkedLink.getRedirectCount());
	                statement.setLong(10, checkedLink.getStatusId());
	                
    				statement.execute();
    			}
    		} 	
    	}  	
    	
    	return true;
    }


	@Override
	public int getCount(CheckedLinkFilter filter) throws SQLException {
		try(Connection con = this.connectionProvider.getConnection()){
			try(PreparedStatement stmt = con.prepareStatement("SELECT count(DISTINCT u.id) AS count " + filter)){
				try(ResultSet rs = stmt.executeQuery()){
					if(rs.next())
						return rs.getInt("count");
				}
			}
		}
		return 0;
	}


	@Override
	public Statistics getStatistics(CheckedLinkFilter filter) throws SQLException {
	    final Connection con = connectionProvider.getConnection();
	    final PreparedStatement statement = con.prepareStatement("SELECT IFNULL(AVG(s.duration), 0.0) AS avgDuration, IFNULL(MAX(s.duration), 0) AS maxDuration, COUNT(s.duration) AS count " + filter);
	    final ResultSet rs = statement.executeQuery();
	    return DSL.using(con)
	            .fetchStream(rs)
	            .map(rec -> new Statistics(   
	    				rec.get("count", Long.class),
	    		        rec.get("avgDuration", Double.class),
	    		        rec.get("maxDuration", Long.class)	
	    	        ))
	            .onClose(() -> {
	                try {
	                    rs.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close resultset.");
	                }
	                try {
	                    statement.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close prepared statement.");
	                }
	                try {
	                    con.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close connection.");
	                }
	            }).findFirst().orElseGet(() -> new Statistics(0L, 0.0, 0L));
	}


	@Override
	public Stream<CategoryStatistics> getCategoryStatistics(CheckedLinkFilter filter) throws SQLException {
	    final Connection con = connectionProvider.getConnection();
	    final PreparedStatement statement = con.prepareStatement("SELECT s.category, IFNULL(AVG(s.duration), 0.0) AS avgDuration, IFNULL(MAX(s.duration), 0) AS maxDuration, COUNT(s.duration) AS count " + filter + " GROUP BY s.category ORDER BY s.category");
	    final ResultSet rs = statement.executeQuery();
	    return DSL.using(con)
	            .fetchStream(rs)
	            .map(rec -> new CategoryStatistics(   
	    				Category.valueOf(rec.get("category", String.class)),
	    				rec.get("count", Long.class),
	    		        rec.get("avgDuration", Double.class),
	    		        rec.get("maxDuration", Long.class)	
	    	        ))
	            .onClose(() -> {
	                try {
	                    rs.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close resultset.");
	                }
	                try {
	                    statement.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close prepared statement.");
	                }
	                try {
	                    con.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close connection.");
	                }
	            });
	}


	@Override
	public Stream<StatusStatistics> getStatusStatistics(CheckedLinkFilter filter) throws SQLException {
	    final Connection con = connectionProvider.getConnection();
	    final PreparedStatement statement = con.prepareStatement("SELECT s.status, AVG(s.duration) AS avgDuration, MAX(s.duration) AS maxDuration, COUNT(s.duration) AS count " + filter);
	    final ResultSet rs = statement.executeQuery();
	    return DSL.using(con)
	            .fetchStream(rs)
	            .map(rec -> new StatusStatistics(   
	    				rec.get("status", Integer.class),
	    				rec.get("count", Long.class),
	    		        rec.get("avgDuration", Double.class),
	    		        rec.get("maxDuration", Long.class)	
	    	        ))
	            .onClose(() -> {
	                try {
	                    rs.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close resultset.");
	                }
	                try {
	                    statement.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close prepared statement.");
	                }
	                try {
	                    con.close();
	                } catch (SQLException e) {
	                    LOG.error("Can't close connection.");
	                }
	            });
	}


	@Override
	public CheckedLinkFilter getCheckedLinkFilter() {
		
		return new CheckedLinkFilterImpl();
	}
}
