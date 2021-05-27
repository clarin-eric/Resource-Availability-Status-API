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

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.impl.ACDHRasaFactory;
import eu.clarin.cmdi.rasa.linkResources.CategoryStatisticsResource;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class TestConfig {

    private static RasaFactory rasaFactory;

    static CheckedLinkResource checkedLinkResource;
    static LinkToBeCheckedResource linkToBeCheckedResource;
//    static StatisticsResource statisticsResource;
    static CategoryStatisticsResource statisticsResource;

    //same urls as in initDB.sql
    static List<String> otherUrls = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "http://www.ailla.org/audio_files/WBA1M3A2.mp3", "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "http://www.ailla.org/audio_files/KUA2M1A1.mp3", "http://www.ailla.org/text_files/KUA2M1.pdf", "http://www.ailla.org/audio_files/sarixojani.mp3", "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "http://www.ailla.org/text_files/TEH11M7.pdf", "http://dspin.dwds.de:8088/ddc-sru/dta/", "http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "http://dspin.dwds.de:8088/ddc-sru/rem/", "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml");
    static List<String> googleUrls = Arrays.asList("https://www.google.com", "https://maps.google.com", "https://drive.google.com");

    private static DB database;

    @BeforeClass
    public static void setUp() throws SQLException, IOException, ManagedProcessException {
        database = DB.newEmbeddedDB(3308);


        database.start();
        database.createDB("linkchecker");


        //create database and fill it with initDB.sql
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/linkchecker?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
        ScriptRunner runner = new ScriptRunner(con);
        InputStreamReader reader = new InputStreamReader(new FileInputStream("./src/test/resources/initDB.sql"));
        runner.runScript(reader);
        reader.close();
        con.close();

        rasaFactory = new ACDHRasaFactory("jdbc:mysql://localhost:3308/linkchecker?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
        checkedLinkResource = rasaFactory.getCheckedLinkResource();
        linkToBeCheckedResource = rasaFactory.getLinkToBeCheckedResource();
        statisticsResource = rasaFactory.getStatisticsResource();

    }

    @AfterClass
    public static void tearDown() throws ManagedProcessException {
        database.stop();
        rasaFactory.tearDown();
    }

}
