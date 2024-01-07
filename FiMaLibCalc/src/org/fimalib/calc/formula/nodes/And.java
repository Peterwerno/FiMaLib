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
import org.fimalib.calc.Boolean;
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.FormulaException;

/**
 * This class implements the logical AND operator.
 * 
 * It returns true if both subnodes are true, otherwise it returns false
 * 
 * @author Peter Werno
 */
public class And extends Node {
    /**
     * Creates a new instance of And
     */
    public And() {
        super(2);
    }
    
    /**
     * Creates a new instance of And with the two subnodes given.
     * 
     * @param left (Node) the left node
     * @param right (Node) the right node
     */
    public And(Node left, Node right) {
        super(2);
        
        this.subNodes[0] = left;
        this.subNodes[1] = right;
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
        Number lVal = this.subNodes[0].calculate(parameters);
        Number rVal = this.subNodes[1].calculate(parameters);
        
        if((lVal instanceof Boolean) && (rVal instanceof Boolean)) {
            Boolean bLVal = (Boolean)lVal;
            Boolean bRVal = (Boolean)rVal;
            
            return new Boolean(bLVal.getBooleanValue() && bRVal.getBooleanValue(), lVal.getNumberFormat());
        }
        
        throw new FiMaLibCalcException("AND-comparison (&&) can only be applied to nested boolean values");
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
     * Creates a copy of this node
     * 
     * @return the copy (Node)
     */
    @Override
    public Node copy() {
        return new And(this.subNodes[0].copy(), this.subNodes[1].copy());
    }

    @Override
    public String getName() {
        return "And";
    }

    /**
     * Returns the formula node encoded as a string
     * 
     * @return the encoded node (String)
     */
    @Override
    public String toString() {
        int level0 = this.subNodes[0].getLevel();
        int level1 = this.subNodes[1].getLevel();
        
        StringBuilder retVal = new StringBuilder("");
        if(level0<=this.getLevel()) retVal.append("(");
        retVal.append(this.subNodes[0].toString());
        if(level0<=this.getLevel()) retVal.append(")");
        retVal.append("&&");
        if(level1<=this.getLevel()) retVal.append("(");
        retVal.append(this.subNodes[1].toString());
        if(level1<=this.getLevel()) retVal.append(")");
        
        return retVal.toString();
    }

    /**
     * Returns the level of the node
     * 
     * @return the level (int)
     */
    @Override
    public int getLevel() {
        return Node.LEVEL_LOGIC;
    }
}
