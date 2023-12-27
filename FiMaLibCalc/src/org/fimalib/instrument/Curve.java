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
package org.fimalib.instrument;

import java.util.ArrayList;
import java.util.Date;
import org.fimalib.calc.cal.daycount.DaycountException;
import org.fimalib.instrument.curve.CurveInterpolationException;
import org.fimalib.instrument.curve.CurveInterpolator;

/**
 * Curve implements the methods for a curve calculation.
 * A curve is usually based on certain pillars (e.g. spot rates for a number
 * of maturities) and allows to interpolate between these pillars.
 * 
 * @author Peter Werno
 */
public class Curve extends Instrument {
    ArrayList<TermRate> pillars;
    Date tradeDate;
    CurveInterpolator interpolator;
    
    public Curve(ArrayList<TermRate> pillars, Date tradeDate, CurveInterpolator interpolator) {
        this.pillars = pillars;
        this.tradeDate = tradeDate;
        this.interpolator = interpolator;
    }
    
    public Rate interpolateRate(Date forwardDate) throws CurveInterpolationException, DaycountException {
        return interpolator.interpolate(forwardDate);
    }
}
