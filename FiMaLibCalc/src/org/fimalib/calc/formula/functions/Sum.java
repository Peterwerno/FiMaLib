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

import java.util.HashMap;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.formula.nodes.Node;
import org.fimalib.calc.formula.nodes.Variable;

/**
 * This class implements a summation function.
 * It takes 4 parameters.
 * The first parameter is the name of the running variable
 * The second parameter is the start value,
 * the third parameter is the end value (both INCLUDING)
 * the fourth parameter is the formula which is repeatedly calculated and
 * summed up
 * 
 * @author Peter Werno
 */
public class Sum extends Function {
    String parameter;
    Node startValue;
    Node endValue;
    Node formula;
    
    /**
     * Creates a new instance of Sum
     */
    public Sum() {
        super(4);
    }
    
    /**
     * Creates a new instance of Sum with all parameters given
     * 
     * @param parameter (Node) the parameter node
     * @param startValue (Node) the start value
     * @param endValue (Node) the end value
     * @param formula (Node) the formula that is to be summed up
     * @throws FormulaException 
     */
    public Sum(Node parameter, Node startValue, Node endValue, Node formula) throws FormulaException {
        super(4);
        
        if(parameter instanceof Variable) {
            Variable param = (Variable)parameter;
            
            this.parameter = param.getName();
        }
        else
            throw new FormulaException("First parameter of sum function must be a variable name!");
        this.startValue = startValue;
        this.endValue = endValue;
        this.formula = formula;
    }

    /**
     * Creates a new instance of Sum with all parameters given
     * 
     * @param parameter (String) the parameter name
     * @param startValue (Node) the start value
     * @param endValue (Node) the end value
     * @param formula (Node) the formula that is to be summed up
     */
    public Sum(String parameter, Node startValue, Node endValue, Node formula) {
        super(4);
        
        this.parameter = parameter;
        this.startValue = startValue;
        this.endValue = endValue;
        this.formula = formula;
    }

    /**
     * Sets a parameter node
     * 
     * @param nodeNum (int) the parameter/node number
     * @param node (Node) the node
     */
    @Override
    public void setParameterNode(int nodeNum, Node node) {
        switch (nodeNum) {
            case 0: // run variable/parameter name
                if(node instanceof Variable) {
                    Variable var = (Variable)node;
                    this.parameter = var.getName();
                }
                else
                    this.parameter="x";
                break;
                
            case 1: // start Value
                this.startValue = node;
                break;
                
            case 2: // end value
                this.endValue = node;
                break;
                
            case 3: // formula
                this.formula = node;
                break;
                
            default:
                throw new AssertionError("Sum only takes 4 paraeters");
        }
    }
    
    /**
     * Returns the number of parameters that are at least required (4)
     * 
     * @return 4 (int)
     */
    @Override
    public int getMinimumParameters() {
        return 4;
    }

    /**
     * Returns the maximum number of parameters that allowed (4)
     * 
     * @return 4 (int)
     */
    @Override
    public int getMaximumParameters() {
        return 4;
    }

    /**
     * Calculates the value of the formula node with a given parameter set
     * 
     * @param parameters (HashMap) the parameter(s)
     * @return the result of the calculation
     * @throws FiMaLibCalcException 
     */
    @Override
    public Number calculate(HashMap<String, Number> parameters) throws FiMaLibCalcException {
        Number startVal = this.startValue.calculate(parameters);
        Number endVal = this.endValue.calculate(parameters);
        boolean containsParameter = false;
        Number oldParameter = null;
        
        // If same parameter/variabel already in use, back up
        if(parameters.containsKey(this.parameter)) {
            containsParameter = true;
            oldParameter = parameters.get(this.parameter);
        }
        
        Number value = startVal.copy();
        Number retVal = new Double(0.0, value.getNumberFormat());
        Number one = new Double(1.0, value.getNumberFormat());
        
        if(value.compareTo(endVal) > 0) return retVal;
        
        while(value.compareTo(endVal) <= 0) {
            parameters.put(this.parameter, value);
            retVal = retVal.add(this.formula.calculate(parameters));
            value = value.add(one, false);
        }
        
        // restore old parameter
        if(containsParameter) {
            parameters.put(this.parameter, oldParameter);
        }
        
        return retVal;
    }

    /**
     * Returns the derivative of the sum function
     * 
     * @param parameterName (String) the variable name by which to integrate
     * @return the integrated funtion (Node)
     * @throws FormulaException 
     */
    @Override
    public Node derive(String parameterName) throws FormulaException {
        return new Sum(this.parameter, this.startValue, this.endValue, this.formula.derive(parameterName));
    }

    /**
     * Returns the integration of the sum function
     * 
     * @param parameterName (String) the variable name by which to integrate
     * @return the integrated function (Node)
     * @throws FormulaException 
     */
    @Override
    public Node integrate(String parameterName) throws FormulaException {
        return new Sum(this.parameter, this.startValue, this.endValue, this.formula.integrate(parameterName));
    }

    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public int getLevel() {
        return Node.LEVEL_FUNCTION_CONST;
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("sum(");
        retVal.append(this.parameter);
        retVal.append(",");
        retVal.append(this.startValue.toString());
        retVal.append(",");
        retVal.append(this.endValue.toString());
        retVal.append(",");
        retVal.append(this.formula.toString());
        retVal.append(")");
        
        return retVal.toString();
    }
    
}
