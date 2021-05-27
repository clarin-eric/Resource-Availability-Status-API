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

package eu.clarin.cmdi.rasa.filters;

import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

/**
 * This class creates a filter for the status table with the given values through the constructor
 */
public interface CheckedLinkFilter extends Filter {

    /**
     *
     * @return the date set for checkedBefore
     */
    LocalDateTime getCheckedBeforeDate();

    /**
     *
     * @return the date set for checkedAfter
     */
    LocalDateTime getCheckedAfterDate();

    /**
     * Gets the collection
     * @return collection
     */
    @Deprecated
    String getCollection();
    
    String getProviderGroup();

    /**
     * Gets the zoneID
     * @return zone id for the filter
     */
    ZoneId getZone();

    /**
     * gets the category
     * @return category for this filter
     */
    Category getCategory();

    /**
     * Sets the start.If there are 20 results and start is set to 10, it will start from the 10th and go until 20.
     * @param start starting line to read from the database
     * @return this
     */
    CheckedLinkFilter setStart(int start);

    /**
     * Sets the end.If there are 20 results and end is set to 10, it will start from the 0 and go until 10.
     * @param end last line to read from the database
     * @return this
     */
    CheckedLinkFilter setEnd(int end);
    
    CheckedLinkFilter setUrls(Collection urls);

}
