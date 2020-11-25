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

package eu.clarin.cmdi.rasa.helpers;

import eu.clarin.cmdi.rasa.linkResources.CategoryStatisticsResource;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;

public interface RasaFactory {

    /**
     * Get checkedLinkResource to query and update the status table
     *
     * @return checkedLinkResource to query and update the status table
     */
    CheckedLinkResource getCheckedLinkResource();

    /**
     * Get linkToBeCheckedResource to query and update the urls table
     *
     * @return checkedLinkResource to query and update the status table
     */
    LinkToBeCheckedResource getLinkToBeCheckedResource();

    /**
     * Get statisticsResource to query both status and urls tables
     *
     * @return statisticsResource to query both status and urls tables
     */
    CategoryStatisticsResource getStatisticsResource();

    /**
     * Close all opened connections
     */
    void tearDown();
}
