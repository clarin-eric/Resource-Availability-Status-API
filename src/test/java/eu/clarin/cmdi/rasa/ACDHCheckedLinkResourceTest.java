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
import eu.clarin.cmdi.rasa.helpers.impl.ACDHRasaFactory;
import eu.clarin.cmdi.rasa.links.CheckedLink;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ACDHCheckedLinkResourceTest extends TestConfig {

    //2019-10-11 00:00:00, same as initDB
    private static final Timestamp then = Timestamp.valueOf(LocalDateTime.of(2019, 10, 11, 0, 0, 0));

    @Test
    public void basicGETTestShouldReturnCorrectResults() throws SQLException {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", 200, "text/html; charset=UTF-8", 100, 132, then, null, 0, null, null);
        CheckedLink actual = checkedLinkResource.get("http://www.ailla.org/waiting.html");
        assertEquals(expected, actual);

        for (String url : urls) {
            CheckedLink checkedLink = checkedLinkResource.get(url);
            assertEquals(checkedLink.getUrl(), url);
        }
    }

    @Test
    public void basicGETWithCollectionTestShouldReturnCorrectResults() throws SQLException {

        for (String url : googleUrls) {
            CheckedLink checkedLink = checkedLinkResource.get(url, "Google");
            assertEquals(checkedLink.getUrl(), url);
            assertEquals(checkedLink.getCollection(), "Google");
        }
    }

//    @Test
//    public void collectionFilterShouldReturnCorrectResults() throws SQLException {
//        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google");
//        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
//        long count = links.count();
//        assertEquals(3, count);
//
//        filter = new ACDHCheckedLinkFilter("noCollection");
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(0, count);
//
//    }

//    @Test
//    public void dateFiltersShouldReturnCorrectResults() {
//        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault());
//        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
//        long count = links.count();
//        assertEquals(22, count);
//
//        filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().plusDays(1), null, ZoneId.systemDefault());
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(22, count);
//
//        filter = new ACDHCheckedLinkFilter(null, null, LocalDateTime.now().minusDays(1), ZoneId.systemDefault());
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(22, count);
//
//        filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), ZoneId.systemDefault());
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(0, count);
//    }
//
//    @Test
//    public void statusCodeFilterShouldReturnCorrectResults() {
//
//        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google", 200);
//        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
//        long count = links.count();
//        assertEquals(3, count);
//
//        filter = new ACDHCheckedLinkFilter(Range.between(100, 600), null, null, ZoneId.systemDefault());
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(22, count);
//
//        filter = new ACDHCheckedLinkFilter(Range.between(1, 90), null, null, ZoneId.systemDefault());
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(0, count);
//
//    }
//
//    @Test
//    public void combinedFilterShouldReturnCorrectResults() {
//        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(Range.between(100, 300), LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault(), "Google");
//        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
//        long count = links.count();
//        assertEquals(3, count);
//    }

}
