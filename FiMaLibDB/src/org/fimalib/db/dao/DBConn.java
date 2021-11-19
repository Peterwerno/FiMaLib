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

import com.sun.media.jfxmedia.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This class is used to handle the database connections
 * 
 * @author peter
 */
public class DBConn {
    public static final int CONFIG_FILE_IN_JAR          = 1;
    public static final int CONFIG_FILE_IN_FILESYSTEM   = 2;
    
    public static String configFileLocation = "/org/fimalib/db/dao/dbconnections.properties";
    public static int configFileOffset = CONFIG_FILE_IN_JAR;
    
    static String dbDriver = "com.mysql.jdbc.Driver";
    static String dbUserURL = "jdbc:mysql://localhost:8889/fimalib_user";
    static String dbStaticURL = "jdbc:mysql://localhost:8889/fimalib_static";
    static String dbDataURL = "jdbc:mysql://localhost:8889/fimalib_data";
    static String userName = "root"; 
    static String password = "root"; 
    static String salt = "FiMaLibGenericSalt";
    static String mailhost = "localhost";
    static String mailport = "25";
    static boolean maillogin = false;
    static String mailuser = null;
    static String mailpassword = null;
    
    /**
     * Initialize the static variables from properties file
     */
    static {
        loadConfigurationData();
    }
    
    public static void loadConfigurationData() {
        // Load properties file
        // TODO: Add reading a environment variable which could point to the config file.
        Properties props = new Properties();
        try {
            InputStream is;
            
            switch (configFileOffset) {
                case CONFIG_FILE_IN_JAR:
                    is = DBConn.class.getResourceAsStream(configFileLocation);
                    break;
                    
                case CONFIG_FILE_IN_FILESYSTEM:
                    is = new FileInputStream(configFileLocation);
                    break;
                default:
                    throw new AssertionError();
            }
            
            
            props.load(is);
            is.close();
        }
        catch (IOException ex) {
            Logger.logMsg(Logger.ERROR, "Error loading configuration file");
        }
        
        // Find right environment
        StringTokenizer st = new StringTokenizer(props.getProperty("environments"), ",");
        
        while(st.hasMoreElements()) {
            String env = st.nextToken();
            String user = props.getProperty(env + ".user.name");
            String thisUser = System.getProperties().getProperty("user.name");
            
            if(user.equals(thisUser)) {
                dbDriver = props.getProperty(env + ".db.driver");
                dbUserURL = props.getProperty(env + ".db.userurl");
                dbStaticURL = props.getProperty(env + ".db.staticurl");
                dbDataURL = props.getProperty(env + ".db.dataurl");
                userName = props.getProperty(env + ".db.user");
                password = props.getProperty(env + ".db.password");
                salt = props.getProperty(env + ".db.salt");
                mailhost = props.getProperty(env + ".mail.host");
                mailport = props.getProperty(env + ".mail.post");
                boolean isMailLogin = "true".equals(props.getProperty(env + ".mail.login"));
                maillogin = isMailLogin;
                if(isMailLogin) {
                    mailuser = props.getProperty(env + ".mail.user");
                    mailpassword = props.getProperty(env + ".mail.password");
                }
                
                break;
            }
        }
    }
    
    public static String getDBUserUrl() {
        return dbUserURL;
    }
    
    public static String getDBStaticUrl() {
        return dbStaticURL;
    }
    
    public static String getDBDataUrl() {
        return dbDataURL;
    }
    
    public static String getSalt() {
        return salt;
    }
    
    public static String getMailHost() {
        return mailhost;
    }
    
    public static String getMailPort() {
        return mailport;
    }
    
    public static boolean isMailLogin() {
        return maillogin;
    }
    
    public static String getMailUser() {
        if((mailuser == null) || ("".equals(mailuser)))
            return null;
        return mailuser;
    }
    
    public static String getMailPassword() {
        if((mailpassword == null) || ("".equals(mailpassword)))
            return null;
        return mailpassword;
    }
    
    /**
     * Returns a new database connection into the user database
     * 
     * @return the new connection (Connection)
     * @throws SQLException
     */
    public static Connection getUserConnection() throws SQLException {
        Connection con = null;
        
        try {
            Class.forName(dbDriver);
            con = DriverManager.getConnection(dbUserURL, userName, password);
        }
        catch (ClassNotFoundException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error getting DB connection to User DB " + ex.getMessage());
            throw new SQLException(ex);
        }
        
        return con;
    }
    
    /**
     * Returns a new database connection into the static database
     * 
     * @return the new connection (Connection)
     * @throws SQLException
     */
    public static Connection getStaticConnection() throws SQLException {
        Connection con = null;
        
        try {
            Class.forName(dbDriver);
            con = DriverManager.getConnection(dbStaticURL, userName, password);
        }
        catch (ClassNotFoundException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error getting DB connection to Static DB " + ex.getMessage());
            throw new SQLException(ex);
        }
        
        return con;
    }
    
    /**
     * Returns a new database connection into the data database
     * 
     * @return the new connection (Connection)
     * @throws SQLException
     */
    public static Connection getDataConnection() throws SQLException {
        Connection con = null;
        
        try {
            Class.forName(dbDriver);
            con = DriverManager.getConnection(dbDataURL, userName, password);
        }
        catch (ClassNotFoundException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error getting DB connection to Data DB " + ex.getMessage());
            throw new SQLException(ex);
        }
        
        return con;
    }
    
    /**
     * Closes a db connection
     * 
     * @param con (Connection) the db connection that should be closed
     * @throws SQLException
     */
    public static void closeConnection(Connection con) throws SQLException {
        con.close();
    }
}
