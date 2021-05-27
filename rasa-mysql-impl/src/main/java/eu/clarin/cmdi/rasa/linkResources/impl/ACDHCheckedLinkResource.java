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
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ACDHCheckedLinkResource implements CheckedLinkResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHCheckedLinkResource.class);

    private final ConnectionProvider connectionProvider;

    public ACDHCheckedLinkResource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    

    @Override
    public Optional<CheckedLink> get(String url) throws SQLException {
    	
        try (Connection con = connectionProvider.getConnection()) {
            final String urlQuery = 
            		"SELECT s.id AS status_id, l.id AS link_id, l.url, s.method, s.statusCode, s.contentType, s.byteSize, s.duration, s.checkingDate, s.message, s.redirectCount, s.category"
            				+ " FROM status s, link l"
            				+ " WHERE l.url_hash=MD5(?)"
            				+ " AND l.id=s.link_id";
            try (PreparedStatement statement = con.prepareStatement(urlQuery)) {
                statement.setString(1, url);

                try (ResultSet rs = statement.executeQuery()) {
                	return rs.next()?Optional.of(getCheckedLink(rs)):Optional.empty();
                }
            }
        }
    }

    @Override
    public Optional<CheckedLink> get(String url, String collection) throws SQLException {
    	
        final String urlCollectionQuery = 
        		"SELECT s.id AS status_id, l.id AS link_id, l.url, s.method, s.statusCode, s.contentType, s.byteSize, s.duration, s.checkingDate, s.message, s.redirectCount, s.category"
    	        		+ " FROM status s, link l, link_context lc, context c, providerGroup p" 
    	        		+ " WHERE l.url_hash=MD5(?)"
    	        		+ " AND p.name_hash=MD5(?)"
    	        		+ " AND c.providerGroup_id = p.id"
    	        		+ " AND lc.context_id = c.id"
    	        		+ " AND l.id = lc.link_id"
    	        		+ " AND s.link_id = l.id";

        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(urlCollectionQuery)) {
                statement.setString(1, url);
                statement.setString(2, collection);

                try (ResultSet rs = statement.executeQuery()) {
                	return rs.next()?Optional.of(getCheckedLink(rs)):Optional.empty();             
                }
            }
        }
    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> optional) throws SQLException {
    	if(optional.isEmpty())
    		return Stream.empty();
    	
    	Collection<CheckedLink> col = new ArrayList<CheckedLink>();
    	
		try (Connection con = connectionProvider.getConnection()) {
			try (PreparedStatement statement = optional.get().getStatement(con)) {
				try (ResultSet rs = statement.executeQuery()) {
					while(rs.next()) {
						col.add(getCheckedLink(rs));
					}
				}
			}
		}
    	
    	return col.stream();
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
 

    //call this method in a try with resources so that the underlying resources are closed after use
    //TG: Why should't start and end just be part of the filter interface?
    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filterOptional, int start, int end) throws SQLException {
    	if(filterOptional.isEmpty()) {
    		filterOptional = Optional.of(new ACDHCheckedLinkFilter(start, end));
    	}
    	else {
	    	filterOptional.get().setStart(start);
	    	filterOptional.get().setStart(end);
    	}
    	return get(filterOptional);
    }

    @Override
    public Map<String, CheckedLink> get(Collection<String> urls, Optional<CheckedLinkFilter> optional) throws SQLException {
        if (urls.isEmpty()) {
            return Collections.emptyMap();
        } else {
        	Map<String, CheckedLink> map = new HashMap<String, CheckedLink>();
        	
        	CheckedLinkFilter filter = optional.isEmpty()? new ACDHCheckedLinkFilter(urls):optional.get().setUrls(urls);
        	
            try (Connection con = connectionProvider.getConnection()){
                try (PreparedStatement statement = filter.getStatement(con)) {
                    try (ResultSet rs = statement.executeQuery()) {
                    	while(rs.next()) {
                    		
                    		map.put(
		            				rs.getString("url"), 
		            				getCheckedLink(rs)
                				);
                    	}
                    }
                }
            }
            return map;
        }
    }

    @Override
    public Boolean save(CheckedLink checkedLink) throws SQLException {
    	String query = null;

    	if(checkedLink.getLinkId() == null) {
	    	try (Connection con = connectionProvider.getConnection()) {
	    		
	    		query = "SELECT id FROM link where url_hash=MD5(?)";
	    		try(PreparedStatement statement = con.prepareStatement(query)){
	    			statement.setString(1, checkedLink.getUrl());
	    			
	    			try(ResultSet rs = statement.executeQuery()){
	    				if(rs.next()) {
	    					checkedLink.setLinkId(rs.getLong("id"));
	    				}
	    				else {
	    					return false;
	    				}
	    			}
	    		}
	    		
	    		query = "SELECT id FROM status WHERE link_id=?";
	    		try(PreparedStatement statement = con.prepareStatement(query)){
	    			statement.setLong(1, checkedLink.getLinkId());
	    			
	    			try(ResultSet rs = statement.executeQuery()){
	    				if(rs.next()) {
	    					checkedLink.setStatusId(rs.getLong("id"));
	    				}
	    			}
	    		}
	    		
	    		if(checkedLink.getStatusId() == null) {//insert
	    			query = "INSERT INTO status(link_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)"
            		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
	    			
	    	           try (PreparedStatement statement = con.prepareStatement(query)) {
	    	                statement.setLong(1, checkedLink.getLinkId());
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
	    		else {
	    			query = "INSERT INTO history(status_id, link_id, statusCode, message, category, method, contentType, byteSize, duration, checkingDate, redirectCount)"
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
    	}
    	
    	
    	return true;
    }


    @Override
    public Boolean saveToHistory(CheckedLink checkedLink) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            String query = "INSERT INTO history(status_id, link_id, statusCode, message, category, method, contentType, byteSize, duration, checkingTime, redirectCount)"
            		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setLong(1, checkedLink.getStatusId());
                statement.setLong(2, checkedLink.getLinkId());
                statement.setInt(3,  checkedLink.getStatus());
                statement.setString(4, checkedLink.getMessage());
                statement.setString(5, checkedLink.getCategory().toString());
                statement.setString(6, checkedLink.getMethod());
                statement.setString(7, checkedLink.getContentType());
                statement.setInt(8, checkedLink.getByteSize());
                statement.setInt(9, checkedLink.getDuration());
                statement.setTimestamp(10, checkedLink.getCheckingDate());
                statement.setInt(11, checkedLink.getRedirectCount());
                
                return statement.execute();
            }
        }
    }

    @Override
    public Boolean saveToHistory(String url) throws SQLException {
        final String query = "INSERT INTO history(status_id, link_id, statusCode, message, category, method, contentType, byteSize, duration, checkingTime, redirectCount)"
        		+ " SELECT s.* FROM link l, status s WHERE l.url=? AND l.id=s.link_id";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {

                preparedStatement.setString(1, url);

                //affected rows
                int row = preparedStatement.executeUpdate();

                return row == 1;
            }
        }
    }

    @Override
    public Boolean delete(String url) throws SQLException {
        _logger.error("method \"delete(String url)\" not implemented");
        return false;
    }

    @Override
    public List<CheckedLink> getHistory(String url, Order order) throws SQLException {
    	List<CheckedLink> list = new ArrayList<CheckedLink>();
        //not requested much, so no need to optimize
        final String query = "SELECT h.*, l.url FROM history h, link l WHERE l.url=? AND l.id=h.link_id ORDER BY checkingDate " + order.name();
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, url);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                	while(rs.next()) {
                		list.add(getCheckedLink(rs));
                    }
                }
            }
        }
        return list;
    }
    
    
    private CheckedLink getCheckedLink(ResultSet rs) throws SQLException {
    	return new CheckedLink(   
				rs.getLong("status_id"),
				rs.getLong("link_id"),
		        rs.getString("url"),
		        rs.getString("method"),
		        (Integer) rs.getObject("statusCode"),           		        
		        rs.getString("contentType"),
		        (Integer) rs.getObject("byteSize"),
		        (Integer) rs.getObject("duration"),
		        rs.getTimestamp("checkingDate"),
		        rs.getString("message"),
        		rs.getInt("redirectCount"),
		        Category.valueOf(rs.getString("category"))		
	        );
    }
}
