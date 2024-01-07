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
package org.fimalib.calc.formula.nodes;

import java.util.HashMap;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.FormulaException;

/**
 * This class implements the trigonometrical function tan
 * (tangent)
 * 
 * @author Peter Werno
 */
public class Tan extends Node {
    /**
     * Creates a new instance of Tan
     */
    public Tan() {
        super(1);
    }
    
    /**
     * Creates a new instance of Tan with given subnode
     * 
     * @param subNode (Node) the subnode
     */
    public Tan(Node subNode) {
        super(1);
        this.subNodes[0] = subNode;
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
        return this.subNodes[0].calculate(parameters).tan();
    }

    /**
     * Returns the derivative of the node
     * 
     * @param parameterName (String) the variable by with to derive
     * @return the derivative (Node)
     * @throws FormulaException 
     */
    @Override
    public Node derive(String parameterName) throws FormulaException {
        Node subDer = this.subNodes[0].derive(parameterName);
        Double two = new Double(2.0);
        
        if(subDer.isNumber()) {
            try {
                Number result = subDer.calculate(null);
                Double one = new Double(1.0, result.getNumberFormat());
                
                if(result.equals(one)) {
                    return new Pow(new Sec(this.subNodes[0].copy()), new Constant(two));
                }
            }
            catch (FiMaLibCalcException ex) {
                throw new FormulaException("Error deriving sin function", ex);
            }
        }
        
        return new Mul(subDer, new Pow(new Sec(this.subNodes[0].copy()), new Constant(two)));
    }

    @Override
    public Node integrate(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Creates a copy of this node
     * 
     * @return the copy (Node)
     */
    @Override
    public Node copy() {
        return new Tan(this.subNodes[0].copy());
    }

    /**
     * Returns the name of the formula node (class)
     * 
     * @return the name of the class (String)
     */
    @Override
    public String getName() {
        return "Tan";
    }

    /**
     * Returns the formula node encoded as a string
     * 
     * @return the encoded node (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("tan(");
        retVal.append(this.subNodes[0].toString());
        retVal.append(")");
        
        return retVal.toString();
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
