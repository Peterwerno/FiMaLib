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
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;

/**
 * This class implements an islamic calendar which can be used to obtain
 * holidays based on islamic calendar.
 * 
 * @author Peter Werno
 */
public class IslamicCalendar extends GregorianCalendar {
    public static String[] monthNames = {
        "al-Muḥarram", "Ṣafar", "Rabīʿ al-ʾAwwal", "Rabīʿ ath-Thānī", "Jumādā al-ʾŪlā", "Jumādā ath-Thāniyah", "Rajab", "Shaʿbān", "Ramaḍān", "Shawwāl", "Ḏū al-Qaʿdah", "Ḏū al-Ḥijjah"
    };
            
    public static String[] weekDayNames = {
        "al-Ahad", "al-Ithnayn", "ath-Thulāthāʾ", "al-ʾArbiʿāʾ", "al-Khamīs", "al-Jumʿah", "as-Sabt"
    };
           
    /**
     * Creates a new instance of IslamicCalendar
     */
    public IslamicCalendar() {
        super();
    }
    
    /**
     * Creates a new instance of IslamicCalendar with a given locale and 
     * timezone.
     * 
     * @param locale (Locale) the locale
     * @param timeZone (TimeZone) the time zone
     */
    public IslamicCalendar(Locale locale, TimeZone timeZone) {
        super(locale, timeZone);
    }

    /**
     * Returns the islamic calendar year for a given gregorian date
     * 
     * @param date (Date) the gregorian Date
     * @return the islamic calendar year (int)
     */
    @Override
    public int getYear(Date date) {
        LocalDate ld = LocalDate.of(super.getYear(date), super.getMonth(date), super.getDayOfMonth(date));
        HijrahDate hd = HijrahDate.from(ld);
        
        return hd.get(ChronoField.YEAR);
    }

    /**
     * Returns the islamic calendar month for a given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the islamic calendar month (int)
     */
    @Override
    public int getMonth(Date date) {
        LocalDate ld = LocalDate.of(super.getYear(date), super.getMonth(date), super.getDayOfMonth(date));
        HijrahDate hd = HijrahDate.from(ld);
        
        return hd.get(ChronoField.MONTH_OF_YEAR);
    }

    /**
     * Returns the islamic calendar day of month for a given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the islamic calendar day of month (int)
     */
    @Override
    public int getDayOfMonth(Date date) {
        LocalDate ld = LocalDate.of(super.getYear(date), super.getMonth(date), super.getDayOfMonth(date));
        HijrahDate hd = HijrahDate.from(ld);
        
        return hd.get(ChronoField.DAY_OF_MONTH);
    }

    /**
     * Returns the islamic calendar day of week for a given gregorian date
     * 
     * @param date (Date) the gregorian date
     * @return the islamic calendar day of week (int)
     */
    @Override
    public int getDayOfWeek(Date date) {
        LocalDate ld = LocalDate.of(super.getYear(date), super.getMonth(date), super.getDayOfMonth(date));
        HijrahDate hd = HijrahDate.from(ld);
        
        return hd.get(ChronoField.DAY_OF_WEEK);
    }

    /**
     * Returns the list of month names
     * 
     * @return the month names (String[])
     */
    @Override
    public String[] getMonthNames() {
        return monthNames;
    }

    /**
     * Returns the list of weekday names
     * 
     * @return the weekday names (String[])
     */
    @Override
    public String[] getWeekdayNames() {
        return weekdayNames;
    }
}
