package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.GeneralLedger;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.GeneralLedgerRepository;
import com.bitsvalley.micro.services.CallCenterService;
import com.bitsvalley.micro.services.GeneralLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class GeneralLedgerController extends SuperController{

    @Autowired
    GeneralLedgerService generalLedgerService;

    @Autowired
    GeneralLedgerRepository generalLedgerRepository;

    @GetMapping(value = "/gl/{accountNumber}")
    public String showCustomer(@PathVariable("accountNumber") String accountNumber, ModelMap model, HttpServletRequest request) {
        List<GeneralLedger> glList = generalLedgerService.findByAccountNumber(accountNumber);
        Collections.reverse(glList);
        model.put("glList", glList);
        model.put("accountNumber",accountNumber );
        return "gl";
    }

    @GetMapping(value = "/gl/{reference}")
    public String showGlReference(@PathVariable("reference") String reference, ModelMap model, HttpServletRequest request) {
        List<GeneralLedger> glList = generalLedgerService.findByAccountNumber(reference);
        Collections.reverse(glList);
        model.put("glList", glList);
        model.put("reference",reference );
        return "gl";
    }

    @GetMapping(value = "/gl")
    public String showAllGL( ModelMap model, HttpServletRequest request) {
        Iterable<GeneralLedger> glIterable= generalLedgerRepository.findAll();

        Iterator<GeneralLedger> iterator = glIterable.iterator();
        ArrayList<GeneralLedger> glList = new ArrayList<>();
        iterator.forEachRemaining(glList::add);

        model.put("glList", glList);
        return "gl";
    }

}
