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
package org.fimalib.instrument.curve;

import java.util.ArrayList;
import java.util.Date;
import org.fimalib.calc.cal.daycount.DaycountConvention;
import org.fimalib.calc.cal.daycount.DaycountException;
import org.fimalib.instrument.TermRate;

/**
 * This class implements an interpolator for curves which interpolates linear
 * between the pillar points.
 * 
 * @author peter
 */
public class LinearCurveInterpolator implements CurveInterpolator {
    protected ArrayList<TermRate> pillars;
    protected Date tradeDate;
    protected DaycountConvention daycountConvention;
    
    public LinearCurveInterpolator(DaycountConvention daycountConvention) {
        this.daycountConvention = daycountConvention;
    }
    
    @Override
    public void initialize(Date tradeDate, ArrayList<TermRate> pillars) throws CurveInterpolationException, DaycountException {
        this.tradeDate = tradeDate;
        this.pillars = pillars;
        
        // No need to pre-calculate anything here
    }

    @Override
    public TermRate interpolate(Date settlementDate) throws CurveInterpolationException, DaycountException {
        TermRate rateBefore = null;
        TermRate rateAfter = null;
        
        for(TermRate rate : pillars) {
            int relPos = rate.getSettlementDate().compareTo(settlementDate);
            
            if(relPos == 0) {
                return rate;
            }
            else if(relPos < 0) {
                if(rateBefore == null)
                    rateBefore = rate;
                else {
                    if(rate.getSettlementDate().after(rateBefore.getSettlementDate()))
                        rateBefore = rate;
                }
            }
            else {
                if(rateAfter == null)
                    rateAfter = rate;
                else {
                    if(rate.getSettlementDate().before(rateAfter.getSettlementDate()))
                        rateAfter = rate;
                }
            }
        }
        
        // If this point is reached, then we need to interpolate:
        if(rateBefore == null) {
            if(rateAfter == null) {
                throw new CurveInterpolationException("No curve pillars available");
            }
            
            return extrapolateBefore(rateAfter, settlementDate);
        }
        else {
            if(rateAfter == null)
                return extrapolateAfter(rateBefore, settlementDate);
            
            double daysBetweenPillars = daycountConvention.getNumerator(rateBefore.getSettlementDate(), rateAfter.getSettlementDate());
            double daysFromBefore = daycountConvention.getNumerator(rateBefore.getSettlementDate(), settlementDate);
            double daysToAfter = daycountConvention.getNumerator(settlementDate, rateAfter.getSettlementDate());
            double rateValueBefore = rateBefore.getRate();
            double rateValueAfter = rateAfter.getRate();
            
            double rateResult = (rateValueAfter * daysFromBefore / daysBetweenPillars) + (rateValueBefore * daysToAfter / daysBetweenPillars);
            
            return new TermRate(rateResult, tradeDate, settlementDate);
        }
    }
    
    protected TermRate extrapolateBefore(TermRate rateAfter, Date settlementDate) throws CurveInterpolationException, DaycountException {
        return rateAfter;
    }
    
    protected TermRate extrapolateAfter(TermRate rateBefore, Date settlementDate) throws CurveInterpolationException, DaycountException {
        return rateBefore;
    }
}
