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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Filter {


    /**
     * Prepares a statement with the given values of the filter in the constructor
     * @param con database connection
     * @return fully prepared statement with filter variables set and ready to execute
     * @throws SQLException can occur during preparing the statement
     */
    PreparedStatement getStatement(Connection con) throws SQLException;
}
