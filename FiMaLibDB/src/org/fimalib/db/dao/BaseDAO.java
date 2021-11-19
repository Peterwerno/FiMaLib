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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.StringTokenizer;
import org.fimalib.db.beans.BaseBean;
import org.fimalib.db.beans.GroupAccessRightBean;
import org.fimalib.db.beans.GroupBean;
import org.fimalib.db.beans.UserBean;

/**
 * This class is the base class for all DAO classes.
 * It handles the special need for "audit safety", such that it stores auditing/
 * historization data which makes it possible for users to retrieve the
 * information as it has been before some user's changes
 * 
 * @author peter
 */
public class BaseDAO {
    public static final int DATABASE_ID_USER    = 1;
    public static final int DATABASE_ID_STATIC  = 2;
    public static final int DATABASE_ID_DATA    = 3;
    
    public static final int UPDATE_TYPE_INSERT  = 1;
    public static final int UPDATE_TYPE_UPDATE  = 2;
    public static final int UPDATE_TYPE_DELETE  = 3;
    
    /**
     * Check if a given permissions string contains the requested for string
     * (probably replace this with some more sophisticated handling?)
     * 
     * @param rightString (String) the permission string
     * @param requestFor (String) the requested item (null = "*" required)
     * @return whether permission is granted (boolean)
     */
    protected static boolean isLike(String rightString, String requestFor) {
        if((rightString == null) || rightString.equals("*")) return true;
        if(requestFor == null) return false;
        StringTokenizer st = new StringTokenizer(rightString, ",");
        
        while(st.hasMoreElements()) {
            String table = st.nextToken();
            
            if(table.equalsIgnoreCase(requestFor)) return true;
        }
        
        return false;
    }
    
    /**
     * Check if a given user has access to a certain table/field/...)
     * 
     * @param user
     * @param databaseID
     * @param table
     * @param fields
     * @param sources
     * @param readRights
     * @param writeRights
     * @return 
     */
    protected static boolean checkAccessRights(UserBean user, int databaseID, String table, String fields, String sources, boolean readRights, boolean writeRights) {
        
        for(GroupBean group : user.getGroups()) {
            for(GroupAccessRightBean gab : group.getAccessRights()) {
                if(gab.getDatabaseID() == databaseID) {
                    if(isLike(gab.getTables(), table)) {
                        if(isLike(gab.getFields(), fields)) {
                            if(isLike(gab.getSources(), sources)) {
                                if(((readRights && gab.isRead()) || !readRights) &&
                                   ((writeRights && gab.isWrite()) || !writeRights)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns a SQL string that runs a simple select query into a table
     * 
     * @param tableName (String) the table name
     * @param fieldNames (String[]) the list of all field names
     * @param asOfDate (Date) the historic state of the table (see documentation)
     * @param allEntries (boolean) wether or not to retrieve all or just one entry/ies
     * @return the SQL command string
     */
    protected static String getSelectString(String tableName, String[] fieldNames, Date asOfDate, boolean allEntries) {
        StringBuilder sb = new StringBuilder("SELECT ");
        
        if(asOfDate != null) {
            sb.append(" pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID, ");
        }
        
        for(int i=0; i<fieldNames.length; i++) {
            if(i>0) sb.append(", ");
            sb.append(fieldNames[i]);
        }
        
        sb.append(" FROM ").append(tableName);
        boolean first = true;
        if(asOfDate != null) {
            sb.append("_hist WHERE (fldHistNextDate IS NULL OR fldHistNextDate > ?) AND (pkHistUpdateDate <= ?)");
            first = false;
        }
        
        if(!allEntries) {
            for (String fieldName : fieldNames) {
                if (fieldName.startsWith("pk")) {
                    if(first)
                        sb.append(" WHERE ");
                    else
                        sb.append(" AND ");
                    sb.append(fieldName).append(" = ?");
                    first = false;
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Returns a SQL String for inserting an entry in the table
     * 
     * @param tableName (String) the name of the table
     * @param fieldNames (String[]) the list of the field names
     * @param insertAllFields (boolean) wether or not to also insert all "pk"-fields
     * @return the SQL command string
     */
    protected static String getInsertString(String tableName, String[] fieldNames, boolean[] isAutoNumber, boolean insertAllFields) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName);
        StringBuilder sbQs = new StringBuilder("");
        
        boolean first = true;
        
        for(int i=0; i<fieldNames.length; i++) {
            if(!isAutoNumber[i] || insertAllFields) {
                if(first) {
                    sb.append(" (");
                    sbQs.append(") VALUES (?");
                }
                else {
                    sb.append(", ");
                    sbQs.append(", ?");
                }
                
                sb.append(fieldNames[i]);
                first = false;
            }
        }
        
        sb.append(sbQs.toString()).append(")");
        
        return sb.toString();
    }
    
    /**
     * Returns a SQL command string for updating an existing table entry.
     * 
     * @param tableName (String) the table name
     * @param fieldNames (String[]) the field names
     * @return the SQL command string
     */
    protected static String getUpdateString(String tableName, String[] fieldNames) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        boolean first = true;
        
        for(int i=0; i<fieldNames.length; i++) {
            if(!fieldNames[i].startsWith("pk")) {
                if(!first) sb.append(", ");
                sb.append(fieldNames[i]).append(" = ?");
                first = false;
            }
        }
        
        sb.append(" WHERE ");
        
        first = true;
        for(int i=0; i<fieldNames.length; i++) {
            if(fieldNames[i].startsWith("pk")) {
                if(!first) sb.append(" AND ");
                sb.append(fieldNames[i]).append(" = ?");
                first = false;
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Returns a SQL command string to delete a table entrx
     * 
     * @param tableName (String) the table name
     * @param fieldNames (String[]) the field names
     * @return the SQL command string
     */
    protected static String getDeleteString(String tableName, String[] fieldNames) {
        StringBuilder sb = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
        boolean first = true;
        
        for(int i=0; i<fieldNames.length; i++) {
            if(fieldNames[i].startsWith("pk")) {
                if(!first) sb.append(" AND ");
                sb.append(fieldNames[i]).append(" = ?");
                first = false;
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Returns a SQL command to update the most recent entry in the audit safe
     * historization tables (i.e. before creating a new entry in the same
     * table).
     * 
     * @param tableName (String) the (underlying) table name
     * @param fieldNames (String[]) the field names
     * @return the SQL command string
     */
    protected static String getHistUpdatePreviousString(String tableName, String[] fieldNames) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(tableName).append("_hist SET fldHistNextDate = CURRENT_TIMESTAMP(6) WHERE fldHistNextDate IS NULL");
        
        for(String fieldName : fieldNames) {
            if(fieldName.startsWith("pk")) {
                sb.append(" AND ").append(fieldName).append(" = ?");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Returns a SQL command to insert a new entry in the audit safe
     * historization tables.
     * 
     * @param tableName (String) the (underlying) table name
     * @param fieldNames (String[]) the field names
     * @param updateType (int) the update type (see definitions above)
     * @return the SQL command string
     */
    protected static String getHistInsertString(String tableName, String[] fieldNames, int updateType) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append("_hist (pkHistUpdateDate, fldHistNextDate, fldHistUpdateType, fkHistUserID");
        StringBuilder sbValue = new StringBuilder("(CURRENT_TIMESTAMP(6), ");
        if(updateType == UPDATE_TYPE_DELETE)
            sbValue.append("CURRENT_TIMESTAMP(6)");
        else
            sbValue.append("NULL");
        sbValue.append(", ?, ?");
        
        for(String fieldName: fieldNames) {
            sb.append(", ").append(fieldName);
            sbValue.append(", ?");
        }
        
        sb.append(") VALUES ").append(sbValue).append(")");
        
        return sb.toString();
    }
    
    /**
     * Derives the setter or getter method name from a field's name and type
     * 
     * @param fieldName (String) the field name
     * @param type (int) the field type
     * @param getter (boolean) true = getter, false = setter
     * @return the method name (String)
     */
    private static String getMethodName(String fieldName, int type, boolean getter) {
        if(fieldName.startsWith("pk"))
            fieldName = fieldName.substring(2);
        
        if(fieldName.startsWith("fk"))
            fieldName = fieldName.substring(2);
        
        if(fieldName.startsWith("fld"))
            fieldName = fieldName.substring(3);
        
        StringBuilder sb = new StringBuilder("");
        if(getter) {
            if(type == Types.BOOLEAN)
                sb.append("is");
            else
                sb.append("get");
        }
        else
            sb.append("set");
        
        sb.append(fieldName.substring(0, 1).toUpperCase());
        sb.append(fieldName.substring(1));
        
        return sb.toString();
    }
    
    /**
     * Returns the field name as used in a bean from the SQL field name
     * 
     * @param fieldName (String) the sql field name
     * @return the bean field name (String)
     */
    private static String getFieldName(String fieldName) {
        if(fieldName.startsWith("pk"))
            fieldName = fieldName.substring(2);
        
        if(fieldName.startsWith("fk"))
            fieldName = fieldName.substring(2);
        
        if(fieldName.startsWith("fld"))
            fieldName = fieldName.substring(3);
        
        StringBuilder sb = new StringBuilder("");
        
        sb.append(fieldName.substring(0, 1).toLowerCase());
        sb.append(fieldName.substring(1));
        
        return sb.toString();
    }
    
    /**
     * Fills a bean with the data from a SQL result set
     * 
     * @param bean (BaseBean) the bean to be filled
     * @param fieldNames (String[]) the field names
     * @param fieldTypes (int[]) the field types
     * @param rs (ResultSet) the SQL result set
     * @param fillAsOfDate (boolean) whether the asof - tables are used
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected static void fillBeanFromSelect(BaseBean bean, String[] fieldNames, int[] fieldTypes, ResultSet rs, boolean fillAsOfDate) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class cArg[] = new Class[1];
        Method m;
        
        if(fillAsOfDate) {
            bean.setHistUpdateDate(rs.getDate("pkHistUpdateDate"));
            bean.setHistNextDate(rs.getDate("fldHistNextDate"));
            bean.setHistUpdateType(rs.getInt("fldHistUpdateType"));
            bean.setHistUserID(rs.getInt("fkHistUserID"));
        }
        
        for(int i=0; i<fieldNames.length; i++) {
            switch (fieldTypes[i]) {
                case Types.BOOLEAN:
                    cArg[0] = boolean.class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getInt(fieldNames[i]) != 0);
                    break;
                    
                case Types.INTEGER:
                    cArg[0] = int.class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getInt(fieldNames[i]));
                    break;
                    
                case Types.FLOAT:
                    cArg[0] = double.class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getDouble(fieldNames[i]));
                    break;
                    
                case Types.VARCHAR:
                    cArg[0] = String.class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getString(fieldNames[i]));
                    break;
                    
                case Types.DATE:
                    cArg[0] = Date.class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getDate(fieldNames[i]));
                    break;
                    
                case Types.BINARY:
                    cArg[0] = byte[].class;
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], false), cArg);
                    m.invoke(bean, rs.getBytes(fieldNames[i]));
                    break;
                    
                default:
                    throw new AssertionError();
            }
        }
    }
    
    /**
     * Sets all the parameters for an update statement
     * 
     * @param bean (Object) the bean with the data
     * @param fieldNames (String[]) the field names
     * @param fieldTypes (int[]) the field types
     * @param isNullable (boolean[]) wether or not the field can be null
     * @param ps (PreparedStatement) the statement to prepare
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected static void fillUpdateStatement(Object bean, String[] fieldNames, int[] fieldTypes, boolean[] isNullable, PreparedStatement ps) throws SecurityException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        int position = 1;
        Method m;
        
        // First, set the non-primary-key values
        for(int i=0; i<fieldNames.length; i++) {
            if(!fieldNames[i].startsWith("pk")) {
                switch (fieldTypes[i]) {
                    case Types.BOOLEAN:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Boolean bVal = (Boolean)m.invoke(bean);
                        ps.setInt(position++, bVal.booleanValue()?1:0);
                        break;
                        
                    case Types.INTEGER:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Integer iVal = (Integer)m.invoke(bean);
                        if(isNullable[i] && (iVal.intValue() == 0))
                            ps.setNull(position++, Types.INTEGER);
                        else
                            ps.setInt(position++, iVal.intValue());
                        break;
                        
                    case Types.FLOAT:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Double dVal = (Double)m.invoke(bean);
                        if(isNullable[i] && (dVal.isNaN()))
                            ps.setNull(position++, Types.FLOAT);
                        else
                            ps.setDouble(position++, dVal.doubleValue());
                        break;

                    case Types.VARCHAR:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        String sVal = (String)m.invoke(bean);
                        if(isNullable[i] && (sVal == null))
                            ps.setNull(position++, Types.VARCHAR);
                        else
                            ps.setString(position++, sVal);
                        break;
                        
                    case Types.DATE:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Date dtVal = (Date)m.invoke(bean);
                        if(isNullable[i] && (dtVal == null))
                            ps.setNull(position++, Types.DATE);
                        else
                            ps.setDate(position++, new java.sql.Date(dtVal.getTime()));
                        break;
                        
                    case Types.BINARY:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        byte[] baVal = (byte[])m.invoke(bean);
                        if(isNullable[i] && (baVal == null))
                            ps.setNull(position++, Types.BINARY);
                        else
                            ps.setBytes(position++, baVal);
                        break;
                        
                    default:
                        throw new AssertionError();
                }
            }
        }
        
        // Second, set the primary key values
        for(int i=0; i<fieldNames.length; i++) {
            if(fieldNames[i].startsWith("pk")) {
                switch (fieldTypes[i]) {
                    case Types.BOOLEAN:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Boolean bVal = (Boolean)m.invoke(bean);
                        ps.setInt(position++, bVal.booleanValue()?1:0);
                        break;
                        
                    case Types.INTEGER:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Integer iVal = (Integer)m.invoke(bean);
                        if(isNullable[i] && (iVal.intValue() == 0))
                            ps.setNull(position++, Types.INTEGER);
                        else
                            ps.setInt(position++, iVal.intValue());
                        break;
                        
                    case Types.FLOAT:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Double dVal = (Double)m.invoke(bean);
                        if(isNullable[i] && (dVal.isNaN()))
                            ps.setNull(position++, Types.FLOAT);
                        else
                            ps.setDouble(position++, dVal.doubleValue());
                        break;

                    case Types.VARCHAR:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        String sVal = (String)m.invoke(bean);
                        if(isNullable[i] && (sVal == null))
                            ps.setNull(position++, Types.VARCHAR);
                        else
                            ps.setString(position++, sVal);
                        break;
                        
                    case Types.DATE:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Date dtVal = (Date)m.invoke(bean);
                        if(isNullable[i] && (dtVal == null))
                            ps.setNull(position++, Types.DATE);
                        else
                            ps.setDate(position++, new java.sql.Date(dtVal.getTime()));
                        break;
                        
                    case Types.BINARY:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        byte[] baVal = (byte[])m.invoke(bean);
                        if(isNullable[i] && (baVal == null))
                            ps.setNull(position++, Types.BINARY);
                        else
                            ps.setBytes(position++, baVal);
                        break;
                        
                        
                    default:
                        throw new AssertionError();
                }
            }
        }
    }
    
    /**
     * Sets all the parameters for an insert statement
     * 
     * @param bean (Object) the bean data
     * @param fieldNames (String[]) the field names
     * @param fieldTypes (int[]) the field types
     * @param isNullable (boolean[]) wether the fields can be null
     * @param isAutoNumber (boolean[]) whether the field is auto numbered
     * @param ps (PreparedStatement) the sql statement
     * @param insertAllFields (boolean) wether the primary keys are preset
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected static void fillInsertStatement(Object bean, String[] fieldNames, int[] fieldTypes, boolean[] isNullable, boolean[] isAutoNumber, PreparedStatement ps, boolean insertAllFields) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException {
        int position = 1;
        Method m;
        
        for(int i=0; i<fieldNames.length; i++) {
            if(!isAutoNumber[i] || insertAllFields) {
                switch (fieldTypes[i]) {
                    case Types.BOOLEAN:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Boolean bVal = (Boolean)m.invoke(bean);
                        ps.setInt(position++, bVal.booleanValue()?1:0);
                        break;
                        
                    case Types.INTEGER:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Integer iVal = (Integer)m.invoke(bean);
                        if(isNullable[i] && (iVal.intValue() == 0))
                            ps.setNull(position++, Types.INTEGER);
                        else
                            ps.setInt(position++, iVal.intValue());
                        break;
                        
                    case Types.FLOAT:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Double dVal = (Double)m.invoke(bean);
                        if(isNullable[i] && (dVal.isNaN()))
                            ps.setNull(position++, Types.FLOAT);
                        else
                            ps.setDouble(position++, dVal.doubleValue());
                        break;

                    case Types.VARCHAR:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        String sVal = (String)m.invoke(bean);
                        if(isNullable[i] && (sVal == null))
                            ps.setNull(position++, Types.VARCHAR);
                        else
                            ps.setString(position++, sVal);
                        break;
                        
                    case Types.DATE:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Date dtVal = (Date)m.invoke(bean);
                        if(isNullable[i] && (dtVal == null))
                            ps.setNull(position++, Types.DATE);
                        else
                            ps.setDate(position++, new java.sql.Date(dtVal.getTime()));
                        break;
                        
                    case Types.BINARY:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        byte[] baVal = (byte[])m.invoke(bean);
                        if(isNullable[i] && (baVal == null))
                            ps.setNull(position++, Types.BINARY);
                        else
                            ps.setBytes(position++, baVal);
                        break;
                        
                    default:
                        throw new AssertionError();
                }
            }
        }
    }
    
    /**
     * Sets all the parameters for a delete statement (i.e. by filling all
     * primary key fields)
     * 
     * @param bean (Object) the bean object
     * @param fieldNames (String[]) the field names
     * @param fieldTypes (int[]) the field types
     * @param ps (PreparedStatement) the prepared delete statement
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected static void fillDeleteStatement(Object bean, String[] fieldNames, int[] fieldTypes, PreparedStatement ps) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException, IllegalAccessException, SQLException {
        int position = 1;
        Method m;
    
        for(int i=0; i<fieldNames.length; i++) {
            if(fieldNames[i].startsWith("pk")) {
                switch (fieldTypes[i]) {
                    case Types.BOOLEAN:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Boolean bVal = (Boolean)m.invoke(bean);
                        ps.setInt(position++, bVal.booleanValue()?1:0);
                        break;
                        
                    case Types.INTEGER:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Integer iVal = (Integer)m.invoke(bean);
                        ps.setInt(position++, iVal.intValue());
                        break;
                        
                    case Types.FLOAT:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Double dVal = (Double)m.invoke(bean);
                        ps.setDouble(position++, dVal.doubleValue());
                        break;

                    case Types.VARCHAR:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        String sVal = (String)m.invoke(bean);
                        ps.setString(position++, sVal);
                        break;
                        
                    case Types.DATE:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        Date dtVal = (Date)m.invoke(bean);
                        ps.setDate(position++, new java.sql.Date(dtVal.getTime()));
                        break;
                        
                    case Types.BINARY:
                        m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                        byte[] baVal = (byte[])m.invoke(bean);
                        ps.setBytes(position++, baVal);
                        break;
                        
                        
                    default:
                        throw new AssertionError();
                }
            }
        }
    }
    
    /**
     * Fills the statement that updates the audit safe historization tables
     * 
     * @param login (UserBean) the login under which the operation is run
     * @param bean (Object) the bean with the related data
     * @param updateType (int) see defintions above
     * @param fieldNames (String[]) all field names
     * @param fieldTypes (int[]) all field types
     * @param isNullable (boolean[]) whehter the fields can be null
     * @param ps (PreparedStatement) the SQL statement
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    protected static void fillHistInsertStatement(UserBean login, Object bean, int updateType, String[] fieldNames, int[] fieldTypes, boolean[] isNullable, PreparedStatement ps) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int position = 3;
        Method m;
        
        // First parameter is update type
        ps.setInt(1, updateType);
        // Second parameter is the user ID
        ps.setInt(2, login.getUserID());
        
        for(int i=0; i<fieldNames.length; i++) {
            switch (fieldTypes[i]) {
                case Types.BOOLEAN:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    Boolean bVal = (Boolean)m.invoke(bean);
                    ps.setInt(position++, bVal.booleanValue()?1:0);
                    break;

                case Types.INTEGER:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    Integer iVal = (Integer)m.invoke(bean);
                    if(isNullable[i] && (iVal.intValue() == 0))
                        ps.setNull(position++, Types.INTEGER);
                    else
                        ps.setInt(position++, iVal.intValue());
                    break;

                case Types.FLOAT:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    Double dVal = (Double)m.invoke(bean);
                    if(isNullable[i] && (dVal.isNaN()))
                        ps.setNull(position++, Types.FLOAT);
                    else
                        ps.setDouble(position++, dVal.doubleValue());
                    break;

                case Types.VARCHAR:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    String sVal = (String)m.invoke(bean);
                    if(isNullable[i] && (sVal == null))
                        ps.setNull(position++, Types.VARCHAR);
                    else
                        ps.setString(position++, sVal);
                    break;

                case Types.DATE:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    Date dtVal = (Date)m.invoke(bean);
                    if(isNullable[i] && (dtVal == null))
                        ps.setNull(position++, Types.DATE);
                    else
                        ps.setDate(position++, new java.sql.Date(dtVal.getTime()));
                    break;

                case Types.BINARY:
                    m = bean.getClass().getDeclaredMethod(getMethodName(fieldNames[i], fieldTypes[i], true));
                    byte[] baVal = (byte[])m.invoke(bean);
                    if(isNullable[i] && (baVal == null))
                        ps.setNull(position++, Types.BINARY);
                    else
                        ps.setBytes(position++, baVal);
                    break;

                default:
                    throw new AssertionError();
            }
        }
    }
}
