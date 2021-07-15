package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserRoleService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

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
            SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user, false);
            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
            return "userHome";
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


    public String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
