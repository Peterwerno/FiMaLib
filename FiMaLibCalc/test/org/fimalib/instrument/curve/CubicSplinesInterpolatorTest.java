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
import java.util.Calendar;
import java.util.Date;
import org.fimalib.calc.cal.daycount.D30_360_ISDA;
import org.fimalib.instrument.TermRate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peter
 */
public class CubicSplinesInterpolatorTest {
    Date tradeDate;
    double delta = 0.000001;
    
    Date[] dateList = { 
        createDate(2001, 01, 01),
        createDate(2001, 05, 24),
        createDate(2000, 06, 01),
        createDate(2008, 11, 17),
        createDate(2015, 01, 01)
    };
    
    double[] resultList = {
        0.02,
        0.02088315560285366,
        0.018678651685393347,
        0.02779265732340746,
        0.034235955056179816
    };
    
    public CubicSplinesInterpolatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    static private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        
        return cal.getTime();
    }
    
    private ArrayList<TermRate> getTermRateList() {
        tradeDate = createDate(2000, 01, 01);
        TermRate rate1y = new TermRate(0.02, tradeDate, createDate(2001, 01, 01));
        TermRate rate2y = new TermRate(0.022, tradeDate, createDate(2002, 01, 01));
        TermRate rate3y = new TermRate(0.023, tradeDate, createDate(2003, 01, 01));
        TermRate rate5y = new TermRate(0.025, tradeDate, createDate(2005, 01, 01));
        TermRate rate10y = new TermRate(0.029, tradeDate, createDate(2010, 01, 01));
        
        ArrayList<TermRate> rateList = new ArrayList<>();
        
        rateList.add(rate1y);
        rateList.add(rate2y);
        rateList.add(rate3y);
        rateList.add(rate5y);
        rateList.add(rate10y);
        
        return rateList;
    }
    
    @Test
    public void testCubicSplineInterpolation() throws Exception {
        CubicSplinesInterpolator csi = new CubicSplinesInterpolator(new D30_360_ISDA(), LinearCurveInterpolatorExtrapolator.EXTRAPOLATE_LINEAR);
        
        csi.initialize(tradeDate, getTermRateList());
        
        for(int i=0; i<dateList.length; i++) {
            Date date = dateList[i];
            double expResult = resultList[i];
            double result = csi.interpolate(date).getRate();
            
            System.out.println("result: " + result);
            assertEquals("CubicSplineInterpolator should have returned " + expResult, expResult, result, delta);
        }
    }
}
