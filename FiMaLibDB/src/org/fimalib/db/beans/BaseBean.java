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

import java.util.Date;

/**
 * This class is the base class for all bean classes in the FiMaLib project.
 * 
 * @author peter
 */
public class BaseBean {
    Date histUpdateDate;
    Date histNextDate;
    int histUpdateType;
    int histUserID;

    public Date getHistNextDate() {
        return histNextDate;
    }

    public void setHistNextDate(Date histNextDate) {
        this.histNextDate = histNextDate;
    }

    public int getHistUpdateType() {
        return histUpdateType;
    }

    public void setHistUpdateType(int histUpdateType) {
        this.histUpdateType = histUpdateType;
    }

    public int getHistUserID() {
        return histUserID;
    }

    public void setHistUserID(int histUserID) {
        this.histUserID = histUserID;
    }

    public Date getHistUpdateDate() {
        return histUpdateDate;
    }

    public void setHistUpdateDate(Date histUpdateDate) {
        this.histUpdateDate = histUpdateDate;
    }
}
