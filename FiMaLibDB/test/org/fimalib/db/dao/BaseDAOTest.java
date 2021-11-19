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

import java.util.Date;
import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit test tests the functionality of the class
 * 
 * org.fimalib.db.dao.BaseDAO
 * 
 * @author peter
 */
public class BaseDAOTest {

    /**
     * Test the isLike method which is supposed to check for occurrence in a
     * delimited list with the additional support of asterisk comparison.
     */
    @Test
    public void testIsLike() {
        assertTrue("Incorrect return for asterisk value in isLike", BaseDAO.isLike("*", "tblUsers"));
        assertTrue("Incorrect return for delimited list in isLike", BaseDAO.isLike("fldA,fldB,fldC,fldD", "fldB"));
        assertFalse("Incorrect return for delimited list despite not found in isLike", BaseDAO.isLike("fldA,fldB,fldC,fldD", "fldF"));
        assertTrue("Incorrect return for null value in isLike", BaseDAO.isLike("*", null));
    }
    
    /**
     * Test the checkAccessRights method which is supposed to check if a user
     * has access rights to a certain table/field/source combination.
     */
    @Test
    public void testCheckAccessRights() {
        // Set up a user with some rights, first!
        UserBean testUser = new UserBean();
        GroupBean testGroup = new GroupBean();
        GroupAccessRightBean testRight = new GroupAccessRightBean();
        
        testRight.setDatabaseID(BaseDAO.DATABASE_ID_STATIC);
        testRight.setFields("*");
        testRight.setTables("tblTest");
        testRight.setSources("*");
        testRight.setRead(true);
        testRight.setWrite(true);
        
        testGroup.addAccessRight(testRight);
        testUser.addGroup(testGroup);
        
        assertTrue("Incorrect return for access rights", BaseDAO.checkAccessRights(testUser, BaseDAO.DATABASE_ID_STATIC, "tblTest", "fldID", null, false, true));
        assertTrue("Incorrect return for access rights", BaseDAO.checkAccessRights(testUser, BaseDAO.DATABASE_ID_STATIC, "tblTest", null, null, false, true));
        assertTrue("Incorrect return for access rights", BaseDAO.checkAccessRights(testUser, BaseDAO.DATABASE_ID_STATIC, "tblTest", "fldTest", "1", true, true));
        assertFalse("Incorrect return for access rights", BaseDAO.checkAccessRights(testUser, BaseDAO.DATABASE_ID_STATIC, "tblFail", "fldID", null, false, true));
        assertFalse("Incorrect return for access rights", BaseDAO.checkAccessRights(testUser, BaseDAO.DATABASE_ID_USER, "tblTest", "fldTest", "1", true, true));
    }
    
    /**
     * Test the getSelectString method which returns a select statement for a
     * given table/field name list combination.
     */
    @Test
    public void testGetSelectString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        Date asOfDate = new Date();
        String expectedResult = "SELECT pkTestID, fldTestName, fldTestDate FROM tblTestTable WHERE pkTestID = ?";
        String expectedResultAsOfDate = "SELECT  pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, pkTestID, fldTestName, fldTestDate FROM tblTestTable_hist WHERE (fldHistNextDate IS NULL OR fldHistNextDate > ?) AND (pkHistUpdateDate <= ?) AND pkTestID = ?";
        
        assertEquals("Incorrect return from getSelectString", BaseDAO.getSelectString(tableName, fields, null, false), expectedResult);
        assertEquals("Incorrect return from getSelectString", BaseDAO.getSelectString(tableName, fields, asOfDate, false), expectedResultAsOfDate);
    }
    
    /**
     * Test the getInsertString method which returns an insert statement for a
     * given table/field name list/autonumber list combination.
     */
    @Test
    public void testGetInsertString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        boolean[] autoNumber = new boolean[] { true, false, false };
        String expectedResult = "INSERT INTO tblTestTable (fldTestName, fldTestDate) VALUES (?, ?)";
        String expectedResultAllFields = "INSERT INTO tblTestTable (pkTestID, fldTestName, fldTestDate) VALUES (?, ?, ?)";
        
        assertEquals("Incorrect return from getInsertString", BaseDAO.getInsertString(tableName, fields, autoNumber, false), expectedResult);
        assertEquals("Incorrect return from getInsertString", BaseDAO.getInsertString(tableName, fields, autoNumber, true), expectedResultAllFields);
    }
    
    /**
     * Test the getUpdateString method which returns an update statement for a
     * given table/field name list combination.
     */
    @Test
    public void testGetUpdateString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        String expectedResult = "UPDATE tblTestTable SET fldTestName = ?, fldTestDate = ? WHERE pkTestID = ?";
        
        assertEquals("Incorrect return from getUpdateString", BaseDAO.getUpdateString(tableName, fields), expectedResult);
    }
    
    /**
     * Test the getDeleteString method which returns a delete statement for a
     * given table/field name list combination.
     */
    @Test
    public void testGetDeleteString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        String expectedResult = "DELETE FROM tblTestTable WHERE pkTestID = ?";
        
        assertEquals("Incorrect return from getDeleteString", BaseDAO.getDeleteString(tableName, fields), expectedResult);
    }
    
    /**
     * Test the getHistUpdatePreviousString method which returns an update
     * statement for a given table/field name list combination.
     */
    @Test
    public void testGetHistUpdatePreviousString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        String expectedResult = "UPDATE tblTestTable_hist SET fldHistNextDate = CURRENT_TIMESTAMP(6) WHERE fldHistNextDate IS NULL AND pkTestID = ?";
        
        assertEquals("Incorrect return from getHistUpdatePreviousString", BaseDAO.getHistUpdatePreviousString(tableName, fields), expectedResult);
        
    }
    
    /**
     * Test the getHistInsertString method which returns an insert statement
     * for a given table/fieldname list combination.
     */
    @Test
    public void testGetHistInsertString() {
        String tableName = "tblTestTable";
        String[] fields = new String[] {"pkTestID", "fldTestName", "fldTestDate"};
        String expectedResult = "INSERT INTO tblTestTable_hist (pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, pkTestID, fldTestName, fldTestDate) VALUES (CURRENT_TIMESTAMP(6), NULL, ?, ?, ?, ?, ?)";
        String expectedResult3 = "INSERT INTO tblTestTable_hist (pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, pkTestID, fldTestName, fldTestDate) VALUES (CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), ?, ?, ?, ?, ?)";
        
        assertEquals("Incorrect return from getHistInsertString", BaseDAO.getHistInsertString(tableName, fields, 1), expectedResult);
        assertEquals("Incorrect return from getHistInsertString", BaseDAO.getHistInsertString(tableName, fields, 3), expectedResult3);
    }
    
    /**
     * Test the fillBeanFromSelect method which should fill any bean class from
     * the returns of a select statement.
     */
    @Test
    public void testFillBeanFromSelect() {
        // TODO: Code this using some mock objects!
    }
    
    /**
     * Test the fillUpdateStatement method which should fill a prepared sql
     * update statement with the correct values from a bean object.
     */
    @Test
    public void testFillUpdateStatement() {
        // TODO: Code this using some mock objects!
    }
    
    /**
     * Test the fillInsertStatement method which should fill a prepared sql
     * insert statement with the correct values from a bean object.
     */
    @Test
    public void testFillInsertStatement() {
        // TODO: Code this using some mock objects!
    }
    
    /**
     * Test the fillDeleteStatement method which should fill a prepared sql
     * delete statement which the correct values from a bean object.
     */
    @Test
    public void testFillDeleteStatement() {
        // TODO: Code this using some mock objects!
    }
    
    /**
     * Test the fillHistInsertStatement method which should fill a prepared
     * sql insert statement into the related audit-safe "historization" table
     * with the correct values from a bean object.
     */
    @Test
    public void testFillHistInsertStatement() {
        // TODO: Code this using some mock objects!
    }
}
