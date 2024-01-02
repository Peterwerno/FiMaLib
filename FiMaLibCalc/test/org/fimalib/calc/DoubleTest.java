/*
 * Copyright (C) 2024 peter.
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
package org.fimalib.calc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This unit test tests various methods of the Double class
 * 
 * @author Peter Werno
 */
public class DoubleTest {
    public static final double delta = 0.000001;
    
    public DoubleTest() {
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
    
    double[] testValues = {
        26.5, 13.123, 7, 0, -55, 123456789.1, -0.00001, Math.PI, Math.E
    };
    
    /* =======================================================================
    * Testing Access, constructor and comparison methods
    * ========================================================================
    */
    
    @Test
    public void testConstructor() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                
                assertEquals("Values should be identical", db.getValue(), testValues[i], delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testEquals() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    if(testValues[i] == testValues[j])
                        assertTrue("Method .equals should return true", db.equals(db2));
                    else
                        assertFalse("Method .equals should return false", db.equals(db2));
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }
    
    @Test
    public void testCompare() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    int compare = db.compareTo(db2);
                    
                    if(testValues[i] < testValues[j])
                        assertEquals("Method .compareTo should return -1", -1, compare);
                    else if(testValues[i] > testValues[j])
                        assertEquals("Method .compareTo should return +1", 1, compare);
                    else
                        assertEquals("Method .compareTo should return 0", 0, compare);
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }
    
    /* =======================================================================
    * Testing basic arithmetic functions
    * ========================================================================
    */
    
    @Test
    public void testAdd() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    assertEquals("Result of add operation is incorrect", testValues[i] + testValues[j], db.add(db2).getValue(), delta);
                    
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }
    
    @Test
    public void testSub() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    assertEquals("Result of sub operation is incorrect", testValues[i] - testValues[j], db.sub(db2).getValue(), delta);
                    
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }
    
    @Test
    public void testMul() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    assertEquals("Result of mul operation is incorrect (" + testValues[i] + "*" + testValues[j] + ")", testValues[i] * testValues[j], db.mul(db2).getValue(), delta);
                    
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }

    @Test
    public void testDiv() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    
                    if(testValues[j] != 0.0) 
                        assertEquals("Result of div operation is incorrect", testValues[i] / testValues[j], db.div(db2).getValue(), delta);
                    else {
                        try {
                            db.div(db2);
                            assertTrue("There should be an exception", false);
                        }
                        catch (FiMaLibDivisionByZeroException ex) {
                            // It would be ok to be here
                        }
                    }
                }
                catch (FiMaLibDivisionByZeroException ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }

    @Test
    public void testPow() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Double db = new Double(testValues[i]);
                    Double db2 = new Double(testValues[j]);
                    double result = Math.pow(testValues[i], testValues[j]);
                    
                    assertEquals("Result of pow operation is incorrect (" + testValues[i] + "^" + testValues[j] + ")", result, db.pow(db2).getValue(), delta);
                    
                }
                catch (Exception ex) {
                    assertTrue("There should not be an exception", false);
                }
            }
        }
    }

    /* =======================================================================
    * Testing non-trigonometric functions
    * ========================================================================
    */
    
    @Test
    public void testAbs() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.abs(testValues[i]);
                
                assertEquals("Values should be identical", result, db.abs().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testSgn() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.signum(testValues[i]);
                
                assertEquals("Values should be identical", result, db.sgn().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testSqrt() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.sqrt(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.sqrt().getValue()));
                else
                    assertEquals("Values should be identical", result, db.sqrt().getValue(), delta);
            }
            catch (FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", testValues[i] < 0.0);
            }
        }
    }
    
    @Test
    public void testExp() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.exp(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.exp().getValue()));
                else
                    assertEquals("Values should be identical", result, db.exp().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testLn() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.log(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.ln().getValue()));
                else
                    assertEquals("Values should be identical", result, db.ln().getValue(), delta);
            }
            catch (FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception " + testValues[i], testValues[i] <= 0.0);
            }
        }
    }
    
    @Test
    public void testLog() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.log10(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.log().getValue()));
                else
                    assertEquals("Values should be identical", result, db.log().getValue(), delta);
            }
            catch (FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", testValues[i] <= 0.0);
            }
        }
    }
    
    /* =======================================================================
    * Testing trigonometric functions
    * ========================================================================
    */
    
    
    @Test
    public void testSin() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.sin(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.sin().getValue()));
                else
                    assertEquals("Values should be identical", result, db.sin().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testCos() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.cos(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.cos().getValue()));
                else
                    assertEquals("Values should be identical", result, db.cos().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testTan() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Double db = new Double(testValues[i]);
                double result = Math.tan(testValues[i]);
                
                if(java.lang.Double.isNaN(result))
                    assertTrue("Value should be NaN", java.lang.Double.isNaN(db.tan().getValue()));
                else
                    assertEquals("Values should be identical", result, db.tan().getValue(), delta);
            }
            catch (Exception ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
}
