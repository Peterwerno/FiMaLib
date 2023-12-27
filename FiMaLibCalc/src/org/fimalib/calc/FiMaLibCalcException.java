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

/**
 *
 * @author peter
 */
public class FiMaLibCalcException extends Exception {

    /**
     * Creates a new instance of <code>FiMaLibCalcException</code> without
     * detail message.
     */
    public FiMaLibCalcException() {
    }

    /**
     * Constructs an instance of <code>FiMaLibCalcException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FiMaLibCalcException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>FiMaLibCalcException</code> with the
     * specified detail message and a root cause exception
     * 
     * @param msg the detail message
     * @param parent the root cause
     */
    public FiMaLibCalcException(String msg, Exception parent) {
        super(msg, parent);
    }
}
