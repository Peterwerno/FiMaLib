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
import org.fimalib.calc.matrix.LESSolver;
import org.fimalib.calc.matrix.MatrixException;
import org.fimalib.instrument.TermRate;

/**
 * This class implements an interpolator for cubic spline interpolation.
 * 
 * What are cubic splines:
 * The idea behind cubic spline interpolation is to have a curve that is
 * to continuous and differentiable. To achive this, it is assumed that
 * between the pillars there is a cubic polinomial 
 * (i.e. a*x^3 + b*x^2 + c*x + d)
 * These polinomials are aligned to
 * a) fit the actual value (rate) of the pillars at the date of the pillar(s)
 * b) have the same slope at the pillar points
 * c) have the same convexity (second derivative) at the pillar points
 * 
 * These 
 * 
 * @author Peter Werno
 */
public class CubicSplinesInterpolator extends LinearCurveInterpolatorExtrapolator {
    /**
     * This class implements a cubic polinomial in the form
     * a*x^3 + b*x^2 + c*x + d
     */
    static class CubePolynomial {
        double a,b,c,d;
        
        public CubePolynomial(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
        
        public double calculate(double x) {
            return a*x*x*x + b*x*x + c*x + d;
        }
    }
    
    ArrayList<CubePolynomial> splines = new ArrayList<>();
    
    public CubicSplinesInterpolator(DaycountConvention daycountConvention, int extrapolationType) {
        super(daycountConvention, extrapolationType);
    }
    
    @Override
    public void initialize(Date tradeDate, ArrayList<TermRate> pillars) throws CurveInterpolationException, DaycountException {
        super.initialize(tradeDate, pillars);
        
        // SPLINE ALGORITHM:
        //
        // there are n rates 0<=x<n
        // there are n-1 splines 0<=i<n-1
        // there are 4*(n-1) variables to solve, so 4*(n-1) equations are needed
        //
        // t(x) = settlement Date of rate x, 0<=x<n
        // r(x) = rate value of rate x, 0<=x<n
        // fi(x) = spline between rate x and x+1 (0<=x<n-1)
        //
        // 1. fi(t(i)) = r(i)               (0<=i<n-1)  (n-1 equations)
        // 2. fi(t(i+1)) = r(i+1)           (0<=i<n-1)  (n-1 equations)
        // 3. fi'(t(i+1)) = fi+1'(t(i+1))   (0<=i<n-2)  (n-2 equations)
        // 4. fi''(t(i+1)) = fi+1''(t(i+1)) (0<=i<n-2)  (n-2 equations)
        // There are always 2 more equations missing. This is achieved by
        // setting the curvature at the end points to zero:
        // 5. f0''(t(0)) = 0
        // 6. fn-2''(t(n-1)) = 0
        
        // parameters a1, b1, c1, d1, a2, b2, .... cn-2, dn-2
        double[][] parameters = new double[(this.sortedPillars.size()-1) * 4][(this.sortedPillars.size()-1) * 4];
        double[] results = new double[(this.sortedPillars.size()-1) * 4];
        
        // 1.)
        for(int i=0; i<this.sortedPillars.size()-1; i++) {
            TermRate rateLeft = this.sortedPillars.get(i);
            
            // fi(t(i)) = r(i)
            // fi(0) = rateLeft
            parameters[i][i*4]      = 0.0;  // a*0^3
            parameters[i][i*4+1]    = 0.0;  // b*0^2
            parameters[i][i*4+2]    = 0.0;  // c*0
            parameters[i][i*4+3]    = 1.0;  // d
            results[i]              = rateLeft.getRate();
        }

        // 2.)
        for(int i=0; i<this.sortedPillars.size()-1; i++) {
            TermRate rateLeft = this.sortedPillars.get(i);
            TermRate rateRight = this.sortedPillars.get(i+1);
            double dateDiff = this.daycountConvention.getNumerator(rateLeft.getSettlementDate(), rateRight.getSettlementDate());
            
            // fi(t(i+1) = r(i+1)
            // fi(dateDiff) = rateRight
            parameters[i + this.sortedPillars.size() - 1][i*4]      = dateDiff*dateDiff*dateDiff;  // a*dateDiff^3
            parameters[i + this.sortedPillars.size() - 1][i*4+1]    = dateDiff*dateDiff;  // b*dateDiff^2
            parameters[i + this.sortedPillars.size() - 1][i*4+2]    = dateDiff;  // c*dateDiff
            parameters[i + this.sortedPillars.size() - 1][i*4+3]    = 1.0;  // d
            results[i + this.sortedPillars.size() - 1]              = rateRight.getRate();
        }
        
        // 3.)
        double lastDateDiff = 0.0;
        for(int i=0; i<this.sortedPillars.size()-2; i++) {
            TermRate rateLeft = this.sortedPillars.get(i);
            TermRate rateRight = this.sortedPillars.get(i+1);
            double dateDiff = this.daycountConvention.getNumerator(rateLeft.getSettlementDate(), rateRight.getSettlementDate());
            lastDateDiff = dateDiff;
            
            // fi'(t(i+1)) = fi+1'(t(i+1))
            // fi'(dateDiff) = fi+1'(0)
            // f'(x) = 3*a*x^2 + 2*b*x + c
            
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4] = 3.0*dateDiff*dateDiff;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+1] = 2.0*dateDiff;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+2] = 1.0;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+3] = 0.0;
            
            // fi+1'(0)
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+4] = 0.0;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+5] = 0.0;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+6] = -1.0;
            parameters[i + 2*(this.sortedPillars.size()-1)][i*4+7] = 0.0;
            
            results[i + 2*(this.sortedPillars.size()-1)] = 0.0;
            
            // 4.) fi''(t(i+1)) = fi+1''(t(i+1))
            // fi''(dateDiff) = fi+1''(0)
            // f''(x) = 6*a*x + 2*b
            
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4] = 6.0*dateDiff;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+1] = 2.0;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+2] = 0.0;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+3] = 0.0;
            
            // fi+1''(0)
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+4] = 0.0;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+5] = -2.0;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+6] = 0.0;
            parameters[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)][i*4+7] = 0.0;
            
            results[i + 2*(this.sortedPillars.size()-1) + (this.sortedPillars.size()-2)] = 0.0;
        }
        
        // 5.) f0''(0) = 0
        parameters[2*(this.sortedPillars.size()-1) + 2*(this.sortedPillars.size()-2)][1] = 2.0;
        // 6.) fn-2''(t(n-1)) = 0
        parameters[2*(this.sortedPillars.size()-1) + 2*(this.sortedPillars.size()-2)+1][(this.sortedPillars.size()-2)*4] = 6.0 * lastDateDiff;
        parameters[2*(this.sortedPillars.size()-1) + 2*(this.sortedPillars.size()-2)+1][(this.sortedPillars.size()-2)*4+1] = 2.0;
        
        try {
            LESSolver solver = new LESSolver(parameters, results);
            solver.solve();
            
            
            for(int i=0; i<this.sortedPillars.size()-1; i++) {
                double a = solver.getResults().getValue(i*4, 0);
                double b = solver.getResults().getValue(i*4+1, 0);
                double c = solver.getResults().getValue(i*4+2, 0);
                double d = solver.getResults().getValue(i*4+3, 0);
                
                CubePolynomial spline = new CubePolynomial(a, b, c, d);
                
                this.splines.add(spline);
            }
        }
        catch (MatrixException ex) {
            throw new CurveInterpolationException("Cannot solve for Cubic splines", ex);
        }
        
    }
    
    @Override
    public TermRate interpolate(Date settlementDate) throws CurveInterpolationException, DaycountException {
        int count = -1;
        for(TermRate rate : this.sortedPillars) {
            if(rate.getSettlementDate().after(settlementDate)) {
                break;
            }
            count++;
        }
        
        CubePolynomial linearExt = null;
        
        if(count == -1) {
            switch (this.extrapolationType) {
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_CONSTANT:
                    TermRate leftRate = this.sortedPillars.get(0);
                    TermRate retVal = new TermRate(leftRate.getRate(), leftRate.getTradeDate(), settlementDate);
                    return retVal;
                    
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_LINEAR:
                    CubePolynomial leftPoly = this.splines.get(0);
                    linearExt = new CubePolynomial(0.0, 0.0, leftPoly.c, leftPoly.d);
                    break;
                    
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_OTHER:
                    count = 0;
                    break;

                default:
                    throw new CurveInterpolationException("Cannot extrapolate curve with selected method");
            }
            count = 0;
        }
        if(count >= this.splines.size()) count = this.splines.size()-1;
        
        TermRate lastRate = this.sortedPillars.get(this.sortedPillars.size()-1);
        if(lastRate.getSettlementDate().before(settlementDate)) {
            switch (this.extrapolationType) {
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_CONSTANT:
                    TermRate retVal = new TermRate(lastRate.getRate(), lastRate.getTradeDate(), settlementDate);
                    return retVal;
                    
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_LINEAR:
                    CubePolynomial rightPoly = this.splines.get(this.splines.size()-1);
                    linearExt = new CubePolynomial(0.0, 0.0, rightPoly.c, rightPoly.d);
                    // TODO: Code this
                    break;
                    
                case LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_OTHER:
                    break;
                default:
                    throw new CurveInterpolationException("Cannot extrapolate curve with selected method");
            }
        }
        
        TermRate rateBefore = this.sortedPillars.get(count);
        
        double xValue = 0.0;
        
        if(rateBefore.getSettlementDate().after(settlementDate))
            xValue = -this.daycountConvention.getNumerator(settlementDate, rateBefore.getSettlementDate());
        else
            xValue = this.daycountConvention.getNumerator(rateBefore.getSettlementDate(), settlementDate);
        
        double rate = 0.0;
        
        if(linearExt != null)
            rate = linearExt.calculate(xValue);
        else
            rate = this.splines.get(count).calculate(xValue);
        
        TermRate retVal = new TermRate(rate, rateBefore.getTradeDate(), settlementDate);
        
        return retVal;
    }        
}
