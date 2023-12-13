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
package org.fimalib.calc.cal.daycount;

import java.util.Calendar;
import java.util.Date;

/**
 * This class impements the daycount convention "30US/360 SIA".
 * 
 * Convention:
 * If month(endDate) = February and day(endDate) = lastday and month(startDate) = February and day(startDate) = lastday, then set day(endDate) = 30
 * If month(startDate) = February and day(startDate) = lastday, then set day(startDate) = 30
 * 
 * If day(startDate) = 31, then day(startDate) is set to 30
 * If day(endDate) = 31, then day(endDate) is set to 30
 * 
 * daycount = (year(endDate) - year(startDate)) * 360 + (month(endDate) - month(startDate)) * 30 + day(endDate) - day(startDate)
 *
 * @author peter
 */
public class D30U_360_SIA extends D30_360_ISDA {
    
    /**
     * This method checks if a given date is the "last day of February".
     * 
     * @param date (Date) the date to check
     * @return whether this is the last day in february (boolean)
     */
    public boolean isEndOfFebruary(Date date) {
        
        Calendar calTest = Calendar.getInstance();
        calTest.setTime(date);
        
        if(calTest.get(Calendar.MONTH) == Calendar.FEBRUARY) {
            calTest.add(Calendar.DATE, 1);
            if(calTest.get(Calendar.MONTH) == Calendar.MARCH)
                return true;
        }
        
        return false;
    }
    
    @Override
    public double getNumerator(Date startDate, Date endDate) throws DaycountException {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        
        calStart.setTime(startDate);
        calEnd.setTime(endDate);
        
        if(calStart.after(calEnd)) throw new DaycountException("Start date must be before or equal to end date!");
        
        int startDay = calStart.get(Calendar.DAY_OF_MONTH);
        int startMonth = calStart.get(Calendar.MONTH);
        int startYear = calStart.get(Calendar.YEAR);
        int endDay = calEnd.get(Calendar.DAY_OF_MONTH);
        int endMonth = calEnd.get(Calendar.MONTH);
        int endYear = calEnd.get(Calendar.YEAR);
        
        if(isEndOfFebruary(startDate)) {
            if(isEndOfFebruary(endDate))
                endDay = 30;
            startDay = 30;
        }
        
        if(startDay > 30) startDay = 30;
        if(endDay > 30) endDay = 30;
        
        int retVal = 0;
        
        if(startDay < endDay) {
            retVal += endDay - startDay;
            startDay = endDay;
        }
        else if(startDay > endDay) {
            retVal += endDay - startDay + 30;
            startMonth++;
            startDay = endDay;
        }
        
        while(startMonth != endMonth) {
            if(startMonth == 11) {
                startMonth = 0;
                startYear++;
            }
            else {
                startMonth++;
            }
            
            retVal += 30;
        }
        
        while(startYear < endYear) {
            startYear++;
            retVal += 360;
        }
        
        return retVal;
    }

}
