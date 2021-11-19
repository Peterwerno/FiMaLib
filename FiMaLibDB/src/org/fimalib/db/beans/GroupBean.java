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
package org.fimalib.db.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores information about a certain Group.
 * For details, please refer to the Design documentation and the tables
 * tblGroups and tblGroups_hist in the users database.
 * 
 * @author peter
 */
public class GroupBean extends BaseBean {
    int groupID;
    String groupName;
    List<GroupAccessRightBean> accessRights;

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<GroupAccessRightBean> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(List<GroupAccessRightBean> accessRights) {
        this.accessRights = accessRights;
    }
    
    public void addAccessRight(GroupAccessRightBean accessRight) {
        if(this.accessRights == null) this.accessRights = new ArrayList<>();
        this.accessRights.add(accessRight);
    }
    
    public void deleteAccessRight(GroupAccessRightBean accessRight) {
        if(this.accessRights.contains(accessRight))
            this.accessRights.remove(accessRight);
    }
}
