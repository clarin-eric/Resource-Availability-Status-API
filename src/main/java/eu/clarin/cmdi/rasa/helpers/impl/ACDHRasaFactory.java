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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHLinkToBeCheckedResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHStatisticsResource;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACDHRasaFactory implements eu.clarin.cmdi.rasa.helpers.RasaFactory {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHRasaFactory.class);

    private MongoDatabase database;

    public ACDHRasaFactory(String databaseName, String databaseURI) {
        connectDatabase(databaseName, databaseURI);
    }

    @Override
    public ACDHCheckedLinkResource getCheckedLinkResource() {
        MongoCollection<Document> linksChecked = database.getCollection("linksChecked");
        MongoCollection<Document> linksCheckedHistory = database.getCollection("linksCheckedHistory");
        MongoCollection<Document> linksToBeChecked = database.getCollection("linksToBeChecked");
        return new ACDHCheckedLinkResource(linksChecked, linksCheckedHistory, linksToBeChecked);
    }

    @Override
    public ACDHLinkToBeCheckedResource getLinkToBeCheckedResource() {
        MongoCollection<Document> linksToBeChecked = database.getCollection("linksToBeChecked");
        return new ACDHLinkToBeCheckedResource(linksToBeChecked);
    }

    @Override
    public ACDHStatisticsResource getStatisticsResource() {
        MongoCollection<Document> linksChecked = database.getCollection("linksChecked");
        MongoCollection<Document> linksToBeChecked = database.getCollection("linksToBeChecked");
        return new ACDHStatisticsResource(linksChecked, linksToBeChecked);
    }

    private void connectDatabase(String databaseName, String databaseURI) {
        _logger.info("Connecting to database...");

        MongoClient mongoClient;
        if (databaseURI==null || databaseURI.isEmpty()) {//if it is empty, try localhost
            mongoClient = MongoClients.create();
        } else {
            mongoClient = MongoClients.create(databaseURI);
        }

        MongoDatabase database = mongoClient.getDatabase(databaseName);

        _logger.info("Connected to database.");

        this.database = database;

    }
}
