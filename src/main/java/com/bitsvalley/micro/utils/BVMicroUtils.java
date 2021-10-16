package com.bitsvalley.micro.utils;

import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BVMicroUtils {

    public static final String TRANSFER = "TRANSFER";
    public static final String DEBIT_LOAN_TRANSFER = "DEBIT_LOAN_TRANSFER";
    public static final String DEBIT_DEBIT_TRANSFER = "DEBIT_DEBIT_TRANSFER";

    public static final String SAVINGS_MINIMUM_BALANCE_ADDED_BY = "Savings minimum balance added by: ";
    public static final String SAVING_ACCOUNT_CREATED = "Saving account created ";

    public static final String CUSTOMER_IN_USE = "customerInUse";
    public static final String DATE_FORMATTER= "dd-MM-yyyy HH:mm";
    public static final String DATE_ONLY_FORMATTER= "dd-MM-yyyy";
    public static final String SYSTEM = "SYSTEM";
    public static final String REGULAR_MONTHLY_PAYMENT_MISSING = "Regular Monthly payment not on schedule might be missing payment for some months. " +
            "Please check the account statement";

    public static  String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; //TODO: avoid collision
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

    public  static String formatDateOnly(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_ONLY_FORMATTER);
        String formatDateTime = localDate.format(formatter);
        return formatDateTime;
    }

    public static String formatCurrency(double totalSaved) {
        Locale locale = new Locale("en", "CM");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        String total = fmt.format(totalSaved);
        total = total.substring(3,total.length());
        total = total.replaceFirst("F","-");
        return total;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm");
        String format = formatter.format(date);
        return format;
    }


    public static String getCobacSavingsAccountNumber(String country, String productCode, String branch, long count) {
        count = count + 1000000001;
        String accountNumber = count + "";
        accountNumber = accountNumber.replaceFirst("1", "");
        accountNumber = country + productCode + accountNumber + branch;
        return accountNumber;
    }



//    @ResponseBody
//    public static byte[] getLogoImage(Path path) throws IOException {
////        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
////        Path path = Paths.get(logoPath);
//        byte[] data = Files.readAllBytes(path);
//        return data;
//    }
}
