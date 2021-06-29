package com.bitsvalley.micro.utils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class BVMicroUtils {


    public static final String CUSTOMER_IN_USE = "customerInUse";
    public static final String DATE_FORMATTER= "dd-MM-yyyy HH:mm:ss";

    public static  String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 9) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

//    public static String dateFormatter(LocalDateTime localDateTime){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String formattedDateTime = localDateTime.format(formatter);
//        return formattedDateTime;
//    }


//    public static void givenUsingApache_whenGeneratingRandomStringBounded_thenCorrect() {
//
//        int length = 10;
//        boolean useLetters = true;
//        boolean useNumbers = false;
//        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
//
//        System.out.println(generatedString);
//    }

    //    public static String RandomStringUnbounded_thenCorrect() {
//        byte[] array = new byte[8]; // length is bounded by 8
//        new Random().nextBytes(array);
//        String generatedString = new String(array, Charset.forName("UTF-8"));
//        return generatedString;
//    }

    public  static String formatDateTime(LocalDateTime localDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
            String formatDateTime = localDateTime.format(formatter);
            return formatDateTime;

        }

}
