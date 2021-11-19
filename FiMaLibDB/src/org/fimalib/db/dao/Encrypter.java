/*
 * Copyright (C) 2020 peter.
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

import com.sun.media.jfxmedia.logging.Logger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class is used to securely encrypt a user's password
 * 
 * @author peter
 */
public class Encrypter {
    private static final SecureRandom RAND = new SecureRandom();
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    
    /**
     * Generate a salt string with given length
     * 
     * @param length (int) the length of the salt
     * @return the salt (String)
     */
    public static Optional<String> generateSalt (final int length) {
        if (length < 1) {
            Logger.logMsg(Logger.ERROR, "error in generateSalt: length must be > 0");
            return Optional.empty();
        }
        
        byte[] salt = new byte[length];
        RAND.nextBytes(salt);
        
        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }
    
    /**
     * Encrypt a password string using a salt string
     * 
     * @param password (String) the password
     * @param salt (String) the salt
     * @return the encrypted password (String)
     */
    public static Optional<String> hashPassword (String password, String salt) {
        String fullSalt = DBConn.getSalt();
        
        if(fullSalt == null) 
            fullSalt = salt;
        else 
            fullSalt += salt;
        
        char[] chars = password.toCharArray();
        byte[] bytes = fullSalt.getBytes();
        
        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
        
        Arrays.fill(chars, Character.MIN_VALUE);
        
        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.logMsg(Logger.ERROR, "Exception encountered in hashPassword()");
            return Optional.empty();
        }
        finally {
            spec.clearPassword();
        }
    }
    
    /**
     * Verify that a password is correct compared to an encrypted (stored)
     * password and a given salt string
     * 
     * @param password (String) the password to test
     * @param key (String) the encrypted password
     * @param salt (String) the salt string
     * @return whether the password given was correct (boolean)
     */
    public static boolean verifyPassword (String password, String key, String salt) {
        Optional<String> optEncrypted = hashPassword(password, salt);
        if (!optEncrypted.isPresent()) return false;
        return optEncrypted.get().equals(key);
    }
    
    public static void main(String[] args) {
    }
}
