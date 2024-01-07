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
import org.fimalib.calc.Number;
import org.fimalib.calc.Double;
import org.fimalib.calc.FiMaLibCalcException;
import org.fimalib.calc.formula.FormulaException;

/**
 * This class implements a calculation/formula node which calculates the 
 * first sub-node to the power of the second sub-node.
 * 
 * @author Peter Werno
 */
public class Pow extends Node {
    /**
     * Creates a new instance of Pow
     */
    public Pow() {
        super(2);
    }
    
    /**
     * Creates a new instance of Pow with given subnodes.
     * Calculates a ^ b
     * 
     * @param left (Node) the left subnode (a)
     * @param right (Node) the right subnode (b)
     */
    public Pow(Node left, Node right) {
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
        
        return lVal.pow(rVal);
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
        if(this.subNodes[1].isNumber()) {
            try {
                Number calc = this.subNodes[1].calculate(null);
                Number calc_1 = calc.sub(new Double(1.0, calc.getNumberFormat()));
                Double one = new Double(1.0, calc.getNumberFormat());
                
                if(calc_1.equals(one))
                    return new Mul(new Mul(new Constant(calc), this.subNodes[0].derive(parameterName)), this.subNodes[0].copy());
                else 
                    return new Mul(new Mul(new Constant(calc), this.subNodes[0].derive(parameterName)), new Pow(this.subNodes[0].copy(), new Constant(calc_1)));
            }
            catch (FiMaLibCalcException ex) {
                throw new FormulaException("Error when deriving Pow", ex);
            }
        }
        else 
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
        return new Pow(this.subNodes[0].copy(), this.subNodes[1].copy());
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
        retVal.append("^");
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
        return "Pow";
    }

    /**
     * Returns the level of the node
     * 
     * @return the level (int)
     */
    @Override
    public int getLevel() {
        return Node.LEVEL_EXPONENTIAL;
    }
}
