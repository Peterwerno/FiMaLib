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
import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;
import org.fimalib.db.beans.UserGroupBean;

/**
 *
 * @author peter
 */
public class GroupDAO extends BaseDAO {
    private static final String tableName = "tblGroups";
    private static final String[] fieldNames = {"pkGroupID", "fldGroupName" };
    private static final int[] fieldTypes = { Types.INTEGER, Types.VARCHAR };
    private static final boolean[] isNullable = { false, false };
    private static final boolean[] isAutoNumber = { true, false };
    
    /**
     * Returns a single group entry from the table tblGroups
     * 
     * @param login (UserBean) the user that requests the data
     * @param groupID (int) the group ID of the group that should be loaded
     * @param asOfDate (Date) the backward looking date (see design document)
     * @return the Group's data (GroupBean)
     * @throws DBException if there was a problem
     */
    public static GroupBean load(UserBean login, int groupID, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        
        GroupBean retVal = new GroupBean();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            PreparedStatement ps = con.prepareStatement(getSelectString(tableName, fieldNames, asOfDate, false));
            
            if(asOfDate != null) {
                ps.setDate(1, new java.sql.Date(asOfDate.getTime()));
                ps.setDate(2, new java.sql.Date(asOfDate.getTime()));
                ps.setInt(3, groupID);
            }
            else {
                ps.setInt(1, groupID);
            }
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                fillBeanFromSelect(retVal, fieldNames, fieldTypes, rs, asOfDate != null);
                return retVal;
            }
            
            return null;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupDAO.load " + ex.getMessage());
            throw new DBException("Cannot load Group", ex);
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
     * Returns all entries from the table tblGroups
     * 
     * @param login (UserBean) the user that requests the data
     * @param asOfDate (Date) the backward looking date (see design document)
     * @return the group's data (List)
     * @throws DBException if there was a problem
     */
    public static List<GroupBean> loadAll(UserBean login, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        
        ArrayList<GroupBean> retVal = new ArrayList<>();
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
                GroupBean groupBean = new GroupBean();
                fillBeanFromSelect(groupBean, fieldNames, fieldTypes, rs, asOfDate != null);
                retVal.add(groupBean);
            }
                
            return retVal;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupDAO.loadAll " + ex.getMessage());
            throw new DBException("Cannot load Group", ex);
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
     * Update an entry in the table tblGroups
     * 
     * @param login (UserBean) the login under which the update is done
     * @param group (GroupBean) the group to be updated
     * @throws DBException if there was an error
     */
    public static void update(UserBean login, GroupBean group) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getUpdateString(tableName, fieldNames));
            
            fillUpdateStatement(group, fieldNames, fieldTypes, isNullable, ps1);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(group, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_UPDATE));
            fillHistInsertStatement(login, group, BaseDAO.UPDATE_TYPE_UPDATE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupDAO.update " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot update Group", ex);
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
     * Inserts a new entry into the table tblGroups
     * 
     * @param login (UserBean) the login under which to add
     * @param group (GroupBean) the group to add
     * @throws DBException if there was an error
     */
    public static void insert(UserBean login, GroupBean group) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getInsertString(tableName, fieldNames, isAutoNumber, false), Statement.RETURN_GENERATED_KEYS);
            
            fillInsertStatement(group, fieldNames, fieldTypes, isNullable, isAutoNumber, ps1, false);
            ps1.execute();
            ResultSet rs = ps1.getGeneratedKeys();
            if(rs.next()) {
                group.setGroupID(rs.getInt(1));
            }
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(group, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            String cmd = getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_INSERT);
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_INSERT));
            fillHistInsertStatement(login, group, BaseDAO.UPDATE_TYPE_INSERT, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupDAO.insert " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot insert Group", ex);
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
     * Deletes a group from the table tblGroups. This *Should* not happen, as
     * users should be deleted logically only, but well...
     * 
     * @param login (UserBean) the login under which to delete
     * @param group (GroupBean) the group to delete
     * @throws DBException if there was a problem
     */
    public static void delete(UserBean login, GroupBean group) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            
            // First, delete related group access rights
            List<GroupAccessRightBean> allRights = GroupAccessRightDAO.loadGroupAccessRights(login, group.getGroupID(), null);
            for(GroupAccessRightBean garb : allRights) {
                GroupAccessRightDAO.delete(login, garb);
            }
            
            // Second, delete related user group assignments
            List<UserGroupBean> allGroups = UserGroupDAO.loadGroupUsers(login, group.getGroupID());
            for(UserGroupBean ugb : allGroups) {
                UserGroupDAO.delete(login, ugb);
            }
            
            // Then delete the data in the main table
            PreparedStatement ps1 = con.prepareStatement(getDeleteString(tableName, fieldNames));
            fillDeleteStatement(group, fieldNames, fieldTypes, ps1);
            ps1.execute();
            
            // Finally adjust the shadow table
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(group, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_DELETE));
            fillHistInsertStatement(login, group, BaseDAO.UPDATE_TYPE_DELETE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupDAO.delete " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot Delete Group", ex);
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
