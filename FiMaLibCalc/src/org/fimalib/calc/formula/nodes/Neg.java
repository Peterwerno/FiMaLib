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
import org.fimalib.calc.formula.FormulaException;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.Boolean;
import org.fimalib.calc.Complex;
import org.fimalib.calc.Double;
import org.fimalib.calc.Number;

/**
 * The Neg class implements a negation - node which just negates the one single
 * subnote that it has.
 * 
 * @author Peter Werno
 */
public class Neg extends Node {
    /**
     * Creates a new instance of Neg
     */
    public Neg() {
        super(1);
    }
    
    /**
     * Creates a new instance of Neg with a given subnode
     * 
     * @param subNode (Node) the subnode
     */
    public Neg(Node subNode) {
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
        Number retVal = this.subNodes[0].calculate(parameters);
        Number multiplier;
        
        if(retVal instanceof Complex)
            multiplier = new Complex(-1.0, 0.0, retVal.getNumberFormat());
        else if(retVal instanceof Boolean) {
            Boolean bVal = (Boolean)retVal;
            return bVal.not();
        }
        else
            multiplier = new Double(-1.0, retVal.getNumberFormat());
        
        return retVal.mul(multiplier);
    }

    @Override
    public Node derive(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node integrate(String parameterName) throws FormulaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Returns the formula node encoded as a string
     * 
     * @return the encoded node (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("(-");
        
        if(this.subNodes[0].getLevel() < this.getLevel()) retVal.append("(");
        retVal.append(this.subNodes[0].toString());
        if(this.subNodes[0].getLevel() < this.getLevel()) retVal.append(")");
        retVal.append(")");
        
        return retVal.toString();
    }

    /**
     * Returns the name of the formula node (class)
     * 
     * @return the name of the class (String)
     */
    @Override
    public String getName() {
        return "Neg";
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
