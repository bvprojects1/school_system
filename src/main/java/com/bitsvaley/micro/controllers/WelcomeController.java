package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WelcomeController extends SuperController{

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/")
    public String showIndexPage(ModelMap model, HttpServletRequest request) {
        model.put("name", getLoggedinUserName());
        request.getSession().setAttribute("user", userRepository.findByUserName(getLoggedinUserName()) );
        return "welcome";
    }

    @GetMapping(value = "/landing")
    public String showLandingPage(ModelMap model) {
        return "landing";
    }

    private String getLoggedinUserName() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }
}
