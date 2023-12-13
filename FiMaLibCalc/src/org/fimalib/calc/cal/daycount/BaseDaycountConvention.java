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

import java.util.Date;

/**
 * This class implements the getFraction method of the DaycountConvention
 * interface and provides other functionality which may be used by actual
 * impementations of the DaycountConvention interface.
 * 
 * @author peter
 */
public abstract class BaseDaycountConvention implements DaycountConvention {
    /**
     * Returns the fraction of an interest period between two dates.
     * 
     * @param startDate (Date) the start Date
     * @param endDate (Date) the end Date
     * @return the fraction of the period (double)
     * @throws DaycountException
     */
    @Override
    public double getFraction(Date startDate, Date endDate) throws DaycountException {
        return getNumerator(startDate, endDate) / getDenominator(startDate, endDate);
    }
    
}
