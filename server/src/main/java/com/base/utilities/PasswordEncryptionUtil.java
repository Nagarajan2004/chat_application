package com.base.utilities;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password encryption and verification using BCrypt.
 */
public class PasswordEncryptionUtil {

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainPassword the plain text password to be hashed
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainPassword the plain text password to be verified
     * @param hashPassword the hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashPassword){
        return BCrypt.checkpw(plainPassword, hashPassword);
    }
}