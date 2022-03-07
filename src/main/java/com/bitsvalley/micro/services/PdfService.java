package com.bitsvalley.micro.services;


import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.BranchRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.Amortization;
import com.bitsvalley.micro.utils.AmortizationRowEntry;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class PdfService {

//    private SavingAccountService savingAccountService;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    UserRepository userRepository;

    public String generateSavingTransactionReceiptPDF(SavingAccountTransaction savingAccountTransaction, RuntimeSetting rt) {
        Double showAmount = 0.0;
        String representativeText = StringUtils.equals(savingAccountTransaction.getRepresentative(),BVMicroUtils.getFullName(savingAccountTransaction.getSavingAccount().getUser()))?"":"Customer Representative: "+savingAccountTransaction.getRepresentative();
        User aUser = userRepository.findByUserName(savingAccountTransaction.getCreatedBy());
        if (savingAccountTransaction.getAccountOwner() != null && StringUtils.equals("true", savingAccountTransaction.getAccountOwner())) {
            showAmount = savingAccountTransaction.getSavingAccount().getAccountBalance();
        }
        String currentBilanzNoInterest = "<font style=\"font-size:1.4em;color:black;\">" +
                "<b>RECEIPT FOR SAVING ACCOUNT TRANSACTION</b></font>" +
                "<table border=\"1\" width=\"100%\">" +
                "<tr> <td><table><tr><td>" +
                "<img width=\"75\" src=\"file:/"+rt.getUnionLogo()+"\"/><br/> Reference No:<br/>"+ savingAccountTransaction.getReference() +
                "</td><td><b><font style=\"font-size:1.6em;color:black;\"> "+ rt.getBusinessName() +"</font></b><br/>" + rt.getAddress()+"<br/>" +rt.getTelephone() +"<br/>" +rt.getEmail() +"<br/>" +
                "</td></tr></table></td>" +
                "<td>"+
                " Branch No: "+savingAccountTransaction.getBranchCode()+
                "<br/>"+savingAccountTransaction.getModeOfPayment()+":" + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) +"<br/> "+ representativeText +"<br/>Date:" + BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate()) + "</td></tr>" +
                "<tr><td>" +
                "Account Number:<b>" + BVMicroUtils.getFormatAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber())

                + "</b><br/>Customer: <b>"+ BVMicroUtils.getFullName(savingAccountTransaction.getSavingAccount().getUser())+

                "</b> </td>" +
                "<td>Account Balance: <b>" + BVMicroUtils.formatCurrency(showAmount) + "</b><br/> Current Amount: <font style=\"font-size:1.6em;color:black;\">"
                + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) + "</font></td></tr>" +
                "        <tr><td colspan=\"2\">" +
                "Agent Representative: <b>" + savingAccountTransaction.getCreatedBy() + " - </b>"+ BVMicroUtils.getFullName(aUser) +"<br/>Notes:"+savingAccountTransaction.getNotes()+"</td>\n" +
                "    </tr></table>" +
                "    <table  border=\"1\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th><font style=\"font-size:1.2em;color:black;\">Bill Selection - Cash Breakdown</font><font style=\"font-size:1.6em;color:black;\"> " + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) + "frs CFA</font></th>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "               <td> 10 000 x " + savingAccountTransaction.getTenThousand() + " = <b>" + 10000 * savingAccountTransaction.getTenThousand() + "</b>," +
                "               5 000 x " + savingAccountTransaction.getFiveThousand() + " = <b>" + 5000 * savingAccountTransaction.getFiveThousand() + "</b>," +
                "               2 000 x " + savingAccountTransaction.getTwoThousand() + " = <b>" + 2000 * savingAccountTransaction.getTwoThousand() + "</b>," +
                "               1 000 x " + savingAccountTransaction.getOneThousand() + " = <b>" + 1000 * savingAccountTransaction.getOneThousand() + "</b>" +
                "               500 x " + savingAccountTransaction.getFiveHundred() + " = <b>" + 500 * savingAccountTransaction.getFiveHundred() + "</b>," +
                "               100 x " + savingAccountTransaction.getOneHundred() + " = <b>" + 100 * savingAccountTransaction.getOneHundred() + "</b>," +
                "               50 x " + savingAccountTransaction.getFifty() + " = <b>" + 50 * savingAccountTransaction.getFifty() + "</b>," +
                "               25 x " + savingAccountTransaction.getTwentyFive() + " = <b>" + 25 * savingAccountTransaction.getTwentyFive() + "</b>" +
                "               Amount in Letters: <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"> "
                +               savingAccountTransaction.getSavingAmountInLetters() + "</font> </td></tr>" +
                "        </table>" +
                "       <table><tr><td><br/><br/>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/> "+branchRepository.findByCode(savingAccountTransaction.getBranchCode()).getName() +"</td>" +
                "</tr></table><br/>";
        currentBilanzNoInterest = "<html><head></head><body>" + currentBilanzNoInterest + currentBilanzNoInterest+"</body></html>";
        return currentBilanzNoInterest;
    }


//    public String generateTransactionReceiptPDF(SavingAccountTransaction savingAccountTransaction, RuntimeSetting rt) {
//        Double showAmount = 0.0;
//        User aUser = userRepository.findByUserName(savingAccountTransaction.getCreatedBy());
//
//        if( savingAccountTransaction.getAccountOwner() != null && StringUtils.equals("true",savingAccountTransaction.getAccountOwner())){
//            showAmount = savingAccountTransaction.getSavingAccount().getAccountBalance();
//        }
//        String savingBilanzNoInterest =
//                "<br/><br/><font style=\"font-size:1.4em;color:black;\">" +
//                "<b>RECEIPT FOR SAVING ACCOUNT TRANSACTION</b></font>" +
//                "<table border=\"1\" width=\"100%\">" +
//                "<tr> <td><table><tr><td>" +
//                "<img width=\"75\" src=\"file:/"+rt.getUnionLogo()+"\"/><br/> Reference No:"+ savingAccountTransaction.getReference() +
//                "</td><td><b><font style=\"font-size:1.6em;color:black;\"> "+ rt.getBusinessName() +"</font></b><br/><br/>" + rt.getAddress()+"<br/>" +rt.getTelephone() +"<br/>" +rt.getEmail() +"<br/>" +
//                "</td></tr></table></td>" +
//                "<td>"+
//                " Branch No: "+savingAccountTransaction.getBranchCode()+
//                "<br/>"+savingAccountTransaction.getModeOfPayment()+" Account Owner:" + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) + "<br/>Date:" + BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate()) + "</td></tr>" +
//                "<tr><td>" +
//                "Account Number: "+ savingAccountTransaction.getSavingAccount().getAccountNumber()
//                +"<br/>Customer: <b>"+savingAccountTransaction.getSavingAccount().getUser().getGender()+" "+savingAccountTransaction.getSavingAccount().getUser().getFirstName()+ " "+savingAccountTransaction.getSavingAccount().getUser().getLastName()
//                +"</b> </td>" +
//                "<td>Account Balance: <b>" + BVMicroUtils.formatCurrency(showAmount) +"</b><br/> Saving Amount:<font style=\"font-size:1.6em;color:black;\">"
//                + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) + "</font></td></tr>" +
//                "        <tr><td colspan=\"2\">" +
//                "Representative: <b>"+ savingAccountTransaction.getCreatedBy()+"</b> - <br/> "+ BVMicroUtils.getFullName(aUser) +"<br/> Amount in Letters: <font color=\""+rt.getThemeColor()+"\" size=\"8px\"> "
//                +savingAccountTransaction.getSavingAmountInLetters()+"</font><br/>Notes:"+savingAccountTransaction.getNotes()+"</td>\n" +
//                "    </tr></table>" +
//                "    <table  border=\"1\" width=\"100%\" class=\"center\">\n" +
//                "            <tr>\n" +
//                "                <th><font style=\"font-size:1.2em;color:black;\">Bill Selection - Cash Breakdown</font><font style=\"font-size:1.6em;color:black;\"> "+BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()) +"frs CFA</font></th>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "               <td> 10 000 x "+savingAccountTransaction.getTenThousand()+" = <b>" + 10000 * savingAccountTransaction.getTenThousand()+"</b>,"+
//                "               5 000 x "+savingAccountTransaction.getFiveThousand()+" = <b>" + 5000 * savingAccountTransaction.getFiveThousand()+"</b>,"+
//                "               2 000 x "+savingAccountTransaction.getTwoThousand()+" = <b>" + 2000 * savingAccountTransaction.getTwoThousand()+"</b>,"+
//                "               1 000 x "+savingAccountTransaction.getOneThousand()+" = <b>" +  1000 * savingAccountTransaction.getOneThousand()+"</b>"+
//                "               500 x "+savingAccountTransaction.getFiveHundred()+" = <b>" + 500 * savingAccountTransaction.getFiveHundred() +"</b>,"+
//                "               100 x "+savingAccountTransaction.getOneHundred()+" = <b>" +100 * savingAccountTransaction.getOneHundred() +"</b>,"+
//                "               50 x "+savingAccountTransaction.getFifty()+" = <b>" + 50 * savingAccountTransaction.getFifty() +"</b>,"+
//                "               25 x "+savingAccountTransaction.getTwentyFive()+" = <b>" + 25 * savingAccountTransaction.getTwentyFive() +"</b><br/>"+
//                "                </td></tr>" +
//                        "</table><br/> " +
//                "       <table><tr><td>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/> "+branchRepository.findByCode(savingAccountTransaction.getBranchCode()).getName() +"</td>" +
//                "</tr></table>";
//        savingBilanzNoInterest = "<html><body>"+savingBilanzNoInterest + savingBilanzNoInterest+"</body></html>";
//        return savingBilanzNoInterest;
//
//    }

    public String generateShareDetailsPDF(ShareAccountTransaction shareAccountTransaction, RuntimeSetting rt) {
        User aUser = userRepository.findByUserName(shareAccountTransaction.getCreatedBy());
        String currentBilanzNoInterest = "<html><head>" +
                "</head><body><br/><br/><font style=\"font-size:1.4em;color:black;\">" +
                "<b>RECEIPT FOR SHARE ACCOUNT TRANSACTION</b></font>" +
                "<table border=\"1\" width=\"100%\">" +
                "<tr> <td><img width=\"75\" src=\"file:/"+rt.getUnionLogo() + "\"/><br/> Reference No:" + shareAccountTransaction.getReference() +
                "<br/>Date:<b>" + BVMicroUtils.formatDateTime(shareAccountTransaction.getCreatedDate()) + "</b> </td>" +
                "<td>" +
                "<b><font style=\"font-size:1.6em;color:black;\"> " + rt.getBusinessName() + "</font></b><br/> Branch No: " + shareAccountTransaction.getBranchCode() +
                "<br/>Address:" + rt.getAddress() + "<br/> Telephone:" + rt.getTelephone() +
                "<br/>" + shareAccountTransaction.getModeOfPayment() + " Current Account" + BVMicroUtils.formatCurrency(shareAccountTransaction.getShareAmount()) + "</td></tr>" +
                "<tr><td>" +
                "Account Number: " + shareAccountTransaction.getShareAccount().getAccountNumber()
                + "<br/>Customer: <b>" + shareAccountTransaction.getShareAccount().getUser().getLastName() + ","
                + shareAccountTransaction.getShareAccount().getUser().getFirstName() + "</b> </td>" +
                "<td>Total Share Balance: <b>" + BVMicroUtils.formatCurrency(shareAccountTransaction.getShareAccount().getAccountBalance()) + "</b><br/> Current Amount:<font style=\"font-size:1.6em;color:black;\">"
                + BVMicroUtils.formatCurrency(shareAccountTransaction.getShareAmount()) + "</font></td></tr>" +
                "        <tr><td>" +
                "Representative: <b>" + shareAccountTransaction.getCreatedBy() + "</b> - "+ BVMicroUtils.getFullName(aUser) +"<br/> </td><td> <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"> "+
                "</font><br/>Notes:"+shareAccountTransaction.getNotes()+"</td>\n" +
                "    </tr></table>" +
                "    <table  border=\"1\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th><font style=\"font-size:1.2em;color:black;\">Bill Selection - Cash Breakdown</font><font style=\"font-size:1.6em;color:black;\"> " + BVMicroUtils.formatCurrency(shareAccountTransaction.getShareAmount()) + "frs CFA</font></th>\n" +
                "            </tr>\n" +
                "        </table>" +
                "<br/> " +
                "       <table>" +
                "       <tr><td>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/>  Bamenda Branch, N W Region,</td>" +
                "       <td></td></tr>" +
                "       </table>" +
                "<br/><br/>" +
                "" +
                "</body></html>";
        return currentBilanzNoInterest;
    }

    public String generateCurrentTransactionReceiptPDF(CurrentAccountTransaction currentAccountTransaction, RuntimeSetting rt) {
        Double showAmount = 0.0;
        String representativeText = StringUtils.equals(currentAccountTransaction.getRepresentative(),BVMicroUtils.getFullName(currentAccountTransaction.getCurrentAccount().getUser()))?"":"Customer Representative: "+currentAccountTransaction.getRepresentative();
        User aUser = userRepository.findByUserName(currentAccountTransaction.getCreatedBy());
        if (currentAccountTransaction.getAccountOwner() != null && StringUtils.equals("true", currentAccountTransaction.getAccountOwner())) {
            showAmount = currentAccountTransaction.getCurrentAccount().getAccountBalance();
        }
        String currentBilanzNoInterest = "<font style=\"font-size:1.4em;color:black;\">" +
                "<b>RECEIPT FOR CURRENT ACCOUNT TRANSACTION</b></font>" +
                "<table border=\"1\" width=\"100%\">" +
                "<tr> <td><table><tr><td>" +
                "<img width=\"75\" src=\"file:/"+rt.getUnionLogo()+"\"/><br/> Reference No:<br/>"+ currentAccountTransaction.getReference() +
                "</td><td><b><font style=\"font-size:1.6em;color:black;\"> "+ rt.getBusinessName() +"</font></b><br/>" + rt.getAddress()+"<br/>" +rt.getTelephone() +"<br/>" +rt.getEmail() +"<br/>" +
                "</td></tr></table></td>" +
                "<td>"+
                " Branch No: "+currentAccountTransaction.getBranchCode()+
                "<br/>"+currentAccountTransaction.getModeOfPayment()+":" + BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()) +"<br/> "+ representativeText +"<br/>Date:" + BVMicroUtils.formatDateTime(currentAccountTransaction.getCreatedDate()) + "</td></tr>" +
                "<tr><td>" +
                "Account Number:<b>" + BVMicroUtils.getFormatAccountNumber(currentAccountTransaction.getCurrentAccount().getAccountNumber())

                + "</b><br/>Customer: <b>"+ BVMicroUtils.getFullName(currentAccountTransaction.getCurrentAccount().getUser())+

                "</b> </td>" +
                "<td>Account Balance: <b>" + BVMicroUtils.formatCurrency(showAmount) + "</b><br/> Current Amount: <font style=\"font-size:1.6em;color:black;\">"
                + BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()) + "</font></td></tr>" +
                "        <tr><td colspan=\"2\">" +
                "Agent Representative: <b>" + currentAccountTransaction.getCreatedBy() + " - </b>"+ BVMicroUtils.getFullName(aUser) +"<br/>Notes:"+currentAccountTransaction.getNotes()+"</td>\n" +
                "    </tr></table>" +
                "    <table  border=\"1\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th><font style=\"font-size:1.2em;color:black;\">Bill Selection - Cash Breakdown</font><font style=\"font-size:1.6em;color:black;\"> " + BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()) + "frs CFA</font></th>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "               <td> 10 000 x " + currentAccountTransaction.getTenThousand() + " = <b>" + 10000 * currentAccountTransaction.getTenThousand() + "</b>," +
                "               5 000 x " + currentAccountTransaction.getFiveThousand() + " = <b>" + 5000 * currentAccountTransaction.getFiveThousand() + "</b>," +
                "               2 000 x " + currentAccountTransaction.getTwoThousand() + " = <b>" + 2000 * currentAccountTransaction.getTwoThousand() + "</b>," +
                "               1 000 x " + currentAccountTransaction.getOneThousand() + " = <b>" + 1000 * currentAccountTransaction.getOneThousand() + "</b>" +
                "               500 x " + currentAccountTransaction.getFiveHundred() + " = <b>" + 500 * currentAccountTransaction.getFiveHundred() + "</b>," +
                "               100 x " + currentAccountTransaction.getOneHundred() + " = <b>" + 100 * currentAccountTransaction.getOneHundred() + "</b>," +
                "               50 x " + currentAccountTransaction.getFifty() + " = <b>" + 50 * currentAccountTransaction.getFifty() + "</b>," +
                "               25 x " + currentAccountTransaction.getTwentyFive() + " = <b>" + 25 * currentAccountTransaction.getTwentyFive() + "</b>" +
                "               Amount in Letters: <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"> "
                +               currentAccountTransaction.getCurrentAmountInLetters() + "</font> </td></tr>" +
                "        </table>" +
                "       <table><tr><td><br/><br/>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/> "+branchRepository.findByCode(currentAccountTransaction.getBranchCode()).getName() +"</td>" +
                "</tr></table><br/>";
                currentBilanzNoInterest = "<html><head></head><body>" + currentBilanzNoInterest + currentBilanzNoInterest+"</body></html>";
        return currentBilanzNoInterest;
    }

    public String generateLoanTransactionReceiptPDF(LoanAccountTransaction loanAccountTransaction, RuntimeSetting rt) {

    Double showAmount = 0.0;
    String representativeText = StringUtils.equals(loanAccountTransaction.getRepresentative(),BVMicroUtils.getFullName(loanAccountTransaction.getLoanAccount().getUser()))?"":"Customer Representative: "+loanAccountTransaction.getRepresentative();
    User aUser = userRepository.findByUserName(loanAccountTransaction.getCreatedBy());
        if (loanAccountTransaction.getAccountOwner() != null && StringUtils.equals("true", loanAccountTransaction.getAccountOwner())) {
        showAmount = loanAccountTransaction.getLoanAccount().getCurrentLoanAmount();
    }
    String loanBilanzNoInterest = "<font style=\"font-size:1.4em;color:black;\">" +
            "<b>RECEIPT FOR LOAN ACCOUNT TRANSACTION</b></font>" +
            "<table border=\"1\" width=\"100%\">" +
            "<tr> <td><table><tr><td>" +
            "<img width=\"75\" src=\"file:/"+rt.getUnionLogo()+"\"/><br/> Reference No:<br/>"+ loanAccountTransaction.getReference() +
            "</td><td><b><font style=\"font-size:1.6em;color:black;\"> "+ rt.getBusinessName() +"</font></b><br/>" + rt.getAddress()+"<br/>" +rt.getTelephone() +"<br/>" +rt.getEmail() +"<br/>" +
            "</td></tr></table></td>" +
            "<td>"+
            " Branch No: "+loanAccountTransaction.getBranchCode()+
            "<br/>"+loanAccountTransaction.getModeOfPayment()+":" + BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) +"<br/> "+ representativeText +"<br/>Date:" + BVMicroUtils.formatDateTime(loanAccountTransaction.getCreatedDate()) + "</td></tr>" +
            "<tr><td>" +
            "Account Number:<b>" + BVMicroUtils.getFormatAccountNumber(loanAccountTransaction.getLoanAccount().getAccountNumber())

            + "</b><br/>Customer: <b>"+ BVMicroUtils.getFullName(loanAccountTransaction.getLoanAccount().getUser())+

            "</b> </td>" +
            "<td>Loan Balance: <b>" + BVMicroUtils.formatCurrency(showAmount) + "</b><br/> Repayment Amount: <font style=\"font-size:1.6em;color:black;\">"
            + BVMicroUtils.formatCurrency(loanAccountTransaction.getAmountReceived()) + "</font></td></tr>" +
            "        <tr><td colspan=\"2\">" +
            "Agent Representative: <b>" + loanAccountTransaction.getCreatedBy() + " - </b>"+ BVMicroUtils.getFullName(aUser) +"<br/>Notes:"+loanAccountTransaction.getNotes()+"</td>\n" +
            "    </tr></table>" +
            "    <table  border=\"1\" width=\"100%\" class=\"center\">\n" +
            "            <tr>\n" +
            "                <th><font style=\"font-size:1.2em;color:black;\">Bill Selection - Cash Breakdown</font><font style=\"font-size:1.6em;color:black;\"> " + BVMicroUtils.formatCurrency(loanAccountTransaction.getAmountReceived()) + "frs CFA</font></th>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "               <td> 10 000 x " + loanAccountTransaction.getTenThousand() + " = <b>" + 10000 * loanAccountTransaction.getTenThousand() + "</b>," +
            "               5 000 x " + loanAccountTransaction.getFiveThousand() + " = <b>" + 5000 * loanAccountTransaction.getFiveThousand() + "</b>," +
            "               2 000 x " + loanAccountTransaction.getTwoThousand() + " = <b>" + 2000 * loanAccountTransaction.getTwoThousand() + "</b>," +
            "               1 000 x " + loanAccountTransaction.getOneThousand() + " = <b>" + 1000 * loanAccountTransaction.getOneThousand() + "</b>" +
            "               500 x " + loanAccountTransaction.getFiveHundred() + " = <b>" + 500 * loanAccountTransaction.getFiveHundred() + "</b>," +
            "               100 x " + loanAccountTransaction.getOneHundred() + " = <b>" + 100 * loanAccountTransaction.getOneHundred() + "</b>," +
            "               50 x " + loanAccountTransaction.getFifty() + " = <b>" + 50 * loanAccountTransaction.getFifty() + "</b>," +
            "               25 x " + loanAccountTransaction.getTwentyFive() + " = <b>" + 25 * loanAccountTransaction.getTwentyFive() + "</b>" +
            "               Amount in Letters: <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"> "
            +               loanAccountTransaction.getLoanAmountInLetters() + "</font> </td></tr>" +
            "        </table>" +
            "       <table><tr><td><br/><br/>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/> "+branchRepository.findByCode(loanAccountTransaction.getBranchCode()).getName() +"</td>" +
            "</tr></table><br/>";
    loanBilanzNoInterest = "<html><head></head><body>" + loanBilanzNoInterest + loanBilanzNoInterest+"</body></html>";
        return loanBilanzNoInterest;
}

//    public String generateLoanTransactionReceiptPDF_old(LoanAccountTransaction loanAccountTransaction, RuntimeSetting rt) {
//        User aUser = userRepository.findByUserName(loanAccountTransaction.getCreatedBy());
//        String representativeText = StringUtils.equals(loanAccountTransaction.getRepresentative(),BVMicroUtils.getFullName(loanAccountTransaction.getLoanAccount().getUser()))?"":"Customer Representative: "+loanAccountTransaction.getRepresentative();
//
//        String loanBilanzNoInterest = "<html><head>" +
//                "</head><body><br/><br/>\"<font style=\"font-size:1.4em;color:black;\">\" +<b>RECEIPT FOR LOAN PAYMENT MADE</b></font>" +
//                "<table border=\"1\" width=\"100%\">" +
//                "<tr> <td> Form N. 120000029    </td>" +
//                "<td colspan=\"3\"><img width=\"125\" src=\"file:/"+rt.getUnionLogo() + "\"/><br/><b>" + rt.getBusinessName() + "</b><br/> BranchName <br/>" + rt.getAddress() + " " + rt.getTelephone() + "</td>" +
//                "<td>" + loanAccountTransaction.getModeOfPayment() +":" + BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) +"<br/> "+ representativeText +"<br/>Date:" + BVMicroUtils.formatDateTime(loanAccountTransaction.getCreatedDate()) + "</td></tr>" +
//                "        <tr><td colspan=\"3\">" +
//                "Account Number: " + loanAccountTransaction.getLoanAccount().getAccountNumber() + "<br/>Customer: <b>" + loanAccountTransaction.getLoanAccount().getUser().getLastName() + "," + loanAccountTransaction.getLoanAccount().getUser().getFirstName() + "</b> </td>" +
//                "<td>Date:<br/><b>" + BVMicroUtils.formatDateTime(loanAccountTransaction.getCreatedDate()) + "</b></td>" +
//                "<td>Amount <b>" + BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) + "</b></td></tr>" +
//                "        <tr><td colspan=\"4\">" +
//                "Representative: <b>" + loanAccountTransaction.getCreatedBy() + "</b> - "+ BVMicroUtils.getFullName(aUser) +"<br/></td>" +
//                "</tr>" +
//                "<tr><td></td>\n" +
//                "        <td colspan=\"4\">Amount in Letters: <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"> " + loanAccountTransaction.getLoanAmountInLetters() + "</font></td>\n" +
//                "        </tr></table><br/><br/><br/>" +
//                "    <table  border=\"0\" width=\"100\" class=\"center\">\n" +
//                "            <tr>\n" +
//                "                <th colspan=\"2\">Description</th>\n" +
//                "                <th>Amount </th>\n" +
//                "                <th>Charge</th>\n" +
//                "                <th>Balance</th>\n" +
//                "                <th></th>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\">" + loanAccountTransaction.getLoanAccount().getAccountType().getName() + "</td>\n" +
//                "                <td>" + BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) + "</td>\n" +
//                "                <td>0</td>\n" +
//                "                <td>1000</td>\n" +
//                "                <td></td>\n" +
//                "            </tr>" +
//                "        </table>" +
//                "<br/><br/><br/>" +
//                "       <table width=\"100%\">" +
//                "        <tr><td colspan=\"3\">" +
//                "<img width=\"100px\" src=\"" + rt.getUnionLogo() + "\"/> </td>" +
//                "<td>Customer Name:<b>" + loanAccountTransaction.getLoanAccount().getUser().getFirstName() + " " + loanAccountTransaction.getLoanAccount().getUser().getLastName() + "</b></td><td>Account No. <b>" + loanAccountTransaction.getLoanAccount().getAccountNumber() + "</b></td></tr>" +
//                "<tr><td></td>\n" +
//                "        <td> <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"><b>RECEIPT FOR PAYMENT MADE</b></font></td>\n" +
//                "        <td>Total: <font color=\"" + rt.getThemeColor() + "\" size=\"8px\"><b>" + BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) + "</b>frs cfa</font></td>\n" +
//                "        <td></td>\n" +
//                "        <td></td>\n" +
//                "        </tr></table><br/><br/><br/>Cash Breakdown" +
//                "    <table  border=\"0\" width=\"100%\" class=\"center\">\n" +
//                "            <tr>\n" +
//                "                <th>Value</th>\n" +
//                "                <th>Number</th>\n" +
//                "                <th>Amount</th>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td>1000</td>\n" +
//                "                <td>5</td>\n" +
//                "                <td>5 000</td>\n" +
//                "            </tr>" +
//                "            <tr>" +
//                "                <td></td>" +
//                "                <td></td>" +
//                "                <td></td></tr>" +
//                "        </table>" +
//                "<br/><br/><br/><br/><br/>" +
//                "       <table>" +
//                "       <tr><td>Cashier Signature: ------------------------------ Customer Signature: ------------------------------<br/>  Bamenda Branch, N W Region,</td>" +
//                "       <td></td></tr>" +
//                "       </table>" +
//                "</body></html>";
//        return loanBilanzNoInterest;
//    }

    public String generatePDFSavingBilanzList(SavingBilanzList savingBilanzList, SavingAccount savingAccount, String logoPath, RuntimeSetting rt) throws IOException {
        String savingBilanzNoInterest = "<html><head><style>\n" +
                "#transactions {\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "#transactions td, #customers th {\n" +
                "  border: 1px solid #ddd;\n" +
                "  padding: 4px;\n" +
                "}\n" +
                "\n" +
                "#transactions tr:nth-child(even){background-color: \""+rt.getThemeColor2()+"\";}\n" +
                "\n" +
                "#transactions tr:hover {background-color: #ddd;}\n" +
                "\n" +
                "#transactions th {\n" +
                "  padding-top: 6px;\n" +
                "  padding-bottom: 6px;\n" +
                "  text-align: left;\n" +
                "  background-color: #cda893;\n" +
                "  color: white;\n" +
                "}\n" +
                "</style>" +
                "</head><body><br/><br/>" +
                "    <table border=\"0\" width=\"100%\">" +
                "        <tr><td align=\"center\"> <img width=\"125px\" src=\""+ logoPath+"\"/><br/>bitsvalley <br/> Together each achieves more</td>" +
                "       <td colspan=\"2\"><b><font size=\"4\" color=\""+rt.getThemeColor()+"\">ACCOUNT STATEMENT</font></b></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Period From:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> 01 January 2021 </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Period To</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Account Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getAccountNumber() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Product Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getProductCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Branch Code:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getBranchCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Branch Name:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> Bamenda Branch </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"4\" color=\""+rt.getThemeColor()+"\">Customer:</font></td>" +
                "       <td align=\"right\">"+ savingAccount.getUser().getGender() +". " + savingAccount.getUser().getFirstName() + " "+savingAccount.getUser().getLastName() +"</td></tr>" +
                "       </table><br/><br/><br/>" +
                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Branch</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Reference</th>\n" +
                "                <th>Notes</th>\n" +
                "                <th>Debit</th>\n" +
                "                <th>Credit</th>\n" +
                "                <th></th>\n" +
                "            </tr>\n" + getTableList(savingBilanzList) +
                    "            <tr>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td colspan=\"3\">Total Saved:<font size=\"10px\"><b>" +savingBilanzList.getTotalSaving()+"</b></font></td>\n" +
                    "                \n" +
                    "            </tr>"+
                    "        </table><br/>" +
//                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
//                "       <tr><th id=\"transactions\">Opening Balance</th><th>1000</th></tr>" +
//                "       <tr><td>Credit Sum</td> <td></td></tr>" +
//                "       <tr><td>Debit Sum</td> <td></td></tr></table>" +
                "       <table><tr><th>Closing Balance</th><th>" +savingBilanzList.getTotalSaving()+ "</th></tr>" +
//              "       <tr><td>Bamenda Branch, N W Region</td><td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table>" +
                "</body></html>";
        return savingBilanzNoInterest;
    }

    public String generatePDFCurrentBilanzList(CurrentBilanzList currentBilanzList, CurrentAccount currentAccount,
                                               String logoPath, RuntimeSetting rt) throws IOException {
        String currentBilanzNoInterest = "<html><head><style>\n" +
                "#transactions {\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "#transactions td, #customers th {\n" +
                "  border: 1px solid #ddd;\n" +
                "  padding: 4px;\n" +
                "}\n" +
                "\n" +
                "#transactions tr:nth-child(even){background-color: \""+rt.getThemeColor2()+"\";}\n" +
                "\n" +
                "#transactions tr:hover {background-color: #ddd;}\n" +
                "\n" +
                "#transactions th {\n" +
                "  padding-top: 6px;\n" +
                "  padding-bottom: 6px;\n" +
                "  text-align: left;\n" +
                "  background-color: #cda893;\n" +
                "  color: white;\n" +
                "}\n" +
                "</style>" +
                "</head><body><br/><br/>" +
                "    <table border=\"0\" width=\"100%\">" +
                "        <tr><td align=\"center\"> <img width=\"125px\" src=\""+ logoPath+"\"/><br/>"+rt.getBusinessName()+" <br/>"+rt.getSlogan()+"</td>" +
                "       <td colspan=\"2\"><b><font size=\"4\" color=\""+rt.getThemeColor()+"\">ACCOUNT STATEMENT</font></b></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Period From:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> 01 January 2021 </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Period To</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Account Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ currentAccount.getAccountNumber() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Product Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ currentAccount.getProductCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Branch Code:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ currentAccount.getBranchCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\""+rt.getThemeColor()+"\">Branch Name:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> Bamenda Branch </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"4\" color=\""+rt.getThemeColor()+"\">Customer:</font></td>" +
                "       <td align=\"right\">"+ currentAccount.getUser().getGender() +". " + currentAccount.getUser().getFirstName() +" " + currentAccount.getUser().getLastName() +"</td></tr>" +
                "       </table><br/><br/><br/>" +
                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Branch</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Reference</th>\n" +
                "                <th>Notes</th>\n" +
                "                <th>Debit</th>\n" +
                "                <th>Credit</th>\n" +
                "                <th></th>\n" +
                "            </tr>\n" + getTableList(currentBilanzList) +
                "            <tr>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "                <td colspan=\"3\">Total Saved:<font size=\"10px\"><b>" +currentBilanzList.getTotalCurrent()+"</b></font></td>\n" +
                "                \n" +
                "            </tr>"+
                "        </table><br/>" +
//                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
//                "       <tr><th id=\"transactions\">Opening Balance</th><th>1000</th></tr>" +
//                "       <tr><td>Credit Sum</td> <td></td></tr>" +
//                "       <tr><td>Debit Sum</td> <td></td></tr></table>" +
                "       <table><tr><th>Closing Balance</th><th>" +currentBilanzList.getTotalCurrent()+ "</th></tr>" +
//              "       <tr><td>Bamenda Branch, N W Region</td><td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return currentBilanzNoInterest;
    }

    public String generatePDFLoanBilanzList(LoanBilanzList loanBilanzList, LoanAccount loanAccount, String logoPath, RuntimeSetting rt) throws IOException {
        String savingBilanzNoInterest = "<html><head><style>\n" +
                "#transactions {\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "#transactions td, #customers th {\n" +
                "  border: 1px solid #ddd;\n" +
                "  padding: 4px;\n" +
                "}\n" +
                "\n" +
                "#transactions tr:nth-child(even){background-color: "+rt.getThemeColor2()+";}\n" +
                "\n" +
                "#transactions tr:hover {background-color: "+rt.getThemeColor2()+";}\n" +
                "\n" +
                "#transactions th {\n" +
                "  padding-top: 6px;\n" +
                "  padding-bottom: 6px;\n" +
                "  text-align: left;\n" +
                "  background-color: "+rt.getThemeColor()+";\n" +
                "  color: white;\n" +
                "}\n" +
                "</style>" +
                "</head><body><br/><br/>" +
                "    <table border=\"0\" width=\"100%\">" +
                "        <tr><td align=\"center\"> <img width=\"125px\" src=\""+ logoPath+"\"/><br/>bitsvalley <br/> Together each achieves more</td>" +
                "       <td colspan=\"2\"><b><font size=\"4\" color=\"green\">LOAN ACCOUNT STATEMENT</font></b></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Period From:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> 01 January 2021 </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Period To</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Account Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ loanAccount.getAccountNumber() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Product Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ loanAccount.getProductCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Branch Code:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ loanAccount.getBranchCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Branch Name:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> Bamenda Branch </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"4\" color=\"green\">Customer Shortname:</font></td>" +
                "       <td align=\"right\">" + loanAccount.getUser().getLastName() +"</td></tr>" +
                "       </table><br/><br/><br/>" +
                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Branch</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Reference</th>\n" +
                "                <th>Notes</th>\n" +
                "                <th>VAT</th>\n" +
                "                <th>Interest</th>\n" +
                "                <th>Debit</th>\n" +
                "                <th>Credit</th>\n" +
                "                <th>Balance</th>\n" +
                "            </tr>\n" + getTableList(loanBilanzList) +
                "        </table><br/>" +
//                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
//                "       <tr><th id=\"transactions\">Opening Balance</th><th>1000</th></tr>" +
//                "       <tr><td>Credit Sum</td> <td></td></tr>" +
//                "       <tr><td>Debit Sum</td> <td></td></tr></table>" +
                "       <table><tr><th>Closing Balance</th><th>" +loanBilanzList.getCurrentLoanBalance()+ "</th></tr>" +
//              "       <tr><td>Bamenda Branch, N W Region</td><td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return savingBilanzNoInterest;
    }

    private String getTableList(LoanBilanzList loanBilanzList) {
        String tableHtml = "";
        for (LoanBilanz bilanz: loanBilanzList.getLoanBilanzList()){
            tableHtml = tableHtml +  "<tr><td>"+bilanz.getCreatedDate()+"</td>" +
                    "<td>"+bilanz.getBranch()+"</td>" +
                    "<td>"+bilanz.getModeOfPayment()+"</td>" +
                    "<td>"+bilanz.getAgent()+"</td>" +
                    "<td>"+bilanz.getReference()+"</td>" +
                    "<td>"+bilanz.getNotes()+"</td>" +
                    "<td>"+bilanz.getVatPercent()+"</td>" +
                    "<td>"+bilanz.getInterestAccrued()+"</td>" +
                    "<td>" + getLoanDebitBalance(bilanz)+"</td>" +
                    "<td>" + getLoanCreditBalance(bilanz)+"</td>" +
                    "<td>"+bilanz.getCurrentBalance()+"</td>" +
                    "</tr>";
        }
        return tableHtml;
    }

    private String getLoanDebitBalance(LoanBilanz loanBilanz){
        if(loanBilanz.getModeOfPayment().equals("RECEIPT")){
            return loanBilanz.getLoanAmount();
        }
        return "";
    }

    private String getLoanCreditBalance(LoanBilanz loanBilanz){
        if(!loanBilanz.getModeOfPayment().equals("RECEIPT")){
            return loanBilanz.getAmountReceived();
        }
        return "";
    }

    private double getSavingDebitBalance(SavingBilanz savingBilanz){
        if( savingBilanz.getSavingAmount() < 0){
            return savingBilanz.getSavingAmount();
        }
        return 0;
    }

    private double getSavingCreditBalance(SavingBilanz savingBilanz){
        if(savingBilanz.getSavingAmount() > 0){
            return savingBilanz.getSavingAmount();
        }
        return 0;
    }

    private String getTableList(CurrentBilanzList currentBilanzList) {
        String tableHtml = "";
        for (CurrentBilanz bilanz: currentBilanzList.getCurrentBilanzList()){
            tableHtml = tableHtml +  "<tr><td>"+bilanz.getCreatedDate()+"</td>" +
                    "<td>"+bilanz.getBranch()+"</td>" +
                    "<td>"+bilanz.getModeOfPayment()+"</td>" +
                    "<td>"+bilanz.getAgent()+"</td>" +

                    "<td>"+bilanz.getReference()+"</td>" +
                    "<td>"+bilanz.getNotes()+"</td>" +
//                    "<td>" + getDebitBalance(bilanz.getAmountReceived(),bilanz.getAccountType())+"</td>" +
//                    "<td>" + getCreditBalance(bilanz.getAmountReceived(),bilanz.getAccountType())+"</td>" +
                    "<td>"+bilanz.getCurrentBalance()+"</td>" +
                    "</tr>";
        }
        return tableHtml;
    }

    private String getTableList(SavingBilanzList savingBilanzList) {
        String tableHtml = "";
        for (SavingBilanz bilanz: savingBilanzList.getSavingBilanzList()){
            tableHtml = tableHtml +  "<tr><td>"+bilanz.getCreatedDate()+"</td>" +
                    "<td>"+bilanz.getBranch()+"</td>" +
                    "<td>"+bilanz.getModeOfPayment()+"</td>" +
                    "<td>"+bilanz.getAgent()+"</td>" +

                            "<td>"+bilanz.getReference()+"</td>" +
                            "<td>"+bilanz.getNotes()+"</td>" +
                    "<td>" + BVMicroUtils.formatCurrency(getSavingDebitBalance(bilanz))+"</td>" +
                    "<td>" + BVMicroUtils.formatCurrency(getSavingCreditBalance(bilanz))+"</td>" +
                            "<td>"+bilanz.getCurrentBalance()+"</td>" +
                    "</tr>";
        }
        return tableHtml;
    }

    public ByteArrayOutputStream generatePDF(String completeHtml, HttpServletResponse response)
    {
        ByteArrayOutputStream os = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            completeHtml = completeHtml.replaceAll("&","&amp;");
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(completeHtml.getBytes(StandardCharsets.UTF_8)));
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(doc,null);
            renderer.layout();
            os = new ByteArrayOutputStream();
            renderer.createPDF(os,true);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return os;
    }

    public String generateAmortizationPDF(Amortization amortizationHT, Amortization amortization, RuntimeSetting rt, String firstName) {
    return "<html><body>"+
    "<h3>LOAN PAYMENT DETAILS - AMORTIZATION REPORT</h3>"+
    "<table border=\"0\" width=\"100%\"><tr><td colspan=\"6\"><img width=\"100px\" src=\"file:/"+rt.getUnionLogo()+"\"/><br/><br/></td></tr><tr>" +

//            <td>Number: <br/><b> <span th:text="${amortization.loanMonths}"/></b></td>
//            <td>Start Date:<br/><b> <span th:text="${amortization.startDate}"/></b></td>
//            <td>Annual Rate HT: <b> <span th:text= "${amortizationHT.interestRate}"/></b><br/>
//                Annual Rate TTC: <b> <span th:text="${amortization.interestRate}"/></b><br/>
//
//            </td>
//            <td>
//                VAT: <b> <span th:text="${session.runtimeSettings.vatPercent}"/></b>
//                <br/>Total VAT Interest:
//                <b>
//                    <span th:text="${#numbers.formatDecimal(amortization.interestVAT, 0, 'COMMA', 0, 'POINT')}"/>
//                </b>
//            </td>
//            <td>Total Interest:
//                <b>
//                    <span th:text="${#numbers.formatDecimal(amortization.totalInterest, 0, 'COMMA', 0, 'POINT')}"/>
//                </b>


            "<td>Number: <br/><b> "+amortization.getLoanMonths()+"</b></td>"+
            "<td>Start Date:<br/><b> "+amortization.getStartDate()+"</b></td>"+
            "<td>Annual Rate HT: <br/><b> "+amortizationHT.getInterestRate()+
                    "</b><br/>Annual Rate TTC: <br/><b> "+amortization.getInterestRate()+"</b></td>"+
            "<td>VAT Interest: <br/><b>"+rt.getVatPercent()+"</b><br/>"+
            "Total VAT Interest: <br/><b>"+BVMicroUtils.formatCurrency(amortization.getInterestHT())+"</b></td>"+
            "<td>Total Interest:<br/>"+
                "<b>"+BVMicroUtils.formatCurrency(amortization.getTotalInterest())+"</b>"+
            "</td>" +
            "<td>Monthly Payment:<br/><b>"+amortization.getMonthlyPayment()+"</b><br/> Total Payments:<b><br/>"+amortization.getTotalInterestLoanAmount()+"</b></td>"+
            "<td>Loan Amount: <br/><b>"+BVMicroUtils.formatCurrency(amortization.getLoanAmount())+"</b></td>"+
            "</tr></table>" +
            "<table style= \"{tr:nth-child(even) = background-color: #c2ddf2;}\" width=\"100%\" border=\"1\"><tr><td><br/><b>Number</b></td><td><br/><b>Balance</b></td><td><br/><b>Principal</b></td><td><br/>" +
            "<b>Interest On TTC</b></td><td><br/><b>VAT On Interest</b></td><td><br/><b>Interest On HT</b></td>"+
            "<td><br/><b>Payment</b></td><td><br/><b>Due Date</b></td></tr>"+
            getAmortizationRow(amortization.getAmortizationRowEntryList())+
            "<tr><td colspan=\"8\" align=\"center\"><br/> Prepared by "+ firstName +" <br/> Date: "+ BVMicroUtils.formatDate(new Date()) +"</td></tr>"+
            "<tr><td colspan=\"8\" align=\"center\"><br/> This loan offer is valid till "+ LocalDateTime.now().plusDays(14).toString().substring(0,9) +"</td></tr>"+
    "</table></body></html>";
    }

    private String getAmortizationRow(List<AmortizationRowEntry> amortizationRowEntryList) {
        String row = "";
        for (AmortizationRowEntry amortizationRowEntry: amortizationRowEntryList){
            row = row +
                    "<tr>" +
                    "<td>"+amortizationRowEntry.getMonthNumber()+"</td>" +
                    "<td>"+amortizationRowEntry.getLoanBalance()+"</td>" +
                    "<td>"+amortizationRowEntry.getPrincipal()+"</td>" +
                    "<td>"+BVMicroUtils.formatCurrency(amortizationRowEntry.getMonthlyInterest())+"</td>" +
                    "<td>"+BVMicroUtils.formatCurrency(amortizationRowEntry.getVATOnInterest())+"</td>" +
                    "<td>"+BVMicroUtils.formatCurrency(amortizationRowEntry.getInterestOnHT())+"</td>" +
                    "<td>"+amortizationRowEntry.getPayment()+"</td>" +
                    "<td>"+amortizationRowEntry.getDate()+"</td>" +
                    "</tr>";
        }
        return row;
    }

}


