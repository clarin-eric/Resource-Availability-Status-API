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

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import org.apache.commons.lang3.Range;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ACDHCheckedLinkResourceTest extends TestConfig {

    //2019-10-11 00:00:00, same as initDB
    private static final LocalDateTime thenDateTime = LocalDateTime.of(2019, 10, 11, 0, 0, 0);
    private static final Timestamp then = Timestamp.valueOf(thenDateTime);

    @Test
    public void basicGETTestShouldReturnCorrectResults() throws SQLException {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", 200, "text/html; charset=UTF-8", 100, 132, then, "NotGoogle", 0, "record", null);
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

    @Test
    public void collectionFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter("noCollection");
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

    }

    @Test
    public void dateFiltersShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault());
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, null, thenDateTime.minusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, thenDateTime.minusDays(1), thenDateTime.plusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);
    }

    @Test
    public void statusCodeFilterShouldReturnCorrectResults() throws SQLException {

        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google", 200);
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter(Range.between(100, 600), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(Range.between(1, 90), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

    }

    @Test
    public void combinedFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(Range.between(100, 300), thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault(), "Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);
    }

    @Test
    public void basicSaveTestShouldSaveCorrectly() {

    }

    @Test
    public void saveWithoutTupleInUrlsTableTestShouldNotSave() throws SQLException {
        assertFalse(checkedLinkResource.save(new CheckedLink("not in urls table url", null, 0, null, 0, 0, null, null, 0, null, null)));
    }

}
