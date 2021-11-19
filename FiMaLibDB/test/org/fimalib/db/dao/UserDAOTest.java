package org.fimalib.db.dao;

/*
 * Copyright (C) 2021 peter.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

import java.util.List;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.UserBean;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit class tests the functionality of the class
 * 
 * org.fimalib.db.dao.UserDAO
 * 
 * Attention: For resetting the table's auto number counter, you may run the
 * following statements after running the tests:
 * truncate table tblUsers_hist;
 * truncate table tblUsers;
 * truncate table tblGroups;
 * truncate table tblGroups_hist;
 * truncate table tblGroupAccessRights_hist;
 * truncate table tblGroupAccessRights;
 * truncate table tblUserGroups;
 * truncate table tblUserGroups_hist;
 * 
 * @author peter
 */
public class UserDAOTest {
    public static final String testUserName = "Test Test Test Test Test Test User Name";
    public static UserBean login;
    
    public UserDAOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws DBException {
        // Prepare user login with full rights first
        login = new UserBean();
        GroupBean testGroup = new GroupBean();
        GroupAccessRightBean testRight = new GroupAccessRightBean();
        
        testRight.setDatabaseID(BaseDAO.DATABASE_ID_USER);
        testRight.setFields("*");
        testRight.setTables("*");
        testRight.setSources("*");
        testRight.setRead(true);
        testRight.setWrite(true);
        
        testGroup.addAccessRight(testRight);
        login.addGroup(testGroup);
        
        // then check, if a user with the user name as defined above exists,
        // if so, delete it first!
        
        List<UserBean> allUsers = UserDAO.loadAll(login, null);
        for(UserBean user : allUsers) {
            if(user.getUserName().equals(testUserName)) {
                UserDAO.delete(login, user);
                break;
            }
        }
    }
    
    /**
     * Test the load method which should return a single user entry from 
     * tblUsers identified by the user ID.
     * 
     * @throws DBException 
     */
    @Test
    public void testLoad() throws DBException {
        UserBean user = UserDAO.load(login, 1, null);
        
        // TODO: Do something with this!?
    }
    
    /**
     * Test the insert, update and delete methods which should insert, update
     * or delete an entry into the table tblUsers.
     * 
     * @throws DBException 
     */
    @Test
    public void testInsertUpdateDelete() throws DBException {
        // Set up a user bean first
        UserBean user = new UserBean();
        
        user.setAddress("Address");
        user.setConfirmed(false);
        user.setCreationDate(new java.util.Date());
        user.setDeleted(false);
        user.setDeletionDate(null);
        user.setExternalID("External ID");
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        user.setPassword("Password");
        user.setUserName(testUserName);
        user.setEmail("Email");

        // First test, INSERT
        UserDAO.insert(login, user);
        assertNotEquals("User ID should be different from 0 after insert", user.getUserID(), 0);
        
        // Second test, UPDATE
        // Load the demo user bean first
        user = UserDAO.load(login, user.getUserID(), null);
        assertNotNull("User Bean should not be null, as user should exist!", user);
        user.setHashCode("Hash Code");
        UserDAO.update(login, user);
        
        // and load again
        UserBean reload = UserDAO.load(login, user.getUserID(), null);
        assertEquals("User ID should be equal", user.getUserID(), reload.getUserID());
        assertEquals("Hash Code should be equal", user.getHashCode(), reload.getHashCode());
        
        // Third test, DELETE
        // Load the demo user bean first
        user = UserDAO.load(login, user.getUserID(), null);
        assertNotNull("User Bean should not be null, as user should exist!", user);
        
        // Now try to delete the user
        UserDAO.delete(login, user);
        
        // and load again
        reload = UserDAO.load(login, user.getUserID(), null);
        assertNull("User should not exist anymore, but data is returned", reload);
    }
}
