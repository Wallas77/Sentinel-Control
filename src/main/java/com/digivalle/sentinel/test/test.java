/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.digivalle.sentinel.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Wallas
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
       /* String input = "ea599ab2d2d1dc07f116e341c39bf32e"; // Example input string
        input = input.concat("beb9ac49ee");*/
        String input = "752bc79840abd0294161eb2f3d2fac76"; // Example input string
        input = input.concat("075457e7ef");
        input = input.concat(String.valueOf(System.currentTimeMillis()/1000));
       
        System.out.println("input=>"+input);

        try {
            // Step 2: Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Step 3: Compute the hash of the input data
            byte[] hashBytes = digest.digest(input.getBytes());

            //System.out.println("SHA-256 Hash (byte array): " + Arrays.toString(hashBytes));
            // Step 4: Convert the hash byte array to a hexadecimal string
            String hashHex = bytesToHex(hashBytes);

            System.out.println("SHA-256 Hash in Hex: " + hashHex);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        
        String sha256hex = DigestUtils.sha256Hex(input);
        System.out.println("sha256hex=>"+sha256hex);
    }
    
    // Helper method to convert a byte array to a hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
}
