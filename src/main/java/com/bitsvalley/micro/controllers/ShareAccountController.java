package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.CurrentAccount;
import com.bitsvalley.micro.domain.ShareAccount;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.BranchRepository;
import com.bitsvalley.micro.repositories.ShareAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.BranchService;
import com.bitsvalley.micro.services.ShareAccountService;
import com.bitsvalley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

//    @PostMapping(value = "/registerShareAccountForm")
//    public String registerSavingForm(@ModelAttribute("shareAccount") ShareAccount shareAccount,
//                                     ModelMap model, HttpServletRequest request) {
//        shareAccountRepository.save(shareAccount);
//        model.put("shareAccount", shareAccount );
//        model.put("shareAccountInfo", shareAccount.getQuantity() + " at "+ shareAccount.getSharePrice() +"- acquired" );
//        return "shareAccount";
//    }

    @PostMapping(value = "/registerShareAccountForm")
    public String registerShareAccount(@ModelAttribute("shareAccount") ShareAccount shareAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());//TODO Create branch repo
        shareAccount.setBranchCode(branchInfo.getCode());
        shareAccount.setCountry(branchInfo.getCountry());
//        shareAccountService.createShareAccount(shareAccount, user);
        return findUserByUserName(user, model, request);
    }

    @GetMapping(value = "/registerShareAccount")
    public String registerBranch(ModelMap model, HttpServletRequest request) {
        ShareAccount shareAccount = new ShareAccount();
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