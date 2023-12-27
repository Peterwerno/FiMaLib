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

import org.fimalib.calc.matrix.LESSolver;

/**
 *
 * @author peter
 */
public class LESSolverTest {
    
    public LESSolverTest() {
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
    
    String[] allTests = new String[] {
            "((1,2,3,5),(4,5,6,7),(7,8,10,11))|(1,2,1)",
            "((2,4),(3,7),(2,2))|(2,1,0)",
            "((1,0,0,0,0),(2,3,0,0,0),(1,2,4,0,0),(3,4,1,3,0),(0,1,2,1,3))|(1,2,3,4,5)",
            };
    
    String[] allResults = new String[] {
            "((1,0,0,-1.667),(-0,1,0,0.333),(0,0,1,2))|(-2.333,4.667,-2)",
            "((1,0),(0,1),(2,2))|(5,-2,0)",
            "((1,0,0,0,0),(0,1,0,0,0),(0,0,1,0,0),(0,0,0,1,0),(0,0,0,0,1))|(1,0,0.5,0.167,1.278)",
            };
    
    boolean[] exceptions = new boolean[] {
            false,
            false,
            false,
            };

    @Test
    public void solverTest() throws Exception {
        for(int i=0; i<allTests.length; i++) {
            try {
                LESSolver solver = new LESSolver(allTests[i]);
                solver.solve();
                
                System.out.println("Result of solver: " + solver.toString());
                assertEquals("LES Result incorrect", allResults[i], solver.toString());
                assertFalse("This LES should cause an exception " + allTests[i], exceptions[i]);
            }
            catch (MatrixException ex) {
                assertTrue("This LES should NOT cause an exception " + allTests[i], exceptions[i]);
            }
        }
    }
}
