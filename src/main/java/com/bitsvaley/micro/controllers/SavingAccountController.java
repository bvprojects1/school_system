package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.services.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class SavingAccountController extends SuperController{

    @Autowired
    SavingAccountService savingAccountService;

    @GetMapping(value = "/registerSavingAccount")
    public String registerSaving(ModelMap model) {
        SavingAccount savingAccount = new SavingAccount();
        model.put("savingAccount", savingAccount);
        return "savingAccount";
    }

    @PostMapping(value = "/registerSavingAccountForm")
    public String registerSavingForm( @ModelAttribute("saving") SavingAccount savingAccount, ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        savingAccountService.createSavingAccount(savingAccount,user);
        request.getSession().setAttribute("savingAccount",savingAccount);
        model.put("savingAccount", savingAccount);
        return "userDetails";
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
        String blogId = request.getParameter("blogId");
        Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(savingAccountId));
        savingAccountTransaction.setSavingAccount(savingAccount.get());
        savingAccountService.createSavingAccountTransaction(savingAccountTransaction);
//        savingAccountService.findBySavingAccount(String Saving acco);
        model.put("savingAccountTransaction", savingAccountTransaction);
        return "savingAccountTransactions";
    }

}
