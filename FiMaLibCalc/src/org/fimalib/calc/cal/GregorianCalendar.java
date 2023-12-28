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
 * This class implements the functionalities to compute holiday calendars
 * with a gregorian calendar.
 * 
 * @author Peter Werno
 */
public class GregorianCalendar extends Calendar {
    java.util.Calendar calendar;
    
    public static final String[] monthNames = {
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
    };
    
    public static final String[] weekdayNames = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    
    public GregorianCalendar() {
        super();
        
        this.calendar = java.util.Calendar.getInstance(this.timeZone, this.locale);
    }
    
    public GregorianCalendar(Locale locale, TimeZone timeZone) {
        super(locale, timeZone);
        
        this.calendar = java.util.Calendar.getInstance(this.timeZone, this.locale);
    }
    
    @Override
    public int getYear(Date date) {
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.YEAR);
    }

    @Override
    public int getMonth(Date date) {
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.MONTH);
    }

    @Override
    public int getDayOfMonth(Date date) {
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getDayOfWeek(Date date) {
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.DAY_OF_WEEK);
    }

    @Override
    public String[] getMonthNames() {
        return monthNames;
    }

    @Override
    public String[] getWeekdayNames() {
        return weekdayNames;
    }
    
}
