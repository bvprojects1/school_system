package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.services.SavingAccountService;
import com.bitsvaley.micro.services.UserRoleService;
import com.bitsvaley.micro.services.UserService;
import com.bitsvaley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
public class SuperController {


    @Autowired
    private UserService userService;

    @Autowired
    private SavingAccountService savingAccountService;

    @Autowired
    private UserRoleService userRoleService;

    public String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }


    public String findUserByUserName(User user, ModelMap model, HttpServletRequest request) {
        User aUser = userService.findUserByUserName(user.getUserName());
        if(null != aUser && null != aUser.getSavingAccount() && 0 < aUser.getSavingAccount().size()){
        }else {
            SavingAccount savingAccount = savingAccountService.findByAccountNumber(user.getUserName());
            if(null != savingAccount){
                aUser = savingAccount.getUser();
            }
        }
        if(null != aUser && null != aUser.getSavingAccount() && 0 < aUser.getSavingAccount().size()){
            model.put("user", aUser);
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
            return "userDetails";
        }
            return "userDetailsNoAccount";
    }

    public ArrayList<User> getAllCustomers() {
        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
        UserRole customer = userRoleService.findUserRoleByName("CUSTOMER");
        userRoleList.add(customer);
        ArrayList<User> customerList = userService.findAllByUserRoleIn(userRoleList);
        return customerList;
    }

    public String getLoggedinUserName() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }
}
