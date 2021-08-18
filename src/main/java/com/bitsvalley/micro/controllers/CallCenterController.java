package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.repositories.UserRoleRepository;
import com.bitsvalley.micro.services.CallCenterService;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class CallCenterController extends SuperController{

    @Autowired
    CallCenterService callCenterService;

    @Autowired
    CallCenterRepository callCenterRepository;

    @GetMapping(value = "/callcenter/{accountNumber}")
    public String showCustomer(@PathVariable("accountNumber") String accountNumber, ModelMap model, HttpServletRequest request) {
        List<CallCenter> callCenterList = callCenterRepository.findByAccountNumber(accountNumber);
        Collections.reverse(callCenterList);
        model.put("callCenterList", callCenterList);

        String aAccountNumber = callCenterList.get(0).getAccountNumber();
        String accountHolderName = callCenterList.get(0).getAccountHolderName();
        model.put("accountNumber",aAccountNumber );
        model.put("accountHolderName",accountHolderName );
        return "callCenter";
    }

}
