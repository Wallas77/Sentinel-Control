/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digivalle.sentinel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Wallas
 */
public class ObjectUtils {
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(destDirectory + File.separator + fileName);

            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                FileOutputStream fos = new FileOutputStream(newFile);
                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
            }
            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();
    }
    
    public static void moveFile(String sourceFilePath, String destinationFolderPath) throws IOException {
        Path sourcePath = Paths.get(sourceFilePath);
        Path destinationPath = Paths.get(destinationFolderPath, sourcePath.getFileName().toString());

        // Move the file
        Files.move(sourcePath, destinationPath);

        // Alternatively, if you want to replace an existing file
        // Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static boolean haveDifferentAttributes(Object obj1, Object obj2) {
        Class<?> class1 = obj1.getClass();
        Class<?> class2 = obj2.getClass();

        if (!class1.equals(class2)) {
            return true; // Objects are of different types
        }

        Field[] fields = class1.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);

                if (value1 == null && value2 == null) {
                    continue; // Both values are null
                }

                if (value1 == null || value2 == null || !value1.equals(value2)) {
                    return true; // Found a differing attribute
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return true; // Error accessing field
            }
        }

        return false; // No differing attributes found
    }
    
    public static String diferenceBetweenDates(Date date1, Date date2){
        
        // Calculate the difference in milliseconds between the two dates
        long timeDiff = Math.abs(date1.getTime() - date2.getTime());

        // Convert milliseconds to days, hours, minutes, and seconds
        long daysDiff = timeDiff / (1000 * 60 * 60 * 24);
        long hoursDiff = (timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutesDiff = (timeDiff % (1000 * 60 * 60)) / (1000 * 60);
        long secondsDiff = (timeDiff % (1000 * 60)) / 1000;

        // Print the difference between the two dates
        //System.out.println("Difference between the two dates:");
        //System.out.println(daysDiff + " days, " + hoursDiff + " hours, " + minutesDiff + " minutes, " + secondsDiff + " seconds");
        return daysDiff + " days, " + hoursDiff + " hours, " + minutesDiff + " minutes, " + secondsDiff + " seconds";

    }
    
    public static boolean areDatesDifferent(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) ||
               cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH) ||
               cal1.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH);
    }


    
}
