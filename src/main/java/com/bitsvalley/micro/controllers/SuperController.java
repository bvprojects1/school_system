package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.LoanAccountService;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserRoleService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
public class SuperController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private SavingAccountService savingAccountService;

    @Autowired
    private LoanAccountService loanAccountService;

    @Autowired
    private UserRoleService userRoleService;


    public String  findUserByUserName(User user, ModelMap model, HttpServletRequest request) {
        User aUser = userService.findUserByUserName(user.getUserName());
        if(aUser == null){
            SavingAccount savingAccount = savingAccountService.findByAccountNumber(user.getUserName());
            if( null != savingAccount ){
                aUser = savingAccount.getUser();
            }
            if(aUser==null){
                Optional<SavingAccountTransaction> byReference
                        = savingAccountTransactionRepository.findByReference(user.getUserName());

                if(byReference.isPresent()){
                    aUser = byReference.get().getSavingAccount().getUser();
                }
            }
        }
        if("ROLE_CUSTOMER".equals(aUser.getUserRole().get(0).getName())){
            model.put("createSavingAccountEligible", true);
            model.put("createLoanAccountEligible", true);
        }else{
            model.put("createSavingAccountEligible", false);
            model.put("createLoanAccountEligible", false);
        }
        if(null != aUser){
            model.put("user", aUser); //TODO: stay consitent session or model
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
        }
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser, false);
        LoanBilanzList loanBilanzByUserList = loanAccountService.getLoanBilanzByUser(aUser, false);
//        if(null != aUser.getSavingAccount() && 0 < aUser.getSavingAccount().size()){
            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
//        }
//        if(null != aUser.getLoanAccount() && 0 < aUser.getLoanAccount().size()){
            request.getSession().setAttribute("loanBilanzList",loanBilanzByUserList);
//        }

        if(aUser.getSavingAccount().size() == 0 && aUser.getLoanAccount().size() == 0){
            model.put("name", getLoggedInUserName());
            request.getSession().setAttribute("savingBilanzList", savingBilanzByUserList);
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
            return "userHomeNoAccount";
    }
        return "userHome";
    }




    public ArrayList<User> getAllCustomers() {
        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
        UserRole customer = userRoleService.findUserRoleByName("ROLE_CUSTOMER");
        userRoleList.add(customer);
        ArrayList<User> customerList = userService.findAllByUserRoleIn(userRoleList);
        return customerList;
    }

    public ArrayList<User> getAllManager() {
        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
        UserRole manager = userRoleService.findUserRoleByName("ROLE_MANAGER");
        userRoleList.add(manager);
        ArrayList<User> managerList = userService.findAllByUserRoleIn(userRoleList);
        return managerList;
    }


    public ArrayList<User> getAllUsers() {
        Iterable<User> all = userRepository.findAll();
        Iterator<User> iterator = all.iterator();
        ArrayList<User> userList = new ArrayList<>();
        iterator.forEachRemaining(userList::add);
        return userList;
    }


    public String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    protected Branch getBranchInfo(String userName){
        User loggedInUser = userRepository.findByUserName(getLoggedInUserName());
        final Branch branch = loggedInUser.getBranch();
        return branch;
    }

}
