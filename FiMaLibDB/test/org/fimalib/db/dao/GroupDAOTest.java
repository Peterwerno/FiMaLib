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
package org.fimalib.db.dao;

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
public class GroupDAOTest {
    public static final String testGroupName = "Test Test Test Test Test Test Group Name";
    public static final String testGroupReName = "Test Test Test Test Test Test Group ReName";
    public static UserBean login;
    
    public GroupDAOTest() {
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
        
        List<GroupBean> allGroups = GroupDAO.loadAll(login, null);
        for(GroupBean group : allGroups) {
            if(group.getGroupName().equals(testGroupName)) {
                GroupDAO.delete(login, group);
            }
        }
    }
    
    /**
     * Test the load method which should return a single user entry from 
     * tblGroups identified by the group ID.
     * 
     * @throws DBException 
     */
    @Test
    public void testLoad() throws DBException {
        GroupBean group = GroupDAO.load(login, 1, null);
        
        // TODO: Do something with this!?
    }
    
    /**
     * Test the insert, update and delete methods which should insert, update
     * or delete an entry into the table tblGroups.
     * 
     * @throws DBException 
     */
    @Test
    public void testInsertUpdateDelete() throws DBException {
        // Set up a user bean first
        GroupBean group = new GroupBean();
        
        group.setGroupName(testGroupName);
        
        // First test, INSERT
        GroupDAO.insert(login, group);
        assertNotEquals("Group ID should be different from 0 after insert", group.getGroupID(), 0);
        
        // Second test, UPDATE
        // Load the demo group bean first
        group = GroupDAO.load(login, group.getGroupID(), null);
        assertNotNull("Group Bean should not be null, as group should exist!", group);
        group.setGroupName(testGroupReName);
        GroupDAO.update(login, group);
        
        // and load again
        GroupBean reload = GroupDAO.load(login, group.getGroupID(), null);
        assertEquals("Group ID should be equal", group.getGroupID(), reload.getGroupID());
        assertEquals("Group Name should be equal", group.getGroupName(), reload.getGroupName());
        
        // Third test, DELETE
        // Load the demo group bean first
        group = GroupDAO.load(login, group.getGroupID(), null);
        assertNotNull("Group Bean should not be null, as group should exist!", group);
        
        // Now try to delete the group
        GroupDAO.delete(login, group);
        
        // and load again
        reload = GroupDAO.load(login, group.getGroupID(), null);
        assertNull("Group should not exist anymore, but data is returned", reload);
    }
}
