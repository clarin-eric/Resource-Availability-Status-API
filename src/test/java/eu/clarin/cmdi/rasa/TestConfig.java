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

package eu.clarin.cmdi.rasa;

import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.impl.ACDHRasaFactory;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import eu.clarin.cmdi.rasa.linkResources.StatisticsResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCheckedLinkResource;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.apache.log4j.LogManager;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.ConnectException;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public abstract class TestConfig {

    public static RasaFactory rasaFactory;
    public static ACDHCheckedLinkResource checkedLinkResource;
    public static LinkToBeCheckedResource linkToBeCheckedResource;
    public static StatisticsResource statisticsResource;

    public static MongoCollection<Document> linksChecked;
    public static MongoCollection<Document> linksCheckedHistory;
    public static MongoCollection<Document> linksToBeChecked;

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static String databaseName;


    @BeforeClass
    public static void setUp() {

        LogManager.getLogger("org.mongodb.driver.cluster").setLevel(org.apache.log4j.Level.OFF);


        try {
            mongoClient = MongoClients.create();

            UUID uuid = UUID.randomUUID();
            databaseName = uuid.toString();

            createCollections();

            rasaFactory = new ACDHRasaFactory(databaseName, null);//localhost

            checkedLinkResource = rasaFactory.getCheckedLinkResource();
            linkToBeCheckedResource = rasaFactory.getLinkToBeCheckedResource();
            statisticsResource = rasaFactory.getStatisticsResource();
        } catch (MongoTimeoutException e) {
            System.err.println("RASA needs a running Mongo instance on localhost:27017 (default) for tests to work. \n" +
                    "These are integration tests, that test the functionality of RASA with a working database. \n" +
                    "Tests will be run on a created database with a randomly generated name on the mongo instance.\n" +
                    "The database is deleted after the tests are run. \n\n" +
                    "Please start a mongo database instance in the environment!");
            fail();
        }


    }

    private static void createCollections() {

        database = mongoClient.getDatabase(databaseName);

        database.createCollection("linksChecked");
        linksChecked = database.getCollection("linksChecked");

        IndexOptions unique = new IndexOptions().unique(true);
        linksChecked.createIndex(Indexes.ascending("url"), unique);
        linksChecked.createIndex(Indexes.ascending("record"));
        linksChecked.createIndex(Indexes.ascending("status"));
        linksChecked.createIndex(Indexes.ascending("collection"));
        linksChecked.createIndex(Indexes.ascending("collection", "status"));
        linksChecked.createIndex(Indexes.ascending("collection", "url"));
        linksChecked.createIndex(Indexes.ascending("record", "status"));
        linksChecked.createIndex(Indexes.ascending("status", "url"));
        linksChecked.createIndex(Indexes.ascending("timestamp"));
        linksChecked.createIndex(Indexes.ascending("collection", "record", "url"));
        linksChecked.createIndex(Indexes.ascending("record", "url"));


        database.createCollection("linksToBeChecked");
        linksToBeChecked = database.getCollection("linksToBeChecked");
        linksToBeChecked.createIndex(Indexes.ascending("url"), unique);
        linksToBeChecked.createIndex(Indexes.ascending("collection"));
        linksToBeChecked.createIndex(Indexes.ascending("record"));
        linksToBeChecked.createIndex(Indexes.ascending("collection", "url"));
        linksToBeChecked.createIndex(Indexes.ascending("record", "url"));

        database.createCollection("linksCheckedHistory");
        linksCheckedHistory = database.getCollection("linksCheckedHistory");
        linksCheckedHistory.createIndex(Indexes.ascending("timestamp"));

    }

    @AfterClass
    public static void tearDown() {

        dropCollections();

        database.drop();

    }

    private static void dropCollections() {
        linksChecked.drop();
        linksCheckedHistory.drop();
        linksToBeChecked.drop();
    }


}
