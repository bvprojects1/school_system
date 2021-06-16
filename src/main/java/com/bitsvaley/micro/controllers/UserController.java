package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.services.TodoService;
import com.bitsvaley.micro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController extends SuperController{

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping(value = "/registerUser")
    public String registerUser(ModelMap model) {
        User user = new User();
        model.put("user", user);
        return "user";
    }

    @PostMapping(value = "/registerUserForm")
    public String registerUserForm(@ModelAttribute("user") User user, ModelMap model) {
          userService.createUser(user);
        return "userSaved";
    }

    @PostMapping(value = "/findUserByUserName")
    public String findUserByUserName(@ModelAttribute("user") User user, ModelMap model) {
        User aUser = userService.findUserByUserName(user.getUserName());
        List<SavingAccount> savingAccount = aUser.getSavingAccount();
        model.put("user", aUser);
        if(null != aUser.getSavingAccount() && 0 < aUser.getSavingAccount().size()){
            return "userDetails";
        }else
        return "userDetailsNoAccount";
    }

}
