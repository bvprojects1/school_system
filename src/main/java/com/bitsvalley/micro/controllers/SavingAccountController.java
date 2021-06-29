package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccountType;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.SavingAccountTypeService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        model.put("savingAccountTransaction", savingAccountTransaction);
        return "savingAccountTransaction";
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
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user);
        request.getSession().setAttribute("savingBilanzList", savingBilanzByUserList);
        model.put("savingAccountTransaction", savingAccountTransaction);
        return "savingAccountTransaction";
    }


    @GetMapping(value = "/showSavingBilanz/{id}")
    public String showSavingBilanz(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user);
        model.put("name", getLoggedinUserName());
        model.put("savingBilanzList", savingBilanzByUserList);
        return "savingBilanz";
    }

}
