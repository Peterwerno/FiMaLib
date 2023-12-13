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
 * This class impements the daycount convention "30E/360 ISMA".
 * 
 * Convention:
 * If day(startDate) = 31, then day(startDate) is set to 30
 * If day(endDate) = 31, then day(endDate) is set to 30
 * 
 * daycount = (year(endDate) - year(startDate)) * 360 + (month(endDate) - month(startDate)) * 30 + day(endDate) - day(startDate)
 *
 * @author peter
 */
public class D30E_360_ISMA extends D30_360_ISDA {
    
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
