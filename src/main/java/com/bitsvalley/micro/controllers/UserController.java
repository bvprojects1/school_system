package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.SavingAccountService;
import com.bitsvalley.micro.services.UserRoleService;
import com.bitsvalley.micro.services.UserService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
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
        user = getUserRoleFromRequest(user,aUserRole);
        userService.createUser(user);
        model.put("user", user);
        model.put("userRoleTemp", aUserRole);
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user);
        if(null == savingBilanzByUserList || savingBilanzByUserList.getSavingBilanzList() == null
                ||  savingBilanzByUserList.getSavingBilanzList().size() == 0 ){ //first time login
            savingBilanzByUserList = new SavingBilanzList();
            savingBilanzByUserList.setSavingBilanzList(new ArrayList<SavingBilanz>());
            savingBilanzByUserList.setTotalSaving("0");
        }
        request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
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

    private User getUserRoleFromRequest(User user, String aUserRoleInput) {

        UserRole aUserRole = userRoleService.findUserRoleByName(com.bitsvalley.micro.utils.UserRole.CUSTOMER.name());
        if(aUserRole==null){
            aUserRole = new UserRole();
            aUserRole.setName(aUserRoleInput);
            userRoleService.saveUserRole(aUserRole);
        }
        UserRole userRoleByName = userRoleService.findUserRoleByName(aUserRoleInput);
        ArrayList<UserRole> roles = new ArrayList<UserRole>();
        roles.add(userRoleByName);
        user.setUserRole(roles);
        return user;
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
        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user);
        model.put("name", getLoggedinUserName());
        request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, user);
        return "userHome";
    }

//            ArrayList<User> customerList = getAllCustomers();
//            model.put("name", getLoggedinUserName());
//            model.put("userList", customerList );
//            return "customers";
//        }

}
