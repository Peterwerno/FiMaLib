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
 * This class implements a holiday rule which is based on easter and an optional
 * offset to easter sunday.
 * 
 * @author Peter Werno
 */
public class HolidayRuleEaster extends HolidayRule {
    int offset;
    
    /**
     * Creates a new instance of HolidayRuleEaster with offset set to zero.
     */
    public HolidayRuleEaster() {
        this.offset = 0;
    }
    
    /**
     * Creates a new instance of HolidayRuleEaster with a given offset.
     * 
     * @param offset (int) the offset
     */
    public HolidayRuleEaster(int offset) {
        this.offset = offset;
    }
    
    /*
    * Getters and Setters
    */
    
    /**
     * Returns the offset parameter
     * 
     * @return the offset parameter (int)
     */
    public int getOffset() {
        return this.offset;
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
     * Returns if the given date is a holiday according to this easter holiday
     * rule.
     * 
     * @param date (Date) the date to test
     * @param calendar (Calendar) the fimalib calendar
     * @return if the date is a holiday (boolean)
     */
    @Override
    public boolean isHoliday(Date date, Calendar calendar) {
        int year = calendar.getYear(date);
        
        Date easter = calendar.getEaster(year);
        
        Date offsetDate = date;
        if(this.offset != 0) {
            offsetDate = new Date(date.getTime() - (long)this.offset * 86400000L);
        }
        
        return calendar.equals(easter, offsetDate);
    }
    
}
