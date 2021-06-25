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

import java.sql.Timestamp;

/**
 * This class creates a filter for the status table with the given values through the constructor
 */
public interface CheckedLinkFilter extends Filter<CheckedLinkFilter>{
	
	public CheckedLinkFilter setStatusIs(Integer status);
    
    public CheckedLinkFilter setStatusBetween(Integer statusFrom, Integer statusTo);
    
    public CheckedLinkFilter setCheckedBetween(Timestamp checkedAfter, Timestamp checkedBefore);
    
    public CheckedLinkFilter setCategoryIs(Category category);
    
    public CheckedLinkFilter setCategoryIn(Category... categories);  
    
    public CheckedLinkFilter setIsActive(boolean active);
}
