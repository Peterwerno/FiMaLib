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
package org.fimalib.calc.matrix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.fimalib.calc.matrix.Matrix;

/**
 *
 * @author peter
 */
public class MatrixTest {
    private static final double delta = 0.0001;
    
    public MatrixTest() {
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

    String[] testMatrices = new String[] {
                "((1,2),(3,4))",
                "(((",
                "((1,0.5,2),(1,3,4))",
                "((1,2,3,4),(2,3,4,5),(1,2))",
            };
    
    boolean[] exceptions = new boolean[] {
            false,
            true,
            false,
            true,
            };
    
    
    @Test
    public void testMatrixConstructor1() {
        // First test, initialize a matrix with all zeros
        System.out.println("Testing Matrix Constructor (1)");
        Matrix mat = new Matrix(3, 3);
        String result = mat.toString();
        
        System.out.println("Result: " + result);
        assertEquals("Matrix should be all zeros (3x3)", "((0,0,0),(0,0,0),(0,0,0))", result);
    }
    
    @Test
    public void testMatrixConstructor2() {
        // Second test, initialize a matrix with diagonal set
        System.out.println("Testing Matrix Constructor (2)");
        Matrix mat = new Matrix(3, 3, 1.0);
        String result = mat.toString();
        
        System.out.println("Result: " + result);
        assertEquals("Matrix should be diagonal ones (3x3)", "((1,0,0),(0,1,0),(0,0,1))", result);
        
        mat = new Matrix(15, 2, 3.0);
        result = mat.toString();
        System.out.println("Result: " + result);
        assertEquals("Matrix should be diagonal 3 (15x2)", "((3,0),(0,3),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0),(0,0))", result);
    }
    
    @Test
    public void testMatrixConstructor3() {
        // Third test, initialize a matrix with an initialization string
        System.out.println("Testing Matrix Constructor (3)");
        
        for(int i=0; i<testMatrices.length; i++) {
            try {
                Matrix mat = new Matrix(testMatrices[i]);
                String result = mat.toString();
                System.out.println("Result: " + result);
                assertEquals("Matrix incorrectly constructed from string", testMatrices[i], result);
                assertFalse("Constructing this matrix SHOULD throw an exception: " + testMatrices[i], exceptions[i]);
                
                System.out.println("Result of add: " + mat.add(1.5, true));
            }
            catch (MatrixException ex) {
                assertTrue("Constructing this matrix should not throw an exception: " + testMatrices[i], exceptions[i]);
            }
        }
    }
    
    @Test
    public void testMatrixScalarAddition() throws Exception {
        Matrix mat = new Matrix("((1,2,3),(4,5,6),(7,8,9))");
        Matrix result = mat.add(2.0,true);
        assertEquals("Matrix addition incorrect", "((3,4,5),(6,7,8),(9,10,11))", result.toString());
        
        mat = new Matrix("((1,2,3),(4,5,6))");
        result = mat.add(15.0,true);
        assertEquals("Matrix addition incorrect", "((16,17,18),(19,20,21))", result.toString());
    }
    
    @Test
    public void testMatrixScalarSubtraction() throws Exception {
        Matrix mat = new Matrix("((1,2,3),(4,5,6),(7,8,9))");
        Matrix result = mat.sub(2.0,true);
        assertEquals("Matrix subtraction incorrect", "((-1,0,1),(2,3,4),(5,6,7))", result.toString());
        
        mat = new Matrix("((1,2,3),(4,5,6))");
        result = mat.sub(15.0,true);
        assertEquals("Matrix subtraction incorrect", "((-14,-13,-12),(-11,-10,-9))", result.toString());
    }
    
    @Test
    public void testMatrixScalarMultiplication() throws Exception {
        Matrix mat = new Matrix("((1,2,3),(4,5,6),(7,8,9))");
        Matrix result = mat.mul(2.0,true);
        assertEquals("Matrix multiplication incorrect", "((2,4,6),(8,10,12),(14,16,18))", result.toString());
        
        mat = new Matrix("((1,2,3),(4,5,6))");
        result = mat.mul(15.0,true);
        assertEquals("Matrix multiplication incorrect", "((15,30,45),(60,75,90))", result.toString());
    }
    
    @Test
    public void testMatrixScalarDivision() throws Exception {
        Matrix mat = new Matrix("((1,2,3),(4,5,6),(7,8,9))");
        Matrix result = mat.div(2.0,true);
        assertEquals("Matrix division incorrect", "((0.5,1,1.5),(2,2.5,3),(3.5,4,4.5))", result.toString());
        
        mat = new Matrix("((1,2,3),(4,5,6))");
        result = mat.div(15.0,true);
        assertEquals("Matrix division incorrect", "((0.067,0.133,0.2),(0.267,0.333,0.4))", result.toString());
    }
    
    @Test
    public void testMatrixMultiplication() throws Exception {
        Matrix mat1 = new Matrix("((1,2),(3,4))");
        Matrix mat2 = new Matrix("((2,0),(0,2))");
        
        System.out.println("result of mul: " + mat1.mul(mat2));
    }
    
    @Test
    public void testMatrixInversion() throws Exception {
        Matrix mat = new Matrix("((4,6),(3,8))");
        Matrix result = mat.invert(true);
        System.out.println("result of inversion: " + result);
        assertEquals("Matrix inversion failed", result.toString(), "((0.571,-0.429),(-0.214,0.286))");
        
        mat = new Matrix("((1,2,1),(2,1,1),(2,0,0))");
        result = mat.invert(true);
        System.out.println("result of inversion: " + result);
        assertEquals("Matrix inversion failed", result.toString(), "((0,0,0.5),(1,-1,0.5),(-1,2,-1.5))");
    }
    
    @Test
    public void testMatrixDet() throws Exception {
        Matrix mat = new Matrix("((4,6),(3,8))");
        double det = mat.getDet();
        
        assertEquals("Determinant calculation incorrect", 14.0, det, delta);
        
        mat = new Matrix("((6,1,1),(4,-2,5),(2,8,7))");
        det = mat.getDet();
        
        assertEquals("Determinant calculation incorrect", -306.0, det, delta);
    }
}
