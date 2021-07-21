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

import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

//methods should be executed in alphabetical order, so letters in the start of the names matter
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHLinkToBeCheckedResourceTest extends TestConfig {

    @BeforeClass
    public static void setup() {

    }

    @Test
    public void AAbasicSimpleGETTestShouldReturnCorrectly() throws SQLException {
        //same as the first entry in initDB.sql
        String urlA = "http://www.ailla.org/waiting.html";
        String urlB = "https://www.google.com";
        
        LinkToBeChecked linkToBeCheckedA = new LinkToBeChecked(urlA, null, null, null, null);
        LinkToBeChecked linkToBeCheckedB = new LinkToBeChecked(urlB, null, null, null, null);

        // test UrlIs
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setUrlIs(urlA))){
        	assertEquals(linkToBeCheckedA, stream.findFirst().get());
        }

        // test UrlIn
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setUrlIn(urlA, urlB))){
        	List<LinkToBeChecked> list = stream.collect(Collectors.toList());
        	assertEquals(2, list.size());
        	assertTrue(list.contains(linkToBeCheckedA));
        	assertTrue(list.contains(linkToBeCheckedB));
        }  
        
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setUrlIn(urlA, urlB).setProviderGroupIs("Google"))){
        	List<LinkToBeChecked> list = stream.collect(Collectors.toList());
        	assertEquals(1, list.size());
        	assertFalse(list.contains(linkToBeCheckedA));
        	assertTrue(list.contains(linkToBeCheckedB));
        }  
        
        // test record
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setRecordIs("GoogleRecord"))){        	
        	assertEquals(3, stream.count());
        } 
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setRecordIs("GoogleRecord").setProviderGroupIs("NotGoogle"))){        	
        	assertEquals(0, stream.count());
        } 
        
        // test limit
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setLimit(0, 10))){        	
        	assertEquals(10, stream.count());
        } 
        
        // test limit
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setLimit(20, 10))){        	
        	assertEquals(2, stream.count());
        } 
    }
    
    @Test
    public void BTestProviderGroupDeactivation() throws SQLException {
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter())){
        	assertEquals(22, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true))){
        	assertEquals(22, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true).setProviderGroupIs("NotGoogle"))){
        	assertEquals(19, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true).setProviderGroupIs("Google"))){
        	assertEquals(3, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(false))){
        	assertEquals(0, stream.count());
        }
        
        // this shouldn't deactivate any links since the providerGroupMap contains the name 'NotGoogle'
        linkToBeCheckedResource.save(new LinkToBeChecked("http://www.ailla.org/waiting.html", "record", "NotGoogle", null, tomorrow));
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(false))){
        	assertEquals(0, stream.count());
        }
        
        // creates a new linkToCheckedRessource with empty providerGroupMap
        linkToBeCheckedResource = rasaFactory.getLinkToBeCheckedResource();
        // deactivates all links of providerGroup 'NotGoogle' and activates the saved link
        linkToBeCheckedResource.save(new LinkToBeChecked("http://www.ailla.org/waiting.html", "record", "NotGoogle", null, tomorrow));
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter())){
        	assertEquals(22, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true))){
        	assertEquals(4, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true).setProviderGroupIs("NotGoogle"))){
        	assertEquals(1, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(true).setProviderGroupIs("Google"))){
        	assertEquals(3, stream.count());
        }
        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setIsActive(false))){
        	assertEquals(18, stream.count());
        }
    }


    @Test
    public void CgetProviderGroupNamesTestShouldReturnCorrectNames() throws SQLException {
        List<String> collectionNames = linkToBeCheckedResource.getProviderGroupNames();
        assertEquals(2, collectionNames.size());
        assertTrue(collectionNames.contains("Google"));
        assertTrue(collectionNames.contains("NotGoogle"));
    }
}
