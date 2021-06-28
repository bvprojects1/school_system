package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.services.SavingAccountService;
import com.bitsvaley.micro.services.UserRoleService;
import com.bitsvaley.micro.services.UserService;
import com.bitsvaley.micro.utils.BVMicroUtils;
import com.bitsvaley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class UserController extends SuperController{

    @Autowired
    private UserService userService;

    @Autowired
    private SavingAccountService savingAccountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

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

    @PostMapping(value = "/registerUserPreviewForm")
    public String registerUserPreviewForm(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request) {
        String aUserRole = (String) request.getParameter("userRoleTemp");
        getUserRoleFromRequest(user,aUserRole);
        userService.createUser(user);
        model.put("user", user);
        model.put("userRoleTemp", aUserRole);
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, user);
        return "userDetails";
    }

    @PostMapping(value = "/registerUserForm")
    public String registerUserForm(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request) {
        String aUserRole = (String) request.getParameter("aUserRole");
//        ArrayList<UserRole> userRole = getUserRoleFromRequest(user, aUserRole);
        model.put("userRoleTemp", aUserRole);
        model.put("user", user);
        return "userSavedPreview";
    }

    private ArrayList<UserRole> getUserRoleFromRequest(User user, String aUserRoleInput) {
        UserRole aUserRole = userRoleService.findUserRoleByName(com.bitsvaley.micro.utils.UserRole.CUSTOMER.name());
        if(aUserRole==null){
            aUserRole = new UserRole();
            aUserRole.setName(aUserRoleInput);
        }
        ArrayList<UserRole> roles = new ArrayList<UserRole>();
        roles.add(aUserRole);
        user.setUserRole(roles);
        return roles;
    }

    @PostMapping(value = "/findUserByUserName")
    public String findUserByUsername(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request){
        return findUserByUserName(user,model,request);
    }

    @GetMapping(value = "/findAllCustomers")
    public String findUserByUserRole(ModelMap model) {
        ArrayList<User> customerList = getAllCustomers();
        model.put("name", getLoggedinUserName());
        model.put("userList", customerList );
        return "customers";
    }


    @GetMapping(value = "/showCustomer/{id}")
    public String showCustomer(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        Optional<User> userById = userRepository.findById(id);
        User user = userById.get();
        SavingBilanzList savingsBilanzByUserList = savingAccountService.getSavingsBilanzByUser(user);
        model.put("savingsBilanzList", savingsBilanzByUserList);
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, user);
        return "userDetails";
    }

}
