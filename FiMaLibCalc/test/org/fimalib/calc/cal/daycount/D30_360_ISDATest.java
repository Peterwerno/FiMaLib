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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
public class D30_360_ISDATest {
    
    D30_360_ISDA conv;
    public static double delta = 0.0001;
    
    public D30_360_ISDATest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.conv = new D30_360_ISDA();
    }
    
    @After
    public void tearDown() {
    }

    protected Date getDate(String dateStringUK) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        
        return sdf.parse(dateStringUK);
    }
    
    public static final String[] startDates = new String[] {
        "19911201",
        "20000229",
        "20050505",
        "19970703",
        "20020630",
    };
    public static final String[] endDates = new String[] {
        "19911204",
        "20010115",
        "20060505",
        "19960601",
        "20020731",
    };
    public static final boolean[] exceptions = new boolean[] {
        false,
        false,
        false,
        true,
        false,
    };
    public static final double[] numerators = new double[] {
        3.0,
        316.0,
        360.0,
        0.0,
        30.0,
    };
    public static final double[] denominators = new double[] {
        360.0,
        360.0,
        360.0,
        0.0,
        360.0,
    };
    public static final double[] fractions = new double[] {
        3.0/360.0,
        316.0/360.0,
        1.0,
        0.0,
        30.0/360.0,
    };
    
    
    @Test
    public void testNominator() throws ParseException {
        System.out.println("Testing D30_360_ISDA daycount convention");
        
        for(int i=0; i<startDates.length; i++) {
            Date startDate = getDate(startDates[i]);
            Date endDate = getDate(endDates[i]);
            
            try {
                double numerator = conv.getNumerator(startDate, endDate);
                double denominator = conv.getDenominator(startDate, endDate);
                double fraction = conv.getFraction(startDate, endDate);
                
                System.out.println(startDates[i] + " - " + endDates[i] + " result: " + numerator + " / " + denominator);
                
                assertFalse("This should throw an exception " + i, exceptions[i]);
                assertEquals("Nominator value mismatch for " + i, numerator, numerators[i], delta);
                assertEquals("Denominator value mismatch for " + i, denominator, denominators[i], delta);
                assertEquals("Fraction value mismatch for " + i, fraction, fractions[i], delta);
            }
            catch (DaycountException ex) {
                assertTrue("This should not throw an exception " + i, exceptions[i]);
            }
        }
        
    }
}
