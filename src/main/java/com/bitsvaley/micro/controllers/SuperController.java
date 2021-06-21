package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.services.UserService;
import com.bitsvaley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
public class SuperController {


    @Autowired
    private UserService userService;

    public String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }


    public String findUserByUserName(User user, ModelMap model, HttpServletRequest request) {
        User aUser = userService.findUserByUserName(user.getUserName());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
        List<SavingAccount> savingAccount = aUser.getSavingAccount();
        model.put("user", aUser);
        if(null != aUser.getSavingAccount() && 0 < aUser.getSavingAccount().size()){
            return "userDetails";
        }else
            return "userDetailsNoAccount";
    }

}
