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
package org.fimalib.db.dao;

import java.util.Base64;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit test tests the functionality of the class
 * 
 * org.fimalib.db.dao.Encrypter
 * 
 * @author peter
 */
public class EncrypterTest {
    
    /**
     * Test the generateSalt method which should generate a random salt string
     * of a requested length.
     */
    @Test
    public void testGenerateSalt() {
        String salt = Encrypter.generateSalt(512).get();
        assertTrue("Error with salt generation, salt length shorter than requested!", salt.length() >= 512);
        
        byte[] decoded = Base64.getDecoder().decode(salt);
        assertEquals("Error with salt generation, decoded length not equal to requested length!", decoded.length, 512);
    }
    
    /**
     * Test the verifyPassword method which checks if a given password
     * corresponds to a given combination of encrypted password and salt.
     */
    @Test
    public void testVerifyPassword() {
        String password="HalloWelt";
        String salt="abcdefg123456789dsfhjksdafghjööadskf12354632";
        String result = Encrypter.hashPassword(password, salt).get();
        assertTrue("Error with password check, password was not considered equal", Encrypter.verifyPassword(password, result, salt));
    }
}
