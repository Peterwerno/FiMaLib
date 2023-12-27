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
 * The class LESSolver implements a solver algorithm for linear equation systems.
 * 
 * @author Peter Werno
 */
public class LESSolver extends Matrix {
    Matrix results;
        
    /**
     * Creates a new instance of LESSolver with a given height and width and
     * a results vector.
     * 
     * @param height (int) the height of the parameter matrix
     * @param width (int) the width of the parameter matrix
     * @param results (Vector) the results vector
     * @throws MatrixException 
     */
    public LESSolver(int height, int width, Vector results) throws MatrixException {
        super(height, width);
        
        if(results.getHeight() != height) throw new MatrixException("Result vector must be same height as parameter matrix");
        this.results = results;
    }
    
    /**
     * Creates a new instance of LESSolver with a given parameter matrix and
     * a results - matrix.
     * This might be used by the Matrix' class invert function
     * 
     * @param parameters (Matrix) the parameter matrix
     * @param results (Matrix) the results matrix
     * @throws MatrixException 
     */
    public LESSolver(Matrix parameters, Matrix results) throws MatrixException {
        super(parameters.values);
        
        this.results = results;
    }
    
    /**
     * Creates a new instance of LESSolver with a given parameter matrix and
     * results vector(array)
     * 
     * @param parameters (double[][]) the parameters
     * @param results (double[]) the results vector
     * @throws MatrixException 
     */
    public LESSolver(double[][] parameters, double[] results) throws MatrixException {
        super(parameters);
        
        this.results = new Vector(results);
    }
    
    /**
     * Creates a new instance of LESSolver from a parameter string encoded 
     * matrix and a string encoded results vector
     * 
     * @param parameters (String) the parameter matrix encoded
     * @param results (String) the results vector encoded
     * @throws MatrixException 
     */
    public LESSolver(String parameters, String results) throws MatrixException {
        super(parameters);
        
        this.results = new Vector(results);
    }
    
    /**
     * Creates a new instance of LESSolver from a single initializer string
     * 
     * @param initializerString (String) the encoded LES
     * @throws MatrixException 
     */
    public LESSolver(String initializerString) throws MatrixException {
        super(getMatrixPart(initializerString));
        
        this.results = new Vector(getResultsPart(initializerString));
    }

    /**
     * Returns the matrix part of the string encoded LES
     * 
     * @param fullInitializerString (String) the full string
     * @return the matrix part (String)
     * @throws MatrixException 
     */
    public static String getMatrixPart(String fullInitializerString) throws MatrixException {
        int pos = fullInitializerString.indexOf("|");
        
        if(pos < 0) throw new MatrixException("Syntax error in LES Initialization string " + fullInitializerString);
        
        return fullInitializerString.substring(0, pos);
    }
    
    /**
     * Returns the results vector part of the string encoded LES
     * 
     * @param fullInitializerString (String) the full string
     * @return the results vector part (String)
     * @throws MatrixException 
     */
    public static String getResultsPart(String fullInitializerString) throws MatrixException {
        int pos = fullInitializerString.indexOf("|");
        
        if(pos < 0) throw new MatrixException("Syntax error in LES Initialization string " + fullInitializerString);
        
        return fullInitializerString.substring(pos+1);
    }
    
    /**
     * Returns the results vector
     * 
     * @return the results (Vector)
     */
    public Matrix getResults() {
        return this.results;
    }
    
    /**
     * Returns the LES as an encoded string in the format
     * ((1,2,3),(4,5,6),(7,8,10))|(1,2,3)
     * 
     * @return the encoded LES (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        retVal.append(super.toString());
        retVal.append("|");
        retVal.append(this.results.toString());
        return retVal.toString();
    }
    
    /**
     * Solve the LES
     * 
     * @throws MatrixException 
     */
    public void solve() throws MatrixException {
        int size = this.height;
        if(this.width < size) size = this.width;
        
        // Step 1: Clear out bottom-left triangle
        for(int i=0; i<size-1; i++) {
            sortForSolve(i);
            
            double value = this.values[i][i];
            if(value == 0.0) throw new MatrixException("TODO: Error cannot solve!");
            
            this.mulRow(i, 1.0/value, false);
            this.results.mulRow(i, 1.0/value, false);
            
            for(int j=i+1; j<size; j++) {
                double val2 = this.values[j][i];
                if(val2 != 0.0) {
                    this.addRowToRow(i, j, -val2, false);
                    this.results.addRowToRow(i, j, -val2, false);
                }
            }
        }
        
        // Step 2: Clear out top-right triangle
        for(int i=size-1; i>=1; i--) {
            //sortForSolve(i);
            
            double value = this.values[i][i];
            if(value == 0.0) throw new MatrixException("TODO: error cannot solve!");
            
            this.mulRow(i, 1.0/value, false);
            this.results.mulRow(i, 1.0/value, false);
            
            for(int j=i-1; j>=0; j--) {
                double val2 = this.values[j][i];
                if(val2 != 0.0) {
                    this.addRowToRow(i, j, -val2, false);
                    this.results.addRowToRow(i, j, -val2, false);
                }
            }
        }
        
        // Done. Results should be in the results vector
    }
    
    /**
     * Prepares the solving of the LES by sortting the rows and columns to
     * ensure there is no "0" on the diagonal!
     * 
     * @param col (int) for which position on the diagonal
     * @throws MatrixException 
     */
    public void sortForSolve(int col) throws MatrixException {
        // TODO: Code this (basically check if there is a zero in [col,col] and shuffle
        
        if(this.values[col][col] == 0.0) {
            for(int i=col+1; i<this.height; i++) {
                if(this.values[i][col] != 0.0) {
                    this.swapRows(col, i, false);
                    this.results.swapRows(col, i, false);
                    return;
                }
            }
            throw new MatrixException("Error: Cannot solve!");
        }
    }
}
