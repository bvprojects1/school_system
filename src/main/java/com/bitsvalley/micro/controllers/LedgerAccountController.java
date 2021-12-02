package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
import com.bitsvalley.micro.services.GeneralLedgerService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LedgerEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class LedgerAccountController extends SuperController{

    @Autowired
    LedgerAccountRepository ledgerAccountRepository;

    @Autowired
    GeneralLedgerService generalLedgerService;

    @GetMapping(value = "/newLedgerAccount")
    public String newLedgerAccount(ModelMap model, HttpServletRequest request) {

        Iterable<LedgerAccount> all = ledgerAccountRepository.findAll();
        if( !all.iterator().hasNext()){
            //init ledgerAccounts
            List<LedgerAccount> ledgerAccountList = new ArrayList<LedgerAccount>();
//
            LedgerAccount ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.CASH);
//            ledgerAccount.setCategory(BVMicroUtils.ASSETS);
//            ledgerAccount.setCode(BVMicroUtils.CASH_1001);
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SAVINGS_GL_3003);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setCode(BVMicroUtils.SAVINGS_GL_3003);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.SAVINGS);
//            ledgerAccount.setCategory("3000 – 3999");
//            ledgerAccount.setCode(BVMicroUtils.SAVINGS_3004);
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.LOAN_GL_3001);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setCode(BVMicroUtils.LOAN_GL_3001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CASH_GL_5001);
            ledgerAccount.setCategory("5000 – 5999");
            ledgerAccount.setCode(BVMicroUtils.CASH_GL_5001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.LOAN_INTEREST_GL_7001);
            ledgerAccount.setCategory("7000 – 7999");
            ledgerAccount.setCode(BVMicroUtils.LOAN_INTEREST_GL_7001);
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.VAT_GL_4002);
            ledgerAccount.setCode(BVMicroUtils.VAT_GL_4002);
            ledgerAccount.setCategory("4000 – 4999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.CURRENT_GL_3004);
            ledgerAccount.setCode(BVMicroUtils.CURRENT_GL_3004);
            ledgerAccount.setCategory("3000 – 3999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);

            ledgerAccount = new LedgerAccount();
            ledgerAccount.setName(BVMicroUtils.SHARE_GL_5004);
            ledgerAccount.setCode(BVMicroUtils.SHARE_GL_5004);
            ledgerAccount.setCategory("5000 – 5999");
            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
            ledgerAccountList.add(ledgerAccount);


//
//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.REVENUE);
//            ledgerAccount.setCode("400");
//            ledgerAccount.setCategory("REVENUE 400 – 599");
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccountList.add(ledgerAccount);
//
//            ledgerAccount = new LedgerAccount();
//            ledgerAccount.setName(BVMicroUtils.EXPENSES);
//            ledgerAccount.setCode("600");
//            ledgerAccount.setCategory("EXPENSES 600 – 799");
//            ledgerAccount.setStatus(BVMicroUtils.ACTIVE);
//            ledgerAccountList.add(ledgerAccount);

            Iterable<LedgerAccount> ledgerListIterable = ledgerAccountList;
            ledgerAccountRepository.saveAll(ledgerListIterable);
        }




        model.put("ledgerAccount",new LedgerAccount());
        model.put("ledgerAccountList", all);
        return "ledgeraccount";
    }

    @PostMapping(value = "/saveLedgerAccountForm")
    public String saveLedgerAccountForm(@ModelAttribute("ledgerAccount") LedgerAccount ledgerAccount,
                                        ModelMap model ) {
        if(null == ledgerAccount.getStatus()) ledgerAccount.setStatus(BVMicroUtils.INACTIVE);
        ledgerAccount.setStatus(ledgerAccount.getStatus().equals("true")?"ACTIVE":"INACTIVE");
        ledgerAccountRepository.save(ledgerAccount);

        model.put("ledgerAccountList", ledgerAccountRepository.findAll());
        model.put("ledgerAccountInfo", "Created "+ledgerAccount.getName()+ " successfully ");
        return "ledgeraccount";
    }

    @GetMapping(value = "/addGeneralLedgerEntry/{id}")
    public String addGeneralLedgerEntry(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        List<LedgerAccount> destinationLedgerAccount = ledgerAccountRepository.findAllExcept(id);
        LedgerAccount originLedgerAccount = ledgerAccountRepository.findById(id).get();

        model.put("ledgerEntryDTO", new LedgerEntryDTO());
        model.put("destinationLedgerAccounts", destinationLedgerAccount);
        model.put("originLedgerAccount", originLedgerAccount);

        return "glAddEntry";
    }


    @PostMapping(value = "/addLedgerEntryFormReviewForm")
    public String addLedgerEntryFormReviewForm(@ModelAttribute("ledgerEntryDTO") LedgerEntryDTO ledgerEntryDTO,
                                        ModelMap model, HttpServletRequest request ) {

        generalLedgerService.updateManualAccountTransaction(ledgerEntryDTO);

//        model.put("ledgerAccountList", ledgerAccountRepository.findAll());
//        model.put("ledgerAccountInfo", "Created "+ledgerAccount.getName()+ " successfully ");

        return "ledgeraccount";
    }

}
