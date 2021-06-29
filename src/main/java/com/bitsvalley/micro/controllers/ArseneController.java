package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ArseneController {

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping(value = "/returnLogin")
    public String returnLogin(ModelMap model) {
        User user = new User();
        model.put("user", user);
        return "loginArsene";
    }

    @PostMapping(value = "/returnLoginForm")
    public String returnLoginForm(ModelMap model) {
        User user = new User();
        model.put("user", user);
        return "loginArsene";
    }

    @GetMapping(value = "/returnLanding")
    public String returnLanding(ModelMap model) {
        User user = new User();
        model.put("user", user);
        return "landingArsene";
    }

}
