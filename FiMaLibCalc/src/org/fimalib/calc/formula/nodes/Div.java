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
import org.fimalib.calc.Double;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.formula.FormulaException;

/**
 * This class implements a calculation/formula node which just divides the 
 * first sub-node by the second sub-node.
 * 
 * @author Peter Werno
 */
public class Div extends Node {
    /**
     * Creates a new instance of Neg
     */
    public Div() {
        super(2);
    }
    
    /**
     * Creates a new instance of Div with given subnodes
     * 
     * @param left (Node) the left subnode
     * @param right (Node) the right subnode
     */
    public Div(Node left, Node right) {
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
    public org.fimalib.calc.Number calculate(HashMap<String, org.fimalib.calc.Number> parameters) throws FiMaLibCalcException {
        org.fimalib.calc.Number lVal = this.subNodes[0].calculate(parameters);
        org.fimalib.calc.Number rVal = this.subNodes[1].calculate(parameters);
        
        return lVal.div(rVal);
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
        Node lDerived = this.subNodes[0].derive(parameterName);
        Node rDerived = this.subNodes[1].derive(parameterName);
        
        
        Node mull = new Mul(lDerived, this.subNodes[0].copy());
        Node mulr = new Mul(this.subNodes[1].copy(), rDerived);
        
        Node sub = new Add(mull, mulr);
        
        Node n2 = new Pow(this.subNodes[1].copy(), new Constant(new Double(2.0)));
        
        return new Div(sub, n2);
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
        return new Div(this.subNodes[0].copy(), this.subNodes[1].copy());
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
        if(level0<this.getLevel()) retVal.append("(");
        retVal.append(this.subNodes[0].toString());
        if(level0<this.getLevel()) retVal.append(")");
        retVal.append("/");
        if(level1<this.getLevel()) retVal.append("(");
        retVal.append(this.subNodes[1].toString());
        if(level1<this.getLevel()) retVal.append(")");
        
        return retVal.toString();
    }

    /**
     * Returns the name of the formula node (class)
     * 
     * @return the name of the class (String)
     */
    @Override
    public String getName() {
        return "Div";
    }

    /**
     * Returns the level of the node
     * 
     * @return the level (int)
     */
    @Override
    public int getLevel() {
        return Node.LEVEL_MULTIPLICATION;
    }
}
