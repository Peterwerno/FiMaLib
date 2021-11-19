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
import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit class tests the functionality of the class
 * 
 * org.fimalib.db.dao.GroupAccessRightDAO
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
public class GroupAccessRightDAOTest {
    public static final String testGroupName = "Test Test Test Test Test Test Group Name Rights";
    public static UserBean login;
    
    public GroupAccessRightDAOTest() {
    }
    
    /**
     * Initialize the login user
     * 
     * @throws DBException 
     */
    @Before
    public void setUp() throws DBException {
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
    }
    
    /**
     * Compare two access rights bean instances for equal contents
     * @param bean1
     * @param bean2
     * @return whether the two contain the same values
     */
    protected boolean beanEquals(GroupAccessRightBean bean1, GroupAccessRightBean bean2) {
        if(bean1.getDatabaseID() != bean2.getDatabaseID()) return false;
        if(!bean1.getFields().equals(bean2.getFields())) return false;
        if(bean1.getGroupID() != bean2.getGroupID()) return false;
        if(bean1.getRightsID() != bean2.getRightsID()) return false;
        if(!bean1.getSources().equals(bean2.getSources())) return false;
        return bean1.getTables().equals(bean2.getTables());
    }
    
    /**
     * Test the store method and then the load method
     * 
     * @throws DBException 
     */
    @Test
    public void testStoreAndLoad() throws DBException {
        // Set up a user bean first
        GroupBean group = new GroupBean();
        
        group.setGroupName(testGroupName);
        
        // First test, INSERT
        GroupDAO.insert(login, group);
        assertNotEquals("Group ID should be different from 0 after insert", group.getGroupID(), 0);
        
        // Then add the first access rights
        GroupAccessRightBean garb1 = new GroupAccessRightBean();
        garb1.setGroupID(group.getGroupID());
        garb1.setDatabaseID(BaseDAO.DATABASE_ID_STATIC);
        garb1.setTables("*");
        garb1.setFields("*");
        garb1.setSources("*");
        garb1.setRead(true);
        garb1.setWrite(false);
        
        group.addAccessRight(garb1);
        
        // and add another access right
        GroupAccessRightBean garb2 = new GroupAccessRightBean();
        garb2.setGroupID(group.getGroupID());
        garb2.setDatabaseID(BaseDAO.DATABASE_ID_DATA);
        garb2.setTables("*");
        garb2.setFields("*");
        garb2.setSources("public");
        garb2.setRead(true);
        garb2.setWrite(false);
        
        group.addAccessRight(garb2);
        
        // Now save the rights
        GroupAccessRightDAO.storeGroupAccessRights(login, group);
        
        // Created correctly?
        assertNotEquals("Rights ID should not be 0 for the first item!", garb1.getRightsID(), 0);
        assertNotEquals("Rights ID should not be 0 for the second item!", garb2.getRightsID(), 0);
        
        // Now test the load method
        List<GroupAccessRightBean> allRights = GroupAccessRightDAO.loadGroupAccessRights(login, group.getGroupID(), null);
        assertEquals("There should be two rights entries!", allRights.size(), 2);
        for(GroupAccessRightBean garb : allRights) {
            assertTrue("The contents of the rights bean differ!", beanEquals(garb, garb1) || beanEquals(garb, garb2));
        }
        
        // Finally, delete:
        GroupDAO.delete(login, group);
        
        // And check, if the access rights have been deleted, too!
        List<GroupAccessRightBean> allRightsAfterDelete = GroupAccessRightDAO.loadGroupAccessRights(login, group.getGroupID(), null);
        assertEquals("Rights were not deleted correctly!", allRightsAfterDelete.size(), 0);
    }
}
