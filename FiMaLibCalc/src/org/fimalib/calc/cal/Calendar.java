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
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class is the base class for all calendar classes. 
 * 
 * A calendar implements the behaviour of a certain calendar, e.g. the julian,
 * the gregorian, chinese, etc. calendars. It allows to define public holidays
 * based on a non-gregorian calendar to be re-calculated into the gregorian
 * calendar as implemented by java.util.Date and java.util.Calendar.
 * 
 * @author Peter Werno
 */
public abstract class Calendar {
    protected Locale locale;
    protected TimeZone timeZone;
    
    public Calendar() {
        this.locale = Locale.getDefault();
        this.timeZone = TimeZone.getDefault();
    }
    
    public Calendar(Locale locale, TimeZone timeZone) {
        this.locale = locale;
        this.timeZone = timeZone;
    }
    
    /**
     * Calculates the date of easter sunday for a given year.
     * 
     * @param year
     * @return 
     */
    public Date getEaster(int year) {
        int a = year % 19;
        int bc = (year + year/4) % 7;
        int k = year / 100;
        int p = (13 + 8*k) / 25;
        int q = k/4;
        int M = 15 - p + k - q;
        int N = 4 + k - q;
        int d = (19*a + M) % 30;
        
        if((d == 29) || ((d == 28) && (a > 10))) d--;
        
        int e = (35 + N - bc - d) % 7;
        
        java.util.Calendar cal = java.util.Calendar.getInstance(this.timeZone, this.locale);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.set(java.util.Calendar.YEAR, year);
        
        if((d + e + 22) < 32) {
            // Easter is on d + e + 22 of March
            
            cal.set(java.util.Calendar.MONTH, java.util.Calendar.MARCH);
            cal.set(java.util.Calendar.DAY_OF_MONTH, d + e + 22);
        }
        else {
            // Easter is on d + e - 9 of April
            
            cal.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
            cal.set(java.util.Calendar.DAY_OF_MONTH, d + e - 9);
        }
        
        return cal.getTime();
    }
    
    public int compare(Date date1, Date date2) {
        int year1 = this.getYear(date1);
        int year2 = this.getYear(date2);
        
        if(year1<year2) return -1;
        if(year1>year2) return 1;
        
        int month1 = this.getMonth(date1);
        int month2 = this.getMonth(date2);
        
        if(month1<month2) return -1;
        if(month1>month2) return 1;
        
        int day1 = this.getDayOfMonth(date1);
        int day2 = this.getDayOfMonth(date2);
        
        if(day1<day2) return -1;
        if(day1>day2) return 1;
        return 0;
    }
    
    public boolean equals(Date date1, Date date2) {
        return compare(date1, date2) == 0;
    }
    
    public abstract int getYear(Date date);
    public abstract int getMonth(Date date);
    public abstract int getDayOfMonth(Date date);
    public abstract int getDayOfWeek(Date date);
    public abstract String[] getMonthNames();
    public abstract String[] getWeekdayNames();
    
    
    
}
