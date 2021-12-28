package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.BranchRepository;
import com.bitsvalley.micro.repositories.ShareAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import com.bitsvalley.micro.webdomain.TransferBilanz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class ShareAccountController extends SuperController {

    @Autowired
    ShareAccountRepository shareAccountRepository;

    @Autowired
    ShareAccountService shareAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BranchService branchService;

    @Autowired
    InitSystemService initSystemService;

    @Autowired
    CurrentAccountService currentAccountService;


    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    private CallCenterService callCenterService;

    @GetMapping(value = "/shareDetails/{id}")
    public String shareDetails( @PathVariable("id") long id, ModelMap model, HttpServletRequest request ) {
        ShareAccount byId = shareAccountRepository.findById(id).get();

        User user = byId.getUser();
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, user);
        model.put("transferBilanz", new TransferBilanz() );
        model.put("share", byId);
        if(byId.getAccountStatus().equals(AccountStatus.PENDING_PAYOUT)){
            model.put("showTransferBilanzSection", true );
        }else{
            model.put("showTransferBilanzSection", false );
        }
        return "shareDetails";
    }

    @GetMapping(value = "/shareDetailsAccNumber/{accountNumber}")
    public String shareDetailAccNumber( @PathVariable("accountNumber") String accountNumber, ModelMap model, HttpServletRequest request ) {
        ShareAccount byId = shareAccountRepository.findByAccountNumber(accountNumber);

        User user = byId.getUser();
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, user);
        model.put("transferBilanz", new TransferBilanz() );
        model.put("share", byId);

        if(byId.getAccountStatus().equals(AccountStatus.PENDING_PAYOUT)){
            model.put("showTransferBilanzSection", true );
        }else{
            model.put("showTransferBilanzSection", false );
        }

        return "shareDetails";
    }

    @Transactional
    @GetMapping(value = "/approveShareAccount/{id}")
    public String approveShare(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
        ShareAccount byId = shareAccountRepository.findById(id).get();
        model.put("transferBilanz", new TransferBilanz() );
        if( byId.getCreatedBy().equals(getLoggedInUserName())){
            model.put("error", "A different authorized user should approve this purchase" );
            model.put("showTransferBilanzSection", false );
        }else{
            byId.setAccountStatus(AccountStatus.PENDING_PAYOUT);
            byId.setApprovedBy(getLoggedInUserName());
            byId.setApprovedDate(new Date());
            shareAccountRepository.save(byId);
            callCenterService.saveCallCenterLog("PENDING PAYOUT", getLoggedInUserName(), byId.getAccountNumber(),"Share ACCOUNT APPROVED now pending payout"); //TODO ADD DATE
            model.put("showTransferBilanzSection", true );
        }
        return shareDetailAccNumber(byId.getAccountNumber(),model,request);

    }

    @Transactional
    @PostMapping(value = "/transferFromCurrentToShareAccountsFormReview")
    public String transferFromCurrentToLoanAccountsFormReview(ModelMap model,
                                                             @ModelAttribute("transferBilanz") TransferBilanz transferBilanz
                                                            ,HttpServletRequest request) {
        model.put("transferBilanz", transferBilanz);
        String shareId = request.getParameter("shareId");
        CurrentAccount fromAccount= currentAccountService.findByAccountNumber(transferBilanz.getTransferFromAccount());
        ShareAccount toAccount = shareAccountRepository.findById(new Long(shareId)).get();

        if(fromAccount.getAccountBalance() < toAccount.getAccountBalance() ){
            model.put("error", "NOT ENOUGH FUNDS AVAILABLE TO MAKE PURCHASE");
            model.put("transferBilanz", new TransferBilanz() );
            model.put("share", toAccount);
            return "shareDetails";
        }else{
            shareAccountService.transferFromCurrentToShareAccount(
                    fromAccount,
                    toAccount,
                    transferBilanz.getTransferAmount(),
                    transferBilanz.getNotes());
            String value = BVMicroUtils.formatCurrency(transferBilanz.getTransferAmount());

            model.put("transferType", BVMicroUtils.CURRENT_SHARE_TRANSFER);
            model.put("fromTransferText", " Balance: " + BVMicroUtils.formatCurrency(fromAccount.getAccountBalance()) +"  Minimum Balance:"+ BVMicroUtils.formatCurrency(fromAccount.getAccountMinBalance()) );
            model.put("toTransferText", toAccount.getAccountStatus().name() +"   Balance: " + value );

            model.put("transferAmount", value);
            model.put("notes", transferBilanz.getNotes());
            return "transferConfirm";
        }


    }


    @PostMapping(value = "/registerShareAccountForm")
    public String registerShareAccount(@ModelAttribute("shareAccount") ShareAccount shareAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());//TODO Create branch repo
        shareAccount.setBranchCode(branchInfo.getCode());
        shareAccount.setCountry(branchInfo.getCountry());
        shareAccountService.createShareAccount(shareAccount, user);
        return findUserByUserName(user, model, request);
    }

    @GetMapping(value = "/registerShareAccount")
    public String registerBranch(ModelMap model, HttpServletRequest request) {
        ShareAccount shareAccount = new ShareAccount();
        String byPropertyName = initSystemService.findByPropertyName(BVMicroUtils.UNIT_SHARE_VALUE);
        shareAccount.setUnitSharePrice(new Double(byPropertyName));
        model.put("shareAccount", shareAccount);
        return "shareAccount";
    }

    @GetMapping(value = "/shareAccounts")
    public String shareAccounts(ModelMap model, HttpServletRequest request) {
        Iterable<ShareAccount> shares = shareAccountRepository.findAll();
        Iterator<ShareAccount> iterator = shares.iterator();
//        ArrayList<ShareAccount> branchList = new ArrayList<>();
//        iterator.forEachRemaining(branchList::add);
        model.put("shareAccountsList", iterator);
        return "shareAccounts";
    }

}