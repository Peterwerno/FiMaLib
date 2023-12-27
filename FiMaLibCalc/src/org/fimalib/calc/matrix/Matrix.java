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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * The matrix class stores a n x m Matrix and supplies a set of matrix 
 * manipulation/calculation methods.
 * 
 * The majority of manipulation methods will have an additional boolean
 * parameter (returnNewMatrix). If this is set to true, the original Matrix
 * object will remain unchanged. A new matrix object will be created that holds
 * the results of the manipulation and the new object will be returned.
 * If the parameter is set to false, the original Matrix will be manipulated
 * and the old values/contents will be lost.
 * 
 * @author Peter Werno
 */
public class Matrix {
    double[][] values;
    int width;
    int height;
    NumberFormat nf = NumberFormat.getInstance(Locale.US);
    
    /**
     * Create an empty Matrix (all elements 0) of a given size
     * 
     * @param height (int) the number of rows
     * @param width (int) the number of columns
     */
    public Matrix(int height, int width) {
        this.height = height;
        this.width = width;
        this.values = new double[height][width];
        this.nf.setGroupingUsed(false);
    }
    
    /**
     * Create a Matrix with all elements 0 except the diagonal filled with
     * the value provided
     * 
     * @param height (int) the number of rows
     * @param width (int) the number of columns
     * @param value (double) the diagonal value
     */
    public Matrix(int height, int width, double value) {
        this.height = height;
        this.width = width;
        this.values = new double[height][width];
        this.nf.setGroupingUsed(false);
        
        for(int i=0; i<(width<height?width:height); i++) {
            this.values[i][i] = value;
        }
    }
    
    /**
     * Create a new matrix with a predefined array of elements.
     * 
     * @param values (array of doubles) the elements
     * @throws MatrixException 
     */
    public Matrix(double[][] values) throws MatrixException {
        this.values = values;
        this.height = this.values.length;
        this.nf.setGroupingUsed(false);
        
        if(this.height == 0)
            throw new MatrixException("Matrix must have at least one row");
        
        this.width = this.values[0].length;

        for(int i=1; i<this.height; i++) {
            if(this.width != this.values[i].length)
                throw new MatrixException("Matrix must have the same number of columns in each row!");
        }
    }
    
    /**
     * Create a new matrix and initialze from a string representation in the
     * form of:
     * 
     * "((1,2,3),(4,5,6),(7,8,9))"
     * where (1,2,3) is the first row, (4,5,6) the second, etc.
     * 
     * @param initializeString (String) see above
     * @throws MatrixException 
     */
    public Matrix(String initializeString) throws MatrixException {
        int strLen = initializeString.length();
        StringBuilder buf = new StringBuilder("");
        this.nf.setGroupingUsed(false);
        
        // First, count the number of rows and columns!
        int rowCount = 0;
        int colCount = -1;
        int level = 0; 
        int thisRowColCount = 0;
        
        for(int i=0; i<strLen; i++) {
            char c = initializeString.charAt(i);
            
            switch (c) {
                case '(':
                    level++;
                    thisRowColCount = 0;
                    if(level > 2) throw new MatrixException("Matrix must have two dimensions");
                    break;
                    
                case ')':
                    if(level == 2) {
                        thisRowColCount++;
                        if(colCount == -1) 
                            colCount = thisRowColCount;
                        else if ((colCount != thisRowColCount))
                            throw new MatrixException("Each row must have the same number of columns!");
                    }
                    else if(level == 1) {
                        rowCount++;
                    }
                    level--;
                    break;
                    
                case ',':
                    switch (level) {
                        case 2:
                            thisRowColCount++;
                            break;
                        case 1:
                            rowCount++;
                            break;
                        default:
                            throw new MatrixException("Syntax error in initialzation String");
                    }
                    break;
                    
                default:
                    // Do nothing
                    break;
            }
        }
        
        if(level != 0) throw new MatrixException("Syntax error in matrix, unexpected end of string");
        
        this.width = colCount;
        this.height = rowCount;
        this.values = new double[rowCount][colCount];
        
        int curCol = 0;
        int curRow = 0;
        
        // Now fill the array
        for(int i=0; i<strLen; i++) {
            char c = initializeString.charAt(i);
            
            switch (c) {
                case '(':
                    level++;
                    buf = new StringBuilder("");
                    curCol = 0;
                    break;
                    
                case ')':
                case ',':
                    if(level == 2) {
                        try {
                            this.values[curRow][curCol] = nf.parse(buf.toString()).doubleValue();
                        }
                        catch (ParseException ex) {
                            throw new MatrixException("Error parsing value " + buf.toString(), ex);
                        }
                        curCol++;
                        buf = new StringBuilder("");
                    }
                    else if((level == 1) && (c == ',')) {
                        curRow++;
                    }
                    if(c == ')') level--;
                    break;
                    
                default:
                    if(level == 2)
                        buf.append(c);
                    break;
            }
        }
    }
    
    /**
     * Returns the content of the matrix in the same style as the initialization
     * string needs to be formatted (see constructors above)
     * 
     * @return (String) the content of the matrix
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder("(");
        
        for(int i=0; i<this.height; i++) {
            if(i>0) retVal.append(",");
            retVal.append("(");
            for(int j=0; j<this.width; j++) {
                if(j>0) retVal.append(",");
                retVal.append(nf.format(this.values[i][j]));
            }
            retVal.append(")");
        }
        
        retVal.append(")");
        
        return retVal.toString();
    }
    
    /**
     * Changes the number formatter which is used for writing to string or 
     * initializing from string.
     * 
     * @param nf (NumberFormat) the new number formatter
     */
    public void setNumberFormat(NumberFormat nf) {
        this.nf = nf;
    }
    
    /*
    * Getters and setters for the various contents
    */
    
    /**
     * Returns the matrix' width
     * 
     * @return the width (int)
     */
    public int getWidth() {
        return this.width;
    }
    
    /**
     * Returns the matrix' height
     * 
     * @return the height (int)
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Changes an element in the matrix at the given position.
     * 
     * @param row (int) the row
     * @param col (int) the column
     * @param value (doulbe) the new value for this position
     */
    public void setValue(int row, int col, double value) {
        this.values[row][col] = value;
    }
    
    /**
     * Returns an element from the matrix at a given position.
     * 
     * @param row (int) the row
     * @param col (int) the column
     * @return the value at the given position (double)
     */
    public double getValue(int row, int col) {
        return this.values[row][col];
    }
    
    /*
    * Methods for adding/subtracting/multiplying etc. with a scalar value
    * (i.e. use the operation on each matrix element individually)
    */
    
    /**
     * Adds a scalar value to the matrix.
     * 
     * @param value (double) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix add(double value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            double[][] retValues = new double[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j] + value;
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j] += value;
                }
            }
            
            return this;
        }
    }

    /**
     * Subtracts a scalar value from the matrix.
     * 
     * @param value (double) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix sub(double value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            double[][] retValues = new double[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j] - value;
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j] -= value;
                }
            }
            
            return this;
        }
    }

    /**
     * Multiplies the matrix with a scalar value.
     * 
     * @param value (double) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix mul(double value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            double[][] retValues = new double[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j] * value;
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j] *= value;
                }
            }
            
            return this;
        }
    }
    
    /**
     * Divides tha matrix by a scalar value.
     * 
     * @param value (double) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix div(double value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            double[][] retValues = new double[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j] / value;
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j] /= value;
                }
            }
            
            return this;
        }
    }
    
    /*
    * Methods for adding / subtracting full matrices (must be of same size)
    */
    
    /**
     * Adds another matrix to the current matrix.
     * 
     * @param other (Matrix) the other matrix
     * @param returnNewMatrix (boolean) see above
     * @return the result of the addition (Matrix)
     * @throws MatrixException 
     */
    public Matrix add(Matrix other, boolean returnNewMatrix) throws MatrixException {
        if(other.getWidth() != this.width) throw new MatrixException("Matrices must have identical width");
        if(other.getHeight() != this.height) throw new MatrixException("Matrices must have identical height");
        double[][] valResult = this.values;
        
        if(returnNewMatrix) {
            valResult = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    valResult[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                valResult[row][col] += other.getValue(row, col);
            }
        }
        
        if(returnNewMatrix) {
            return new Matrix(valResult);
        }
        
        return this;
    }

    /**
     * Subtracts another matrix from the current matrix.
     * 
     * @param other (Matrix) the other matrix
     * @param returnNewMatrix (boolean) see above
     * @return the result of the subtraction (Matrix)
     * @throws MatrixException 
     */
    public Matrix sub(Matrix other, boolean returnNewMatrix) throws MatrixException {
        if(other.getWidth() != this.width) throw new MatrixException("Matrices must have identical width");
        if(other.getHeight() != this.height) throw new MatrixException("Matrices must have identical height");
        double[][] valResult = this.values;
        
        if(returnNewMatrix) {
            valResult = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    valResult[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                valResult[row][col] -= other.getValue(row, col);
            }
        }
        
        if(returnNewMatrix) {
            return new Matrix(valResult);
        }
        
        return this;
    }
    
    /**
     * Multiplies another matrix with the current matrix.
     * This will always return a new Matrix object for the result, the original
     * matrices will remain unchanged.
     * 
     * @param other (Matrix) the other matrix
     * @return the result of the multiplication (Matrix)
     * @throws MatrixException 
     */
    public Matrix mul(Matrix other) throws MatrixException {
        if(this.width != other.getHeight()) throw new MatrixException("Matrix multiplication: second matrix height must equal first matrix width!");
        
        Matrix retVal = new Matrix(this.height, other.getWidth());
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<other.getWidth(); col++) {
                // sum up the products of all elements in OLD row with all elements in NEW col
                
                double value = 0.0;
                
                for(int i=0; i<this.width; i++) {
                    value += this.values[row][i] * other.getValue(i, col);
                }
                
                retVal.setValue(row, col, value);
            }
        }
        
        return retVal;
    }
    
    /*
    * Methods for manipulating matrices
    */
    
    /**
     * Transposes a matrix (i.e. flips across the diagonal and turns 
     * an n x m Matrix into an m x n Matrix)
     * 
     * @param returnNewMatrix (boolean) see above
     * @return 
     */
    public Matrix transpose(boolean returnNewMatrix) {
        if(returnNewMatrix) {
            Matrix retVal = new Matrix(this.width, this.height);
            
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    retVal.setValue(col, row, this.values[row][col]);
                }
            }
            
            return retVal;
        }
        
        double[][] newValues = new double[this.width][this.height];
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                newValues[col][row] = this.values[row][col];
            }
        }
        
        this.values = newValues;
        int help = this.height;
        this.height = this.width;
        this.width = help;
        
        return this;
    }
    
    /**
     * Returns a sub-matrix from the current matrix.
     * The parameter returnNewMatrix is omitted as this will always return a 
     * new Matrix object!
     * 
     * @param fromRow
     * @param toRow
     * @param fromCol
     * @param toCol
     * @return the sub-matrix
     * @throws MatrixException 
     */
    public Matrix getSubMatrix(int fromRow, int toRow, int fromCol, int toCol) throws MatrixException {
        if(toRow < fromRow) throw new MatrixException("parameter toRow must be equal or greater than fromRow");
        if(toCol < fromCol) throw new MatrixException("parameter toCol must be equal or greater than fromCol");
        
        if(fromRow < 0) throw new MatrixException("parameter fromRow must not be smaller than zero");
        if(fromCol < 0) throw new MatrixException("parameter fromCol must not be smaller than zero");
        
        if(toRow >= this.height) throw new MatrixException("parameter toRow must be smaller than the matrix height");
        if(toCol >= this.width) throw new MatrixException("parameter toCol must be smaller than the matrix width");
        
        Matrix retVal = new Matrix(toRow-fromRow+1, toCol-fromCol+1);
        
        for(int row=fromRow; row<=toRow; row++) {
            for(int col=fromCol; col<=toCol; col++) {
                retVal.setValue(row-fromRow, col-fromCol, this.values[row][col]);
            }
        }
        
        return retVal;
    }
    
    /**
     * Returns a sub-matrix by dropping a given row and column from the original
     * matrix.
     * The parameter retrunNewMatrix is omitted as this will always return a
     * new Matrix object!
     * @param dropRow
     * @param dropCol
     * @return 
     */
    public Matrix getSubMatrix(int dropRow, int dropCol) {
        Matrix retVal = new Matrix(this.height - 1, this.width - 1);
        
        for(int row=0; row<dropRow; row++) {
            for(int col=0; col<dropCol; col++) {
                retVal.setValue(row, col, this.values[row][col]);
            }
            
            for(int col=dropCol+1; col<this.width; col++) {
                retVal.setValue(row, col-1, this.values[row][col]);
            }
        }
        for(int row=dropRow+1; row<this.height; row++) {
            for(int col=0; col<dropCol; col++) {
                retVal.setValue(row-1, col, this.values[row][col]);
            }
            
            for(int col=dropCol+1; col<this.width; col++) {
                retVal.setValue(row-1, col-1, this.values[row][col]);
            }
        }
        
        return retVal;
    }
    
    /**
     * Swaps two rows in the matrix
     * 
     * @param row1 (int) the first row
     * @param row2 (int) the second row
     * @param returnNewMatrix (boolean) see above
     * @return the matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix swapRows(int row1, int row2, boolean returnNewMatrix) throws MatrixException {
        if(row1<0) throw new MatrixException("Row 1 must be 0 or greater");
        if(row2<0) throw new MatrixException("Row 2 must be 0 or greater");
        if(row1>=this.height) throw new MatrixException("Row 1 must be less than the Matrix' height");
        if(row2>=this.height) throw new MatrixException("Row 2 must be less than the Matrix' height");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            double help = newValues[row1][col];
            newValues[row1][col] = newValues[row2][col];
            newValues[row2][col] = help;
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * mulRow multiplies a single row in the matrix with a scalar value.
     * This is needed e.g. for the linear equation solving algorithm.
     * 
     * @param row (int) the row number
     * @param factor (double) the factor to multiply with
     * @param returnNewMatrix (boolean) see above
     * @return 
     * @throws MatrixException 
     */
    public Matrix mulRow(int row, double factor, boolean returnNewMatrix) throws MatrixException {
        if(row < 0) throw new MatrixException("Row number must be 0 or greater");
        if(row >= this.height) throw new MatrixException("Row number must be below the matrix' height");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int r=0; r<this.height; r++) {
                for(int col=0; col<this.width; col++) {
                    newValues[r][col] = this.values[r][col];
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[row][col] *= factor;
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * mulCol multiplies a single column in the matrix with a scalar value.
     * This might be needed e.g. for the linear equation solving algorithm.
     * 
     * @param col (int) the column number
     * @param factor (double) the factor to multiply with
     * @param returnNewMatrix (boolean) see above
     * @return 
     * @throws MatrixException 
     */
    public Matrix mulCol(int col, double factor, boolean returnNewMatrix) throws MatrixException {
        if(col < 0) throw new MatrixException("Column number must be 0 or greater");
        if(col >= this.width) throw new MatrixException("Column number must be below the matrix' width");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int r=0; r<this.height; r++) {
                for(int c=0; c<this.width; c++) {
                    newValues[r][c] = this.values[r][c];
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            newValues[row][col] *= factor;
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * addRowToRow adds the individual values of one row to the same column
     * values of another row.
     * This is needed e.g. for the linear equation system solver.
     * 
     * @param sourceRow (int) the source row (will be unchanged)
     * @param destRow (int) the destination row (will be changed)
     * @param returnNewMatrix (boolean) see above
     * @return the matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix addRowToRow(int sourceRow, int destRow, boolean returnNewMatrix) throws MatrixException {
        if(sourceRow < 0) throw new MatrixException("Source Row must be 0 or greater");
        if(destRow < 0) throw new MatrixException("Destination Row must be 0 or greater");
        if(sourceRow >= this.height) throw new MatrixException("Source Row must be smaller than the matrix' height");
        if(destRow >= this.height) throw new MatrixException("Destination Row must be smaller than the matrix' height");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.height; col++) {
                    newValues[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[destRow][col] += newValues[sourceRow][col];
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * addRowToRow adds the individual values of one row multiplied by a
     * factor to the same column values of another row.
     * This is needed e.g. for the linear equation system solver.
     * 
     * @param sourceRow (int) the source row (will be unchanged)
     * @param destRow (int) the destination row (will be changed)
     * @param factor (double) the factor
     * @param returnNewMatrix (boolean) see above
     * @return the matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix addRowToRow(int sourceRow, int destRow, double factor, boolean returnNewMatrix) throws MatrixException {
        if(sourceRow < 0) throw new MatrixException("Source Row must be 0 or greater");
        if(destRow < 0) throw new MatrixException("Destination Row must be 0 or greater");
        if(sourceRow >= this.height) throw new MatrixException("Source Row must be smaller than the matrix' height");
        if(destRow >= this.height) throw new MatrixException("Destination Row must be smaller than the matrix' height");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.height; col++) {
                    newValues[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[destRow][col] += newValues[sourceRow][col] * factor;
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * Swaps two columns in the matrix
     * 
     * @param col1 (int) the first column
     * @param col2 (int) the second column
     * @param returnNewMatrix (boolean) see above
     * @return the matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix swapCols(int col1, int col2, boolean returnNewMatrix) throws MatrixException {
        if(col1<0) throw new MatrixException("Column 1 must be 0 or greater");
        if(col2<0) throw new MatrixException("Column 2 must be 0 or greater");
        if(col1>=this.height) throw new MatrixException("Column 1 must be less than the Matrix' width");
        if(col2>=this.height) throw new MatrixException("Column 2 must be less than the Matrix' width");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col];
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            double help = newValues[row][col1];
            newValues[row][col1] = newValues[row][col2];
            newValues[row][col2] = help;
        }
        
        if(returnNewMatrix)
            return new Matrix(newValues);
        
        return this;
    }
    
    /**
     * Calculate the inverted matrix
     * 
     * @param returnNewMatrix (boolean) see above
     * @return
     * @throws MatrixException 
     */
    public Matrix invert(boolean returnNewMatrix) throws MatrixException {
        if(this.width != this.height) throw new MatrixException("Can only invert square matrices");
        
        double[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new double[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col];
                }
            }
        }
        
        Matrix original = new Matrix(newValues);
        Matrix union = new Matrix(this.height, this.width, 1.0);
        
        LESSolver solver = new LESSolver(original, union);
        
        solver.solve();
        
        if(returnNewMatrix)
            return solver.getResults();
        
        this.values = solver.getResults().values;
        return this;
    }
    
    /**
     * Calculates the matrix' determinant.
     * 
     * @return the determinant (double)
     * @throws MatrixException 
     */
    public double getDet() throws MatrixException {
        if(this.width != this.height) throw new MatrixException("Can only calculate determinants for square matrices");
        
        switch (this.width) {
            case 1:
                return this.values[0][0];
            case 2:
                return this.values[0][0] * this.values[1][1] - this.values[1][0] * this.values[0][1];
            default:
                double multiplier = 1.0;
                double value = 0.0;
                for(int i=0; i<this.width; i++) {
                    value += getSubMatrix(0, i).getDet() * this.values[0][i] * multiplier;
                    multiplier *= -1.0;
                }
                return value;
        }
    }
}
