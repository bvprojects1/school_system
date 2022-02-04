package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.CurrentBilanzList;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import com.bitsvalley.micro.webdomain.ShareAccountBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private UserRoleRepository userRoleRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private ShareAccountTransactionRepository shareAccountTransactionRepository;

    @Autowired
    private CurrentAccountTransactionRepository currentAccountTransactionRepository;

    @Autowired
    private SavingAccountService savingAccountService;

    @Autowired
    private LoanAccountService loanAccountService;

    @Autowired
    private LoanAccountTransactionRepository loanAccountTransactionRepository;

    @Autowired
    private CurrentAccountService currentAccountService;

    @Autowired
    private LoanAccountTransactionService loanAccountTransactionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ShareAccountService shareAccountService;

    @Autowired
    private GeneralLedgerRepository generalLedgerRepository;


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
            }if(aUser==null){
                Optional<LoanAccountTransaction> byReference
                        = loanAccountTransactionRepository.findByReference(user.getUserName());
                if(byReference.isPresent()){
                    aUser = byReference.get().getLoanAccount().getUser();
                }
            }if(aUser==null){
                LoanAccount byReference
                        = loanAccountService.findByAccountNumber(user.getUserName());
                if( byReference != null ){
                    aUser = byReference.getUser();
                }
            }if(aUser==null){
                Optional<LoanAccountTransaction> byReference
                        = loanAccountTransactionRepository.findByReference(user.getUserName());
                if(byReference.isPresent()){
                    aUser = byReference.get().getLoanAccount().getUser();
                }
            }if(aUser==null){
                CurrentAccount byReference
                        = currentAccountService.findByAccountNumber(user.getUserName());
                if( byReference != null ){
                    aUser = byReference.getUser();
                }
            }if(aUser==null){
                Optional<CurrentAccountTransaction> byReference
                        = currentAccountTransactionRepository.findByReference(user.getUserName());
                if(byReference.isPresent()){
                    aUser = byReference.get().getCurrentAccount().getUser();
                }
            }if(aUser==null){
                ShareAccount byReference
                        = shareAccountService.findByAccountNumber(user.getUserName());
                if( byReference != null ){
                    aUser = byReference.getUser();
                }
            }if(aUser==null){
                Optional<ShareAccountTransaction> byReference
                        = shareAccountTransactionRepository.findByReference(user.getUserName());
                if(byReference.isPresent()){
                    aUser = byReference.get().getShareAccount().getUser();
                }
            }
            if(aUser == null){ //LoanReference
                Optional<LoanAccountTransaction> byReference = loanAccountTransactionService.
                        findByReference(user.getUserName());
                if(byReference.isPresent()){
                    LoanAccountTransaction loanAccountTransaction = byReference.get();
                    if(loanAccountTransaction != null) { //TODO: Identical code in loanAccountController
                        LoanAccount aLoanAccount = loanAccountTransaction.getLoanAccount();
                        List<LoanAccountTransaction> loanAccountTransactionList = aLoanAccount.getLoanAccountTransaction();
                        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccountTransactionList, false);
                        model.put("name", getLoggedInUserName());
                        model.put("loanBilanzList", loanBilanzByUserList);
                        byReference.get().setLoanAccount(aLoanAccount);
                        model.put("loanAccountTransaction", loanAccountTransaction);
                        return "loanBilanzNoInterest";
                    }
                }

            }
        }

        if(aUser != null && "ROLE_CUSTOMER".equals(aUser.getUserRole().get(0).getName())){
            model.put("createSavingAccountEligible", true);
            model.put("createLoanAccountEligible", true);
            model.put("createCurrentAccountEligible", true);
            model.put("createShareAccountEligible", true);
        }else{
            model.put("createSavingAccountEligible", false);
            model.put("createLoanAccountEligible", false);
            model.put("createCurrentAccountEligible", false);
            model.put("createShareAccountEligible", false);
        }
        if(null != aUser){
            model.put("user", aUser); //TODO: stay consitent session or model
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);

        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(aUser, false);
        LoanBilanzList loanBilanzByUserList = loanAccountService.getLoanBilanzByUser(aUser, false);
        CurrentBilanzList currentBilanzByUserList = currentAccountService.getCurrentBilanzByUser(aUser, false);
        ShareAccountBilanzList shareAccountBilanzList = shareAccountService.getShareAccountBilanzByUser(aUser);

            request.getSession().setAttribute("savingBilanzList",savingBilanzByUserList);
            request.getSession().setAttribute("loanBilanzList",loanBilanzByUserList);
            request.getSession().setAttribute("currentBilanzList",currentBilanzByUserList);
            request.getSession().setAttribute("shareAccountBilanzList",shareAccountBilanzList);

        if(aUser.getSavingAccount().size() == 0 && aUser.getLoanAccount().size() == 0 &&
                aUser.getCurrentAccount().size() == 0){
            model.put("name", getLoggedInUserName());
            request.getSession().setAttribute("savingBilanzList", savingBilanzByUserList);
            request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, aUser);
            return "userHomeNoAccount";
        }
        }
        if(aUser==null){
            model.put("error","No records found");
            return "welcome";
        }else{
            return "userHome";
        }
    }


    public ArrayList<String> getAllNonCustomers() {

//        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
//        UserRole customer = userRoleService.findUserRoleByName("ROLE_CUSTOMER");
//        userRoleList.add(customer);
        ArrayList<String> customerList = generalLedgerRepository.findAllDistinctByCreatedBy();
//        ArrayList<User> customerList = userService.findAllByUserNotRoleIn(userRoleList);
        return customerList;

    }

    public ArrayList<String> getGLEntryUsers() {

//        ArrayList<String> roles = new ArrayList<String>();
//        roles.add("ROLE_CUSTOMER");
//        ArrayList<UserRole> userRoleList = userRoleRepository.findByNameNotIn(roles);

//        ArrayList<User> nonCustomerList = userService.findDistinctByUserRoleIn(userRoleList);

        ArrayList<String> distinctByUser = generalLedgerRepository.findAllDistinctByCreatedBy();

        return distinctByUser;
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


    public void generateByteOutputStream(HttpServletResponse response, String htmlInput) throws IOException {
        response.setContentType("application/pdf");
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            byteArrayOutputStream = pdfService.generatePDF(htmlInput, response);
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
