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
package org.fimalib.calc;

import java.text.NumberFormat;

/**
 * This is the generic number class for all mathematical operations in FiMaLib.
 * It defines the operations that all implementations of number need to provide.
 * 
 * Many methods have a boolean parameter "returnNewNumber".
 * If set to true, the method will return a new instance of a Number object
 * which contains the result of the operation and the original object(s) will
 * remain unchanged.
 * If set to false, the original object will be changed and will contain the
 * result of the operation.
 * 
 * @author Peter Werno
 */
public abstract class Number extends java.lang.Number {
    protected NumberFormat format;
    
    /**
     * Creates a new instance of Number with default number format
     */
    public Number() {
        this.format = NumberFormat.getInstance();
    }
    
    /**
     * Creates a new instance of Number with a given number format
     * 
     * @param format (NumberFormat) the number format
     */
    public Number(NumberFormat format) {
        this.format = format;
    }
    
    /* -----------------------------------------------------------------------
    * Access and comparison methods
    * 
    * This section defines the methods that can be used to access the number's
    * value(s) and for comparison. Also, it implements the methods required
    * by java's java.lang.Number class.
    * ------------------------------------------------------------------------
    */
    
    /**
     * Abstract - must be implemented by subclasses
     * Creates a copy of the number object
     * 
     * @return the copy of the original value (Number)
     */
    public abstract Number copy();
    
    /**
     * Abstract - must be implemented by subclasses
     * Returns the (real part) value of the number as double
     * 
     * @return the value (double)
     */
    public abstract double getValue();
    
    /**
     * Abstract - must be implemented by subclasses
     * Compares the number with another number and returns wether or not they
     * are equal.
     * 
     * @param otherNumber (Number) the other number
     * @return if the numbers are equal (boolean)
     */
    public abstract boolean equals(Number otherNumber);
    
    /**
     * Abstract - must be implemented by subclasses
     * Compares the number with another number and returns:
     *  -1  if THIS number is smaller than the OTHER number
     *   0  if both numbers are equal
     *  +1  if THIS number is larger than the OTHER number
     * 
     * In case of complex numbers, the absolute value is compared!
     * 
     * @param otherNumber (Number) the other number
     * @return see above (int)
     */
    public abstract int compareTo(Number otherNumber);
    
    /**
     * Returns the value of the number as type 'byte'.
     * This might cause an overflow!
     * 
     * @return the value (byte)
     */
    @Override
    public byte byteValue() {
        return (byte)getValue();
    }
    
    /**
     * Returns the value of the number as type 'short'.
     * This might cause an overflow!
     * 
     * @return the value (short)
     */
    @Override
    public short shortValue() {
        return (short)getValue();
    }
    
    /**
     * Returns the value of the number as type 'int'.
     * This might cause an overflow!
     * 
     * @return the value (int)
     */
    @Override
    public int intValue() {
        return (int)getValue();
    }
    
    /**
     * Returns the value of the number as type 'long'.
     * This might cause an overflow!
     * 
     * @return the value (long)
     */
    @Override
    public long longValue() {
        return (long)getValue();
    }
    
    /**
     * Returns the value of the number as type 'float'.
     * This might cause an overflow!
     * 
     * @return the value (float)
     */
    @Override
    public float floatValue() {
        return (float)getValue();
    }
    
    /**
     * Returns the value of the number as type 'double'.
     * 
     * @return the value (double)
     */
    @Override
    public double doubleValue() {
        return (double)getValue();
    }
    
    /**
     * Returns the current number format
     * 
     * @return the number format (NumberFormat)
     */
    public NumberFormat getNumberFormat() {
        return this.format;
    }
    
    /**
     * Sets a new number format
     * 
     * @param format (NumberFormat) the new number format
     */
    public void setNumberFormat(NumberFormat format) {
        this.format = format;
    }
    
    /* -----------------------------------------------------------------------
    * Basic operations (+,-,*,/,^)
    * 
    * This section defines the basic mathematical operations that must be
    * provided by any number class.
    * ------------------------------------------------------------------------
    */
    
    /**
     * Abstract - must be implemented by subclasses
     * Adds another number to the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the addition (Number)
     */
    public abstract Number add(Number otherNumber, boolean returnNewNumber);
    
    /**
     * Abstract - must be implemented by subclasses
     * Subtracts another number from the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the subtraction (Number)
     */
    public abstract Number sub(Number otherNumber, boolean returnNewNumber);
    
    /**
     * Abstract - must be implemented by subclasses
     * Multiplies another number to the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Number)
     */
    public abstract Number mul(Number otherNumber, boolean returnNewNumber);
    
    /**
     * Abstract - must be implemented by subclasses
     * Divides the number by another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the division (Number)
     * @throws FiMaLibDivisionByZeroException if the divisor was zero
     */
    public abstract Number div(Number otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException;
    
    /**
     * Abstract - must be implemented by subclasses
     * Calculates the power of this number by another number
     * (i.e. thisNumber ^ otherNumber)
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return 
     */
    public abstract Number pow(Number otherNumber, boolean returnNewNumber);
    
    /**
     * Adds another number to the number and always returns a new object
     * instance
     * 
     * @param otherNumber (Number) the other number
     * @return the result of the addition (Number)
     */
    public Number add(Number otherNumber) {
        return add(otherNumber, true);
    }
    
    /**
     * Subtracts another number from the number and always returns a new object
     * instance
     * 
     * @param otherNumber (Number) the other number
     * @return the result of the subtraction (Number)
     */
    public Number sub(Number otherNumber) {
        return sub(otherNumber, true);
    }
    
    /**
     * Multiplies another number with the number and always returns a new object
     * instance
     * 
     * @param otherNumber (Number) the other number
     * @return the result of the multiplication (Number)
     */
    public Number mul(Number otherNumber) {
        return mul(otherNumber, true);
    }
    
    /**
     * Divides the number by another number and always returns a new object
     * instance
     * 
     * @param otherNumber (Number) the other number
     * @return the result of the division (Number)
     * @throws FiMaLibDivisionByZeroException if the divisor was zero
     */
    public Number div(Number otherNumber) throws FiMaLibDivisionByZeroException {
        return div(otherNumber, true);
    }
    
    /**
     * Calculates the power of this number to another number and always returns 
     * a new object instance
     * 
     * @param otherNumber (Number) the other number
     * @return the result of the power operation (Number)
     */
    public Number pow(Number otherNumber) {
        return mul(otherNumber, true);
    }

    /* -----------------------------------------------------------------------
    * Non-trigonometrical functions
    * 
    * This section defines the non-trigonometrical functions that must be
    * provided by any number class which do *NOT* require parameters or more
    * numeric values. These are:
    *   abs     - returns the absolute value of the number
    *   sgn     - returns the sign of the number
    *   sqrt    - returns the square root of the number(*)
    *   exp     - returns the result of e^value
    *   ln      - returns the natural logarithm of the value
    *   log     - returns the base-10 logarithm of the value
    *
    * (*) this function will return only one of the potentially two solutions
    * for the sqrt funtion!
    * ------------------------------------------------------------------------
    */
    
    public abstract Number abs(boolean returnNewNumber);
    
    public abstract Number sgn(boolean returnNewNumber);
    
    public abstract Number sqrt(boolean returnNewNumber) throws FiMaLibUndefinedException;
    
    public abstract Number exp(boolean returnNewNumber);
    
    public abstract Number ln(boolean returnNewNumber) throws FiMaLibUndefinedException;
    
    public abstract Number log(boolean returnNewNumber) throws FiMaLibUndefinedException;
    
    public Number abs() {
        return abs(true);
    }
    
    public Number sgn() {
        return sgn(true);
    }
    
    public Number sqrt() throws FiMaLibUndefinedException {
        return sqrt(true);
    }
    
    public Number exp() {
        return exp(true);
    }
    
    public Number ln() throws FiMaLibUndefinedException {
        return ln(true);
    }
    
    public Number log() throws FiMaLibUndefinedException {
        return log(true);
    }
    
    /* -----------------------------------------------------------------------
    * Trigonometrical functions
    * 
    * This section defines the trigonometrical functions that must be
    * provided by any number class which do *NOT* require parameters or more
    * numeric values. These are:
    *   sin     - returns the sinus value of the number
    *   cos     - returns the cosinus value of the number
    *   tan     - returns the tangens value of the number
    *   cot     - returns the cotangens value of the number
    *   sec     - returns the secant value of the number
    *   csc     - returns the cosecant value of the number
    *   arcsin  - returns the arcus sinus value of the number
    *   arccos  - returns the arcus cosinus value of the number
    *   arctan  - returns the arcus tangens value of the number
    *   arccot  - returns the arcus cotangens value of the number
    *   arcsec  - returns the arcus secant value of the number
    *   arccsc  - returns the arcus cosecant value of the number
    *   sinh    - returns the sinus hyperbolicus value of the number
    *   cosh    - returns the cosinus hyperbolicus value of the number
    *   tanh    - returns the tangens hyperbolicus value of the number
    *   coth    - returns the cotangens hyperbolicus value of the number
    *   sech    - returns the secant hyperbolicus value of the number
    *   csch    - returns the cosecant hyperbolicus value of the number
    *   arcsinh - returns the arcus sinus hyperbolicus value of the number
    *   arccosh - returns the arcus cosinus hyperbolicus value of the number
    *   arctanh - returns the arcus tangens hyperbolicus value of the number
    *   arccoth - returns the arcus cotangens hyperbolicus value of the number
    *   arcsech - returns the arcus secant hyperbolicus value of the number
    *   arccsch - returns the arcus cosecant hyperbolicus value of the number
    * ------------------------------------------------------------------------
    */
    
    public abstract Number sin(boolean returnNewNumber);
    public abstract Number cos(boolean returnNewNumber);
    public abstract Number tan(boolean returnNewNumber);
    public abstract Number cot(boolean returnNewNumber);
    public abstract Number sec(boolean returnNewNumber);
    public abstract Number csc(boolean returnNewNumber);
    public abstract Number arcsin(boolean returnNewNumber);
    public abstract Number arccos(boolean returnNewNumber);
    public abstract Number arctan(boolean returnNewNumber);
    public abstract Number arccot(boolean returnNewNumber);
    public abstract Number arcsec(boolean returnNewNumber);
    public abstract Number arccsc(boolean returnNewNumber);
    public abstract Number sinh(boolean returnNewNumber);
    public abstract Number cosh(boolean returnNewNumber);
    public abstract Number tanh(boolean returnNewNumber);
    public abstract Number coth(boolean returnNewNumber);
    public abstract Number sech(boolean returnNewNumber);
    public abstract Number csch(boolean returnNewNumber);
    public abstract Number arcsinh(boolean returnNewNumber);
    public abstract Number arccosh(boolean returnNewNumber);
    public abstract Number arctanh(boolean returnNewNumber);
    public abstract Number arccoth(boolean returnNewNumber);
    public abstract Number arcsech(boolean returnNewNumber);
    public abstract Number arccsch(boolean returnNewNumber);

    public Number sin() {
        return sin(true);
    }
    
    public Number cos() {
        return cos(true);
    }
    
    public Number tan() {
        return tan(true);
    }
    
    public Number cot() {
        return cot(true);
    }
    
    public Number sec() {
        return sec(true);
    }
    
    public Number csc() {
        return csc(true);
    }
    
    public Number arcsin() {
        return arcsin(true);
    }
    
    public Number arccos() {
        return arccos(true);
    }
    
    public Number arctan() {
        return arctan(true);
    }
    
    public Number arccot() {
        return arccot(true);
    }
    
    public Number arcsec() {
        return arcsec(true);
    }
    
    public Number arccsc() {
        return arccsc(true);
    }
    
    public Number sinh() {
        return sinh(true);
    }
    
    public Number cosh() {
        return cosh(true);
    }
    
    public Number tanh() {
        return tanh(true);
    }
    
    public Number coth() {
        return coth(true);
    }
    
    public Number sech() {
        return sech(true);
    }
    
    public Number csch() {
        return csch(true);
    }
    
    public Number arcsinh() {
        return arcsinh(true);
    }
    
    public Number arccosh() {
        return arccosh(true);
    }
    
    public Number arctanh() {
        return arctanh(true);
    }
    
    public Number arccoth() {
        return arccoth(true);
    }
    
    public Number arcsech() {
        return arcsech(true);
    }
    
    public Number arccsch() {
        return arccsch(true);
    }
}
