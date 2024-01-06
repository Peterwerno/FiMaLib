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
 * This class implements all the operations for complex numbers.
 * 
 * Many methods have a returnNewNumber parameter. If this parameter is set to
 * true, the method will return the result of the operation in a new instance
 * of the class Complex. It will also keep the original complex value unchanged.
 * If it is set to false, then the original complex object will be changed and
 * will contain the result of the operation.
 * 
 * @author Peter Werno
 */
public class Complex extends Number {
    double real;
    double img;
    
    /**
     * Creates a new instance of Complex with value 0/0
     */
    public Complex() {
        super();
        
        this.real = 0.0;
        this.img = 0.0;
    }
    
    /**
     * Creates a new instance of Complex with real part defined and imaginary
     * part set to 0
     * 
     * @param real (double) the real part of the complex number
     */
    public Complex(double real) {
        super();
        
        this.real = real;
        this.img = 0.0;
    }
    
    /**
     * Creates a new instance of Complex with real and imaginary part defined
     * 
     * @param real (double) the real part
     * @param img (double) the imaginary part
     */
    public Complex(double real, double img) {
        super();
        
        this.real = real;
        this.img = img;
    }

    /**
     * Creates a new instance of Complex with value 0/0 and given number format
     * 
     * @param format (NumberFormat) the number format
     */
    public Complex(NumberFormat format) {
        super(format);
        
        this.real = 0.0;
        this.img = 0.0;
    }
    
    /**
     * Creates a new instance of Complex with real part defined and imaginary
     * part set to 0 and given number format
     * 
     * @param real (double) the real part of the complex number
     * @param format (NumberFormat) the number format
     */
    public Complex(double real, NumberFormat format) {
        super(format);
        
        this.real = real;
        this.img = 0.0;
    }
    
    /**
     * Creates a new instance of Complex with real and imaginary part defined
     * 
     * @param real (double) the real part
     * @param img (double) the imaginary part
     * @param format (NumberFormat) the number format
     */
    public Complex(double real, double img, NumberFormat format) {
        super(format);
        
        this.real = real;
        this.img = img;
    }
    
    /**
     * Creates a new instance of Complex with the value provided encoded as
     * a string
     * 
     * @param content (String) the value
     * @throws ParseException 
     */
    public Complex(String content) throws ParseException {
        super();
        
        parseString(content, this.format);
    }
    
    /**
     * Creates a new instance of Complex with the value provided encoded as
     * a string
     * 
     * @param content (String) the value
     * @param format (NumberFormat) the number format 
     * @throws ParseException 
     */
    public Complex(String content, NumberFormat format) throws ParseException {
        super(format);
        
        parseString(content, format);
    }
    
    /**
     * Parses a string that encodes a complex number
     * 
     * @param content (String) the encoded complex number
     * @param result (Complex) the complex object that should be evaluated
     * @param format (NumberFormat) the number format
     * @throws ParseException
     */
    final void parseString(String content, NumberFormat format) throws ParseException {
        String sReal;
        String sImg;
        int posPlus = content.indexOf("+");
        if(posPlus > 0) {
            sReal = content.substring(0, posPlus);
            if(posPlus < content.length())
                sImg = content.substring(content.indexOf("+")+1);
            else
                sImg = "";
        }
        else {
            int posMinus = content.indexOf("-");
            if(posMinus == 0) {
                posMinus = content.indexOf("-", 1);
            }
            
            if(posMinus > 0) {
                sReal = content.substring(0, posMinus);
                sImg = content.substring(posMinus);
            }
            else {
                sReal = content;
                sImg = "0";
            }
        }
        
        if(sReal.endsWith("i")) {
            sImg = sReal;
            sReal = "0";
        }
        
        if(sImg.endsWith("i")) {
            sImg = sImg.substring(0, sImg.indexOf("i"));
            if(sImg.equals("")) sImg = "1";
            if(sImg.equals("-")) sImg = "-1";
        }
        
        this.real = format.parse(sReal).doubleValue();
        this.img = format.parse(sImg).doubleValue();
    }

    /**
     * Adds the complex number to another complex number
     * 
     * @param otherNumber (Complex) the other complex number
     * @param returnNewNumber (boolean) see above
     * @return the result of the addition (Complex)
     */
    public Complex add(Complex otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Complex(this.real + otherNumber.getValue(), this.img + otherNumber.getImg(), this.format);
        
        this.real += otherNumber.getValue();
        this.img += otherNumber.getImg();
        
        return this;
    }
    
    /**
     * Adds the complex number to another comples number and always returns a 
     * new instance of Complex
     * 
     * @param otherNumber (Complex) the other complex number
     * @return the result (Complex) in a new object instance
     */
    public Complex add(Complex otherNumber) {
        return add(otherNumber, true);
    }
    
    /**
     * Adds the complex number to another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the addition (Number) [Complex]
     */
    @Override
    public Number add(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return add(other, returnNewNumber);
        }
        else {
            if(returnNewNumber) 
                return new Complex(this.real + otherNumber.getValue(), this.img, this.format);
            
            this.real += otherNumber.getValue();
        }
        
        return this;
    }

    /**
     * Subtractes another complex number from the complex number
     * 
     * @param otherNumber (Complex) the other complex number
     * @param returnNewNumber (boolean) see above
     * @return the result of the subtraction (Complex)
     */
    public Complex sub(Complex otherNumber, boolean returnNewNumber) {
        if(returnNewNumber)
            return new Complex(this.real - otherNumber.getValue(), this.img - otherNumber.getImg(), this.format);
        
        this.real -= otherNumber.getValue();
        this.img -= otherNumber.getImg();
        
        return this;
    }
    
    /**
     * Subtracts another complex number from the complex number and always returns a 
     * new instance of Complex
     * 
     * @param otherNumber (Complex) the other complex number
     * @return the result (Complex) in a new object instance
     */
    public Complex sub(Complex otherNumber) {
        return sub(otherNumber, true);
    }
    
    /**
     * Subtracts another number from this number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the subtraction (Number)
     */
    @Override
    public Number sub(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return sub(other, returnNewNumber);
        }
        else {
            if(returnNewNumber) 
                return new Complex(this.real - otherNumber.getValue(), this.img, this.format);
            
            this.real -= otherNumber.getValue();
        }
        
        return this;
    }
    
    /**
     * Calculates the complex multiple.
     * 
     * (a+bi) * (c+di) = ac-bd + (ad+bc)i
     * 
     * @param otherNumber (Complex) the other complex number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Complex)
     */
    public Complex mul(Complex otherNumber, boolean returnNewNumber) {
        double newReal = this.real*otherNumber.getValue() - this.img*otherNumber.getImg();
        double newImg = this.real*otherNumber.getImg() + this.img*otherNumber.getValue();
            
        if(returnNewNumber) {
            return new Complex(newReal, newImg, this.format);
        }
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }
    
    /**
     * Returns the result of a complex multiplication and always returns a
     * new object instance
     * 
     * @param otherNumber (Complex) the other number
     * @return the result of the multiplication (Complex)
     */
    public Complex mul(Complex otherNumber) {
        return mul(otherNumber, true);
    }
    
    /**
     * Multiplies the number with another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Number)
     */
    @Override
    public Number mul(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return mul(other, returnNewNumber);
        }
        else {
            if(returnNewNumber) 
                return new Complex(this.real + otherNumber.getValue(), this.img, this.format);
            
            this.real += otherNumber.getValue();
        }
        
        return this;
    }

    /**
     * Calculates the complex division.
     * 
     * (a+bi) / (c+di) = (ac+bd)/(c^2+d^2) + (bc-ad)i/(c^2+d^2)
     * 
     * @param otherNumber (Complex) the other complex number
     * @param returnNewNumber (boolean) see above
     * @return the result of the multiplication (Complex)
     * @throws FiMaLibDivisionByZeroException 
     */
    public Complex div(Complex otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException {
        if((otherNumber.getValue() == 0.0) && (otherNumber.getImg() == 0.0))
            throw new FiMaLibDivisionByZeroException("Division by Zero");
        
        double divisor = otherNumber.getValue()*otherNumber.getValue() + otherNumber.getImg()*otherNumber.getImg();
        if(divisor == 0.0)
            throw new FiMaLibDivisionByZeroException("Division by Zero");
        
        double newReal = (this.real*otherNumber.getValue() + this.img*otherNumber.getImg()) / divisor;
        double newImg = (this.img*otherNumber.getValue() - this.real*otherNumber.getImg()) / divisor;
            
        if(returnNewNumber) {
            return new Complex(newReal, newImg, this.format);
        }
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }
    
    /**
     * Calculates the complex division and always returns a new object instance
     * 
     * @param otherNumber (Complex) the other number
     * @return the result of the division (Complex)
     * @throws FiMaLibDivisionByZeroException 
     */
    public Complex div(Complex otherNumber) throws FiMaLibDivisionByZeroException {
        return div(otherNumber, true);
    }
    
    /**
     * Divides the nubmer by another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the division (Number)
     * @throws FiMaLibDivisionByZeroException if the divisor was zero
     */
    @Override
    public Number div(Number otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return this.div(other, returnNewNumber);
        }
        
        double otherValue = otherNumber.getValue();
        if(otherValue == 0.0)
            throw new FiMaLibDivisionByZeroException("Division by zero");
        
        if(returnNewNumber)
            return new Complex(this.real / otherValue, this.img / otherValue, this.format);
        
        this.real /= otherValue;
        this.img /= otherValue;
        
        return this;
    }
    
    /**
     * Calculates the result of the complex number to the power of another
     * complex number
     * 
     * @param otherNumber (Complex) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the calculation (Complex)
     */
    public Complex pow(Complex otherNumber, boolean returnNewNumber) {
        Number myLn = new Complex(java.lang.Double.NaN, this.format);
        try {
            myLn = ln(true);
        }
        catch (FiMaLibUndefinedException ex) {
            // Do nothing here
        }
        Number result = otherNumber.mul(myLn, true).exp(true);
        Complex cResult = (Complex)result;
        
        if(returnNewNumber)
            return cResult;
        
        this.real = cResult.getValue();
        this.img = cResult.getImg();
        
        return this;
    }
    
    /**
     * Calculates the result of the complex number to the power of another
     * complex number and always returns a new object instance.
     * 
     * @param otherNumber (Complex) the other number
     * @return the result of the calculation (Complex)
     */
    public Complex pow(Complex otherNumber) {
        return pow(otherNumber, true);
    }

    /**
     * Calculates the result of this number to the power of another number
     * 
     * @param otherNumber (Number) the other number
     * @param returnNewNumber (boolean) see above
     * @return the result of the calculation (Number)
     */
    @Override
    public Number pow(Number otherNumber, boolean returnNewNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            return pow(other, returnNewNumber);
        }
        else {
            Complex other = new Complex(otherNumber.getValue(), this.format);
            return pow(other, returnNewNumber);
        }
    }

    /**
     * Returns the real part of the complex value
     * 
     * @return the real part (double)
     */
    @Override
    public double getValue() {
        return this.real;
    }
    
    /**
     * Returns the imaginary part of the complex value
     * 
     * @return the imaginary part (double)
     */
    public double getImg() {
        return this.img;
    }
    
    /**
     * Creates a copy of the complex number
     * 
     * @return the copy of the number (Number)
     */
    @Override
    public Number copy() {
        return new Complex(this.real, this.img, this.format);
    }

    /**
     * Returns wether or not this number is equal to another number
     * 
     * @param otherNumber (Number) the other number
     * @return wether or not the numbers are equal (boolean)
     */
    @Override
    public boolean equals(Number otherNumber) {
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            
            return (this.real == other.getValue()) && (this.img == other.getImg());
        }
        
        return (this.real == otherNumber.getValue()) && (this.img == 0.0);
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
        double thisAbs = this.abs(true).getValue();
        double otherAbs = otherNumber.getValue();
        
        if(otherNumber instanceof Complex) {
            Complex other = (Complex)otherNumber;
            otherAbs = other.abs(true).getValue();
        }
        
        if(thisAbs < otherAbs) return -1;
        if(thisAbs > otherAbs) return 1;
        return 0;
    }
    
    /**
     * Returns the content of the complex number as a string
     * 
     * @return the content (String)
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(format.format(this.real));
        if(this.img > 0.0)
            retVal.append("+");
        if(this.img != 0.0) {
            retVal.append(format.format(this.img));
            retVal.append("i");
        }
        
        return retVal.toString();
    }

    /**
     * Returns the absolute value of a complex number.
     * This is basically the pythagoras formula on real and imaginary part
     * 
     * @param returnNewNumber (boolean) see above
     * @return the absolute value (Number)
     */
    @Override
    public Number abs(boolean returnNewNumber) {
        double result = Math.sqrt(this.real*this.real + this.img*this.img);
        if(returnNewNumber)
            return new Complex(result, this.format);
        
        this.real = result;
        this.img = 0.0;
        return this;
    }

    /**
     * Return the sign of the number.
     * This is defined as
     *  -1  if real part is &lt;0
     *   0  if real part equals 0
     *  +1  if real part is &gt;0
     * 
     * @param returnNewNumber (boolean) see above;
     * @return the sign of the number (Number)
     */
    @Override
    public Number sgn(boolean returnNewNumber) {
        double result = 0.0;
        if(this.real > 0.0)
            result = 1.0;
        else if(this.real < 0.0)
            result = -1.0;
        
        if(returnNewNumber)
            return new Complex(result, this.format);
        
        this.real = result;
        this.img = 0.0;
        return this;
    }

    /**
     * Returns the square root of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the result of the square root (Number)
     * @throws FiMaLibUndefinedException 
     */
    @Override
    public Number sqrt(boolean returnNewNumber) throws FiMaLibUndefinedException {
        return pow(new Complex(0.5, this.format), returnNewNumber);
    }

    /**
     * Calculates the complex expontential function
     * 
     * where 
     * 
     * e^(a+bi) = e^a * e^ib = e^a*(cos(b) + i*sin(b))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the result of e^number (Number)
     */
    @Override
    public Number exp(boolean returnNewNumber) {
        double factor = Math.exp(this.real);
        double cos = Math.cos(this.img);
        double sin = Math.sin(this.img);
        double resultReal = factor * cos;
        double resultImg = factor * sin;
        
        if(returnNewNumber)
            return new Complex(resultReal, resultImg, this.format);
        
        this.real = resultReal;
        this.img = resultImg;
        
        return this;
    }

    /**
     * Returns the natural logarithm of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the natural log (Number)
     * @throws FiMaLibUndefinedException 
     */
    @Override
    public Number ln(boolean returnNewNumber) throws FiMaLibUndefinedException {
        double newReal = Math.log(Math.sqrt(this.real*this.real + this.img*this.img));
        double newImg = Math.atan2(this.img, this.real);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Returns the logarithm to the basis 10 of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the log10 of the number (Number)
     * @throws FiMaLibUndefinedException 
     */
    @Override
    public Number log(boolean returnNewNumber) throws FiMaLibUndefinedException {
        double newReal = (Math.log(Math.sqrt(this.real*this.real + this.img*this.img))) / Math.log(10.0);
        double newImg = Math.atan2(this.img, this.real) / Math.log(10.0);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Return the sinus value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the sinus value (Number)
     */
    @Override
    public Number sin(boolean returnNewNumber) {
        double newReal = Math.sin(this.real) * Math.cosh(this.img);
        double newImg = Math.cos(this.real) * Math.sinh(this.img);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Return the cosinus value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosinus value (Number)
     */
    @Override
    public Number cos(boolean returnNewNumber) {
        double newReal = Math.cos(this.real) * Math.cosh(this.img);
        double newImg = -Math.sin(this.real) * Math.sinh(this.img);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Return the tangent value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the tangent value (Number)
     */
    @Override
    public Number tan(boolean returnNewNumber) {
        double tan_a = Math.tan(this.real);
        double tanh_b = Math.tanh(this.img);
        
        double divisor = 1.0 + tan_a*tan_a * tanh_b*tanh_b;
        
        double newReal = (tan_a - tan_a*tanh_b*tanh_b) / divisor;
        double newImg = (tanh_b + tan_a*tan_a*tanh_b) / divisor;
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the cotangent value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cotangent value (Number)
     */
    @Override
    public Number cot(boolean returnNewNumber) {
        double sin_2a = Math.sin(this.real*2.0);
        double sinh_2b = Math.sinh(this.img*2.0);
        double cosh_2b = Math.cosh(this.img*2.0);
        double cos_2a = Math.cos(this.real*2.0);
        
        double divisor = cosh_2b - cos_2a;
        
        double newReal = sin_2a / divisor;
        double newImg = -sinh_2b / divisor;
        
        if(returnNewNumber) 
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Return the secant value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the secant value (Number)
     */
    @Override
    public Number sec(boolean returnNewNumber) {
        double cosreal = Math.cos(this.real);
        double coshimg = Math.cosh(this.img);
        double sinreal = Math.sin(this.real);
        double sinhimg = Math.sinh(this.img);
        
        double newReal = (cosreal*coshimg) / (cosreal*cosreal * coshimg*coshimg + sinreal*sinreal * sinhimg*sinhimg);
        double newImg = (sinreal*sinhimg)  / (cosreal*cosreal * coshimg*coshimg + sinreal*sinreal * sinhimg*sinhimg);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;
    }

    /**
     * Return the cosecant value of the number
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosecant value (Number)
     */
    @Override
    public Number csc(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            return one.div(this.sin());
        }
        catch (FiMaLibDivisionByZeroException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
/*        double cosreal = Math.cos(this.real);
        double coshimg = Math.cosh(this.img);
        double sinreal = Math.sin(this.real);
        double sinhimg = Math.sinh(this.img);
        
        double newReal = (sinreal*coshimg) / (sinreal*sinreal * coshimg*coshimg + cosreal*cosreal * sinhimg*sinhimg);
        double newImg = (cosreal*sinhimg)  / (sinreal*sinreal * coshimg*coshimg + cosreal*cosreal * sinhimg*sinhimg);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        return this;*/
    }

    /**
     * Return the arc sinus of the number.
     * 
     * Defined as 1/i * ln(i*x+sqrt(1-x^2))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc sinus (Number)
     */
    @Override
    public Number arcsin(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = i.mul(this).add(one.sub(this.mul(this, true)).sqrt()).ln().div(i);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc sinus of the number.
     * 
     * Defined as 1/i * ln(x + sqrt(x^2-1))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosinus (Number)
     */
    @Override
    public Number arccos(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = this.add(this.mul(this).sub(one).sqrt()).ln().div(i);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc tangent of the number.
     * 
     * Defined as 1/2i * ln((i-x)/(i+x))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc tangent (Number)
     */
    @Override
    public Number arctan(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex two = new Complex(2.0, 0.0, this.format);
        
        try {
            Number retVal = i.sub(this).div(i.add(this)).ln().div(two.mul(i));
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc cotangent of the number.
     * 
     * Defined as 1/2i * ln((z+i)/(z-i))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cotangent (Number)
     */
    @Override
    public Number arccot(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex two = new Complex(2.0, 0.0, this.format);
        
        try {
            Number retVal = this.add(i).div(this.sub(i)).ln().div(i.mul(two));
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc secant of the number.
     * 
     * Defined as 1/i * ln((1+sqrt(abs(1-x^2)))/x)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc secant (Number)
     */
    @Override
    public Number arcsec(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = one.add(one.sub(this.mul(this)).sqrt()).div(this).ln().div(i);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc cosecant of the number.
     * 
     * Defined as 1/i * ln((i + sqrt(x^2-1)) / x)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosecant (Number)
     */
    @Override
    public Number arccsc(boolean returnNewNumber) {
        Complex i = new Complex(0.0, 1.0, this.format);
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = i.add(this.mul(this).sub(one).sqrt()).div(this).ln().div(i);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the sinus hyperbolicus of the number.
     * 
     * Defined as sinh(a+bi) = sinh(a)*cos(b) + i * cosh(a)*sin(b)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the sinus hyperbolicus (Number)
     */
    @Override
    public Number sinh(boolean returnNewNumber) {
        double newReal = Math.sinh(this.real) * Math.cos(this.img);
        double newImg = Math.cosh(this.real) * Math.sin(this.img);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the cosinus hyperbolicus of the number.
     * 
     * Defined as cosh(a+bi) = cosh(a)*cos(b) + i * sinh(a)*sin(b)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosinus hyperbolicus (Number)
     */
    @Override
    public Number cosh(boolean returnNewNumber) {
        double newReal = Math.cosh(this.real) * Math.cos(this.img);
        double newImg = Math.sinh(this.real) * Math.sin(this.img);
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the tangent hyperbolicus of the number.
     * 
     * Defined as tanh(a+bi) = (tanh(a) + tanh(a)*tan(b)^2) / (1 + tanh(a)^2*tan(b)^2)
     *                        + i * (tan(b) - tanh(a)^2*tan(b) / (1 + tanh(a)^2*tan(b)^2)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the tangent hyperbolicus (Number)
     */
    @Override
    public Number tanh(boolean returnNewNumber) {
        double tanh_a = Math.tanh(this.real);
        double tan_b = Math.tan(this.img);
        double divisor = 1.0 + tanh_a*tanh_a*tan_b*tan_b;
        
        double newReal = (tanh_a + tanh_a*tan_b*tan_b) / divisor;
        double newImg  = (tan_b - tanh_a*tanh_a*tan_b) / divisor;
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the cotangent hyperbolicus of the number.
     * 
     * Defined as coth(a+bi) = (coth(a) + coth(a)*cot(b)^2) / (coth(a)^2 + cot(b)^2) 
     *                        + i * (cot(b) -coth(a)^2*cot(b)) / (coth(a)^2 + cot(b)^2)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cotangent hyperbolicus (Number)
     */
    @Override
    public Number coth(boolean returnNewNumber) {
        double coth_a = 1.0 / Math.tanh(this.real);
        double cot_b = 1.0 / Math.tan(this.img);
        double divisor = coth_a*coth_a + cot_b*cot_b;
        
        double newReal = (coth_a + coth_a*cot_b*cot_b) / divisor;
        double newImg  = (cot_b - coth_a*coth_a*cot_b) / divisor;
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the secant hyperbolicus of the number.
     * 
     * Defined as sech(a+bi) = (cosh(a)*cos(b) - i*sinh(a)*sin(b))
     *                          / (cosh(a)^2*cos(b)^2 + sinh(a)^2*sin(b)^2)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the secant hyperbolicus (Number)
     */
    @Override
    public Number sech(boolean returnNewNumber) {
        double sinh_a = Math.sinh(this.real);
        double sin_b = Math.sin(this.img);
        double cosh_a = Math.cosh(this.real);
        double cos_b = Math.cos(this.img);
        double divisor = cosh_a*cosh_a * cos_b*cos_b + sinh_a*sinh_a*sin_b*sin_b;
        
        double newReal = (cosh_a*cos_b) / divisor;
        double newImg  = -(sinh_a*sin_b) / divisor;
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the cosecant hyperbolicus of the number.
     * 
     * Defined as cach(a+bi) = (sinh(a)*cos(b) -i * cosh(a)*sin(b)) /
     *                         (sinh(a)^2*cos(b)^2 + cosh(a)^2*sin(b)^2)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the cosecant hyperbolicus (Number)
     */
    @Override
    public Number csch(boolean returnNewNumber) {
        double sinh_a = Math.sinh(this.real);
        double sin_b = Math.sin(this.img);
        double cosh_a = Math.cosh(this.real);
        double cos_b = Math.cos(this.img);
        double divisor = sinh_a*sinh_a * cos_b*cos_b + cosh_a*cosh_a*sin_b*sin_b;
        
        double newReal = (sinh_a*cos_b) / divisor;
        double newImg  = -(cosh_a*sin_b) / divisor;
        
        if(returnNewNumber)
            return new Complex(newReal, newImg, this.format);
        
        this.real = newReal;
        this.img = newImg;
        
        return this;
    }

    /**
     * Return the arc sinus hyperbolicus of the number.
     * 
     * Defined as arcsinh(x) = ln(x + sqrt(x^2 + 1))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc sinus hyperbolicus (Number)
     */
    @Override
    public Number arcsinh(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = this.mul(this).add(one).sqrt().add(this).ln();
            
            return retVal;
        }
        catch (FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc cosinus hyperbolicus of the number.
     * 
     * Defined as arccosh(x) = ln(x + sqrt(x^2 - 1))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosinus hyperbolicus (Number)
     */
    @Override
    public Number arccosh(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = this.mul(this).sub(one).sqrt().add(this).ln();
            
            return retVal;
        }
        catch (FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc tangent hyperbolicus of the number.
     * 
     * Defined as arctanh(x) = 1/2 * ln((1+x) / (1-x))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc tangent hyperbolicus (Number)
     */
    @Override
    public Number arctanh(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        Complex two = new Complex(2.0, 0.0, this.format);
        
        try {
            Number retVal = this.add(one).div(one.sub(this)).ln().div(two);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc cotangent hyperbolicus of the number.
     * 
     * Defined as arccoth(x) = 1/2 * ln((x+1)/(x-1))
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cotangent hyperbolicus (Number)
     */
    @Override
    public Number arccoth(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        Complex two = new Complex(2.0, 0.0, this.format);
        
        try {
            Number retVal = this.add(one).div(this.sub(one)).ln().div(two);
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc secant hyperbolicus of the number.
     * 
     * Defined as arcsech(x) = ln((1 + sqrt(1 - x^2)) / x)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc secant hyperbolicus (Number)
     */
    @Override
    public Number arcsech(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = one.sub(this.mul(this)).sqrt().add(one).div(this).ln();
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }

    /**
     * Return the arc cosecant hyperbolicus of the number.
     * 
     * Defined as arccsch(x) = ln((1 + sqrt(x^2 + 1)) / x)
     * 
     * @param returnNewNumber (boolean) see above
     * @return the arc cosecant hyperbolicus (Number)
     */
    @Override
    public Number arccsch(boolean returnNewNumber) {
        Complex one = new Complex(1.0, 0.0, this.format);
        
        try {
            Number retVal = this.mul(this).add(one).sqrt().add(one).div(this).ln();
            
            return retVal;
        }
        catch (FiMaLibDivisionByZeroException | FiMaLibUndefinedException ex) {
            return new Complex(java.lang.Double.NaN, 0.0, this.format);
        }
    }
    
}
