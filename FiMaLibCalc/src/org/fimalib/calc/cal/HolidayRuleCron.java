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
 * This class implements a holiday rule where a certain day, day of week or
 * month (or any combination thereof) can be filtered.
 * 
 * This rule can be used to calculate fixed day holidays such as New Year,
 * Christmas, etc.
 * 
 * @author Peter Werno
 */
public class HolidayRuleCron extends HolidayRule {
    int[] cronDays;
    int[] cronMonths;
    int[] cronDaysOfWeek;

    /**
     * Creates a new instance of HolidayRuleCron that has no parameters.
     * 
     * These parameters will have to be set using the setters afterwards.
     */
    public HolidayRuleCron() {
        super();
        
        this.cronDays = null;
        this.cronMonths = null;
        this.cronDaysOfWeek = null;
    }
    
    /**
     * Creates a new instance of HolidayRuleCron that returns a holiday when
     * the day of month equals to the defined day.
     * 
     * @param day (int) the day of month
     */
    public HolidayRuleCron(int day) {
        super();
        
        this.cronDays = new int[1];
        this.cronDays[0] = day;
        
        this.cronMonths = null;
        this.cronDaysOfWeek = null;
    }
    
    /**
     * Creates a new instance of HolidayRuleCron that returns a holiday when 
     * the day of month and month equal to the defined values
     * 
     * @param day (int) the day of month
     * @param month (int) the month
     */
    public HolidayRuleCron(int day, int month) {
        super();
        
        this.cronDays = new int[1];
        this.cronDays[0] = day;
        
        this.cronMonths = new int[1];
        this.cronMonths[0] = month;
        
        this.cronDaysOfWeek = null;
    }
    
    /**
     * Creates a new instance of HolidayRuleCron that returns a holiday when
     * the day of month, month and day of week all equal the defined values
     * 
     * @param day (int) the day of month
     * @param month (int) the month
     * @param dayOfWeek (int) the day of week
     */
    public HolidayRuleCron(int day, int month, int dayOfWeek) {
        super();
        
        this.cronDays = new int[1];
        this.cronDays[0] = day;
        
        this.cronMonths = new int[1];
        this.cronMonths[0] = month;
        
        this.cronDaysOfWeek = new int[1];
        this.cronDaysOfWeek[0] = dayOfWeek;
    }
    
    /**
     * Creates a new instance of HolidayRuleCron with a list of days of month,
     * months and days of week that should all be matched.
     * This can be used to check for multiple days, e.g. for christmas where
     * day of month is {25, 26} and month is {11}
     * 
     * @param cronDays (int[]) the list of days of month
     * @param cronMonths (int[]) the list of months
     * @param cronDaysOfWeek (int[]) the list of days of week
     */
    public HolidayRuleCron(int[] cronDays, int[] cronMonths, int[] cronDaysOfWeek) {
        super(); 
        
        this.cronDays = cronDays;
        this.cronMonths = cronMonths;
        this.cronDaysOfWeek = cronDaysOfWeek;
    }
    
    /*
    * Getters and Setters
    */

    public int[] getCronDays() {
        return cronDays;
    }

    public void setCronDays(int[] cronDays) {
        this.cronDays = cronDays;
    }

    public int[] getCronMonths() {
        return cronMonths;
    }

    public void setCronMonths(int[] cronMonths) {
        this.cronMonths = cronMonths;
    }

    public int[] getCronDaysOfWeek() {
        return cronDaysOfWeek;
    }

    public void setCronDaysOfWeek(int[] cronDaysOfWeek) {
        this.cronDaysOfWeek = cronDaysOfWeek;
    }

    /**
     * Checks if a given date is a holiday in the given calendar according to
     * this rule.
     * 
     * @param date (Date) the date
     * @param calendar (Calendar) the calendar
     * @return wether it is a holiday (boolean)
     */
    @Override
    public boolean isHoliday(Date date, Calendar calendar) {
        
        boolean match = false;
        if(cronDays != null) {
            int day = calendar.getDayOfMonth(date);
            for(int i=0; i<cronDays.length; i++) {
                if(cronDays[i] == day) {
                    match = true;
                    break;
                }
            }
            
            if(!match) return false;
        }
        
        match = false;
        if(cronMonths != null) {
            int month = calendar.getMonth(date);
            for(int i=0; i<cronMonths.length; i++) {
                if(cronMonths[i] == month) {
                    match = true;
                    break;
                }
            }
            
            if(!match) return false;
        }
        
        match = false;
        if(cronDaysOfWeek != null) {
            int dow = calendar.getDayOfWeek(date);
            for(int i=0; i<cronDaysOfWeek.length; i++) {
                if(cronDaysOfWeek[i] == dow) {
                    match = true;
                    break;
                }
            }
            
            if(!match) return false;
        }
        
        return true;
    }
}
