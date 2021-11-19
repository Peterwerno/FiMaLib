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

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit class tests the functionality of the class
 * 
 * org.fimalib.db.dao.DBConn
 * 
 * @author peter
 */
public class DBConnTest {
    
    /**
     * Test the getUserConnection method which returns a database JDBC 
     * connection into the user database
     * 
     * @throws SQLException if there was a problem
     */
    @Test
    public void testGetUserConnection() throws SQLException {
        
        Connection conn = DBConn.getUserConnection();
        
        assertNotNull("Connection returned is null", conn);
        
        DBConn.closeConnection(conn);
    }

    /**
     * Test the getStaticConnection method which returns a database JDBC 
     * connection into the static database
     * 
     * @throws SQLException if there was a problem
     */
    @Test
    public void testGetStaticConnection() throws SQLException {
        
        Connection conn = DBConn.getStaticConnection();
        
        assertNotNull("Connection returned is null", conn);
        
        DBConn.closeConnection(conn);
    }

    /**
     * Test the getDataConnection method which returns a database JDBC 
     * connection into the data database
     * 
     * @throws SQLException if there was a problem
     */
    @Test
    public void testGetDataConnection() throws SQLException {
        
        Connection conn = DBConn.getDataConnection();
        
        assertNotNull("Connection returned is null", conn);
        
        DBConn.closeConnection(conn);
    }
}
