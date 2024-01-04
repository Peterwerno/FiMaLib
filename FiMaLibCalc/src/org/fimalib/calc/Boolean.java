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
package org.fimalib.calc;

import java.text.NumberFormat;

/**
 * This class implements all the operations for boolean "numbers".
 * 
 * Many methods have a returnNewNumber parameter. If this parameter is set to
 * true, the method will return the result of the operation in a new instance
 * of the class Complex. It will also keep the original complex value unchanged.
 * If it is set to false, then the original complex object will be changed and
 * will contain the result of the operation.
 * 
 * @author peter
 */
public class Boolean extends Number {
    public static final double BOOLEAN_TRUE = 1.0;
    public static final double BOOLEAN_FALSE = 0.0;
    
    boolean value;
    
    /**
     * Creates a new instance of Boolean
     */
    public Boolean() {
        super();
    }
    
    /**
     * Creates a new instance of Boolean with preset value
     * 
     * @param value (boolean) the value
     */
    public Boolean(boolean value) {
        super();
        
        this.value = value;
    }
    
    /**
     * Creates a new instance of Boolean with given number format
     * 
     * @param format (NumberFormat) the number format
     */
    public Boolean(NumberFormat format) {
        super(format);
    }
    
    /**
     * Creates a new instance of Boolean with given value and number format
     * 
     * @param value (boolean) the value
     * @param format (NumberFormat) the number format
     */
    public Boolean(boolean value, NumberFormat format) {
        super(format);
        
        this.value = value;
    }
    
    /**
     * Creates a new instance of Boolean with given value encoded as string
     * and number format
     * 
     * @param value (String) the value encoded as string
     * @param format (NumberFormat) the number format
     */
    public Boolean(String value, NumberFormat format) {
        super(format);
        this.value = value.equals("true");
    }

    /**
     * Creates a new instance of Boolean with the same value
     * 
     * @return the copy (Number)
     */
    @Override
    public Number copy() {
        return new Boolean(this.value, this.format);
    }

    /**
     * Returns the value of the boolean number.
     * See the definitions above
     * 
     * @return the value (double)
     */
    @Override
    public double getValue() {
        if(this.value) 
            return BOOLEAN_TRUE;
        else
            return BOOLEAN_FALSE;
    }

    /**
     * Returns the boolean value
     * 
     * @return the value (boolean)
     */
    public boolean getBooleanValue() {
        return this.value;
    }
    
    /**
     * Returns if this number is equal to another number
     * 
     * @param otherNumber (Number) the other number
     * @return wether the two are equal
     */
    @Override
    public boolean equals(Number otherNumber) {
        if(otherNumber instanceof Boolean) {
            Boolean other = (Boolean)otherNumber;
            
            return this.value == other.getBooleanValue();
        }
        else
            return false;
    }

    /**
     * Compares this number to another number.
     * 
     * if other number is boolean as well, then it returns
     *  0 if both are identical
     * -1 if this is false and other is true
     * +1 if this is true and other is false 
     * 
     * if other number is not boolean, this returns -1
     * 
     * @param otherNumber (Number) the other number
     * @return the comparison result (int)
     */
    @Override
    public int compareTo(Number otherNumber) {
        if(otherNumber instanceof Boolean) {
            Boolean other = (Boolean)otherNumber;
            if(this.value) {
                if(other.getBooleanValue())
                    return 0;
                else
                    return 1;
            }
            else {
                if(other.getBooleanValue())
                    return -1;
                else
                    return 0;
            }
        }
        else
            return -1;
    }
    
    /**
     * Returns the content of the boolean value as string
     * 
     * @return the content (String)
     */
    @Override
    public String toString() {
        if(this.value)
            return "true";
        else
            return "false";
    }
    
    /*
    * Special operations for boolean values:
    */
    
    /**
     * Calculates the logical AND of this and another boolean value
     * 
     * @param otherNumber (Boolean) the other value
     * @param returnNewNumber (boolean) see above
     * @return the result (Boolean)
     */
    public Boolean and(Boolean otherNumber, boolean returnNewNumber) {
        boolean retVal = this.value && otherNumber.getBooleanValue();
        
        if(returnNewNumber)
            return new Boolean(retVal, this.format);
        
        this.value = retVal;
        return this;
    }
    
    /**
     * Calculates the logical AND of this and another boolean value
     * and always returns a new object instance
     * 
     * @param otherNumber (Boolean) the other value
     * @return the result (Boolean)
     */
    public Boolean and(Boolean otherNumber) {
        return this.and(otherNumber, true);
    }
    
    /**
     * Calculates the logical OR of this and another boolean value
     * 
     * @param otherNumber (Boolean) the other value
     * @param returnNewNumber (boolean) see above
     * @return the result (Boolean)
     */
    public Boolean or(Boolean otherNumber, boolean returnNewNumber) {
        boolean retVal = this.value || otherNumber.getBooleanValue();
        
        if(returnNewNumber)
            return new Boolean(retVal, this.format);
        
        this.value = retVal;
        return this;
    }
    
    /**
     * Calculates the logical OR of this and another boolean value
     * and always returns a new object istance
     * 
     * @param otherNumber (Boolean) the other value
     * @return the result (Boolean)
     */
    public Boolean or(Boolean otherNumber) {
        return this.or(otherNumber, true);
    }
    
    /**
     * Calculates the logical XOR of this and another boolean value
     * 
     * @param otherNumber (Boolean) the other value
     * @param returnNewNumber (boolean) see above
     * @return the result (Boolean)
     */
    public Boolean xor(Boolean otherNumber, boolean returnNewNumber) {
        boolean retVal = (this.value && !otherNumber.getBooleanValue()) || (!this.value && (otherNumber.getBooleanValue()));
        
        if(returnNewNumber)
            return new Boolean(retVal, this.format);
        
        this.value = retVal;
        return this;
    }
    
    /**
     * Calculates the logical XOR of this and another boolean value
     * and always returns a new object instance
     * 
     * @param otherNumber (Boolean) the other value
     * @return the result (Boolean)
     */
    public Boolean xor(Boolean otherNumber) {
        return this.xor(otherNumber, true);
    }
    
    /**
     * Calculates the logical negation of this value
     * 
     * @param returnNewNumber (boolean) see above
     * @return the result (Boolean)
     */
    public Boolean not(boolean returnNewNumber) {
        if(returnNewNumber)
            return new Boolean(!this.value, this.format);
        
        this.value = !this.value;
        return this;
    }
    
    /**
     * Calculates the logical negation of this value
     * and always returns a new object instance
     * 
     * @return the result (Boolean)
     */
    public Boolean not() {
        return this.not(true);
    }
    
    /*
    * All the normal functions/operations are not valid for boolean, so return
    * errors here!
    */

    @Override
    public Number add(Number otherNumber, boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sub(Number otherNumber, boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number mul(Number otherNumber, boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number div(Number otherNumber, boolean returnNewNumber) throws FiMaLibDivisionByZeroException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number pow(Number otherNumber, boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number abs(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sgn(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sqrt(boolean returnNewNumber) throws FiMaLibUndefinedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number exp(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number ln(boolean returnNewNumber) throws FiMaLibUndefinedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number log(boolean returnNewNumber) throws FiMaLibUndefinedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sin(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number cos(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number tan(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number cot(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sec(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number csc(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arcsin(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccos(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arctan(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccot(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arcsec(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccsc(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sinh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number cosh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number tanh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number coth(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number sech(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number csch(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arcsinh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccosh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arctanh(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccoth(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arcsech(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number arccsch(boolean returnNewNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
