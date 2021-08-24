package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.PdfService;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.SavingAccountTypeService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class SavingAccountController extends SuperController{

    @Autowired
    UserService userService;

    @Autowired
    CallCenterRepository callCenterRepository;

    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    SavingAccountTypeService savingAccountTypeService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PdfService pdfService;

    @GetMapping(value = "/registerSavingAccount")
    public String registerSaving(ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if(user == null){
            return "findCustomer";
        }
        SavingAccount savingAccount = new SavingAccount();
        model.put("savingAccount", savingAccount);
        return "savingAccount";
    }

    @PostMapping(value = "/registerSavingAccountForm")
    public String registerSavingForm( @ModelAttribute("saving") SavingAccount savingAccount, ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();
        savingAccountService.createSavingAccount(savingAccount, user);
        return findUserByUserName(user, model, request);
    }

    @GetMapping(value = "/registerSavingAccountTransaction/{id}")
    public String registerSavingAccountTransaction(@PathVariable("id") long id,ModelMap model) {
        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
        return  displaySavingBilanzNoInterest(id, model, savingAccountTransaction);
    }

    private String displaySavingBilanzNoInterest(long id, ModelMap model, SavingAccountTransaction savingAccountTransaction) {
        Optional<SavingAccount> savingAccount = savingAccountService.findById(id);
        SavingAccount aSavingAccount = savingAccount.get();
        List<SavingAccountTransaction> savingAccountTransactionList = aSavingAccount.getSavingAccountTransaction();
        SavingBilanzList savingBilanzByUserList = savingAccountService.calculateAccountBilanz(savingAccountTransactionList,false);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);

        savingAccountTransaction.setSavingAccount(aSavingAccount);
        model.put("savingAccountTransaction", savingAccountTransaction);
        return "savingBilanzNoInterest";
    }


    @GetMapping(value = "/printSavingAccountDetails/{id}")
    public String printSavingAccountDetails(@PathVariable("id") long id, ModelMap model,
                  @ModelAttribute("savingAccountTransaction") SavingAccountTransaction savingAccountTransaction,
                  HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = getLoggedInUserName();
        Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(id));
        SavingBilanzList savingBilanzByUserList = savingAccountService.
                calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(),false);
        String htmlInput =    null;
//                pdfService.generatePDFSavingBilanzList(savingBilanzByUserList, username);

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition","attachment;filename="+ "statementPDF.pdf");
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            byteArrayOutputStream = pdfService.generatePDF(htmlInput, response);
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            int bytes;
            while ((bytes = byteArrayInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteArrayInputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "userHome";
    }




    @PostMapping(value = "/registerSavingAccountTransactionForm")
    public String registerSavingAccountTransactionForm(ModelMap model, @ModelAttribute("savingAccountTransaction") SavingAccountTransaction savingAccountTransaction, HttpServletRequest request) {
        String savingAccountId = request.getParameter("savingAccountId");
        Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(savingAccountId));
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);

        if("CASH".equals(savingAccountTransaction.getModeOfPayment())){
            if(!checkBillSelectionMatchesEnteredAmount(savingAccountTransaction)){
                model.put("billSelectionError", "Bills Selection does not match entered amount");
                savingAccountTransaction.setNotes("");
                return displaySavingBilanzNoInterest(new Long(savingAccountId),model,savingAccountTransaction);
            }
        }

        if(request.getParameter("deposit_withdrawal").equals("WITHDRAWAL")){
            savingAccountTransaction.setSavingAmount(savingAccountTransaction.getSavingAmount()*-1);
            String error = savingAccountService.withdrawalAllowed(savingAccountTransaction);
            //Make sure min amount is not violated at withdrawal
            if(!( error == null )){
                model.put( "billSelectionError", error );
                savingAccountTransaction.setNotes("");
                return displaySavingBilanzNoInterest(new Long(savingAccountId),model,savingAccountTransaction);
            }
        }

        String modeOfPayment = request.getParameter("modeOfPayment");
        savingAccountTransaction.setModeOfPayment(modeOfPayment);

        savingAccountService.createSavingAccountTransaction(savingAccountTransaction, user);
        if(savingAccount.get().getSavingAccountTransaction() != null ){
            savingAccount.get().getSavingAccountTransaction().add(savingAccountTransaction);
        }else{
            savingAccount.get().setSavingAccountTransaction(new ArrayList<SavingAccountTransaction>());
            savingAccount.get().getSavingAccountTransaction().add(savingAccountTransaction);
        }
        savingAccountService.save(savingAccount.get());

        CallCenter callCenter = new CallCenter();
        callCenter.setUserName(savingAccount.get().getUser().getUserName());
        callCenter.setAccountNumber(savingAccount.get().getAccountNumber());
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes(savingAccountTransaction.getModeOfPayment() + " Payment/ Deposit made into account amount: " + savingAccountTransaction.getSavingAmount());

        callCenterRepository.save(callCenter);

        SavingBilanzList savingBilanzByUserList = savingAccountService.calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(),false);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        resetSavingsAccountTransaction(savingAccountTransaction); //reset BillSelection and amount
        savingAccountTransaction.setNotes("");
        model.put("savingAccountTransaction", savingAccountTransaction);

        return "savingBilanzNoInterest";

    }

    private void resetSavingsAccountTransaction(SavingAccountTransaction sat) {
        sat.setSavingAmount(0);
        sat.setFifty(0);
        sat.setFiveHundred(0);
        sat.setFiveThousand(0);
        sat.setOneHundred(0);
        sat.setOneThousand(0);
        sat.setTenThousand(0);
        sat.setTwentyFive(0);
        sat.setTwoThousand(0);
    }

    private boolean checkBillSelectionMatchesEnteredAmount(SavingAccountTransaction sat) {
        boolean match = (sat.getSavingAmount() == (sat.getTenThousand()*10000) +
                (sat.getFiveThousand()*5000) +
                (sat.getTwoThousand()*2000) +
                (sat.getOneThousand()*1000) +
                (sat.getFiveHundred()*500) +
                (sat.getOneHundred()*100) +
                (sat.getFifty()*50) +
                (sat.getTwentyFive()*25));
        if(match){
            sat.setNotes( sat.getNotes()
                + addBillSelection(sat) );
        }
        return match;
    }

    private String addBillSelection(SavingAccountTransaction sat) {
        String billSelection =  " Bill Selection \n";
        billSelection = billSelection + concatBillSelection(" 10 000x", sat.getTenThousand()) + "\n";
        billSelection = billSelection + concatBillSelection(" 5 000x", sat.getFiveThousand()) + "\n";
        billSelection = billSelection +  concatBillSelection(" 2 000x", sat.getTwoThousand()) + "\n";
        billSelection = billSelection +  concatBillSelection(" 1 000x", sat.getOneThousand())+ "\n";
        billSelection = billSelection +  concatBillSelection(" 500x", sat.getFiveHundred()) + "\n";
        billSelection = billSelection +  concatBillSelection(" 100x", sat.getOneHundred()) + "\n";
        billSelection = billSelection +  concatBillSelection(" 50x", sat.getFifty());
        return billSelection;
    }

    private String concatBillSelection(String s, int qty) {
        if(qty == 0){
            return "";
        }
        s = s + qty;
        return s;
    }


    @GetMapping(value = "/showUserSavingBilanz/{id}")
    public String showUserSavingBilanz(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user,true);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        return "savingBilanz";
    }

    @GetMapping(value = "/showSavingAccountBilanz/{accountId}")
    public String showSavingAccountBilanz(@PathVariable("accountId") long accountId,ModelMap model, HttpServletRequest request) {
        Optional<SavingAccount> byId = savingAccountService.findById(accountId);
        List<SavingAccountTransaction> savingAccountTransaction = byId.get().getSavingAccountTransaction();
        SavingBilanzList savingBilanzByUserList = savingAccountService.calculateAccountBilanz(savingAccountTransaction,true);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        return "savingBilanz";
    }

}
