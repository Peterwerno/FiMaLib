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
 * This class implements a variable within a formula
 * 
 * @author Peter Werno
 */
public class Variable extends Node {
    String name;
    
    /**
     * Creates a new instance of Variable
     */
    public Variable() {
        super(0);
    }
    
    /**
     * Creates a new instance of Variable with a given variable name
     * 
     * @param name (String) the variable name
     */
    public Variable(String name) {
        super(0);
        this.name = name;
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
        return parameters.get(this.name);
    }

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
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
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
    
    /**
     * Returns that this node is NOT a constant number
     * 
     * @return false (boolean)
     */
    @Override
    public boolean isNumber() {
        return false;
    }
}
