package com.base.utilities;

import java.util.Random;

/**
 * This class provides a method to generate a one-time password (OTP).
 */
public class GenerateOTP {
    // Random instance for generating OTP
    private static Random random;

    // Private constructor to prevent instantiation
    private GenerateOTP(){}

    /**
     * Returns a randomly generated OTP as a string.
     * If the Random instance is not already created, it initializes the instance.
     *
     * @return A string representing the generated OTP
     */
    public static String getOTP(){
        if(random == null) random = new Random();
        return String.valueOf(random.nextInt(999999));
    }
}