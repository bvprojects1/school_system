package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccountType;
import com.bitsvalley.micro.domain.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class SavingAccountController extends SuperController{

    @Autowired

    UserService userService;

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
        String savingType = request.getParameter("savingType");
        SavingAccountType savingAccountType = savingAccountTypeService.getSavingAccountType(savingType);
        savingAccount.setSavingAccountType(savingAccountType);
        savingAccountService.createSavingAccount(savingAccount, user);
        return findUserByUserName(user, model, request);
    }

    @GetMapping(value = "/registerSavingAccountTransaction/{id}")
    public String registerSavingAccountTransaction(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
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


//        model.put("name", getLoggedInUserName());
//        model.put("savingBilanzList", savingBilanzByUserList);
//
//        savingAccountTransaction.setSavingAccount(savingAccount.get());
//        model.put("savingAccountTransaction", savingAccountTransaction);
//
//        return "savingBilanzNoInterest";
    }




    @PostMapping(value = "/registerSavingAccountTransactionForm")
    public String registerSavingAccountTransactionForm(ModelMap model, @ModelAttribute("savingAccountTransaction") SavingAccountTransaction savingAccountTransaction, HttpServletRequest request) {
        String savingAccountId = request.getParameter("savingAccountId");
        Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(savingAccountId));
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);

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

        SavingBilanzList savingBilanzByUserList = savingAccountService.calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(),false);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        model.put("savingAccountTransaction", savingAccountTransaction);


        return "savingBilanzNoInterest";

    }


    @GetMapping(value = "/showSavingBilanz/{id}")
    public String showSavingBilanz(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user,true);
        model.put("name", getLoggedInUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        return "savingBilanz";
    }

}
