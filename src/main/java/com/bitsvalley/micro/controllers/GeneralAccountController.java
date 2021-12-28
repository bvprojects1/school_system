package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.GeneralLedger;
import com.bitsvalley.micro.domain.LedgerAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.GeneralLedgerRepository;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.repositories.UserRoleRepository;
import com.bitsvalley.micro.services.GeneralLedgerService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.GLSearchDTO;
import com.bitsvalley.micro.webdomain.GeneralLedgerBilanz;
import com.bitsvalley.micro.webdomain.GeneralLedgerWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class GeneralAccountController extends SuperController{

    @Autowired
    GeneralLedgerService generalLedgerService;

    @Autowired
    GeneralLedgerRepository generalLedgerRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    LedgerAccountRepository ledgerAccountRepository;

//    @GetMapping(value = "/gl/{accountNumber}")
//    public String showCustomer(@PathVariable("accountNumber") String accountNumber, ModelMap model, HttpServletRequest request) {
//        List<GeneralLedger> glList = generalLedgerService.findByAccountNumber(accountNumber);
//        Collections.reverse(glList);
//        model.put("glList", glList);
//        model.put("accountNumber",accountNumber );
//        return "gl";
//    }



    @PostMapping(value = "/filterGenaralLedger")
    public String showGlReference(ModelMap model, HttpServletRequest request,
                                  @ModelAttribute("glSearchDTO") GLSearchDTO glSearchDTO){

        GeneralLedgerBilanz generalLedgerBilanz = null;
        List<Integer> allLedgerAccount = glSearchDTO.getAllLedgerAccount();

            generalLedgerBilanz =
                    generalLedgerService.searchCriteria(glSearchDTO.getStartDate()+" 00:00:00.000", glSearchDTO.getEndDate()+" 23:59:59.999",
                            glSearchDTO.getAllGLEntryUsers().get(0), glSearchDTO.getAllLedgerAccount().get(0));

        model.put("generalLedgerBilanz",generalLedgerBilanz);

        ArrayList<String> allGlEntryUserNames = getAllNonCustomers();
        glSearchDTO.setAllGLEntryUsers(allGlEntryUserNames);

        model.put("allGLEntryUsers",allGlEntryUserNames);
        model.put("allLedgerAccount",ledgerAccountRepository.findAll());
        model.put("glSearchDTO", glSearchDTO);

        return "gls";
    }

    @GetMapping(value = "/gl/reference/{reference}")
    public String showGlReference(@PathVariable("reference") String reference, ModelMap model, HttpServletRequest request) {
        GeneralLedgerBilanz glList = generalLedgerService.findByReference(reference);
        String accountsInvolved = "";
        for (GeneralLedgerWeb generalLedgerWeb: glList.getGeneralLedgerWeb() ) {
            if(generalLedgerWeb.getLedgerAccount() != null ){
                accountsInvolved = accountsInvolved + generalLedgerWeb.getLedgerAccount().getName() + ", ";
            }
        }
        model.put("accountNameHeader", accountsInvolved.substring(0,accountsInvolved.length()-2) );
        model.put("allLedgerAccount",ledgerAccountRepository.findAll() );
        model.put("generalLedgerBilanz",glList);
        model.put("glSearchDTO",new GLSearchDTO());

        return "gls";
    }


    @GetMapping(value = "/gl")
    public String showAllGL( ModelMap model, HttpServletRequest request) {
        GeneralLedgerBilanz generalLedgerBilanz = generalLedgerService.findAll();
        model.put("accountNameHeader","GENERAL LEDGER TRANSACTIONS");
        model.put("allLedgerAccount",ledgerAccountRepository.findAll());
        model.put("generalLedgerBilanz",generalLedgerBilanz);

//        ArrayList<User> allNonCustomer = userRepository.findAllByUserRoleNotIn();
//
//      userRepository.findAllByUserRoleIn(allNonCustomer);

        GLSearchDTO glSearchDTO = new GLSearchDTO();
//        glSearchDTO.setAllNonCustomers(allNonCustomer);

        ArrayList<String> allGLEntryUsers = getAllNonCustomers();
        glSearchDTO.setAllGLEntryUsers(allGLEntryUsers);
        model.put("allGLEntryUsers",allGLEntryUsers);
        model.put("glSearchDTO",glSearchDTO);
        return "gls";
    }


    @GetMapping(value = "/viewLedgerAccount/{id}")
    public String ledgerAccount(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {

        Iterable<LedgerAccount> all = ledgerAccountRepository.findAll();

        GeneralLedgerBilanz generalLedgerBilanz = generalLedgerService.findGLByLedgerAccount(id);
        model.put("allLedgerAccount", all);
        model.put("generalLedgerBilanz",generalLedgerBilanz);
        model.put("glSearchDTO",new GLSearchDTO());
        return "gls";
    }


    @GetMapping(value = "/findGlByType/{type}")
    public String findByGlType(@PathVariable("type") String type,ModelMap model) {
        GeneralLedgerBilanz generalLedgerBilanz = generalLedgerService.findGLByType(type);
        model.put("allLedgerAccount",ledgerAccountRepository.findAll());
        model.put("generalLedgerBilanz",generalLedgerBilanz);
        model.put("glSearchDTO",new GLSearchDTO());
        return "gls";
    }

}
