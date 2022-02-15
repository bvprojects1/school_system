package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

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
    SavingAccountTransactionService savingAccountTransactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    CallCenterService callCenterService;

    @Autowired
    InitSystemService initSystemService;

    @Autowired
    BranchService branchService;

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

    @GetMapping(value = "/registerCustomer")
    public String registerCustomer(ModelMap model) {
        User user = new User();
        model.put("user", user);
        return "userCustomer";
    }

    @GetMapping(value = "/reloadUser")
    public String reloadUser(ModelMap model, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        model.put("user", user);
        return "reloadUser";
    }
    @Transactional
    @PostMapping(value = "/registerUserPreviewForm")
    public String registerUserPreviewForm(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request) {
        String aUserRole = (String) request.getParameter("userRoleTemp");
        user = getUserRoleFromRequest(user,aUserRole);
        String loggedInUserName = getLoggedInUserName();
        user.setCreatedBy(loggedInUserName);


        // TODO: replace with count customers QUERY
        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
        UserRole customer = userRoleService.findUserRoleByName("ROLE_CUSTOMER");
        userRoleList.add(customer);
        ArrayList<User> customerList = userService.findAllByUserRoleIn(userRoleList);
        int generalCustomerCount = customerList.size() + 100000001;
        user.setCustomerNumber( generalCustomerCount+"" );

        if(user.getId()>0){ //TODO: hmmm operations movin' accounts
            Optional<User> byId = userRepository.findById(user.getId());
            List<SavingAccount> savingAccount = byId.get().getSavingAccount();
            user.setSavingAccount(savingAccount);
            userService.saveUser(user);
        }else{
            Branch branch = branchService.getBranchInfo(loggedInUserName);
            user.setBranch(branch);
            userService.createUser(user);
        }
        return findUserByUsername(user,model,request);
    }

    @PostMapping(value = "/registerUserForm")
    public String registerUserForm(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request) {
        String aUserRole = (String) request.getParameter("aUserRole");
        String gender = (String) request.getParameter("gender");

        if(StringUtils.equals(aUserRole,"ROLE_CUSTOMER")){

        String perc1 = (String) request.getParameter("perc1");
        String perc2 = (String) request.getParameter("perc2");
        String perc3 = (String) request.getParameter("perc3");
        String perc4 = (String) request.getParameter("perc4");
        String perc5 = (String) request.getParameter("perc5");

        String beneficiary1 = (String) request.getParameter("beneficiary1");
        String beneficiary2 = (String) request.getParameter("beneficiary2");
        String beneficiary3 = (String) request.getParameter("beneficiary3");
        String beneficiary4 = (String) request.getParameter("beneficiary4");
        String beneficiary5 = (String) request.getParameter("beneficiary5");

        String relation1 = (String) request.getParameter("relation1");
        String relation2 = (String) request.getParameter("relation2");
        String relation3 = (String) request.getParameter("relation3");
        String relation4 = (String) request.getParameter("relation4");
        String relation5 = (String) request.getParameter("relation5");

            int percentage1 = 0;
            int percentage2 = 0;
            int percentage3 = 0;
            int percentage4 = 0;
            int percentage5 = 0;

        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

        if(StringUtils.isNotEmpty(perc1) && StringUtils.isNotEmpty(beneficiary1)){
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName(beneficiary1);
            beneficiary.setPercentage(perc1);
            beneficiary.setRelation(relation1);
            beneficiaryList.add(beneficiary);
            percentage1 = new Integer(perc1);
        }

        if(StringUtils.isNotEmpty(perc2) && StringUtils.isNotEmpty(beneficiary2)){
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName(beneficiary2);
            beneficiary.setPercentage(perc2);
            beneficiary.setRelation(relation2);
            beneficiaryList.add(beneficiary);
            percentage2 = new Integer(perc2);
        }

        if(StringUtils.isNotEmpty(perc3) && StringUtils.isNotEmpty(beneficiary3)){
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName(beneficiary3);
            beneficiary.setPercentage(perc3);
            beneficiary.setRelation(relation3);
            beneficiaryList.add(beneficiary);
            percentage3 = new Integer(perc3);
        }

        if(StringUtils.isNotEmpty(perc4) && StringUtils.isNotEmpty(beneficiary4)){
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName(beneficiary4);
            beneficiary.setPercentage(perc4);
            beneficiary.setRelation(relation4);
            beneficiaryList.add(beneficiary);
            percentage4 = new Integer(perc4);
        }

        if(StringUtils.isNotEmpty(perc5) && StringUtils.isNotEmpty(beneficiary5)){
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName(beneficiary5);
            beneficiary.setPercentage(perc5);
            beneficiary.setRelation(relation5);
            beneficiaryList.add(beneficiary);
            percentage5 = new Integer(perc5);
        }

            if( (100 == percentage1 + percentage2
                    + percentage3 + percentage4
                    + percentage5 ) ){
                user.setBeneficiary(beneficiaryList);
            }else{
                model.addAttribute("error", "Beneficiary Percentage does not add up");
                return "userCustomer";
            }
        }

        user.setGender(gender); //TODO: Check thymeleaf! should map automatically
        model.put("userRoleTemp", aUserRole);
        model.put("user", user);
        return "userSavedPreview";
    }

    private User getUserRoleFromRequest(User user, String aUserRoleInput) {
        UserRole aUserRole = userRoleService.findUserRoleByName(aUserRoleInput);
        if(aUserRole==null){
            aUserRole = new UserRole();
            aUserRole.setName(aUserRoleInput);
            userRoleService.saveUserRole(aUserRole);
            aUserRole = userRoleService.findUserRoleByName(aUserRoleInput);
        }
        ArrayList<UserRole> roles = new ArrayList<UserRole>();
        roles.add(aUserRole);
        user.setUserRole(roles);
        return user;
    }

    @PostMapping(value = "/findUserByUserName")
    public String findUserByUsername(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request){
        return findUserByUserName(user, model, request);
    }

    @GetMapping(value = "/findAllCustomers")
    public String findUserByUserRole(ModelMap model) {
//        String authority = getAuthorityString();
//        if( authority.equals("ROLE_AGENT") ){
//            model.put("userList", getAllCustomers() );
//        }else if(authority.equals("ROLE_MANAGER") || authority.equals("ROLE_VIEW_ALL_ACCOUNTS")){
            model.put("userList", getAllUsers() );
//        }
//        else if (authority.equals("ROLE_ADMIN")){
//            model.put("userList", getAllUsers() );
//        }else{
//            model.put("userList", getAllCustomers() );
//        }
//        model.put("name", getLoggedInUserName());
        return "customers";
    }

    private String getAuthorityString() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().
                getAuthentication().getAuthorities();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities1 = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> itr = authorities1.iterator();
        String authority = "";
        while(itr.hasNext()) {
            GrantedAuthority element = (GrantedAuthority)itr.next();
            authority = element.getAuthority();
        }
        return authority;
    }


    @GetMapping(value = "/showCustomer/{id}")
    public String showCustomer(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        Optional<User> userById = userRepository.findById(id);
        User user = userById.get();
        return findUserByUserName(user,model,request);
    }


    @GetMapping(value = "/editUserRole/{id}")
    public String editUserRole(@PathVariable("id") long id, ModelMap model,
                                   HttpServletRequest request) {
        User user = userRepository.findById(id).get();
        UserRole role_customer = userRoleService.findUserRoleByName("ROLE_CUSTOMER");
        if(user.getUserRole().contains(role_customer)){
            model.put("error", "Cannot Edit A Customer Role");
            model.put("userList", getAllUsers() );
            model.put("name", getLoggedInUserName());
            return "customers";
        }
        model.put("user", user);
        return "editUserRole";
    }

    @PostMapping(value = "/editUserRoleForm")
    public String editUserRoleForm(ModelMap model, HttpServletRequest req, @ModelAttribute("user") User user) {

        User aUser = userRepository.findById(user.getId()).get();
        String[] userRole = req.getParameterValues("aUserRole");
        int length = userRole.length;

        List<UserRole> userRolesList = new ArrayList<UserRole>();
        boolean userRoleCustomerExists = false;
        for (String newUserRole: userRole) {
            if(StringUtils.equals("ROLE_CUSTOMER",newUserRole)){
                userRoleCustomerExists = true;
            }
            UserRole aUserRole = userRoleService.findUserRoleByName(newUserRole);
            if(null == aUserRole){
                aUserRole = new UserRole();
                aUserRole.setName(newUserRole);
                userRoleService.saveUserRole(aUserRole);
            }
            userRolesList.add(aUserRole);
        }

        if( length > 1 && userRoleCustomerExists ){
            model.put("user", aUser);
            model.put("updatedInfoError", "You cannot select ROLE_CUSTOMER");
            return "editUserRole";
        }

        aUser.setUserRole(userRolesList);
        userRepository.save(aUser);
        model.put("user", aUser);
        model.put("updatedInfo", "User Role updated ");
        return "editUserRole";
    }

    @GetMapping(value = "/lockAccount/{id}")
    public String lockAccount(@PathVariable("id") long id, ModelMap model,
                                 HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
//        if(getAuthorityString().equals("ROLE_MANAGER")){
            Optional<User> userById = userRepository.findById(id);
            User user = userById.get();
            user.setAccountLocked(!user.isAccountLocked());
            userService.saveUser(user);
            String blocked = user.isAccountLocked()?"Blocked":"UnBlocked";
            callCenterService.callCenterUserAccount(user, "Account has been switched "+ "Account is now "+ blocked +"by " + getLoggedInUserName());

//        };
        model.put("userList", getAllUsers() );
        model.put("name", getLoggedInUserName());
        return "customers";
    }


    @GetMapping(value = "/createSavingAccountReceiptPdf/{id}")
    public void savingReceiptPDF(@PathVariable("id") long id, ModelMap model,
                                 HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-disposition","attachment;filename="+ "statementSaving.pdf");

        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
//        try {
            OutputStream responseOutputStream = response.getOutputStream();
            Optional<SavingAccountTransaction> savingAccountTransaction = savingAccountTransactionService.findById(new Long(id));
            SavingAccountTransaction aSavingAccountTransaction = savingAccountTransaction.get();
            String htmlInput = pdfService.generateTransactionReceiptPDF(aSavingAccountTransaction,initSystemService.findAll());
            generateByteOutputStream(response,htmlInput);

    }

}
