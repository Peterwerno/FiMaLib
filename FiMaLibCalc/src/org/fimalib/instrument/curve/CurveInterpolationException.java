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
package org.fimalib.instrument.curve;

/**
 * This Exception is thrown by the curve interpolators if there was a mistake
 * 
 * @author Peter Werno
 */
public class CurveInterpolationException extends Exception {

    /**
     * Creates a new instance of <code>CurveInterpolationException</code>
     * without detail message.
     */
    public CurveInterpolationException() {
    }

    /**
     * Constructs an instance of <code>CurveInterpolationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CurveInterpolationException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>CurveInterpolationException</code> with
     * the specified detail message and a root cause exception.
     * 
     * @param msg (String) the detail message
     * @param ex (Exception) the root cause/parent exception
     */
    public CurveInterpolationException(String msg, Exception ex) {
        super(msg, ex);
    }
}
