/*
 * Copyright (C) 2021 peter.
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
package org.fimalib.calc.cal.daycount;

/**
 * This exception class is used as the base exception within the daycount
 * calculation package.
 * 
 * @author peter
 */
public class DaycountException extends Exception {

    /**
     * Creates a new instance of <code>DaycountException</code> without detail
     * message.
     */
    public DaycountException() {
    }

    /**
     * Constructs an instance of <code>DaycountException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DaycountException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>DaycountExeption</code> with the
     * specified detail message and root cause.
     * 
     * @param msg the detail message.
     * @param root the root cause.
     */
    public DaycountException(String msg, Exception root) {
        super(msg, root);
    }
}
