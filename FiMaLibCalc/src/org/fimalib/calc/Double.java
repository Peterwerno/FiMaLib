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
import java.text.ParseException;

/**
 * This class implements the operations on floating point values of type
 * double.
 * 
 * Many methods have a returnNewNumber parameter. If this parameter is set to
 * true, the method will return the result of the operation in a new instance
 * of the class Complex. It will also keep the original complex value unchanged.
 * If it is set to false, then the original complex object will be changed and
 * will contain the result of the operation.
 * 
 * @author Peter Werno
 */
public class Double extends Number {
    double value;
    
    /**
     * Creates a new instance of Double with value 0
     */
    public Double() {
        super();
        
        this.value = 0.0;
    }
    
    /**
     * Creates a new instance of Double with a given value
     * 
     * @param value (double) the value
     */
    public Double(double value) {
        super();
        
        this.value = value;
    }
    
    /**
     * Creates a new instance of Double with value 0 and a defined number format 
     * @param format (NumberFormat) the number format
     */
    public Double(NumberFormat format) {
        super(format);
        
        this.value = 0.0;
    }

    /**
     * Creates a new instance of Double with a given value and a defined number
     * format
     * 
     * @param value (double) the value
     * @param format (NumberFormat) the number format
     */
    public Double(double value, NumberFormat format) {
        super(format);
        
        this.value = value;
    }
    
    /**
     * Creates a new instance of Double with a given value encoded as a string
     * 
     * @param value (String) the encoded value
     * @throws ParseException 
     */
    public Double(String value) throws ParseException {
        super(NumberFormat.getInstance());
        
        this.value = this.format.parse(value).doubleValue();
    }

    /**
     * Creates a new instance of Double with a given value encoded as as string
     * and a given number format
     * 
     * @param value (String) the value
     * @param format (NumberFormat) the number format
     * @throws ParseException 
     */
    public Double(String value, NumberFormat format) throws ParseException {
        super(format);
        
        this.value = this.format.parse(value).doubleValue();
    }
    /**
     * Adds another double value to the value
     * 
     * @param otherNumber (Double) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the addition (Double)
     */
    public Double add(Double otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(this.value + otherNumber.getValue(), this.format);
        
        this.value+=otherNumber.getValue();
        
        return this;
    }
    
    /**
     * Adds another double value to the value and always returns a new object
     * instance
     * 
     * @param otherNumber (Double) the other value
     * @return the result of the addition (Double)
     */
    public Double add(Double otherNumber) {
        return add(otherNumber, true);
    }
    
    /**
     * Adds another number to the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the addition (Number)
     */
    @Override
    public Number add(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return new Complex(this.value + other.getValue(), other.getImg(), this.format);
        }
        if(returnNewNumber) {
            return new Double(this.value + otherNumber.getValue(), this.format);
        }
        else {
            this.value += otherNumber.getValue();
            return this;
        }
    }

    /**
     * Subtracts another double value from the value
     * 
     * @param otherNumber (Double) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the subtraction (Double)
     */
    public Double sub(Double otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(this.value - otherNumber.getValue(), this.format);
        
        this.value-=otherNumber.getValue();
        
        return this;
    }
    
    /**
     * Subtracts another double value from the value and always returns a new 
     * object instance
     * 
     * @param otherNumber (Double) the other value
     * @return the result of the subtraction (Double)
     */
    public Double sub(Double otherNumber) {
        return sub(otherNumber, true);
    }
    
    /**
     * Subtracts another number from the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the subtraction (Number)
     */
    @Override
    public Number sub(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return new Complex(this.value - other.getValue(), -other.getImg(), this.format);
        }
        if(returnNewNumber) {
            return new Double(this.value - otherNumber.getValue(), this.format);
        }
        else {
            this.value -= otherNumber.getValue();
            return this;
        }
    }

    /**
     * Multiplies another double value with the value
     * 
     * @param otherNumber (Double) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Double)
     */
    public Double mul(Double otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(this.value * otherNumber.getValue(), this.format);
        
        this.value*=otherNumber.getValue();
        
        return this;
    }
    
    /**
     * Multiplies another double value with the value and always returns a new 
     * object instance
     * 
     * @param otherNumber (Double) the other value
     * @return the result of the multiplication (Double)
     */
    public Double mul(Double otherNumber) {
        return mul(otherNumber, true);
    }
    
    /**
     * Multiplies another number with the number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Number)
     */
    @Override
    public Number mul(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return new Complex(this.value, 0.0, this.format).mul(otherNumber, false);
        }
        if(returnNewNumber) {
            return new Double(this.value * otherNumber.getValue(), this.format);
        }
        else {
            this.value *= otherNumber.getValue();
            return this;
        }
    }

    /**
     * Divides the value by another double value
     * 
     * @param otherNumber (Double) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the division (Double)
     * @throws FiMaLibDivisionByZeroException
     */
    public Double div(Double otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException {
        if(otherNumber.getValue() == 0.0)
            throw new FiMaLibDivisionByZeroException("Division by zero");
        
        if(returnNewNumber)
            return new Double(this.value / otherNumber.getValue(), this.format);
        
        this.value-=otherNumber.getValue();
        
        return this;
    }
    
    /**
     * Divides the value by another double value  and always returns a new 
     * object instance
     * 
     * @param otherNumber (Double) the other value
     * @return the result of the division (Double)
     * @throws FiMaLibDivisionByZeroException
     */
    public Double div(Double otherNumber) throws FiMaLibDivisionByZeroException {
        return div(otherNumber, true);
    }
    
    /**
     * Divides the number by another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the division (Number)
     * @throws FiMaLibDivisionByZeroException
     */
    @Override
    public Number div(Number otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return new Complex(this.value, 0.0, this.format).div(otherNumber, false);
        }
        if(otherNumber.getValue() == 0.0)
            throw new FiMaLibDivisionByZeroException("Division by zero");
        if(returnNewNumber) {
            return new Double(this.value / otherNumber.getValue(), this.format);
        }
        else {
            this.value /= otherNumber.getValue();
            return this;
        }
    }

    /**
     * Calculates the power of the value to another double value
     * 
     * @param otherNumber (Double) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the power calculation (Double)
     */
    public Double pow(Double otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.pow(this.value, otherNumber.getValue()), this.format);
        
        this.value = Math.pow(this.value, otherNumber.getValue());
        
        return this;
    }
    
    /**
     * Calculates the power of this value to another value and always returns a
     * new object instance
     * 
     * @param otherNumber (Double) the other value
     * @return the result of the power calculation (Double)
     */
    public Double pow(Double otherNumber) {
        return pow(otherNumber, true);
    }
    
    /**
     * Calculates the power of this number to another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the power calculation (Number)
     */
    @Override
    public Number pow(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return new Complex(this.value, 0.0, this.format).pow(otherNumber, false);
        }
        if(otherNumber instanceof Double) {
            Double other = (Double)otherNumber;
            return pow(other, returnNewNumber);
        }
        
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            Complex copy = new Complex(this.value);
            
            return copy.pow(other);
        }
        
        this.value = Math.pow(this.value, otherNumber.getValue());
        
        return this;
    }

    /**
     * Creates a copy of the value
     * 
     * @return a copy of the object (Number)
     */
    @Override
    public Number copy() {
        return new Double(this.value, this.format);
    }
    
    /**
     * Returns the value
     * 
     * @return the value (double)
     */
    @Override
    public double getValue() {
        return this.value;
    }

    /**
     * Compares this Double number to another number
     * 
     * @param otherNumber (Number) the other number
     * @return if both numbers are equal (boolean)
     */
    @Override
    public boolean equals(Number otherNumber) {
        if(otherNumber instanceof Double) {
            Double other = (Double)otherNumber;
            
            return this.value == other.getValue();
        }
        
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            
            return (this.value == other.getValue()) && (other.getImg() == 0.0);
        }
        
        return this.value == otherNumber.getValue();
    }

    /**
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
    @Override
    public int compareTo(Number otherNumber) {
        if(otherNumber instanceof Complex) {
            double otherValue = otherNumber.abs(true).getValue();
            
            if(this.value < otherValue) 
                return -1;
            if(this.value > otherValue)
                return 1;
            return 0;
        }
        
        if(this.value < otherNumber.getValue())
            return -1;
        if(this.value > otherNumber.getValue())
            return 1;
        return 0;
    }
    
    /**
     * Returns the number as a string
     * 
     * @return the number (String)
     */
    @Override
    public String toString() {
        return format.format(this.value);
    }
    
    /**
     * Returns the absolute value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the absolute value (Number)
     */
    @Override
    public Number abs(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.abs(this.value), this.format);
        
        this.value = Math.abs(this.value);
        return this;
    }

    /**
     * Returns the sign value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the sign value (Number)
     */
    @Override
    public Number sgn(boolean returnNewNumber) {
        double result = 0.0;
        if(this.value < 0.0)
            result = -1.0;
        else if(this.value > 0.0)
            result = 1.0;
        
        if(returnNewNumber) {
            return new Double(result, this.format);
        }
        
        this.value = result;
        return this;
    }

    /**
     * Returns the square root of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the square root (Number)
     * @throws org.fimalib.calc.FiMaLibUndefinedException
     */
    @Override
    public Number sqrt(boolean returnNewNumber) throws FiMaLibUndefinedException {
        if(this.value < 0.0)
            throw new FiMaLibUndefinedException("Cannot calculate square root of negative value");
        
        if(returnNewNumber)
            return new Double(Math.sqrt(this.value), this.format);
        
        this.value = Math.sqrt(this.value);
        return this;
    }

    /**
     * Returns the value of e to the power of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return e^number (Number)
     */
    @Override
    public Number exp(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.exp(this.value), this.format);
        
        this.value = Math.exp(this.value);
        return this;
    }

    /**
     * Returns the natural logarithm of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the natural logarithm (Number)
     * @throws org.fimalib.calc.FiMaLibUndefinedException
     */
    @Override
    public Number ln(boolean returnNewNumber) throws FiMaLibUndefinedException {
        if(this.value <= 0.0)
            throw new FiMaLibUndefinedException("Cannot calc logarithm of negative value");
        
        if(returnNewNumber)
            return new Double(Math.log(this.value), this.format);
        
        this.value = Math.log(this.value);
        return this;
    }

    /**
     * Returns the logarithm to basis 10 of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the logarithm to basis 10 (Number)
     * @throws org.fimalib.calc.FiMaLibUndefinedException
     */
    @Override
    public Number log(boolean returnNewNumber) throws FiMaLibUndefinedException {
        if(this.value <= 0.0)
            throw new FiMaLibUndefinedException("Cannot calc logarithm of negative value");
        
        if(returnNewNumber)
            return new Double(Math.log10(this.value), this.format);
        
        this.value = Math.log10(this.value);
        return this;
    }

    /**
     * Returns the sinus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the sinus (Number)
     */
    @Override
    public Number sin(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.sin(this.value), this.format);
        
        this.value = Math.sin(this.value);
        return this;
    }

    /**
     * Returns the cosinus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosinus (Number)
     */
    @Override
    public Number cos(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.cos(this.value), this.format);
        
        this.value = Math.cos(this.value);
        return this;
    }

    /**
     * Returns the tangent of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the tangent (Number)
     */
    @Override
    public Number tan(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.tan(this.value), this.format);
        
        this.value = Math.tan(this.value);
        return this;
    }

    /**
     * Returns the cotangent of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the contangent (Number)
     */
    @Override
    public Number cot(boolean returnNewNumber) {
        double result;
        double tan = Math.tan(this.value);
        if(tan == 0.0)
            result = java.lang.Double.POSITIVE_INFINITY;
        else
            result = 1.0/tan;
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the secant of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the secant (Number)
     */
    @Override
    public Number sec(boolean returnNewNumber) {
        double result;
        double cos = Math.cos(this.value);
        if(cos == 0.0)
            result = java.lang.Double.POSITIVE_INFINITY;
        else
            result = 1.0/cos;
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the cosecant of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosecant (Number)
     */
    @Override
    public Number csc(boolean returnNewNumber) {
        double result;
        double sin = Math.sin(this.value);
        if(sin == 0.0)
            result = java.lang.Double.POSITIVE_INFINITY;
        else
            result = 1.0/sin;
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc sinus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc sinus (Number)
     */
    @Override
    public Number arcsin(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.asin(this.value), this.format);
        
        this.value = Math.asin(this.value);
        return this;
    }

    /**
     * Returns the arc cosinus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosinus (Number)
     */
    @Override
    public Number arccos(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.acos(this.value), this.format);
        
        this.value = Math.acos(this.value);
        return this;
    }

    /**
     * Returns the arc tangent of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc tangent (Number)
     */
    @Override
    public Number arctan(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.atan(this.value), this.format);
        
        this.value = Math.atan(this.value);
        return this;
    }

    /**
     * Returns the arc cotangent of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cotangent (Number)
     */
    @Override
    public Number arccot(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if(this.value > 0)
            result = Math.atan(1.0/this.value);
        else if(this.value < 0)
            result = Math.atan(1.0/this.value) + Math.PI;
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc secant of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc secant (Number)
     */
    @Override
    public Number arcsec(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if(this.value != 0.0)
            result = Math.acos(1.0 / this.value);
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc cosecant of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosecant (Number)
     */
    @Override
    public Number arccsc(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if(this.value != 0.0)
            result = Math.asin(1.0 / this.value);
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the sinus hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the sinus hyperbolicus (Number)
     */
    @Override
    public Number sinh(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.sinh(this.value), this.format);
        
        this.value = Math.sinh(this.value);
        return this;
    }

    /**
     * Returns the cosinus hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosinus hyperbolicus (Number)
     */
    @Override
    public Number cosh(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.cosh(this.value), this.format);
        
        this.value = Math.cosh(this.value);
        return this;
    }

    /**
     * Returns the tangent hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the tangent hyperbolicus (Number)
     */
    @Override
    public Number tanh(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Double(Math.tanh(this.value), this.format);
        
        this.value = Math.tanh(this.value);
        return this;
    }

    /**
     * Returns the cotangent hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cotangent hyperbolicus (Number)
     */
    @Override
    public Number coth(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        double sinh = Math.sinh(this.value);
        double cosh = Math.cosh(this.value);
        
        if(sinh != 0.0)
            result = cosh / sinh;
        
        if(returnNewNumber) 
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the secant hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the secant hyperbolicus (Number)
     */
    @Override
    public Number sech(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        double cosh = Math.cosh(this.value);
        
        if(cosh != 0.0)
            result = 1.0 / cosh;
        
        if(returnNewNumber) 
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the cosecant hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosecant hyperbolicus (Number)
     */
    @Override
    public Number csch(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        double sinh = Math.sinh(this.value);
        
        if(sinh != 0.0)
            result = 1.0 / sinh;
        
        if(returnNewNumber) 
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc sinus hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc sinus hyperbolicus (Number)
     */
    @Override
    public Number arcsinh(boolean returnNewNumber) {
        double result = Math.log(this.value + (Math.sqrt(this.value * this.value + 1.0)));
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc cosinus hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosinus hyperbolicus (Number)
     */
    @Override
    public Number arccosh(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if(this.value >= 1.0)
            result = Math.log(this.value + (Math.sqrt(this.value * this.value - 1.0)));
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc tangent hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc tangent hyperbolicus (Number)
     */
    @Override
    public Number arctanh(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if((this.value > -1.0) && (this.value < 1.0))
            result = 0.5 * Math.log((1.0 + this.value) / (1.0 - this.value));
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc cotangent hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cotangent hyperbolicus (Number)
     */
    @Override
    public Number arccoth(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;
        
        if((this.value < -1.0) || (this.value > 1.0))
            result = 0.5 * Math.log((this.value + 1.0) / (this.value - 1.0));
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc secant hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc secant hyperbolicus (Number)
     */
    @Override
    public Number arcsech(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;

        if((this.value > 0.0) && (this.value <= 1.0))
            result = Math.log(((1.0 + Math.sqrt(1.0 - this.value*this.value)) / this.value));
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

    /**
     * Returns the arc cosecant hyperbolicus of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosecant hyperbolicus (Number)
     */
    @Override
    public Number arccsch(boolean returnNewNumber) {
        double result = java.lang.Double.NaN;

        if(this.value > 0.0)
            result = Math.log(((1.0 + Math.sqrt(1.0 + this.value*this.value)) / this.value));
        else if (this.value < 0.0)
            result = Math.log(((1.0 - Math.sqrt(1.0 + this.value*this.value)) / this.value));
        
        if(returnNewNumber)
            return new Double(result, this.format);
        
        this.value = result;
        return this;
    }

}
