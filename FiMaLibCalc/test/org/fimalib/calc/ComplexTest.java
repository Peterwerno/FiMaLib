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

import java.text.NumberFormat;
import java.text.ParseException;
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
public class ComplexTest {
    public static final double delta = 0.000001;
    
    public ComplexTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.format.setGroupingUsed(false);
    }
    
    @After
    public void tearDown() {
    }
    
    String[] testValues = {
        "26.5", "13.123", "7", "0", "-55", 
        "123456789.1", "-0.00001", "3.1415", "2.1718", "-1",
        "15+3i", "-1-i", "12i", "1+8i", "15-7i"
    };
    
    double[] realValues = {
        26.5, 13.123, 7, 0, -55,
        123456789.1, -0.00001, 3.1415, 2.1718, -1,
        15, -1, 0, 1, 15
    };
    
    double[] imgValues = {
        0, 0, 0, 0, 0,
        0, 0, 0, 0, 0,
        3, -1, 12, 8, -7
    };
    
    NumberFormat format = NumberFormat.getInstance(Locale.US);
    
    /* =======================================================================
    * Testing Access, constructor and comparison methods
    * ========================================================================
    */
    
    @Test
    public void testConstructor() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                
                assertEquals("Values should be identical " + testValues[i] + " - " + realValues[i] + " / " + imgValues[i], realValues[i], cv.getValue(), delta);
                assertEquals("Values should be identical " + testValues[i] + " - " + realValues[i] + " / " + imgValues[i], imgValues[i], cv.getImg(), delta);
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testEquals() {
        for(int i=0; i<testValues.length; i++) {
            for(int j=0; j<testValues.length; j++) {
                try {
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    if(i == j)
                        assertTrue("Method .equals should return true " + testValues[i] + ", " + testValues[j], cv.equals(cv2));
                    else
                        assertFalse("Method .equals should return false " + testValues[i] + ", " + testValues[j], cv.equals(cv2));
                }
                catch (ParseException ex) {
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
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);

                    double abs = Math.sqrt(realValues[i] * realValues[i] + imgValues[i] * imgValues[i]);
                    double abs2 = Math.sqrt(realValues[j] * realValues[j] + imgValues[j] * imgValues[j]);
                    
                    int compare = cv.compareTo(cv2);
                    
                    if(abs < abs2)
                        assertEquals("Method .compareTo should return -1", -1, compare);
                    else if(abs > abs2)
                        assertEquals("Method .compareTo should return +1", 1, compare);
                    else
                        assertEquals("Method .compareTo should return 0", 0, compare);
                }
                catch (ParseException ex) {
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
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    assertEquals("Result of add operation is incorrect (real part)", realValues[i] + realValues[j], cv.add(cv2).getValue(), delta);
                    assertEquals("Result of add operation is incorrect (img part)", imgValues[i] + imgValues[j], cv.add(cv2).getImg(), delta);
                    
                }
                catch (ParseException ex) {
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
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    assertEquals("Result of sub operation is incorrect (real part)", realValues[i] - realValues[j], cv.sub(cv2).getValue(), delta);
                    assertEquals("Result of sub operation is incorrect (img part)", imgValues[i] - imgValues[j], cv.sub(cv2).getImg(), delta);
                    
                }
                catch (ParseException ex) {
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
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    double resultReal = realValues[i]*realValues[j] - imgValues[i]*imgValues[j];
                    double resultImg  = realValues[i]*imgValues[j]  + imgValues[i]*realValues[j];
                    
                    assertEquals("Result of mul operation is incorrect (real part)", resultReal, cv.mul(cv2).getValue(), delta);
                    assertEquals("Result of mul operation is incorrect (img part)", resultImg, cv.mul(cv2).getImg(), delta);
                }
                catch (ParseException ex) {
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
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    double resultReal = (realValues[i]*realValues[j] + imgValues[i]*imgValues[j]) / (realValues[j] * realValues[j] + imgValues[j] * imgValues[j]);
                    double resultImg  = (realValues[j]*imgValues[i]  - imgValues[j]*realValues[i]) / (realValues[j] * realValues[j] + imgValues[j] * imgValues[j]);

                    assertEquals("Result of div operation is incorrect (real part)", resultReal, cv.div(cv2).getValue(), delta);
                    assertEquals("Result of div operation is incorrect (img part)", resultImg, cv.div(cv2).getImg(), delta);
                }
                catch (ParseException | FiMaLibDivisionByZeroException ex) {
                    if((realValues[j] == 0.0) && (imgValues[j] == 0.0))
                        assertFalse("There should not be an exception", false);
                    else
                        assertTrue("There should not be an exception", false);
                }
            }
        }
    }

    String[][] powResults = {
        { "48039905786754560-321469350635016190i", "-0.078-0.017i", "0.024+0.091i", "-3.152-0.087i", "-2132082384030693630+964243982171428220i" },
        { "-205765.525-53489.052i", "-0.028+0.061i", "-1000236621616.329-1618664634006.331i", "198601323.212+87832353.695i", "0-0i" },
        { "127519904350025.6-53803605834730.4i", "-0.245+0.317i", "-0-0i", "-0+0i", "912280273630894800000-105919826933784310000i" },
        { "-490041187168.768+160294040351.541i", "-0.487+0.201i", "0-0i", "0-0i", "685155914498241920+709823206457603330i" },
        { "-2098320560361501180+6795611591059148800i", "-0.028-0.027i", "-120.336+145.196i", "-544.177-13.464i", "43945932517728720-78901222603746656i" }
    };
    
    @Test
    public void testPow() {
        // For pow, use only a few tests (10-14):
        // "15+3i", "-1-i", "12i", "1+8i", "15-7i"
        
        for(int i=10; i<15; i++) {
            for(int j=10; j<15; j++) {
                try {
                    Complex cv = new Complex(testValues[i], format);
                    Complex cv2 = new Complex(testValues[j], format);
                    
                    assertEquals("Result of pow operation is incorrect (" + testValues[i] + "^" + testValues[j] + ")", powResults[i-10][j-10], cv.pow(cv2).toString());
                    
                }
                catch (ParseException ex) {
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
                Complex cv = new Complex(testValues[i], format);
                double result = Math.sqrt(realValues[i] * realValues[i] + imgValues[i] * imgValues[i]);
                Complex cvres = (Complex)(cv.abs());
                
                assertEquals("Values should be identical (real part)", result, cvres.getValue(), delta);
                assertEquals("Values should be identical (img part)", 0.0, cvres.getImg(), delta);
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    @Test
    public void testSgn() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                double result = Math.signum(realValues[i]);
                Complex cvres = (Complex)(cv.sgn());
                
                assertEquals("Values should be identical (real part)", result, cvres.getValue(), delta);
                assertEquals("Values should be identical (img part)", 0.0, cvres.getImg(), delta);
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] sqrtResults = {
        "5.148", "3.623", "2.646", "##ERR##", "0+7.416i", "11111.111", "0+0.003i",
        "1.772", "1.474", "0+1i", "3.892+0.385i", "0.455-1.099i", "2.449+2.449i",
        "2.129+1.879i", "3.972-0.881i"
    };
    
    @Test
    public void testSqrt() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.sqrt());
                
                if(!sqrtResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", sqrtResults[i], cvres.toString());
                    
                    // Do the control:
                    
                    Complex cvinv = cvres.mul(cvres);
                    assertEquals("Values should be equal (real part)", realValues[i], cvinv.getValue(), delta);
                    assertEquals("Values should be equal (img part)", imgValues[i], cvinv.getImg(), delta);
                }
            }
            catch (ParseException | FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] expResults = {
        "322703570371.155", "500318.413", "1096.633", "1", "0", "##ERR##", "1", 
        "23.139", "8.774", "0.368", "-3236302.67+461323.758i", "0.199-0.31i",
        "0.844-0.537i", "-0.396+2.689i", "2464519.567-2147700.605i"
    };
    
    @Test
    public void testExp() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.exp());
                
                if(!expResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", expResults[i], cvres.toString());
                    
                    // Do the control:
                    
                    Complex cvinv = (Complex)cvres.ln();
                    assertEquals("Values should be equal (real part)", realValues[i], cvinv.getValue(), 0.01);
                    if(cvinv.getImg() != imgValues[i]) {
                        double numPis = (imgValues[i] - cvinv.getImg()) / Math.PI;
                        
                        assertEquals("Values should be integer # of Pis: ", numPis, Math.round(numPis), delta);
                    }
                    else
                        assertEquals("Values should be equal (img part)", imgValues[i], cvinv.getImg(), 0.01);
                }
            }
            catch (ParseException | FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] lnResults = {
        "3.277", "2.574", "1.946", "-∞", "4.007+3.142i", "18.631", "-11.513+3.142i",
        "1.145", "0.776", "0+3.142i", "2.728+0.197i", "0.347-2.356i",
        "2.485+1.571i", "2.087+1.446i", "2.807-0.437i"
    };
    
    @Test
    public void testLn() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.ln());
                
                if(!lnResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", lnResults[i], cvres.toString());
                    
                    // Do the control:
                    
                    Complex cvinv = (Complex)cvres.exp();
                    assertEquals("Values should be equal (real part)", realValues[i], cvinv.getValue(), 0.01);
                    if(cvinv.getImg() != imgValues[i]) {
                        double numPis = (imgValues[i] - cvinv.getImg()) / Math.PI;
                        
                        assertEquals("Values should be integer # of Pis: ", numPis, Math.round(numPis), delta);
                    }
                    else
                        assertEquals("Values should be equal (img part)", imgValues[i], cvinv.getImg(), 0.01);
                }
            }
            catch (ParseException | FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] logResults = {
        "1.423", "1.118", "0.845", "##ERR##", "1.74+1.364i", "8.092", 
        "-5+1.364i", "0.497", "0.337", "0+1.364i", "1.185+0.086i",
        "0.151-1.023i", "1.079+0.682i", "0.906+0.628i", "1.219-0.19i"
    };
    
    @Test
    public void testLog() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.log());
                
                if(!logResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", logResults[i], cvres.toString());
                    
                    // Do the control:
                    
                    Complex cvinv = new Complex(10.0, 0.0, this.format).pow(cvres);
                    assertEquals("Values should be equal (real part)", realValues[i], cvinv.getValue(), 0.01);
                    if(cvinv.getImg() != imgValues[i]) {
                        double num = (imgValues[i] - cvinv.getImg());
                        
                        assertEquals("Values should be integer # of Pis: ", num, Math.round(num), delta);
                    }
                    else
                        assertEquals("Values should be equal (img part)", imgValues[i], cvinv.getImg(), 0.01);
                } 
            }
            catch (ParseException | FiMaLibUndefinedException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    /* =======================================================================
    * Testing trigonometric functions
    * ========================================================================
    */
    
    String[] sinResults = {
        "0.979", "0.528", "0.657", "0", "1", "0.999", "-0", "0", "0.825", 
        "-0.841", "6.547-7.61i", "-1.298-0.635i", "0+81377.396i", 
        "1254.195+805.309i", "356.564+416.549i"
    };
    
    @Test
    public void testSin() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.sin());
                
                if(!sinResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", sinResults[i], cvres.toString());
                }                    
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] cosResults = {
        "0.202", "0.849", "0.754", "1", "0.022", "0.041", "1", "-1", "-0.565", 
        "0.54", "-7.648-6.515i", "0.834-0.989i", "81377.396", 
        "805.309-1254.195i", "-416.55+356.563i"
    };
    
    @Test
    public void testCos() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.cos());
                
                if(!cosResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", cosResults[i], cvres.toString());
                }                    
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] tanResults = {
        "4.845", "0.622", "0.871", "0", "45.183", "24.542", "-0", "-0", "-1.459", 
        "-1.557", "-0.005+0.999i", "-0.272-1.084i", "0+1i", "0+1i", "-0-1i" 
    };
    
    @Test
    public void testTan() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.tan());
                
                if(!tanResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", tanResults[i], cvres.toString());
                }
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] cotResults = {
        "0.206", "1.607", "1.148", "##ERR##", "0.022", "0.041", "-99999.992", "-10792.89", "-0.686", 
        "-0.642", "-0.005-1.001i", "-0.218+0.868i", "0-1i", "0-1i", "-0+1i" 
    };
    
    @Test
    public void testCot() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.cot());
                
                if(!cotResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", cotResults[i], cvres.toString());
                }
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] secResults = {
        "4.947", "1.178", "1.326", "1", "45.194", "24.563", "1", "-1", "-1.768", 
        "1.851", "-0.076+0.065i", "0.498+0.591i", "0", "0+0.001i", "-0.001-0.001i" 
    };
    
    @Test
    public void testSec() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.sec());
                
                if(!secResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", secResults[i], cvres.toString());
                }
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    String[] cscResults = {
        "1.021", "1.893", "1.522", "##ERR##", "1", "1.001", "-100000", "10792.89", "1.212", 
        "-1.188", "0.065-0.076i", "-0.622-0.304i", "0+0i", "0.001+0i", "0.001+0.001i" 
    };
    
    @Test
    public void testCsc() {
        for(int i=0; i<testValues.length; i++) {
            try {
                Complex cv = new Complex(testValues[i], format);
                Complex cvres = (Complex)(cv.csc());
                
                System.out.println("Testing: " + cv + ", result: " + cvres);
                if(!cscResults[i].equals("##ERR##")) {
                    assertEquals("Values should be equal", cscResults[i], cvres.toString());
                }
            }
            catch (ParseException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
}
