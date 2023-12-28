/*
 * Copyright (C) 2023 peter.
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
package org.fimalib.calc.cal;

import java.util.Date;

/**
 * This class implements a "holiday rule" as it is defined for a certain market.
 * 
 * The rule might be something like "Every easter monday between 1980 and 2025".
 * 
 * There are several different rule types:
 *  - A cron-like fixed day / month combination (e.g. every 4th of July, 1st of January)
 *  - A defined weekday (e.g. every first monday in January)
 *  - An offset to a variable day, usually easter, or offset to a defined weekday
 * 
 * Then there might be a special handling if the holiday falls on a day that is
 * generally not a business day e.g. if 4th of July is on a Sunday.
 * 
 * The following rules an be selected:
 *  - Do nothing, if holiday is on a weekend then it stays on a weekend
 *  - Move to next business day
 *  - When Saturday, move to previous business day, when Sunday, move to next
 * 
 * Finally, each rule can have a start date and an end date, as regulations in
 * a market/country can change over time and some holidays may be added or 
 * removed, or the rule for a holiday may change.
 * 
 * @author Peter Werno
 */
public abstract class HolidayRule {
    Date startDate;
    Date endDate;
    
    /**
     * Creates a new instance of HolidayRule with no start- or enddate
     */
    public HolidayRule() {
        this.startDate = null;
        this.endDate = null;
    }
    
    /**
     * Creates a new instance of HolidayRule with start date and end date
     * given.
     * 
     * @param startDate (Date) the start date
     * @param endDate (Date) the end date
     */
    public HolidayRule(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /*
    * Getters and Setters
    */
    
    /**
     * Returns the start date
     * 
     * @return the start date (Date)
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date
     * 
     * @param startDate (Date) the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the end date
     * 
     * @return the end date (Date)
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date
     * 
     * @param endDate (Date) the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    /**
     * Returns if a given date is a holiday according to the holiday rule and
     * the given calendar.
     * 
     * This is an abstract method that must be implemented by any non-abstract
     * subclass.
     * 
     * @param date (Date) the date
     * @param calendar (Calendar) the fimalib calendar
     * @return if the date is a holiday (boolean)
     */
    public abstract boolean isHoliday(Date date, Calendar calendar);
}
