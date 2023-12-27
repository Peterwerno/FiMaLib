/*
 * Copyright (C) 2023 peter.
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
package org.fimalib.calc.matrix;

/**
 * Impements the methods for Vector manipulation.
 * 
 * @author peter
 */
public class Vector extends Matrix {
    
    /**
     * Creates a new instance of Vector with a given height and all elements
     * set to zero
     * 
     * @param height (int) the height
     */
    public Vector(int height) {
        super(height, 1);
    }
    
    /**
     * Creates a new instance of Vector with a given height and all elements 
     * set to a predefined value
     * 
     * @param height (int) the height
     * @param value (double) the initial value
     */
    public Vector(int height, double value) {
        super(height, 1);
        
        for(int i=0; i<height; i++) {
            super.setValue(i, 0, value);
        }
    }
    
    /**
     * Creates a new instance of Vector with a predefined set of values
     * 
     * @param values (double[]) the values
     */
    public Vector(double[] values) {
        super(values.length, 1);
        
        for(int i=0; i<values.length; i++) {
            super.setValue(i, 0, values[i]);
        }
    }
    
    /**
     * Creates a new instance of Vector from a string encoding in the
     * style (1,2,3,4).
     * 
     * @param initializationString (String) the String
     * @throws MatrixException 
     */
    public Vector(String initializationString) throws MatrixException {
        super("("+initializationString+")");
        
        super.transpose(false);
    }
    
    /**
     * Returns the content of the vector as a string
     * 
     * @return the vector (String)
     */
    @Override
    public String toString() {
        String result = this.transpose(true).toString();
        
        int strLen = result.length();
        
        return result.substring(1, strLen-1);
    }
}
