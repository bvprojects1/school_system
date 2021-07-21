package com.bitsvalley.micro.services;


import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import com.lowagie.text.pdf.PdfDocument;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class PdfService {

    private SavingAccountService savingAccountService;


    public String generateTransactionReceiptPDF(SavingAccountTransaction savingAccountTransaction, String logoFilePath) {
        String savingBilanzNoInterest = "<html><head>" +
                "</head><body><br/><br/>" +
                "    <table width=\"100%\">" +
                "        <tr><td colspan=\"3\">" +
                "<img width=\"50px\" src=\"/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/logo.jpeg\"/> </td>" +
                "<td>Customer Name:<b>"+savingAccountTransaction.getSavingAccount().getUser().getFirstName() +" "+savingAccountTransaction.getSavingAccount().getUser().getLastName() +"</b></td><td>Account No. <b>"+savingAccountTransaction.getSavingAccount().getAccountNumber()+"</b></td></tr>" +
                "<tr><td></td>\n" +
                "        <td> <font color=\"green\" size=\"8px\"><b>RECEIPT FOR PAYMENT MADE</b></font></td>\n" +
                "        <td>Total: <font color=\"green\" size=\"8px\"><b>"+savingAccountTransaction.getSavingAmount()+"</b></font></td>\n" +
                "        <td></td>\n" +
                "        <td></td>\n" +
                "        </tr></table><br/><br/><br/>" +
                "    <table  border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Amount</th>\n" +
                "                <th>Notes</th>\n" +
                "                <th></th>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td>"+BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate())+"</td>\n" +
                "                <td>"+savingAccountTransaction.getModeOfPayment()+"</td>\n" +
                "                <td>"+savingAccountTransaction.getCreatedBy()+"</td>\n" +
                "                <td>"+BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount())+"</td>\n" +
                "                <td>"+savingAccountTransaction.getNotes()+"</td>\n" +
                "                <td></td>\n" +
                "            </tr>" +
                "            <tr>" +
                "                <td></td>" +
                "                <td></td>" +
                "                <td></td>" +
                "               <td></td>" +
                "                <td></td>" +
                "                <td></td></tr>" +
                "        </table>" +
                "<br/><br/><br/><br/><br/>" +
                "       <table><tr><td></td>" +
                "       <td>--------------------------------<br/>Bamenda Branch, N W Region</td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return savingBilanzNoInterest;
    }

    public String generatePDFSavingBilanzList(SavingBilanzList savingBilanzList, SavingAccount savingAccount, String logoFilePath) {
        String savingBilanzNoInterest = "<html><head>" +
                "</head><body><br/><br/>" +
                "    <table width=\"100%\">" +
                "        <tr><td colspan=\"3\"><img src=\"/assets/images/logo.jpeg\"/> <img width=\"50px\" src=\"/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/logo.jpeg\"/> </td><td>Customer Name:<b>"+savingAccount.getUser().getFirstName() +" "+savingAccount.getUser().getLastName() +"</b></td><td>Account No. <b>"+savingAccount.getAccountNumber()+"</b></td></tr><tr>" +
                "        <td><font size=\"6\"><b>"+savingAccount.getUser().getUserName()+"</b>\'s </font>" +
                "        </td>\n" +
                "        <td> Saving <br/><font color=\"green\" size=\"6px\"><b>"+savingBilanzList.getTotalSaving()+"</b></font></td>\n" +
                "        <td> Current<br/> <font size=\"6\"> 0</font></td>\n" +
                "        <td> Loan<br/> <font size=\"6\"> 0</font></td>\n" +
                "        <td>Retirement saving <br/> <font size=\"6\" color=\"#A57C00\">0 </font></td>\n" +
                "        </tr></table><br/><br/><br/>" +
                "    <table  border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Amount</th>\n" +
                "                <th>Notes</th>\n" +
                "                <th></th>\n" +
                "            </tr>\n" + getTableList(savingBilanzList) +
                    "            <tr>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td></td>\n" +
                    "                <td>Total Saved</td>\n" +
                    "                <td><font color=\"green\" size=\"10px\"><b>" +savingBilanzList.getTotalSaving()+"</b></font></td>\n" +
                    "            </tr>" +
                "            <tr>" +
                "                <td></td>" +
                "                <td></td>" +
                "                <td></td>" +
                "               <td></td>" +
                "                <td></td>" +
                "                <td></td></tr>" +
                    "        </table>" +
                "<br/><br/><br/><br/><br/>" +
                "       <tr>" +
                "       <table><tr><td></td>" +
                "       <td>Bamenda Branch, N W Region</td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return savingBilanzNoInterest;
    }

    private String getTableList(SavingBilanzList savingBilanzList) {
        String tableHtml = "";
        for (SavingBilanz bilanz: savingBilanzList.getSavingBilanzList()){
            tableHtml = tableHtml +  "<tr><td>"+bilanz.getCreatedDate()+"</td>" +
                    "<td>"+bilanz.getModeOfPayment()+"</td>" +
                    "<td>"+bilanz.getAgent()+"</td>" +
                            "<td>"+bilanz.getSavingAmount()+"</td>" +
                            "<td>"+bilanz.getNotes()+"</td>" +
                            "<td>"+bilanz.getCurrentBalance()+"</td>" +
                    "</tr>";
        }
        return tableHtml;
    }

    public ByteArrayOutputStream generatePDF(String completeHtml, HttpServletResponse response)
    {
        PdfDocument pdfDoc = null;
        ByteArrayOutputStream os = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            completeHtml = completeHtml.replaceAll("&","&amp;");
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(completeHtml.getBytes(StandardCharsets.UTF_8)));
//            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream("<html><body>trsti</body></html>".getBytes(StandardCharsets.UTF_8)));
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


}


