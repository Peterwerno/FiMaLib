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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserGroupBean;
import org.fimalib.db.beans.UserBean;

/**
 * This DAO class is managing the data in the table tblUserGroups.
 * 
 * @author peter
 */
public class UserGroupDAO extends BaseDAO {
    private static final String tableName = "tblUserGroups";
    private static final String[] fieldNames = {"pkfkUserID", "pkfkGroupID" };
    private static final int[] fieldTypes = { Types.INTEGER, Types.INTEGER };
    private static final boolean[] isNullable = { false, false };
    private static final boolean[] isAutoNumber = { false, false };
    
    /**
     * Return the list of all groups that a user belongs to, including loading
     * the access rights coming with the various groups.
     * 
     * @param login (UserBean) the user under which's login to run.
     * @param userID (int) the user ID for which to load the data
     * @param asOfDate
     * @return
     * @throws DBException 
     */
    public static List<GroupBean> loadUserGroups(UserBean login, int userID, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false) && (login.getUserID() != userID)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        ArrayList<GroupBean> retVal = new ArrayList<>();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            String selectString = "SELECT pkfkUserID, pkfkGroupID FROM tblUserGroups WHERE pkfkUserID = ?";
            if(asOfDate != null) {
                selectString = "SELECT pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, pkfkUserID, pkfkGroupID WHERE (fldHistNextDate IS NULL OR (fldHistNextDate > ?)) AND (pkHistUpdateDate <= ?) AND pkfkUserID = ?";
            }
            
            PreparedStatement ps = con.prepareStatement(selectString);
            
            if(asOfDate != null) {
                ps.setDate(1, new java.sql.Date(asOfDate.getTime()));
                ps.setDate(2, new java.sql.Date(asOfDate.getTime()));
                ps.setInt(3, userID);
            }
            else {
                ps.setInt(1, userID);
            }
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                GroupBean group = GroupDAO.load(login, rs.getInt("pkfkGroupID"), asOfDate);
                group.setAccessRights(GroupAccessRightDAO.loadGroupAccessRights(login, group.getGroupID(), asOfDate));
                retVal.add(group);
            }
        }
        catch (SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserGroupDAO.loadUserGroups " + ex.getMessage());
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
        
        return retVal;
    }
    
    /**
     * Return the list of all users that a group belongs to.
     * This is required when a group is to be deleted!
     * 
     * @param login (UserBean) the user under which's login to run.
     * @param groupID (int) the grouop ID for which to load the data
     * @return the list
     * @throws DBException 
     */
    public static List<UserGroupBean> loadGroupUsers(UserBean login, int groupID) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        ArrayList<UserGroupBean> retVal = new ArrayList<>();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            String selectString = "SELECT pkfkUserID, pkfkGroupID FROM tblUserGroups WHERE pkfkGroupID = ?";
            PreparedStatement ps = con.prepareStatement(selectString);
            ps.setInt(1, groupID);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                UserGroupBean ugb = new UserGroupBean();
                fillBeanFromSelect(ugb, fieldNames, fieldTypes, rs, false);
                retVal.add(ugb);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserGroupDAO.loadUserGroups " + ex.getMessage());
            throw new DBException("Cannot load User Group combinations", ex);
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
        
        return retVal;
    }
    
    /**
     * Saves the full list of user group associations to the database
     * 
     * @param login (UserBean) the login under which the operation is done
     * @param user (UserBean) the user for which the groups will be written
     * @throws DBException if there was an error.
     */
    public void storeUserGroups(UserBean login, UserBean user) throws DBException {
        // First, find out if any group associations need to be deleted
        List<GroupBean> allGroups = loadUserGroups(login, user.getUserID(), null);
        for(GroupBean group : allGroups) {
            boolean found = false;
            for(GroupBean newGroup : user.getGroups()) {
                if(group.getGroupID() == newGroup.getGroupID()) {
                    found = true;
                }
            }
            
            if(!found) {
                UserGroupBean ugb = new UserGroupBean();
                ugb.setUserID(user.getUserID());
                ugb.setGroupID(group.getGroupID());
                
                delete(login, ugb);
            }
        }
        
        // Second, insert all new group associations
        allGroups = loadUserGroups(login, user.getUserID(), null);
        
        for(GroupBean newGroup : user.getGroups()) {
            boolean found = false;
            for(GroupBean oldGroup : allGroups) {
                if(oldGroup.getGroupID() == newGroup.getGroupID()) {
                    found = true;
                }
            }
            
            if(!found) {
                UserGroupBean ugb = new UserGroupBean();
                ugb.setUserID(user.getUserID());
                ugb.setGroupID(newGroup.getGroupID());
                
                insert(login, ugb);
            }
        }
    }
    
    /**
     * Inserts a new entry into the table tblUserGroups
     * 
     * @param login (UserBean) the login under which to add
     * @param userGroup (UserGroupBean) user/group combination
     * @throws DBException if there was an error
     */
    public static void insert(UserBean login, UserGroupBean userGroup) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getInsertString(tableName, fieldNames, isAutoNumber, false));
            fillInsertStatement(userGroup, fieldNames, fieldTypes, isNullable, isAutoNumber, ps1, false);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(userGroup, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_INSERT));
            fillHistInsertStatement(login, userGroup, BaseDAO.UPDATE_TYPE_INSERT, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserGroupDAO.insert " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot insert user group", ex);
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
     * Deletes a right from the table tblUserGroups.
     * 
     * @param login (UserBean) the login under which to delete
     * @param userGroup (UserGroupBean) the user/group to delete
     * @throws DBException if there was a problem
     */
    public static void delete(UserBean login, UserGroupBean userGroup) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getDeleteString(tableName, fieldNames));
            fillDeleteStatement(userGroup, fieldNames, fieldTypes, ps1);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(userGroup, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_DELETE));
            fillHistInsertStatement(login, userGroup, BaseDAO.UPDATE_TYPE_DELETE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in UserGroupDAO.delete " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot Delete User Group", ex);
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
