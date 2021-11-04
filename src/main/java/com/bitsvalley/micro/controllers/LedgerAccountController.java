package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.GeneralLedger;
import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.services.GeneralLedgerService;
import com.bitsvalley.micro.services.InitSystemService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.GLSearchDTO;
import com.bitsvalley.micro.webdomain.GeneralLedgerBilanz;
import com.bitsvalley.micro.webdomain.LedgerEntryDTO;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
        model.put("ledgerAccount",new LedgerAccount());
        model.put("ledgerAccountList", ledgerAccountRepository.findAll());
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
