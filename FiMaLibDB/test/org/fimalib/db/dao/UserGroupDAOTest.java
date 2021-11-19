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

import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;
import static org.fimalib.db.dao.UserDAOTest.login;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peter
 */
public class UserGroupDAOTest {
    
    public UserGroupDAOTest() {
    }
    
    @Before
    public void setUp() {
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
     * Test the insert and delete methods.
     */
    @Test
    public void testInsertDelete() {
        // TODO: Code this
    }
}
