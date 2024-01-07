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
package org.fimalib.calc.formula.functions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.Formula;
import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.formula.nodes.Node;

/**
 * This class implements user-defined functions.
 * 
 * Users can freely define functions and add them to the FiMaLib formula
 * tree generator, e.g. "f(x)=3*x^3-2*x+5"
 * 
 * @author peter
 */
public class UserDefinedFunction extends Function {
    NumberFormat format;
    String functionName;
    
    Node definition;
    
    /**
     * Creates a new instance of UserDefinedFunction with a string-encoded
     * function definition and a number format.
     * 
     * The function is encoded as the function name with parameter(s) in 
     * brackets, then "=" and the function definition,
     * 
     * e.g.: "f(x)=3*sin(x)"
     * 
     * @param function (String) the function
     * @param format (NumberFormat) the format
     * @throws org.fimalib.calc.formula.FormulaException
     */
    public UserDefinedFunction(String function, NumberFormat format) throws FormulaException {
        int pos = function.indexOf("=");    // separator of function declaration and definition
        
        if(pos < 0) throw new FormulaException("Incorrect syntax for user defined function: " + function);
        
        String dec = function.substring(0, pos);
        String def = function.substring(pos+1);
        
        pos = dec.indexOf("(");
        this.functionName = dec.substring(0, pos);
        
        dec = dec.substring(pos+1);
        if(!dec.endsWith(")"))
            throw new FormulaException("Incorrect syntax for user defined function: " + function);
        
        dec = dec.substring(0, dec.length()-1);
        
        StringTokenizer st = new StringTokenizer(dec, ",");
        ArrayList<String> paramList = new ArrayList<>();
        while(st.hasMoreElements()) {
            paramList.add(st.nextToken());
        }
        
        this.parameterNames = new String[paramList.size()];
        this.parameterNodes = new Node[paramList.size()];
        
        for(int i=0; i<paramList.size(); i++) {
            this.parameterNames[i] = paramList.get(i);
        }
        
        definition = Formula.parse(def, format);
        
        this.format = format;
    }
    
    /**
     * Creates a new instance of UserDefinedFunction when creating a copy
     * of the original function.
     * 
     * @param functionName (String) the function name
     * @param parameterNames (String[]) the parameter names
     * @param definition (Node) the function definition
     * @param format (NumberFormat) the format
     */
    private UserDefinedFunction(String functionName, String[] parameterNames, Node definition, NumberFormat format) {
        this.functionName = functionName;
        this.parameterNames = parameterNames;
        this.parameterNodes = new Node[this.parameterNames.length];
        this.definition = definition;
        this.format = format;
    }

    /**
     * Creates a copy of the user defined function.
     * 
     * @return a copy of the original function (UserDefinedFunction)
     */
    public UserDefinedFunction copy() {
        return new UserDefinedFunction(this.functionName, this.parameterNames, this.definition, this.format);
    }
    
    /**
     * Returns the minimum required number of parameters.
     * 
     * @return the minimum parameter number (int)
     */
    @Override
    public int getMinimumParameters() {
        return this.parameterNames.length;
    }

    /**
     * Returns the maximum supported number of parameters.
     * 
     * @return the maximum parameter number (int)
     */
    @Override
    public int getMaximumParameters() {
        return this.parameterNames.length;
    }

    /**
     * Calculates the value of the user defined function with a given parameter
     * set.
     * 
     * @param parameters (HashMap) the parameter(s)
     * @return the result of the calculation
     * @throws FiMaLibCalcException 
     */
    @Override
    public Number calculate(HashMap<String, Number> parameters) throws FiMaLibCalcException {
        HashMap<String, Number> newParameters = new HashMap<>();
        
        for(int i=0; i<this.parameterNames.length; i++) {
            Number result = this.parameterNodes[i].calculate(parameters);
            
            newParameters.put(this.parameterNames[i], result);
        }
        
        return this.definition.calculate(newParameters);
    }

    /**
     * Returns the derivative of the function
     * 
     * @param parameterName (String) the parameter by which to derive
     * @return the derived function (Node)
     * @throws FormulaException 
     */
    @Override
    public Node derive(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node integrate(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return this.functionName;
    }

    @Override
    public int getLevel() {
        return Node.LEVEL_FUNCTION_CONST;
    }

    @Override
    public String toString() {
        boolean printDefinition = true;
        
        StringBuilder retVal = new StringBuilder(this.functionName);
        retVal.append("(");
        for(int i=0; i<this.parameterNames.length; i++) {
            if(i>0) retVal.append(",");
            if(this.parameterNodes[i] != null) {
                printDefinition = false;
                retVal.append(this.parameterNodes[i].toString());
            }
            else 
                retVal.append(this.parameterNames[i]);
        }
        retVal.append(")");
        
        if(printDefinition) {
            retVal.append("=");
            retVal.append(this.definition.toString());
        }
        
        return retVal.toString();
    }
    
}
