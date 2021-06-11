package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.services.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SavingAccountController extends SuperController{

    @Autowired
    SavingAccountService savingService;

    @GetMapping(value = "/registerSavingAccount")
    public String registerSaving(ModelMap model) {
        SavingAccount savingAccount = new SavingAccount();
        model.put("savingAccount", savingAccount);
        return "savingAccount";
    }


    @PostMapping(value = "/registerSavingAccountForm")
    public String registerSavingForm(@ModelAttribute("saving") SavingAccount savingAccount) {
        savingService.createSavingAccount(savingAccount);
        return "savingAccountSaved";
    }


    @GetMapping(value = "/registerSavingAccountTransaction")
    public String registerSavingAccountTransaction(ModelMap model) {
        SavingAccount savingAccount = new SavingAccount();
        model.put("savingAccount", savingAccount);
        return "savingAccount";
    }


    @PostMapping(value = "/registerSavingAccountTransactionForm")
    public String registerSavingAccountTransactionForm(@ModelAttribute("saving") SavingAccount user) {
//        savingService.createSavingAccountTransaction(user);
        return "userSaved";
    }

}
