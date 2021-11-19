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
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;
import org.fimalib.db.beans.UserGroupBean;

/**
 * This DAO class is used to handle user data
 * 
 * @author peter
 */
public class UserDAO extends BaseDAO {
    private static final String tableName = "tblUsers";
    private static final String[] fieldNames = {"pkUserID", "fldExternalID", "fldUserName", "fldPassword", "fldHashCode", "fldFirstName", "fldLastName", "fldAddress", "fldEmail", "fldCreationDate", "fldConfirmed", "fldDeleted", "fldDeletionDate" };
    private static final int[] fieldTypes = { Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.BOOLEAN, Types.BOOLEAN, Types.DATE };
    private static final boolean[] isNullable = { false, true, true, true, true, true, true, true, true, true, true, true, true };
    private static final boolean[] isAutoNumber = { true, false, false, false, false, false, false, false, false, false, false, false, false };
    
    /**
     * Returns a single user entry from the users table
     * 
     * @param login (UserBean) the user that requests the data
     * @param userID (int) the user ID of the user that should be loaded
     * @param asOfDate (Date) the backward looking date (see design document)
     * @return the user's data (UserBean)
     * @throws DBException if there was a problem
     */
    public static UserBean load(UserBean login, int userID, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        
        UserBean retVal = new UserBean();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            PreparedStatement ps = con.prepareStatement(getSelectString(tableName, fieldNames, asOfDate, false));
            
            if(asOfDate != null) {
                ps.setDate(1, new java.sql.Date(asOfDate.getTime()));
                ps.setDate(2, new java.sql.Date(asOfDate.getTime()));
                ps.setInt(3, userID);
            }
            else {
                ps.setInt(1, userID);
            }
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                fillBeanFromSelect(retVal, fieldNames, fieldTypes, rs, asOfDate != null);
                return retVal;
            }
            
            return null;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserDAO.load " + ex.getMessage());
            throw new DBException("Cannot load User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error when closing connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
    }
    
    /**
     * Returns all entries from the users table
     * 
     * @param login (UserBean) the user that requests the data
     * @param asOfDate (Date) the backward looking date (see design document)
     * @return the user's data (UserBean)
     * @throws DBException if there was a problem
     */
    public static List<UserBean> loadAll(UserBean login, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        
        ArrayList<UserBean> retVal = new ArrayList<>();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            PreparedStatement ps = con.prepareStatement(getSelectString(tableName, fieldNames, asOfDate, true));
            
            if(asOfDate != null) {
                ps.setDate(1, new java.sql.Date(asOfDate.getTime()));
                ps.setDate(2, new java.sql.Date(asOfDate.getTime()));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserBean userBean = new UserBean();
                fillBeanFromSelect(userBean, fieldNames, fieldTypes, rs, asOfDate != null);
                retVal.add(userBean);
            }
                
            return retVal;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserDAO.loadAll " + ex.getMessage());
            throw new DBException("Cannot load User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error when closing connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
    }
    
    /**
     * Update an entry in the users table
     * 
     * @param login (UserBean) the login under which the update is done
     * @param user (UserBean) the user to be updated
     * @throws DBException if there was an error
     */
    public static void update(UserBean login, UserBean user) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getUpdateString(tableName, fieldNames));
            
            fillUpdateStatement(user, fieldNames, fieldTypes, isNullable, ps1);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(user, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_UPDATE));
            fillHistInsertStatement(login, user, BaseDAO.UPDATE_TYPE_UPDATE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserDAO.update " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot update User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error when closing connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
    }
    
    /**
     * Inserts a new entry into the users table
     * 
     * @param login (UserBean) the login under which to add
     * @param user (UserBean) the user to add
     * @throws DBException if there was an error
     */
    public static void insert(UserBean login, UserBean user) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getInsertString(tableName, fieldNames, isAutoNumber, false), Statement.RETURN_GENERATED_KEYS);
            
            fillInsertStatement(user, fieldNames, fieldTypes, isNullable, isAutoNumber, ps1, false);
            ps1.execute();
            ResultSet rs = ps1.getGeneratedKeys();
            if(rs.next()) {
                user.setUserID(rs.getInt(1));
            }
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(user, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_INSERT));
            fillHistInsertStatement(login, user, BaseDAO.UPDATE_TYPE_INSERT, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserDAO.insert " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot insert User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error when closing connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
    }
    
    /**
     * Deletes a user from the user table. This *Should* not happen, as users
     * should be deleted logically only, but well...
     * 
     * @param login (UserBean) the login under which to delete
     * @param user (UserBean) the user to delete
     * @throws DBException if there was a problem
     */
    public static void delete(UserBean login, UserBean user) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            
            // Before deleting, also delete the related user group assignments
            List<GroupBean> allUserGroups = UserGroupDAO.loadUserGroups(login, user.getUserID(), null);
            for(GroupBean group : allUserGroups) {
                UserGroupBean deleteUserGroup = new UserGroupBean();
                deleteUserGroup.setUserID(user.getUserID());
                deleteUserGroup.setGroupID(group.getGroupID());
                
                UserGroupDAO.delete(login, deleteUserGroup);
            }
            
            // Now delete the user
            PreparedStatement ps1 = con.prepareStatement(getDeleteString(tableName, fieldNames));
            fillDeleteStatement(user, fieldNames, fieldTypes, ps1);
            ps1.execute();
            
            // Then update the shadow table
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(user, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_DELETE));
            fillHistInsertStatement(login, user, BaseDAO.UPDATE_TYPE_DELETE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserDAO.delete " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot Delete User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error when closing connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
    }
}
