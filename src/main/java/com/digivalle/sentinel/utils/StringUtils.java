/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Waldir.Valle
 */
public class StringUtils {

    public static String convertToISO(String s) {
        String out = null;
        try {
            if(s!=null){
                out = new String(s.getBytes("ISO-8859-1"),"UTF-8" );
            }
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    
    public static String convertToUTF8(String s) {
        String out = null;
        try {
            if(s!=null){
                out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    
    public static String getIdFormatted(String id){
        if(id!=null && !id.equals("null")){
            DecimalFormat df = new DecimalFormat("#");
            //return df.format(new Double(id));
            return df.format(Double.valueOf(id));
        }else{
            return null;
        }
    }
    
     public static String removeDecimals(String number){
        String str = "";
        if(number!=null && number.contains(".")){
            str = number.substring(0, number.indexOf("."));
        }
        return str;
    }
     
     public static String encodeFileToBase64(File originalFile){
        //File originalFile = new File(filePath);
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int)originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encodeBase64(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedBase64;
    }
     
    public static int countWordsUsingStringTokenizer(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(sentence);
        return tokens.countTokens();
    }


    public static String unaccent(String src) {
		return Normalizer
				.normalize(src, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}
    
    public static String removeSpecialCharacters(String src){
        src = src.replace(".", "");
        src = src.replace("ª", "");
        src = src.replace("º", "");
        src = src.replace("\\", "");
        src = src.replace("!", "");
        src = src.replace("|", "");
        src = src.replace("\"", "");
        src = src.replace("@", "");
        src = src.replace("·", "");
        src = src.replace("#", "");
        src = src.replace("$", "");
        src = src.replace("%", "");
        src = src.replace("&", "");
        src = src.replace("/", "");
        src = src.replace("(", "");
        src = src.replace(")", "");
        src = src.replace("=", "");
        src = src.replace("?", "");
        src = src.replace("'", "");
        src = src.replace("¿", "");
        src = src.replace("¡", "");
        src = src.replace("^", "");
        src = src.replace("[", "");
        src = src.replace("*", "");
        src = src.replace("+", "");
        src = src.replace("]", "");
        src = src.replace("¨", "");
        src = src.replace("{", "");
        src = src.replace("ç", "");
        src = src.replace("}", "");
        src = src.replace(";", "");
        src = src.replace(",", "");
        src = src.replace(":", "");
        src = src.replace(".", "");
        src = src.replace("-", "");
        src = src.replace("_", "");
        
        return src;
    }
    
    public static String removeAccentsAndSpecialCharacters(String src){
        String returned = removeSpecialCharacters(src);
        return unaccent(returned);
    }

    public static Integer getWoaIdFromRecordName(String recordName){
        if(recordName.contains("WOA-")){
            return Integer.parseInt(recordName.replace("WOA-", ""));
        } else {
            return Integer.parseInt(recordName.replace("ENC-U-", ""));
        }
        
    }
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException | NullPointerException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }
    
    public static int getRandomIntBetweenRange(int min, int max){
        int x = (int)((Math.random()*((max-min)+1))+min);
        return x;
    }
    
    public static char randomChar() {
        Random r = new Random();
        return (char)(r.nextInt(26) + 'A');
    }
        
    
}
