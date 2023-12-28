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
 * This class implements the Julian Calendar
 * 
 * @author Peter Werno
 */
public class JulianCalendar extends GregorianCalendar {
    public static final int CALC_OFFSETS  = 40;
    
    static Date[] offsetDates;
    static int[] offsetValues;
    
    /**
     * Creates a new instance of JulianCalendar.
     */
    public JulianCalendar() {
        super();
        
        setOffsets(this.locale, this.timeZone);
    }
    
    /**
     * Creates a new instance of JulianCalendar with given locale and timezone
     * 
     * @param locale (Locale) the locale
     * @param timeZone (TimeZone) the time zone
     */
    public JulianCalendar(Locale locale, TimeZone timeZone) {
        super(locale, timeZone);

        setOffsets(this.locale, this.timeZone);
    }
    
    /**
     * Pre-calculates the offset dates for the Julian Calendar calculation
     * 
     * @param locale (Locale) the locale
     * @param timeZone (TimeZone) the time zone
     */
    protected static void setOffsets(Locale locale, TimeZone timeZone) {
        int year = 1700;
        int offset = 11;
        
        offsetDates = new Date[CALC_OFFSETS];
        offsetValues = new int[CALC_OFFSETS];
        java.util.Calendar cal = java.util.Calendar.getInstance(timeZone, locale);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.set(java.util.Calendar.MONTH, java.util.Calendar.FEBRUARY);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 28);
        
        for(int i=0; i<CALC_OFFSETS; i++) {
            cal.set(java.util.Calendar.YEAR, year);
            offsetDates[i] = cal.getTime();
            offsetValues[i] = offset;
            
            year += 100;
            offset++;
            
            if(((year/400)*400) == year)
                year += 100;
        }
    }
    
    /**
     * Calculates the julian date from a given (gregorian) date.
     * 
     * @param date (Date) the gregorian date
     * @return the julian date (Date)
     */
    protected Date calcJulianDate(Date date) {
        int last = 10;
        for(int i=0; i<CALC_OFFSETS; i++) {
            if(offsetDates[i].after(date)) break;
            last = offsetValues[i];
        }
        
        Date julianDate = new Date(date.getTime() - (long)last * 86400000L);
        
        return julianDate;
    }

    /**
     * Returns the julian year of the given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the julian year (int)
     */
    @Override
    public int getYear(Date date) {
        return super.getYear(calcJulianDate(date));
    }

    /**
     * Returns the julian month of the given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the julian month (int)
     */
    @Override
    public int getMonth(Date date) {
        return super.getMonth(calcJulianDate(date));
    }

    /**
     * Returns the julian day of month of the given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the julian day of month (int)
     */
    @Override
    public int getDayOfMonth(Date date) {
        return super.getDayOfMonth(calcJulianDate(date));
    }
}
