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
import org.fimalib.calc.Complex;
import org.fimalib.calc.Double;
import org.fimalib.calc.FiMaLibDivisionByZeroException;
import org.fimalib.calc.Number;

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
    Number[][] values;
    int height;
    int width;
    NumberFormat nf = NumberFormat.getInstance(Locale.US);
    
    /**
     * Creates a new instance of Matrix with a given height and width all filled
     * with zeros all of type "Double"
     * 
     * @param height (int) the height
     * @param width (int) the width
     */
    public Matrix(int height, int width) {
        this.height = height;
        this.width = width;
        this.values = new Number[height][width];
        this.nf.setGroupingUsed(false);
        
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                this.values[row][col] = new Double(0.0, this.nf);
            }
        }
    }
    
    /**
     * Create a new instance o Matrix with all elements 0 except the diagonal 
     * filled with the value provided
     * 
     * @param height (int) the number of rows
     * @param width (int) the number of columns
     * @param value (Number) the diagonal value
     */
    public Matrix(int height, int width, Number value) {
        this.height = height;
        this.width = width;
        this.values = new Number[height][width];
        this.nf.setGroupingUsed(false);
        
        value.setNumberFormat(this.nf);
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                if(value instanceof Complex) {
                    if(row != col) 
                        this.values[row][col] = new Complex(0.0, 0.0, this.nf);
                    else
                        this.values[row][col] = value;
                }
                else {
                    if(row != col) 
                        this.values[row][col] = new Double(0.0, this.nf);
                    else
                        this.values[row][col] = value;
                }
            }
        }
    }
    
    /**
     * Create a new instance of matrix with a predefined array of elements.
     * 
     * @param values (array of Number) the elements
     * @throws MatrixException 
     */
    public Matrix(Number[][] values) throws MatrixException {
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
        this.values = new Number[rowCount][colCount];
        
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
                            if(buf.toString().contains("i")) {
                                this.values[curRow][curCol] = new Complex(buf.toString(), this.nf);
                            }
                            else {
                                this.values[curRow][curCol] = new Double(nf.parse(buf.toString()).doubleValue(), this.nf);
                            }
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
                retVal.append(this.values[i][j].toString());
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
     * @param value (Number) the new value for this position
     */
    public void setValue(int row, int col, Number value) {
        this.values[row][col] = value;
    }
    
    /**
     * Returns an element from the matrix at a given position.
     * 
     * @param row (int) the row
     * @param col (int) the column
     * @return the value at the given position (Number)
     */
    public Number getValue(int row, int col) {
        return this.values[row][col];
    }
    
    /*
    * Methods for adding/subtracting/multiplying etc. with a scalar value
    * (i.e. use the operation on each matrix element individually)
    */
    
    /**
     * Adds a scalar value to the matrix.
     * 
     * @param value (Number) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix add(Number value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            Number[][] retValues = new Number[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j].add(value);
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j].add(value);
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
    public Matrix sub(Number value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            Number[][] retValues = new Number[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j].sub(value);
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j].sub(value);
                }
            }
            
            return this;
        }
    }

    /**
     * Multiplies the matrix with a scalar value.
     * 
     * @param value (Number) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     */
    public Matrix mul(Number value, boolean returnNewMatrix) throws MatrixException {
        if (returnNewMatrix) {
            Number[][] retValues = new Number[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j].mul(value);
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j].mul(value);
                }
            }
            
            return this;
        }
    }
    
    /**
     * Divides tha matrix by a scalar value.
     * 
     * @param value (Number) the scalar
     * @param returnNewMatrix (boolean) see above
     * @return the Matrix
     * @throws MatrixException 
     * @throws org.fimalib.calc.FiMaLibDivisionByZeroException 
     */
    public Matrix div(Number value, boolean returnNewMatrix) throws MatrixException, FiMaLibDivisionByZeroException {
        if (returnNewMatrix) {
            Number[][] retValues = new Number[height][width];
            
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    retValues[i][j] = this.values[i][j].div(value);
                }
            }
            
            return new Matrix(retValues);
        }
        else {
            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    this.values[i][j].div(value);
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
        Number[][] valResult = this.values;
        
        if(returnNewMatrix) {
            valResult = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    valResult[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                valResult[row][col] = valResult[row][col].add(other.getValue(row, col));
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
        Number[][] valResult = this.values;
        
        if(returnNewMatrix) {
            valResult = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    valResult[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            for(int col=0; col<this.width; col++) {
                valResult[row][col] = valResult[row][col].sub(other.getValue(row, col));
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
                
                Number value = null;
                
                for(int i=0; i<this.width; i++) {
                    if(i == 0) 
                        value = this.values[row][i].mul(other.getValue(i, col));
                    else
                        value = value.add(this.values[row][i].mul(other.getValue(i, col)));
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
     * @return The transposed Matrix (Matrix)
     */
    public Matrix transpose(boolean returnNewMatrix) {
        if(returnNewMatrix) {
            Matrix retVal = new Matrix(this.width, this.height);
            
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    retVal.setValue(col, row, this.values[row][col].copy());
                }
            }
            
            return retVal;
        }
        
        Number[][] newValues = new Number[this.width][this.height];
        
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
     * @param fromRow (int) the start row from which to copy
     * @param toRow (int) the end row
     * @param fromCol (int) the from column from which to copy
     * @param toCol (int) the end column
     * @return the sub-matrix (Matrix)
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
                retVal.setValue(row-fromRow, col-fromCol, this.values[row][col].copy());
            }
        }
        
        return retVal;
    }
    
    /**
     * Returns a sub-matrix by dropping a given row and column from the original
     * matrix.
     * The parameter retrunNewMatrix is omitted as this will always return a
     * new Matrix object!
     * 
     * @param dropRow (int) the row to drop
     * @param dropCol (int) the column to drop
     * @return the resulting sub-matrix (Matrix)
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
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            Number help = newValues[row1][col];
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
     * @param factor (Number) the factor to multiply with
     * @param returnNewMatrix (boolean) see above
     * @return the resulting matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix mulRow(int row, Number factor, boolean returnNewMatrix) throws MatrixException {
        if(row < 0) throw new MatrixException("Row number must be 0 or greater");
        if(row >= this.height) throw new MatrixException("Row number must be below the matrix' height");
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int r=0; r<this.height; r++) {
                for(int col=0; col<this.width; col++) {
                    newValues[r][col] = this.values[r][col].copy();
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[row][col] = newValues[row][col].mul(factor);
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
     * @return the resulting matric (Matrix)
     * @throws MatrixException 
     */
    public Matrix mulCol(int col, Number factor, boolean returnNewMatrix) throws MatrixException {
        if(col < 0) throw new MatrixException("Column number must be 0 or greater");
        if(col >= this.width) throw new MatrixException("Column number must be below the matrix' width");
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int r=0; r<this.height; r++) {
                for(int c=0; c<this.width; c++) {
                    newValues[r][c] = this.values[r][c].copy();
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            newValues[row][col] = newValues[row][col].mul(factor);
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
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.height; col++) {
                    newValues[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[destRow][col] = newValues[destRow][col].add(newValues[sourceRow][col]);
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
     * @param factor (Number) the factor
     * @param returnNewMatrix (boolean) see above
     * @return the matrix (Matrix)
     * @throws MatrixException 
     */
    public Matrix addRowToRow(int sourceRow, int destRow, Number factor, boolean returnNewMatrix) throws MatrixException {
        if(sourceRow < 0) throw new MatrixException("Source Row must be 0 or greater");
        if(destRow < 0) throw new MatrixException("Destination Row must be 0 or greater");
        if(sourceRow >= this.height) throw new MatrixException("Source Row must be smaller than the matrix' height");
        if(destRow >= this.height) throw new MatrixException("Destination Row must be smaller than the matrix' height");
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.height; col++) {
                    newValues[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int col=0; col<this.width; col++) {
            newValues[destRow][col] = newValues[destRow][col].add(newValues[sourceRow][col].mul(factor));
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
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        for(int row=0; row<this.height; row++) {
            Number help = newValues[row][col1];
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
     * @throws org.fimalib.calc.FiMaLibDivisionByZeroException 
     */
    public Matrix invert(boolean returnNewMatrix) throws MatrixException, FiMaLibDivisionByZeroException {
        if(this.width != this.height) throw new MatrixException("Can only invert square matrices");
        
        Number[][] newValues = this.values;
        
        if(returnNewMatrix) {
            newValues = new Number[this.height][this.width];
            for(int row=0; row<this.height; row++) {
                for(int col=0; col<this.width; col++) {
                    newValues[row][col] = this.values[row][col].copy();
                }
            }
        }
        
        Matrix original = new Matrix(newValues);
        Matrix union = new Matrix(this.height, this.width, new Double(1.0, this.nf));
        
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
    public Number getDet() throws MatrixException {
        if(this.width != this.height) throw new MatrixException("Can only calculate determinants for square matrices");
        
        switch (this.width) {
            case 1:
                return this.values[0][0].copy();
            case 2:
                return this.values[0][0].mul(this.values[1][1]).sub(this.values[1][0].mul(this.values[0][1]));
            default:
                Number multiplier = new Double(1.0, this.nf);
                Number value = null;
                for(int i=0; i<this.width; i++) {
                    if(i == 0) {
                        value = getSubMatrix(0, i).getDet().mul(this.values[0][i]).mul(multiplier);
                    }
                    else {
                        value = value.add(getSubMatrix(0, i).getDet().mul(this.values[0][i]).mul(multiplier));
                    }
                    multiplier = multiplier.mul(new Double(-1.0, this.nf));
                }
                return value;
        }
    }
    
}
