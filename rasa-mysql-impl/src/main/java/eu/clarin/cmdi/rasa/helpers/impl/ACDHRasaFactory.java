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

package eu.clarin.cmdi.rasa.helpers.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.CategoryStatisticsResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCategoryStatisticsResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHLinkToBeCheckedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

public class ACDHRasaFactory implements eu.clarin.cmdi.rasa.helpers.RasaFactory {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHRasaFactory.class);
    private HikariDataSource ds;
    private ConnectionProvider connectionProvider;
    
    /**
     * Create ACDH Rasa Factory with given database parameters. It handles the connection by itself afterwards.
     * Different resources can be obtained with their respective get methods.
     * 
     *
     * @param prpperties database properties (see https://github.com/brettwooldridge/HikariCP)
     * @param userName    username for the database
     * @param password    password for the database
     */
    public ACDHRasaFactory(Properties properties) {
        try {
            connectDatabase(properties);
        } catch (SQLException e) {
            _logger.error("There was a problem connecting to the database. Make sure the uri, username and password are correct: ", e);
        }

    }


    /**
     * Create ACDH Rasa Factory with given database parameters. It handles the connection by itself afterwards.
     * Different resources can be obtained with their respective get methods.
     * 
     * @deprecated
     * please use ACDHRasaFactory(Properties properties) with an explicit setting for the connection pool size instead
     *
     * @param databaseURI uri of the database
     * @param userName    username for the database
     * @param password    password for the database
     */
    public ACDHRasaFactory(String databaseURI, String userName, String password) {
        try {
            connectDatabase(databaseURI, userName, password);
        } catch (SQLException e) {
            _logger.error("There was a problem connecting to the database. Make sure the uri, username and password are correct: ", e);
        }

    }

    @Override
    public ACDHCheckedLinkResource getCheckedLinkResource() {
        return new ACDHCheckedLinkResource(connectionProvider);
    }

    @Override
    public ACDHLinkToBeCheckedResource getLinkToBeCheckedResource() {
        return new ACDHLinkToBeCheckedResource(connectionProvider);
    }

    @Override
    public CategoryStatisticsResource getStatisticsResource() {
        return new ACDHCategoryStatisticsResource(connectionProvider);
    }
    
    private void connectDatabase(Properties properties) throws SQLException {
        _logger.info("Connecting to database...");

        HikariConfig config = new HikariConfig(properties);

        ds = new HikariDataSource(config);
        connectionProvider = () -> ds.getConnection();

        _logger.info("Connected to database.");

    }


    private void connectDatabase(String databaseURI, String userName, String password) throws SQLException {
        _logger.info("Connecting to database...");

        HikariConfig config = new HikariConfig();
        //example: jdbc:mysql://localhost:3306/simpsons
        config.setJdbcUrl(databaseURI);
        config.setUsername(userName);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(20);
        config.setLeakDetectionThreshold(10 * 1000);

        ds = new HikariDataSource(config);
        connectionProvider = () -> ds.getConnection();

        _logger.info("Connected to database.");

    }

    @Override
    public void tearDown() {
        this.ds.close();
    }
}
