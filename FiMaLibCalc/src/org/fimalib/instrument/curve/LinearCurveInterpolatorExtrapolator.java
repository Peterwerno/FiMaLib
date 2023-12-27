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
 * This class implements a curve interpolator that implements a certain
 * behaviour for extrapolation beyond the first or last pillar point.
 * 
 * @author Peter Werno
 */
public class LinearCurveInterpolatorExtrapolator extends LinearCurveInterpolator {
    public static final int EXTRAPOLATE_NONE        = 0;
    public static final int EXTRAPOLATE_CONSTANT    = 1;
    public static final int EXTRAPOLATE_LINEAR      = 2;
    public static final int EXTRAPOLATE_LINEAR_STUB = 3;
    public static final int EXTRAPOLATE_EXPONENTIAL = 4;
    public static final int EXTRAPOLATE_OTHER       = 5;    // Used e.g. by Cubic Splines
    
    protected int extrapolationType;
    protected ArrayList<TermRate> sortedPillars;
    protected TermRate stubRate;
    protected double daysBetweenFirstTwo;
    protected double daysBetweenLastTwo;
    protected double rate1;
    protected double rate2;
    protected double ratel1;
    protected double ratel2;
    
    public LinearCurveInterpolatorExtrapolator(DaycountConvention daycountConvention, int extrapolationType) {
        super(daycountConvention);
        
        this.extrapolationType = extrapolationType;
    }
    
    public LinearCurveInterpolatorExtrapolator(DaycountConvention daycountConvention, TermRate stubRate) {
        super(daycountConvention);
        
        this.extrapolationType = EXTRAPOLATE_LINEAR_STUB;
        this.stubRate = stubRate;
    }
    
    public void setStubRate(TermRate stubRate) {
        this.stubRate = stubRate;
    }
    
    @Override
    public void initialize(Date tradeDate, ArrayList<TermRate> pillars) throws CurveInterpolationException, DaycountException {
        super.initialize(tradeDate, pillars);
        ArrayList<TermRate> sortedList = new ArrayList<>();
        
        
        // Find first and last two items
        int size = pillars.size();
        for(TermRate rate : pillars) sortedList.add(rate);
        
        for(int i=0; i<size-1; i++) {
            TermRate rate1 = sortedList.get(i);
            for(int j=i+1; j<size; j++) {
                TermRate rate2 = sortedList.get(j);
                
                if(rate2.getSettlementDate().before(rate1.getSettlementDate())) {
                    sortedList.set(i, rate2);
                    sortedList.set(j, rate1);
                }
            }
        }
        
        if(size >= 2) {
            TermRate termRate1 = sortedList.get(0);
            TermRate termRate2 = sortedList.get(1);
            
            this.rate1 = termRate1.getRate();
            this.rate2 = termRate2.getRate();
            this.daysBetweenFirstTwo = daycountConvention.getNumerator(termRate1.getSettlementDate(), termRate2.getSettlementDate());
            
            termRate1 = sortedList.get(size-2);
            termRate2 = sortedList.get(size-1);
            
            this.ratel1 = termRate1.getRate();
            this.ratel2 = termRate2.getRate();
            this.daysBetweenLastTwo = daycountConvention.getNumerator(termRate1.getSettlementDate(), termRate2.getSettlementDate());
        }
        
        
        this.sortedPillars = sortedList;
    }

    
    @Override
    protected TermRate extrapolateBefore(TermRate rateAfter, Date settlementDate) throws CurveInterpolationException, DaycountException {
        TermRate termRate1;
        
        switch (this.extrapolationType) {
            case EXTRAPOLATE_NONE:
                throw new CurveInterpolationException("Cannot Extrapolate rate");
                
            case EXTRAPOLATE_CONSTANT:
                return super.extrapolateBefore(rateAfter, settlementDate);
                
            case EXTRAPOLATE_LINEAR:
                if(this.sortedPillars.size() < 2)
                    return super.extrapolateBefore(rateAfter, settlementDate);
                
                termRate1 = this.sortedPillars.get(0);
                
                double daysBeforeFirst = daycountConvention.getNumerator(settlementDate, termRate1.getSettlementDate());
                double newRate = rate1 - ((rate2-rate1) * daysBeforeFirst / this.daysBetweenFirstTwo);
                
                return new TermRate(newRate, termRate1.getTradeDate(), settlementDate);
                
            case EXTRAPOLATE_LINEAR_STUB:
                // TODO: Code this
                
            case EXTRAPOLATE_EXPONENTIAL:
                // TODO: Code this
            default:
                throw new AssertionError("Extrapolation type undefined");
        }
    }
    
    @Override
    protected TermRate extrapolateAfter(TermRate rateBefore, Date settlementDate) throws CurveInterpolationException, DaycountException {
        return rateBefore;
    }
    
    
}
