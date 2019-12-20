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
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHLinkToBeCheckedResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHStatisticsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ACDHRasaFactory implements eu.clarin.cmdi.rasa.helpers.RasaFactory {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHRasaFactory.class);

    private HikariDataSource ds;
    private Connection checkedLinkConnection;
    private Connection linkToBeCheckedConnection;
    private Connection statisticsConnection;

    public ACDHRasaFactory(String databaseURI, String userName, String password) {
        try {
            connectDatabase(databaseURI, userName, password);
        } catch (SQLException e) {
            _logger.error("There was a problem connecting to the database. Make sure the uri, username and password are correct: ",e);
        }
    }

    @Override
    public ACDHCheckedLinkResource getCheckedLinkResource() {
        return new ACDHCheckedLinkResource(checkedLinkConnection);
    }

    @Override
    public ACDHLinkToBeCheckedResource getLinkToBeCheckedResource() {
        return new ACDHLinkToBeCheckedResource(linkToBeCheckedConnection);
    }

    @Override
    public ACDHStatisticsResource getStatisticsResource() {
        return new ACDHStatisticsResource(statisticsConnection);
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
        config.setLeakDetectionThreshold(30 * 1000);

        ds = new HikariDataSource(config);

        checkedLinkConnection = ds.getConnection();
        linkToBeCheckedConnection = ds.getConnection();
        statisticsConnection = ds.getConnection();

        _logger.info("Connected to database.");

    }

    @Override
    public void tearDown() {
        this.ds.close();
    }
}
