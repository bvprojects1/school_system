package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    CallCenterRepository callCenterRepository;

    @Autowired
    InitSystemService initSystemService;

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
        user.setCreatedBy(getLoggedInUserName());
        if(user.getId()>0){ //TODO: hmmm operations movin' accounts
            Optional<User> byId = userRepository.findById(user.getId());
            List<SavingAccount> savingAccount = byId.get().getSavingAccount();
            user.setSavingAccount(savingAccount);
            userService.saveUser(user);
        }else{
            userService.createUser(user);
        }
        return findUserByUsername(user,model,request);
    }

    @PostMapping(value = "/registerUserForm")
    public String registerUserForm(@ModelAttribute("user") User user, ModelMap model, HttpServletRequest request) {
        String aUserRole = (String) request.getParameter("aUserRole");
//        ArrayList<UserRole> userRole = getUserRoleFromRequest(user, aUserRole);
        String gender = (String) request.getParameter("gender");
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
        return findUserByUserName(user,model,request);
    }

    @GetMapping(value = "/findAllCustomers")
    public String findUserByUserRole(ModelMap model) {
        String authority = getAuthorityString();
        if( authority.equals("ROLE_AGENT") ){
            model.put("userList", getAllCustomers() );
        }else if(authority.equals("ROLE_MANAGER")){
            model.put("userList", getAllUsers() );
        }
        else if (authority.equals("ROLE_ADMIN")){
            model.put("userList", getAllUsers() );
        }else{
            model.put("userList", getAllCustomers() );
        }
        model.put("name", getLoggedInUserName());
        return "customers";
    }

    private String getAuthorityString() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().
                getAuthentication().getAuthorities();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities1 = authentication.getAuthorities();
        GrantedAuthority next = authorities1.iterator().next();
        String authority = next.getAuthority();
        return authority;
    }


    @GetMapping(value = "/showCustomer/{id}")
    public String showCustomer(@PathVariable("id") long id,ModelMap model, HttpServletRequest request) {
        Optional<User> userById = userRepository.findById(id);
        User user = userById.get();
        return findUserByUsername(user,model,request);
    }

    @GetMapping(value = "/lockAccount/{id}")
    public String lockAccount(@PathVariable("id") long id, ModelMap model,
                                 HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        if(getAuthorityString().equals("ROLE_MANAGER")){
            Optional<User> userById = userRepository.findById(id);
            User user = userById.get();
            user.setAccountLocked(!user.isAccountLocked());
            userService.saveUser(user);
            String blocked = user.isAccountLocked()?"Blocked":"UnBlocked";
            CallCenter callCenter = new CallCenter();
            callCenter.setNotes(blocked);
            callCenter.setAccountHolderName(user.getFirstName() + " " +user.getLastName());
            callCenter.setUserName(user.getUserName());

            callCenter.setDate(new Date(System.currentTimeMillis()));
            callCenter.setNotes("Account has been switched "+ "Account is now "+ blocked +"by " + getLoggedInUserName());
            callCenterRepository.save(callCenter);
        };
        model.put("userList", getAllUsers() );
        model.put("name", getLoggedInUserName());
        return "customers";
    }


        @GetMapping(value = "/createSavingAccountReceiptPdf/{id}")
    public void savingReceiptPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition","attachment;filename="+ "statementPDF.pdf");
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            Optional<SavingAccountTransaction> savingAccountTransaction = savingAccountTransactionService.findById(new Long(id));
            SavingAccountTransaction aSavingAccountTransaction = savingAccountTransaction.get();


            String htmlInput = pdfService.generateTransactionReceiptPDF(aSavingAccountTransaction,initSystemService.findAll());

            byteArrayOutputStream = pdfService.generatePDF(htmlInput, response);
            response.setHeader("Content-Length",String.valueOf(byteArrayOutputStream.size()));
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            int bytes;
            while ((bytes = byteArrayInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteArrayInputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
    }


    @GetMapping(value = "/statementPDF/{id}")
    public void generateStatementPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition","attachment;filename="+ "statementPDF.pdf");
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(id));
            SavingBilanzList savingBilanzByUserList = savingAccountService.
                    calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(),false);
            String htmlInput = pdfService.generatePDFSavingBilanzList(savingBilanzByUserList, savingAccount.get(),"");
            byteArrayOutputStream = pdfService.generatePDF(htmlInput, response);
            response.setHeader("Content-Length",String.valueOf(byteArrayOutputStream.size()));
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            int bytes;
            while ((bytes = byteArrayInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteArrayInputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
    }
}
