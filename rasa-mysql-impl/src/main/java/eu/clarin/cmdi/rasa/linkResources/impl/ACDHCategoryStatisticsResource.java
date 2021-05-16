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
    public List<CategoryStatistics> getCategoryStatistics(String collection) throws SQLException {
        if (collection == null || collection.equals("Overall")) {
            return getCategoryStatistics();
        }
        List<CategoryStatistics> list = new ArrayList<CategoryStatistics>();
        
        String query = "SELECT category, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=? GROUP BY category ORDER BY category";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = getPreparedStatement(con, collection, query)) {
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
    public Statistics getOverallStatistics(String collection) throws SQLException {
        if (collection == null || collection.equals("Overall")) {
            return getOverallStatistics();
        }
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=?";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = getPreparedStatement(con, collection, query)) {

            	
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
                try (ResultSet rs = statement.executeQuery()) {
                	rs.next();
                    return rs.getLong("count");
                }
            }
        }
    }

    private PreparedStatement getPreparedStatement(Connection con, String collection, String query) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, collection);
        return statement;
    }
}
