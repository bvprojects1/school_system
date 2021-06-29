package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class WelcomeController extends SuperController{


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
        if(null != (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE)){
            return "userDetails";
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
