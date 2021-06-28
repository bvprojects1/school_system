package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.SavingAccountType;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.services.SavingAccountService;
import com.bitsvaley.micro.services.SavingAccountTypeService;
import com.bitsvaley.micro.services.UserService;
import com.bitsvaley.micro.utils.BVMicroUtils;
import com.bitsvaley.micro.webdomain.SavingBilanzList;
import com.bitsvaley.micro.webdomain.SavingsBilanz;
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

        String savingsType = request.getParameter("savingsType");
        SavingAccountType savingAccountType = savingAccountTypeService.getSavingAccountType(savingsType);
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
        model.put("savingAccountTransaction", savingAccountTransaction);
        return "savingAccountTransaction";
    }


    @GetMapping(value = "/showSavingsBilanz/{id}")
    public String showSavingsBilanz(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        SavingBilanzList savingsBilanzByUserList = savingAccountService.getSavingsBilanzByUser(user);
        model.put("name", getLoggedinUserName());
        model.put("savingsBilanzList", savingsBilanzByUserList);
        return "savingsBilanz";
    }

}
