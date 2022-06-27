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

import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import java.util.Properties;

import javax.sql.DataSource;

public interface RasaFactory {

    /**
     * Get checkedLinkResource to query and update the status table
     *
     * @return checkedLinkResource to query and update the status table
     */
    public CheckedLinkResource getCheckedLinkResource();

    /**
     * Get linkToBeCheckedResource to query and update the urls table
     *
     * @return checkedLinkResource to query and update the status table
     */
    public LinkToBeCheckedResource getLinkToBeCheckedResource();
    
    /**
     * 
    * @param properties instance of Properties containing settings for a Hikari connection pool
    * @return
    * @deprecated this method initializes a HikariCP. Since the data source should be set in the parent project, 
    * the initialization should be done there
    */
   @Deprecated(forRemoval = true)
    public RasaFactory init(Properties properties);
    
    /**
    * @param dataSource instance of DataSource, most commonly a HikariDataSource
    * @return
    */
   public RasaFactory init(DataSource dataSource);

    /**
     * Close all opened connections
     * @deprecated in the future the choice of the data source, which might be a connection pool, should be done in the 
     * parent project. If anything has to be done before or after use of the data source, it should be done in the parent project.     
     */
   @Deprecated(forRemoval = true)
    void tearDown();
}
