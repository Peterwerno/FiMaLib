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
import static org.fimalib.db.dao.BaseDAO.DATABASE_ID_USER;
import static org.fimalib.db.dao.BaseDAO.getInsertString;

/**
 * This DAO class is managing the data in the table tblGroupAccessRights.
 * 
 * @author peter
 */
public class GroupAccessRightDAO extends BaseDAO {
    private static final String tableName = "tblGroupAccessRights";
    private static final String[] fieldNames = {"pkRightsID", "fkGroupID", "fldDatabaseID", "fldTables", "fldFields", "fldSources", "fldRead", "fldWrite" };
    private static final int[] fieldTypes = { Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN };
    private static final boolean[] isNullable = { false, false, false, true, true, true, false, false };
    private static final boolean[] isAutoNumber = { true, false, false, false, false, false, false, false };
    
    /**
     * Return the list of all groups that a user belongs to
     * 
     * @param login (UserBean) the user under which's login to load
     * @param groupID (int) the group ID
     * @param asOfDate (Date) 
     * @return the list of access rights
     * @throws DBException 
     */
    public static List<GroupAccessRightBean> loadGroupAccessRights(UserBean login, int groupID, Date asOfDate) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        ArrayList<GroupAccessRightBean> retVal = new ArrayList<>();
        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            String selectString = "SELECT pkRightsID, fkGroupID, fldDatabaseID, fldTables, fldFields, fldSources, fldRead, fldWrite FROM tblGroupAccessRights WHERE fkGroupID = ?";
            if(asOfDate != null) {
                selectString = "SELECT pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, pkRightsID, fkGroupID, fldDatabaseID, fldTables, fldFields, fldSources, fldRead, fldWrite WHERE (fldHistNextDate IS NULL OR (fldHistNextDate > ?)) AND (pkHistUpdateDate <= ?) AND fkGroupID = ?";
            }
            
            PreparedStatement ps = con.prepareStatement(selectString);
            
            if(asOfDate != null) {
                ps.setDate(1, new java.sql.Date(asOfDate.getTime()));
                ps.setDate(2, new java.sql.Date(asOfDate.getTime()));
                ps.setInt(3, groupID);
            }
            else {
                ps.setInt(1, groupID);
            }
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                GroupAccessRightBean garb = new GroupAccessRightBean();
                BaseDAO.fillBeanFromSelect(garb, fieldNames, fieldTypes, rs, asOfDate != null);
                retVal.add(garb);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error when loading User data " + ex.getMessage());
            throw new DBException("Cannot load User", ex);
        }
        finally {
            try {
                if(con != null) DBConn.closeConnection(con);
            }
            catch (SQLException ex) {
                Logger.logMsg(Logger.ERROR, "Error in closing the connection " + ex.getMessage());
                throw new DBException("Cannot close connection", ex);
            }
        }
        
        return retVal;
    }
    
    /**
     * This method checks, if two GroupAccessRightBean instances have differing
     * 
     * content or not
     * @param oldRight
     * @param newRight
     * @return whether the two beans are different (boolean)
     */
    private static boolean isDifferent(GroupAccessRightBean oldRight, GroupAccessRightBean newRight) {
        if(oldRight.getDatabaseID() != newRight.getDatabaseID()) return true;
        if(oldRight.getGroupID() != newRight.getGroupID()) return true;
        
        if(oldRight.getFields() == null) {
            if(newRight.getFields() != null) return true;
        }
        else {
            if(newRight.getFields() == null) return true;
            if(!newRight.getFields().equals(oldRight.getFields())) return true;
        }
        
        if(oldRight.getSources() == null) {
            if(newRight.getSources() != null) return true;
        }
        else {
            if(newRight.getSources() == null) return true;
            if(!newRight.getSources().equals(oldRight.getSources())) return true;
        }
        
        if(oldRight.getTables() == null) {
            if(newRight.getTables() != null) return true;
        }
        else {
            if(newRight.getTables() == null) return true;
            if(!newRight.getTables().equals(oldRight.getTables())) return true;
        }
        
        if(oldRight.isRead() != newRight.isRead()) return true;
        if(oldRight.isWrite() != newRight.isWrite()) return true;
        
        return false;
    }
    
    /**
     * Stores the whole set of group rights to the database
     * 
     * @param login (UserBean) the login under which to operate
     * @param group (GroupBean) the related group
     * @throws DBException if there was a problem
     */
    public static void storeGroupAccessRights(UserBean login, GroupBean group) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, true, false)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        
        // First, find all those that need to be deleted and update where necessary
        List<GroupAccessRightBean> existingRights = loadGroupAccessRights(login, group.getGroupID(), null);
        
        for(GroupAccessRightBean garbOld : existingRights) {
            boolean found = false;
            for(GroupAccessRightBean garbNew : group.getAccessRights()) {
                if(garbNew.getRightsID() == garbOld.getRightsID()) {
                    found = true;
                    if(isDifferent(garbOld, garbNew)) {
                        update(login, garbNew);
                    }
                }
            }
            
            if(!found) {
                delete(login, garbOld);
            }
        }
        
        // Then, insert all new ones
        for(GroupAccessRightBean garbNew : group.getAccessRights()) {
            if(garbNew.getRightsID() == 0) {
                insert(login, garbNew);
            }
        }
    }
    
    /**
     * Inserts a new entry into the table tblGroupAccessRights
     * 
     * @param login (UserBean) the login under which to add
     * @param right (GroupAccessRightBean) the right to add
     * @throws DBException if there was an error
     */
    public static void insert(UserBean login, GroupAccessRightBean right) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getInsertString(tableName, fieldNames, isAutoNumber, false), Statement.RETURN_GENERATED_KEYS);
            
            fillInsertStatement(right, fieldNames, fieldTypes, isNullable, isAutoNumber, ps1, false);
            ps1.execute();
            ResultSet rs = ps1.getGeneratedKeys();
            if(rs.next()) {
                right.setRightsID(rs.getInt(1));
            }
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(right, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_INSERT));
            fillHistInsertStatement(login, right, BaseDAO.UPDATE_TYPE_INSERT, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupAccessRightDAO.insert " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot insert Right", ex);
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
     * Update an entry in the table tblGroupAccessRights
     * 
     * @param login (UserBean) the login under which the update is done
     * @param right (GroupAccessRightBean) the user to be updated
     * @throws DBException if there was an error
     */
    public static void update(UserBean login, GroupAccessRightBean right) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);

        Connection con = null;
        
        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getUpdateString(tableName, fieldNames));
            
            fillUpdateStatement(right, fieldNames, fieldTypes, isNullable, ps1);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(right, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_UPDATE));
            fillHistInsertStatement(login, right, BaseDAO.UPDATE_TYPE_UPDATE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupAccessRightDAO.update " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot update Right", ex);
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
     * Deletes a right from the table tblGroupAccessRights.
     * 
     * @param login (UserBean) the login under which to delete
     * @param right (GroupAccessRightBean) the right to delete
     * @throws DBException if there was a problem
     */
    public static void delete(UserBean login, GroupAccessRightBean right) throws DBException {
        if(!BaseDAO.checkAccessRights(login, DATABASE_ID_USER, tableName, null, null, false, true)) throw new NotPermissionedException("User " + login.getUserName() + " not permissioned for " + tableName);
        Connection con = null;

        try {
            con = DBConn.getUserConnection();
            
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(getDeleteString(tableName, fieldNames));
            fillDeleteStatement(right, fieldNames, fieldTypes, ps1);
            ps1.execute();
            
            PreparedStatement ps2 = con.prepareStatement(getHistUpdatePreviousString(tableName, fieldNames));
            fillDeleteStatement(right, fieldNames, fieldTypes, ps2); // Delete fills the primary keys only!
            
            PreparedStatement ps3 = con.prepareStatement(getHistInsertString(tableName, fieldNames, BaseDAO.UPDATE_TYPE_DELETE));
            fillHistInsertStatement(login, right, BaseDAO.UPDATE_TYPE_DELETE, fieldNames, fieldTypes, isNullable, ps3);
            
            ps2.execute();
            ps3.execute();
            con.commit();
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | SQLException ex) {
            Logger.logMsg(Logger.ERROR, "Error in GroupAccessRightDAO.delete " + ex.getMessage());
            try {
                if(con != null)
                    con.rollback();
            }
            catch (SQLException exr) {
                Logger.logMsg(Logger.ERROR, "Error when rolling back transaction " + exr.getMessage());
            }
            throw new DBException("Cannot Delete Right", ex);
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
