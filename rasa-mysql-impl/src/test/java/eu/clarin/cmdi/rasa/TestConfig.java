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
import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.impl.RasaFactoryBuilderImpl;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class TestConfig {

   static RasaFactory rasaFactory;

   static CheckedLinkResource checkedLinkResource;
   static LinkToBeCheckedResource linkToBeCheckedResource;

   static Timestamp today = new Timestamp((System.currentTimeMillis() / 1000) * 1000);
   static Timestamp yesterday = new Timestamp(today.getTime() - 86400000);
   static Timestamp tomorrow = new Timestamp(today.getTime() + 86400000);

   // same urls as in initDB.sql
   static List<String> otherUrls = Arrays.asList("http://www.ailla.org/waiting.html",
         "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "http://www.ailla.org/audio_files/WBA1M3A2.mp3",
         "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "http://www.ailla.org/audio_files/KUA2M1A1.mp3",
         "http://www.ailla.org/text_files/KUA2M1.pdf", "http://www.ailla.org/audio_files/sarixojani.mp3",
         "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "http://www.ailla.org/text_files/TEH11M7.pdf",
         "http://dspin.dwds.de:8088/ddc-sru/dta/", "http://dspin.dwds.de:8088/ddc-sru/grenzboten/",
         "http://dspin.dwds.de:8088/ddc-sru/rem/", "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml",
         "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml",
         "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml",
         "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml",
         "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml",
         "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml");
   static List<String> googleUrls = Arrays.asList("https://www.google.com", "https://maps.google.com",
         "https://drive.google.com");

   private static DB database;

   @BeforeClass
   public static void setUp() throws SQLException, IOException, ManagedProcessException, InterruptedException {
      database = DB.newEmbeddedDB(3308);

      database.start();
      database.createDB("linkchecker");

      // create database and fill it with initDB.sql
      Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/linkchecker?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
            "root", "");
      ScriptRunner runner = new ScriptRunner(con);
      InputStreamReader reader = new InputStreamReader(new FileInputStream("./src/test/resources/createDB.sql"));
      runner.runScript(reader);
      reader.close();
      con.close();

      Properties properties = new Properties();
      properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
      properties.setProperty("jdbcUrl", "jdbc:mysql://localhost:3308/linkchecker?useUnicode=true");
      properties.setProperty("username", "root");
      properties.setProperty("password", "");

      rasaFactory = new RasaFactoryBuilderImpl().getRasaFactory(properties);
      checkedLinkResource = rasaFactory.getCheckedLinkResource();
      linkToBeCheckedResource = rasaFactory.getLinkToBeCheckedResource();

      linkToBeCheckedResource
            .save(new LinkToBeChecked("http://www.ailla.org/waiting.html", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/audio_files/EMP1M1B1.mp3", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/audio_files/WBA1M3A2.mp3", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/text_files/WBA1M1A2a.mp3", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/audio_files/KUA2M1A1.mp3", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/text_files/KUA2M1.pdf", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/audio_files/sarixojani.mp3", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "record",
            "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://www.ailla.org/text_files/TEH11M7.pdf", "record", "NotGoogle", null, today));
      linkToBeCheckedResource
            .save(new LinkToBeChecked("http://dspin.dwds.de:8088/ddc-sru/dta/", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(
            new LinkToBeChecked("http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "record", "NotGoogle", null, today));
      linkToBeCheckedResource
            .save(new LinkToBeChecked("http://dspin.dwds.de:8088/ddc-sru/rem/", "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml",
            "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml",
            "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "record",
            "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "record",
            "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "record",
            "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml",
            "record", "NotGoogle", null, today));
      linkToBeCheckedResource.save(new LinkToBeChecked("http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml", "record",
            "NotGoogle", null, today));
      linkToBeCheckedResource
            .save(new LinkToBeChecked("https://www.google.com", "GoogleRecord", "Google", null, today));
      linkToBeCheckedResource
            .save(new LinkToBeChecked("https://maps.google.com", "GoogleRecord", "Google", null, today));
      linkToBeCheckedResource
            .save(new LinkToBeChecked("https://drive.google.com", "GoogleRecord", "Google", null, today));

      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/waiting.html", "HEAD", 200,
            "text/html; charset=UTF-8", 100l, 132, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "GET", 400,
            "text/html; charset=UTF-8", 0l, 46, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/audio_files/WBA1M3A2.mp3", "GET", 400,
            "text/html; charset=UTF-8", 0l, 46, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "GET", 400,
            "text/html; charset=UTF-8", 0l, 46, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/audio_files/KUA2M1A1.mp3", "GET", 400,
            "text/html; charset=UTF-8", 0l, 56, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/text_files/KUA2M1.pdf", "HEAD", 200,
            "text/html; charset=UTF-8", 0l, 51, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/audio_files/sarixojani.mp3", "GET",
            400, "text/html; charset=UTF-8", 0l, 48, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "GET",
            400, "text/html; charset=UTF-8", 0l, 48, today, "Broken", 0, Category.Broken));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.ailla.org/text_files/TEH11M7.pdf", "HEAD", 200,
            "text/html; charset=UTF-8", 0l, 57, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://dspin.dwds.de:8088/ddc-sru/dta/", "HEAD", 200,
            "application/xml;charset=utf-8", 2094l, 67, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "HEAD", 200,
            "application/xml;charset=utf-8", 2273l, 57, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://dspin.dwds.de:8088/ddc-sru/rem/", "HEAD", 200,
            "application/xml;charset=utf-8", 2497l, 58, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 591, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 592, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 602, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 613, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 605, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 599, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml",
            "HEAD", 200, "text/html; charset=utf-8", 0l, 591, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "https://www.google.com", "HEAD", 200,
            "text/html; charset=ISO-8859-1", 0l, 222, today, "Ok", 0, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "https://maps.google.com", "HEAD", 200,
            "text/html; charset=UTF-8", 0l, 440, today, "Ok", 2, Category.Ok));
      checkedLinkResource.save(new CheckedLink(null, null, "https://drive.google.com", "HEAD", 200,
            "text/html; charset=UTF-8", 73232l, 413, today, "Ok", 1, Category.Ok));
   }

   @AfterClass
   public static void tearDown() throws ManagedProcessException {
      database.stop();
      rasaFactory.tearDown();
   }
}
