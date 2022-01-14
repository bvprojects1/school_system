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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BVMicroUtils {

    public static final String TRANSFER = "TRANSFER";
    public static final String CURRENT_LOAN_TRANSFER = "CURRENT_LOAN_TRANSFER";
    public static final String SAVING_SHARE_TRANSFER = "SAVING_SHARE_TRANSFER";
    public static final String DEBIT_DEBIT_TRANSFER = "DEBIT_DEBIT_TRANSFER";
    public static final String DEBIT_CURRENT_TRANSFER = "DEBIT_CURRENT_TRANSFER";

    public static final String CURRENT_DEBIT_TRANSFER = "CURRENT_DEBIT_TRANSFER";
    public static final String CURRENT_SHARE_TRANSFER =  "CURRENT_SHARE_TRANSFER";
    public static final String CURRENT_CURRENT_TRANSFER = "CURRENT_CURRENT_TRANSFER";

    public static final String SAVINGS_MINIMUM_BALANCE_ADDED_BY = "Savings minimum balance added by: ";
    public static final String SAVING_ACCOUNT_CREATED = "Saving account created ";

    public static final String CURRENT = "CURRENT";

    public static final String GENERAL_SAVINGS = "GENERAL_SAVINGS";
    public static final String RETIREMENT_SAVINGS = "RETIREMENT_SAVINGS";
    public static final String DAILY_SAVINGS = "DAILY_SAVINGS";
    public static final String MEDICAL_SAVINGS = "MEDICAL_SAVINGS";
    public static final String SOCIAL_SAVINGS = "SOCIAL_SAVINGS";
    public static final String BUSINESS_SAVINGS = "BUSINESS_SAVINGS";
    public static final String CHILDREN_SAVINGS = "CHILDREN_SAVINGS";
    public static final String REAL_ESTATE_SAVINGS = "REAL_ESTATE_SAVINGS";
    public static final String EDUCATION_SAVINGS = "EDUCATION_SAVINGS";

    public static final String CONSUMPTION_LOAN = "CONSUMPTION_LOAN";
    public static final String SHORT_TERM_LOAN = "SHORT_TERM_LOAN";
    public static final String AGRICULTURE_LOAN = "AGRICULTURE_LOAN";
    public static final String BUSINESS_INVESTMENT_LOAN = "BUSINESS_INVESTMENT_LOAN";
    public static final String SCHOOL_FEES_LOAN = "SCHOOL_FEES_LOAN";
    public static final String REAL_ESTATE_LOAN = "REAL_ESTATE_LOAN";
    public static final String OVERDRAFT_LOAN = "OVERDRAFT_LOAN";
    public static final String NJANGI_FINANCING = "NJANGI_FINANCING";
    public static final String NO_NAME = "NO_NAME";

    public static final String ORDINARY_SHARE = "ORDINARY_SHARE";

    public static final String CUSTOMER_IN_USE = "customerInUse";
    public static final String DATE_FORMATTER= "dd-MM-yyyy HH:mm";
    public static final String DATE_ONLY_FORMATTER= "dd-MM-yyyy";
    public static final String DATE_US_ONLY_FORMATTER= "yyyy-MM-dd";
//    public static final String SYSTEM = "SYSTEM";
    public static final String REGULAR_MONTHLY_PAYMENT_MISSING = "Regular Monthly payment not on schedule might be missing payment for some months. " +
            "Please check the account statement";
    public static final String PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String PENDING_PAYOUT = "PENDING_PAYOUT";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    public static final String LOAN_MUST_BE_IN_ACTIVE_STATE = "LOAN MUST BE IN 'ACTIVE' STATE";

    public static final String CREDIT = "CREDIT";
    public static final String DEBIT = "DEBIT";
    public static final String EXPENSES = "EXPENSES";
    public static final String LIABILITIES = "LIABILITIES";
    public static final String REVENUE = "REVENUE";
    public static final String ASSETS = "ASSETS";
    public static final String CASH = "CASH";
    public static final String SAVINGS = "SAVINGS";
    public static final String LOAN = "LOAN";
    public static final String VAT = "VAT";
    public static final String LOAN_INTEREST = "LOAN_INTEREST";

//    public static final String CASH_GL_1001 = "CASH GL 1001";
    public static final String CASH_GL_5001 = "CASH_GL_5001";
    public static final String SHARE_GL_XXXX = "SHARE_GL_XXXX";

    public static final String CURRENT_GL_3004 = "CURRENT_GL_3004";
    public static final String SHARE_GL_5004 = "SHARE_GL_5004";

    public static final String GL_4002 = "GL_4002";
    public static final String LOAN_INTEREST_GL_7001 = "LOAN_INTEREST_GL_7001";
    public static final String LOAN_GL_3001 = "LOAN_GL_3001";
    public static final String SAVINGS_3004 = "SAVINGS_3004";
    public static final String SAVINGS_GL_3003 = "SAVINGS_GL_3003";
    public static final String RETIREMENT_SAVINGS_GL_3005 = "RETIREMENT_SAVINGS_GL_3005";
    public static final String DAILY_SAVINGS_GL_3006 = "DAILY_SAVINGS_GL_3006";

    public static final String EQUITY = "EQUITY";
    public static final String UNIT_SHARE_VALUE = "unitSharePrice";
    public static final String SHARE = "SHARE";
    public static final String GL_3001 = "GL_3001";
    public static final String NO_NAME_0004 = "NO_NAME_0004";
    public static final String INIT_SYSTEM = "INIT_SYSTEM";

    public static final String GL_3004 = "GL_3004";
    public static final String GL_3005 = "GL_3005";
    public static final String GL_3006 = "GL_3006";
    public static final String GL_3007 = "GL_3007";
    public static final String GL_3008 = "GL_3008";
    public static final String GL_3009 = "GL_3009";

    public static final String GL_3010 = "GL_3010";
    public static final String GL_3011 = "GL_3011";
    public static final String GL_3012 = "GL_3012";
    public static final String GL_3013 = "GL_3013";
    public static final String GL_3014 = "GL_3014";
    public static final String GL_3015 = "GL_3015";

    public static final String GL_3016 = "GL_3016";
    public static final String GL_3017 = "GL_3017";
    public static final String GL_3018 = "GL_3018";
    public static final String GL_3019 = "GL_3019";
    public static final String GL_3020 = "GL_3020";
    public static final String GL_3021 = "GL_3021";
    public static final String GL_3022 = "GL_3022";
    public static final String GL_3023 = "GL_3023";
    public static final String GL_5004 = "GL_5004";
    public static final String GL_3003 = "GL_3003";
    public static final String GL_5001 = "GL_5001";
    public static final String GL_7001 = "GL_7001";
    public static final String GL_TRANSFER = "GL_TRANSFER";


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

    public  static String formatUSDateOnly(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_US_ONLY_FORMATTER);
        String formatDateTime = localDate.format(formatter);
        return formatDateTime;
    }

    public static String formatCurrency(double totalSaved) {
        if(totalSaved==0){
            return "";
        }
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

    public static Date formatDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parse;
    }

    public static LocalDateTime formatLocaleDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDateTime localDateTime = convertToLocalDateTimeViaMilisecond(parse);
        return localDateTime;
    }


    public static String getCobacSavingsAccountNumber(String country, String productCode, String branch, long count) {
        count = count + 1000000001;
        String accountNumber = count + "";
        accountNumber = accountNumber.replaceFirst("1", "");
        accountNumber = country + productCode + accountNumber + branch;
        return accountNumber;
    }


    public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertToDate(LocalDateTime dateToConvert) {
        LocalDateTime localDateTime = LocalDateTime.of(
                dateToConvert.getYear(),
                dateToConvert.getMonth(),
                dateToConvert.getDayOfMonth(),
                dateToConvert.getHour(),
                dateToConvert.getMinute(),
                dateToConvert.getSecond());
        Date aDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return aDate;

    }




    public static String getOppositeCreditOrDebit(String creditOrDebit) {
        if(creditOrDebit.equals(BVMicroUtils.DEBIT)){
            return BVMicroUtils.CREDIT;
        }
        return BVMicroUtils.DEBIT;
    }

//    @ResponseBody
//    public static byte[] getLogoImage(Path path) throws IOException {
////        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
////        Path path = Paths.get(logoPath);
//        byte[] data = Files.readAllBytes(path);
//        return data;
//    }
}
