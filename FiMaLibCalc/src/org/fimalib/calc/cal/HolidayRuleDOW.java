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
 * This class implements the holiday rule of "x th weekday in a month" with 
 * optional offset.
 * 
 * This is often used in anglo saxon countries, such as the first Monday in
 * August, etc.
 * 
 * @author Peter Werno
 */
public class HolidayRuleDOW extends HolidayRule {
    int weekDay;
    int countInMonth;
    int month;
    int offset;
    
    /**
     * Creates a new instance of HolidayRuleDOW with no valid parameters.
     * 
     * The individual parameters should later be changed using the setters.
     */
    public HolidayRuleDOW() {
        this.weekDay = -1;
        this.countInMonth = -1;
        this.month = -1;
        this.offset = 0;
    }
    
    /**
     * Creates a new instance of HolidayRuleDOW with all parameters given.
     * 
     * @param weekDay (int) the day of the week (starts at zero)
     * @param countInMonth (int) the how many-eth such weekday
     * @param month (int) the month (starts at zero)
     * @param offset (int) a number of days of offset (- before, + after)
     */
    public HolidayRuleDOW(int weekDay, int countInMonth, int month, int offset) {
        this.weekDay = weekDay;
        this.countInMonth = countInMonth;
        this.month = month;
        this.offset = offset;
    }
    
    /**
     * Creates a new instance of HolidayRuleDOW with a given weekday, count in
     * month and month parameter.
     * 
     * The offset is set to zero.
     * 
     * @param weekDay (int) the day of the week (starts at zero)
     * @param countInMonth (int) the how many-eth such weekday
     * @param month (int) the month (starts at zero)
     */
    public HolidayRuleDOW(int weekDay, int countInMonth, int month) {
        this.weekDay = weekDay;
        this.countInMonth = countInMonth;
        this.month = month;
        this.offset = 0;
    }
    
    /*
    * Getters and Setters
    */

    /**
     * Returns the weekDay parameter
     * 
     * @return the weekDay parameter (int)
     */
    public int getWeekDay() {
        return weekDay;
    }

    /**
     * Sets the weekDay parameter
     * 
     * @param weekDay (int) the new weekDay parameter
     */
    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    /**
     * Returns the countInMonth parameter
     * 
     * @return the countInMonth parameter (int)
     */
    public int getCountInMonth() {
        return countInMonth;
    }

    /**
     * Sets the countInMonth parameter
     * 
     * @param countInMonth (int) the new countInMonth parameter
     */
    public void setCountInMonth(int countInMonth) {
        this.countInMonth = countInMonth;
    }

    /**
     * Returns the month parameter
     * 
     * @return the month parameter (int)
     */
    public int getMonth() {
        return month;
    }

    /**
     * Sets the month parameter
     * 
     * @param month (int) the new month parameter
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Returns the offset parameter
     * 
     * @return the offset parameter (int)
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the offset parameter
     * 
     * @param offset (int) the new offset parameter
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns true if the given date fulfills the holiday requirements defined
     * in this HolidayRule.
     * 
     * @param date (Date) the date
     * @param calendar (Calendar) the fimalib calendar
     * @return if the date is a holiday (boolean)
     */
    @Override
    public boolean isHoliday(Date date, Calendar calendar) {
        Date offsetDate = date;
        if(offset != 0) {
            offsetDate = new Date(date.getTime() - (long)offset * 86400000L);
        }
        
        if((calendar.getMonth(offsetDate) == this.month) && (calendar.getDayOfWeek(offsetDate) == this.weekDay)) {
            if(this.countInMonth == 0) return true;
            String[] weekDays = calendar.getWeekdayNames();
            
            int dayOfMonth = calendar.getDayOfMonth(offsetDate);
            
            if((this.countInMonth * weekDays.length <= dayOfMonth) && ((this.countInMonth+1) * weekDays.length > dayOfMonth))
                return true;
        }
        
        return false;
    }
}
