package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class WelcomeController extends SuperController{


    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/")
    public String showIndexPage(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedinUserName());
        return "welcome";
    }

    @GetMapping(value = "/login")
    public String login(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedinUserName());
        return "login";
    }

    @GetMapping(value = "/welcome")
    public String welcome(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedinUserName());
        User aUser = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if(null != aUser){
            SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser);
            if(null == savingBilanzByUserList || savingBilanzByUserList.getSavingBilanzList() == null
                    ||  savingBilanzByUserList.getSavingBilanzList().size() == 0 ){ //first time login
                savingBilanzByUserList = new SavingBilanzList();
                savingBilanzByUserList.setSavingBilanzList(new ArrayList<SavingBilanz>());
                savingBilanzByUserList.setTotalSaving("0");
            }
            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
            return "userHome";
        }
        return "welcome";
    }


    @GetMapping(value = "/welcomeGlobal")
    public String welcomeGlobal(ModelMap model, HttpServletRequest request) {
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE,null);
        return "welcome";
    }

    @GetMapping(value = "/searchCustomer")
    public String searchCustomer(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedinUserName());
        return "welcome";
    }


    @GetMapping(value = "/landing")
    public String showLandingPage(ModelMap model) {
        return "landing";
    }


}
