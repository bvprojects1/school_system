package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.repositories.UserRoleRepository;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

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
public class WelcomeController extends SuperController{


    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserService userService;

    @GetMapping(value = "/")
    public String showIndexPage(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedInUserName());
        return "welcome";
    }

    @GetMapping(value = "/login")
    public String login(ModelMap model, HttpServletRequest request) {
        if(request.getSession() != null){
            request.getSession().invalidate();
        }
        return "login";
    }

    @GetMapping(value = "/welcome")
    public String welcome(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedInUserName());
        User aUser = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        User byUserName = userRepository.findByUserName(getLoggedInUserName());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byUserName);
        if(null != aUser){
            SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser, false);
            Collections.reverse(savingBilanzByUserList.getSavingBilanzList()); //TODO: reverse during search?
            if(null == savingBilanzByUserList || savingBilanzByUserList.getSavingBilanzList() == null
                    ||  savingBilanzByUserList.getSavingBilanzList().size() == 0 ){ //first time login
                savingBilanzByUserList = new SavingBilanzList();
                savingBilanzByUserList.setSavingBilanzList(new ArrayList<SavingBilanz>());
                savingBilanzByUserList.setTotalSaving("0");
            }
            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
            return "userHome";
        }else{
            int savingAccountCount = savingAccountService.findAllSavingAccountCount();
            ArrayList<com.bitsvalley.micro.domain.UserRole> customerRole = new ArrayList<com.bitsvalley.micro.domain.UserRole>();
            customerRole.add(userRoleRepository.findByName(com.bitsvalley.micro.utils.UserRole.ROLE_CUSTOMER.name()));
            ArrayList<User> allByUserRoleIn = userService.findAllByUserRoleIn(customerRole);
            int customerAccountCount = allByUserRoleIn.size();
            request.getSession().setAttribute("customerAccountCount",customerAccountCount);
            request.getSession().setAttribute("savingAccountCount",savingAccountCount);
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
        model.put("name", getLoggedInUserName());
        return "welcome";
    }


    @GetMapping(value = "/landing")
    public String showLandingPage(ModelMap model) {
        return "landing";
    }


}
