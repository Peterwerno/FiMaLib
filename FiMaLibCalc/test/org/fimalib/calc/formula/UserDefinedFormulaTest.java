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
package org.fimalib.calc.formula;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.HashMap;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.functions.UserDefinedFunction;
import org.fimalib.calc.formula.nodes.Node;

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
public class UserDefinedFormulaTest {
    
    public UserDefinedFormulaTest() {
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
    
    String[] functions = {
        "cube(x)=x^3",
        "mycot(x)=1/tan(x)",
        "pythagoras(a,b)=sqrt(a^2+b^2)",
        "sumup(x)=sum(v,1,x,v)",
        "polygon(x)=3*x^4-2*x^3+x^2-3*x+5",
        "pythagorasddd(a,b,c)=sqrt(a^2+b^2+c^2)",
        
    };
    
    String[] usageFormula = {
        "cube(3*x)",
        "mycot(0.5)",
        "pythagoras(3,4)==5",
        "sumup(10)",
        "polygon(-3)",
        "pythagorasddd(3,4,5)",
    };
    
    String[] results = {
        "216",
        "1.83",
        "true",
        "55",
        "320",
        "7.071",
    };

    @Test
    public void testUDF() {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        for(int i=0; i<functions.length; i++) {
            try {
                UserDefinedFunction udf = new UserDefinedFunction(functions[i], nf);
                System.out.println("User Defined Function: " + udf.toString());
                
                Formula.addUserDefinedFunction(udf);
                
                Node node = Formula.parse(usageFormula[i], nf);
                node.optimize();
                System.out.println("Usage Formula: " + node.toString());
                
                HashMap<String, Number> parameters = new HashMap<>();
                parameters.put("x", new Double(2.0, nf));
                
                Number result = node.calculate(parameters);
                
                System.out.println("result: " + result);
                
                assertEquals("Calculation Result incorrect", this.results[i], result.toString());
            }
            catch (Exception ex) {
                assertFalse("There should not be an exception", true);
            }
        }
    }
}
