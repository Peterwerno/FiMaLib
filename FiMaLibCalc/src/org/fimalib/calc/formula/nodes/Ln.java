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
import org.fimalib.calc.Number;
import org.fimalib.calc.formula.FormulaException;

/**
 * This class implements the natural logarithm
 * 
 * @author Peter Werno
 */
public class Ln extends Node {
    /**
     * Creates a new instance of Ln
     */
    public Ln() {
        super(1);
    }
    
    /**
     * Creates a new instance of Ln with a given subnode
     * 
     * @param subNode (Node) the subnode
     */
    public Ln(Node subNode) {
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
        return this.subNodes[0].calculate(parameters).ln();
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
        return new Ln(this.subNodes[0].copy());
    }

    /**
     * Returns the name of the formula node (class)
     * 
     * @return the name of the class (String)
     */
    @Override
    public String getName() {
        return "Ln";
    }

    /**
     * Returns the formula node encoded as a string
     * 
     * @return the encoded node (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("ln(");
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
