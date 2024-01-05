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
import org.fimalib.calc.Boolean;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.formula.nodes.Node;

/**
 * The If class implements an if function.
 * It takes two or three subnodes/parameters. The first parameter is the
 * condition and should result in a boolean value.
 * The second part is to be executed when the condition returns "true" and
 * the (optional) third one is to be executed when the condition is "false".
 * It no third parameter is provided and the condition is false, then a zero
 * number is returned
 * 
 * @author Peter Werno
 */
public class If extends Function {
    /**
     * Creates a new instance of If
     */
    public If() {
        super(3);
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
        Number condition = parameterNodes[0].calculate(parameters);
        
        if(condition instanceof Boolean) {
            Boolean cond = (Boolean)condition;
            
            if(cond.getBooleanValue()) return parameterNodes[1].calculate(parameters);
            
            if((parameterNodes.length == 3) && (parameterNodes[2] != null))
                return parameterNodes[2].calculate(parameters);
            
            return new Double(0.0, condition.getNumberFormat());
        }
        else
            throw new FiMaLibCalcException("Error in If funtion, condition is not boolean");
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

    /**
     * Returns the integration function of the function
     * 
     * @param parameterName (String) the parameter by which to integrate
     * @return the integrated function (Node)
     * @throws FormulaException 
     */
    @Override
    public Node integrate(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns the function name
     * 
     * @return the name (String)
     */
    @Override
    public String getName() {
        return "if";
    }

    /**
     * Returns the formula encoded as string
     * 
     * @return the formula (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("if(");
        retVal.append(this.parameterNodes[0].toString());
        retVal.append(",");
        retVal.append(this.parameterNodes[1]);
        if((this.parameterNodes.length == 3) && (this.parameterNodes[2] != null)) {
            retVal.append(",");
            retVal.append(parameterNodes[2].toString());
        }
        retVal.append(")");
        
        return retVal.toString();
    }

    /**
     * Returns the minimum number of parameters
     * 
     * @return the minimum number of parameters (int)
     */
    @Override
    public int getMinimumParameters() {
        return 2;
    }

    /**
     * Returns the maximum number of parameters
     * 
     * @return the maximum number of parameters (int)
     */
    @Override
    public int getMaximumParameters() {
        return 3;
    }

    /**
     * Returns the level of the node
     * 
     * @return the level (int)
     */
    @Override
    public int getLevel() {
        return Node.LEVEL_FUNCTION_CONST;
    }
    
}
