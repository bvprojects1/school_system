package com.bitsvalley.micro.services;


import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import com.lowagie.text.pdf.PdfDocument;
import org.springframework.core.io.ClassPathResource;
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
        ClassPathResource classPathResource = new ClassPathResource("assets/image/logo.jpeg");

        String savingBilanzNoInterest = "<html><head>" +
                "</head><body><br/><br/>" +
                "    <table width=\"100%\">" +
                "        <tr><td colspan=\"3\">" +
                "<img width=\"50px\" src=\"/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/logo.jpeg\"/> </td>" +
                "<td>Customer Name:<b>"+savingAccountTransaction.getSavingAccount().getUser().getFirstName() +" "+savingAccountTransaction.getSavingAccount().getUser().getLastName() +"</b></td><td>Account No. <b>"+savingAccountTransaction.getSavingAccount().getAccountNumber()+"</b></td></tr>" +
                "<tr><td></td>\n" +
                "        <td> <font color=\"green\" size=\"8px\"><b>RECEIPT FOR PAYMENT MADE</b></font></td>\n" +
                "        <td>Total: <font color=\"green\" size=\"8px\"><b>"+BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount())+"</b>frs</font></td>\n" +
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
                "       <td>Agent Signature: --------------------------------<br/>Bamenda Branch, N W Region</td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table><br/><br/><br/><br/><br/><br/>" +
                "       <table width=\"100%\">" +
                "        <tr><td colspan=\"3\">" +
                "<img width=\"50px\" src=\"/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/logo.jpeg\"/> </td>" +
                "<td>Customer Name:<b>"+savingAccountTransaction.getSavingAccount().getUser().getFirstName() +" "+savingAccountTransaction.getSavingAccount().getUser().getLastName() +"</b></td><td>Account No. <b>"+savingAccountTransaction.getSavingAccount().getAccountNumber()+"</b></td></tr>" +
                "<tr><td></td>\n" +
                "        <td> <font color=\"green\" size=\"8px\"><b>RECEIPT FOR PAYMENT MADE</b></font></td>\n" +
                "        <td>Total: <font color=\"green\" size=\"8px\"><b>"+BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount())+"</b>frs cfa</font></td>\n" +
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
                "                <td>"+BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount())+"frs cfa</td>\n" +
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
                "       <td>Agent Signature: --------------------------------<br/>Bamenda Branch, N W Region</td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td></td>" +
                "       <td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return savingBilanzNoInterest;
    }

    public String generatePDFSavingBilanzList(SavingBilanzList savingBilanzList, SavingAccount savingAccount, String logoFilePath) {
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
                "#transactions tr:nth-child(even){background-color: #f2f2f2;}\n" +
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
                "        <tr><td align=\"center\"> <img width=\"50px\" src=\"/Users/frusamachifen/bv_micro_workspace/bv_micro/src/main/webapp/assets/images/logo.jpeg\"/><br/>TBC MFI PLC <br/> Together each achieves more</td>" +
                "       <td colspan=\"2\"><b><font size=\"4\" color=\"green\">ACCOUNT STATEMENT</font></b></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Period From:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> 01 January 2021 </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Period To</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Account Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getAccountNumber() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Product Number:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getProductCode() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Branch Code:</font></td>" +
                "       <td align=\"right\"><font size=\"4\">"+ savingAccount.getBranch() +"</font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"6\" color=\"green\">Branch Name:</font></td>" +
                "       <td align=\"right\"><font size=\"4\"> Bamenda Branch </font></td></tr>" +
                "        <tr><td> </td><td> </td>" +
                "       <td align=\"right\"><font size=\"4\" color=\"green\">Customer Shortname:</font></td>" +
                "       <td align=\"right\">" + savingAccount.getUser().getLastName() +"</td></tr>" +
                "       </table><br/><br/><br/>" +
                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
                "            <tr>\n" +
                "                <th>Date</th>\n" +
                "                <th>Branch</th>\n" +
                "                <th>Mode </th>\n" +
                "                <th>Agent</th>\n" +
                "                <th>Reference</th>\n" +
                "                <th>Notes</th>\n" +
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
                    "                <td colspan=\"2\">Total Saved:<font size=\"10px\"><b>" +savingBilanzList.getTotalSaving()+"</b></font></td>\n" +
                    "                \n" +
                    "            </tr>"+
                    "        </table><br/>" +
                "    <table id=\"transactions\" border=\"0\" width=\"100%\" class=\"center\">\n" +
                "       <tr><th id=\"transactions\">Opening Balance</th><th>1000</th></tr>" +
                "       <tr><td>Credit Sum</td> <td></td></tr>" +
                "       <tr><td>Debit Sum</td> <td></td></tr></table>" +
                "       <table><tr><th>Closing Balance</th><th>" +savingBilanzList.getTotalSaving()+ "</th></tr>" +
//              "       <tr><td>Bamenda Branch, N W Region</td><td>"+ BVMicroUtils.formatDate(new Date(System.currentTimeMillis())) +"</td></tr>" +
                "       </table></body></html>";
        return savingBilanzNoInterest;
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
                            "<td>"+bilanz.getSavingAmount()+"</td>" +
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


