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
import org.fimalib.calc.Complex;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.nodes.Node;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Locale;
import org.fimalib.calc.FiMaLibCalcException;

/**
 * This test class tests the formula class, all the nodes in the "nodes" sub-
 * package and the fuctions in the "functions" sub-package
 * 
 * @author Peter Werno
 */
public class FormulaTest {
    
    public FormulaTest() {
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
    
    // some tests for real numbers
    String[] formulas = {
        "(15-2+4^2*3+8+x^2+sin(1)^tan(x))/(-5)",
        "if(x<5,if(x>2,1,2),3)",
        "prod(x,1,10,x)",
        "sum(x,1,10,x)",
        "sum(x,1,10,sin(x))",
        "sin(pi)",
        "cos(pi)",
        "abs(sum(x,-10,-1,x))",
        "x^3+2*x^2+3*x-5",
        "(x>1)&&(x<3)",
        "(x>3)&&(x<5)",
        "(x<0)||(x<3)",
        "arccos(0.75)",
        "arccosh(1.3)",
        "arccot(0.23)",
        "arccoth(3)",
        "sum(x,1,10,x<5)",
        "arccsc(1.6)",
        "arccsch(3.5)",
        "arcsec(23.5)",
        "arcsech(0.7)",     // TODO: Define arcsech for larger values!!
        "arcsin(sqrt(2.0)/2)",
        "arcsinh(12)",
        "arctan(100)",
        "arctanh(0.3)",
        "cos(pi*3/8)",
        "cosh(8)",
        "cot(5)",
        "coth(1.234)",
        "csc(4.2)",
        "csch(1.45)",
        "28/3/5",
        "17==4",
        "x==2",
        "exp(pi)",
        "ln(exp(pi))",
        "x>=2",
        "x>2",
        "int(6.725)",
        "x<=2",
        "x<2",
        "ln(2)",
        "log(1000)",
        "17*4*8*12",
        "neg(99)",
        "x!=3",
        "(x==2)||(x==5)",
        "5^5",
        "1.02^2000",
        "rand(5)<=5",
        "sec(4)",
        "sech(0.9)",
        "sgn(x)",
        "sin(pi/4)",
        "sinh(pi/4)",
        "sqrt(3^2+4^2)",
        "17-3-2-1",
        "tan(0.5)",
        "tanh(0.5)",
        "x",
        "(x==2)##(x==5)",
    };
    
    String[] results = {
        "-14.892",
        "2",
        "3628800",
        "55",
        "1.411",
        "0",
        "-1",
        "55",
        "17",
        "true",
        "false",
        "true",
        "0.723",
        "0.756",
        "1.345",
        "0.347",
        "4",
        "0.675",
        "0.282",
        "1.528",
        "0.896",
        "0.785",
        "3.18",
        "1.561",
        "0.31",
        "0.383",
        "1490.479",
        "-0.296",
        "1.185",
        "-1.147",
        "0.496",
        "1.867",
        "false",
        "true",
        "23.141",
        "3.142",
        "true",
        "false",
        "6",
        "true",
        "false",
        "0.693",
        "3",
        "6528",
        "-99",
        "true",
        "true",
        "3125",
        "158614732760376800",
        "true",
        "-1.53",
        "0.698",
        "1",
        "0.707",
        "0.869",
        "5",
        "11",
        "0.546",
        "0.462",
        "2",
        "true",
    };
    
    @Test
    public void testFormula() {
        HashMap<String,Number> parameters = new HashMap<>();
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setGroupingUsed(false);
        
        parameters.put("x", new Double(2.0, nf));
        
        for(int i=0; i<formulas.length; i++) {
            try {
                System.out.println("Formula testing: " + formulas[i]);
                Node node = Formula.parse(formulas[i], nf);
                Number result = node.calculate(parameters);
                System.out.println("result (x=2): " + result);
                
                assertEquals("Result of function does not match", results[i], result.toString());
            }
            catch (FiMaLibCalcException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
    // some tests for complex numbers
    
    String[] complexFormulas = {
        "sqrt(-1+0i)",
        "abs(3+4i)",
        "5+3i+7-2i-3-1i",
        "x==2+1i",
        "x==2+2i",
        "cos(x)",
        "cot(x)",
        "csc(x)",
        "(5+3i)/x",
        "exp(x)",
        "ln(x)",
        "log(x)",
        "x*(8-3i)",
        "x!=5",
        "x!=2+1i",
        "5^x",
        "sec(x)",
        "sin(x)",
        "sqrt(x)",
        "x-5i",
        "tan(x)",
        "arcsin(x)",
        "arccos(x)",
        "arctan(x)",
        "arccot(x)",
        "arcsec(x)",
        "arccsc(x)",
        "sinh(x)",
        "cosh(x)",
        "tanh(x)",
        "coth(x)",
        "sech(x)",
        "csch(x)",
        "arcsinh(x)",
        "arccosh(x)",
        "arctanh(x)",
        "arccoth(x)",
        "arcsech(x)",
        "arccsch(x)",
    };
    
    String[] complexResults = {
        "0+1i",
        "5",
        "9",
        "true",
        "false",
        "-0.642-1.069i",
        "-0.171-0.821i",
        "0.635-0.222i", // TODO: Wolfram alpha says +0.222i!!
        "2.6+0.2i",
        "3.992+6.218i",
        "0.805+0.464i",
        "0.349+0.201i",
        "19+2i",
        "true",
        "false",
        "-0.966+24.981i",
        "-0.413+0.688i",
        "1.403-0.489i",
        "1.455+0.344i",
        "2-4i",
        "-0.243+1.167i",
        "1.063+1.469i",
        "0.507-1.469i",
        "1.178+0.173i",
        "0.393-0.173i",
        "-1.169-0.216i", // TODO: Wolfram alpha says 1.169+0.216i
        "0.402-0.216i",
        "1.96+3.166i",
        "2.033+3.052i",
        "1.015+0.034i",
        "0.984-0.033i",
        "0.151-0.227i",
        "0.141-0.228i",
        "1.529+0.427i",
        "1.469+0.507i",
        "0.402+1.339i",
        "0.402-0.232i",
        "0.216-1.169i",
        "0.397-0.186i",
    };
    
    @Test
    public void testComplexFormula() {
        HashMap<String,Number> parameters = new HashMap<>();
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setGroupingUsed(false);
        
        parameters.put("x", new Complex(2.0, 1.0, nf));
        
        for(int i=0; i<complexFormulas.length; i++) {
            try {
                System.out.println("Complex formula testing: " + complexFormulas[i]);
                Node node = Formula.parse(complexFormulas[i], nf);
                Number result = node.calculate(parameters);
                System.out.println("result (x=2+1i): " + result);
                
                assertEquals("Result of function does not match", complexResults[i], result.toString());
            }
            catch (FiMaLibCalcException ex) {
                assertTrue("There should not be an exception", false);
            }
        }
    }
    
}
