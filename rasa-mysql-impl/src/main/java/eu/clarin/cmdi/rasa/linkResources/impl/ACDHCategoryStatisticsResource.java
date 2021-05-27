package eu.clarin.cmdi.rasa.linkResources.impl;

import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.filters.StatisticsCountFilter;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.CategoryStatisticsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ACDHCategoryStatisticsResource implements CategoryStatisticsResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHCategoryStatisticsResource.class);

    private final ConnectionProvider connectionProvider;

    public ACDHCategoryStatisticsResource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<CategoryStatistics> getCategoryStatistics(String providerGroup) throws SQLException {
        if (providerGroup == null || providerGroup.equals("Overall")) {
            return getCategoryStatistics();
        }
        List<CategoryStatistics> list = new ArrayList<CategoryStatistics>();
        
        String query = 
        		"SELECT s.category, AVG(s.duration) AS avgDuration, MAX(s.duration) AS maxDuration, COUNT(s.duration) AS count" + 
	        		" FROM status s, link l, link_context lc, context c, providerGroup p" + 
	        		" WHERE p.name_hash=MD5(?)" + 
	        		" AND c.providerGroup_id = p.id" + 
	        		" AND lc.context_id = c.id" + 
	        		" AND l.id = lc.link_id" + 
	        		" AND s.link_id = l.id" + 
	        		" GROUP BY category" + 
	        		" ORDER BY category";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
            	statement.setString(1, providerGroup);
            	
                try (ResultSet rs = statement.executeQuery()) {
                	
                	while(rs.next()) {
                		list.add(
                				new CategoryStatistics(
	                					rs.getString("category"),
	                					rs.getLong("count"),
	                					rs.getDouble("avgDuration"),
	                					rs.getLong("maxDuration")              						
            						)
                				);
                	}
                }
            }
        }
        return list;
    }

    //avgDuration, maxDuration, countStatus should be named so, because in Statistics constructor, they are called as such.
    @Override
    public List<CategoryStatistics> getCategoryStatistics() throws SQLException {
    	List<CategoryStatistics> list = new ArrayList<CategoryStatistics>();
    	
    	String query = "SELECT category, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status GROUP BY category ORDER BY category";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                	while(rs.next()) {
                		list.add(
                				new CategoryStatistics(
	                					rs.getString("category"),
	                					rs.getLong("count"),
	                					rs.getDouble("avgDuration"),
	                					rs.getLong("maxDuration")              						
            						)
                				);
                	}
                }
            }
        }
        
        return list;
    }

    @Override
    public Statistics getOverallStatistics(String providerGroup) throws SQLException {
        if (providerGroup == null || providerGroup.equals("Overall")) {
            return getOverallStatistics();
        }
        String query = 
        		"SELECT s.category, AVG(s.duration) AS avgDuration, MAX(s.duration) AS maxDuration, COUNT(s.duration) AS count" + 
    	        		" FROM status s, link l, link_context lc, context c, providerGroup p" + 
    	        		" WHERE p.name_hash=MD5(?)" + 
    	        		" AND c.providerGroup_id = p.id" + 
    	        		" AND lc.context_id = c.id" + 
    	        		" AND l.id = lc.link_id" + 
    	        		" AND s.link_id = l.id";

        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
            	statement.setString(1, providerGroup);
            	
                try (ResultSet rs = statement.executeQuery()) {
                	if(rs.next() && rs.getLong("count") > 0L) {
                		return new Statistics(
            					rs.getLong("count"),
            					rs.getDouble("avgDuration"),
            					rs.getLong("maxDuration")                  				
            				);
                	}
                	else
                		return null;
                }
            }
        }
    }

    @Override
    public Statistics getOverallStatistics() throws SQLException {
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                	if(rs.next() && rs.getLong("count") > 0L) {
                		return new Statistics(
            					rs.getLong("count"),
            					rs.getDouble("avgDuration"),
            					rs.getLong("maxDuration")                  				
            				);
                	}
                	else
                		return new Statistics(0, 0.0, 0);
                }
            }
        }
    }

	@Override
	public long countTable(StatisticsCountFilter filter) throws SQLException {
		try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = filter.getStatement(con)) {
            	try (ResultSet rs = statement.executeQuery()){
            		if(rs.next())
            			return rs.getLong("count");
            	}
            }
		}
		return -1L;
	}
}
