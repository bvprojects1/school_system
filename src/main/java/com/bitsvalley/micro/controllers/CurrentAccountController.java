package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.CurrentBilanzList;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class CurrentAccountController extends SuperController {

    @Autowired
    UserService userService;

    @Autowired
    CallCenterService callCenterService;

    @Autowired
    CurrentAccountService currentAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CurrentAccountTransactionService currentAccountTransactionService;

    @Autowired
    BranchService branchService;

    @Autowired
    PdfService pdfService;

    @Autowired
    InitSystemService initSystemService;


    @GetMapping(value = "/registerCurrentAccount")
    public String registerCurrent(ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if (user == null) {
            return "findCustomer";
        }
        CurrentAccount currentAccount = new CurrentAccount();
        model.put("currentAccount", currentAccount);
        return "currentAccount";
    }


    @PostMapping(value = "/registerCurrentAccountForm")
    public String registerCurrentAccount(@ModelAttribute("current") CurrentAccount currentAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());//TODO Create branch repo
        currentAccount.setBranchCode(branchInfo.getCode());
        currentAccount.setCountry(branchInfo.getCountry());

        AccountType accountType = new AccountType();
        accountType.setName(BVMicroUtils.CURRENT);
        accountType.setNumber("20");

        currentAccount.setAccountType(accountType);
        currentAccountService.createCurrentAccount(currentAccount, user);
        callCenterService.callCenterCurrentAccount(currentAccount);
        return findUserByUserName(user, model, request);
    }


    @GetMapping(value = "/registerCurrentAccountTransaction/{id}")
    public String registerCurrentAccountTransaction(@PathVariable("id") long id, ModelMap model) {
        CurrentAccountTransaction currentAccountTransaction = new CurrentAccountTransaction();
        return displayCurrentBilanzNoInterest(id, model, currentAccountTransaction);
    }

    private String displayCurrentBilanzNoInterest(long id, ModelMap model, CurrentAccountTransaction currentAccountTransaction) {
        CurrentAccount currentAccount = currentAccountService.findById(id).get();
        List<CurrentAccountTransaction> currentAccountTransactionList = currentAccount.getCurrentAccountTransaction();
        CurrentBilanzList currentBilanzByUserList = currentAccountService.calculateAccountBilanz(currentAccountTransactionList, false);
        model.put("name", getLoggedInUserName());
        model.put("currentBilanzList", currentBilanzByUserList);

        currentAccountTransaction.setCurrentAccount(currentAccount);
        model.put("currentAccountTransaction", currentAccountTransaction);
        return "currentBilanzNoInterest";
    }


    @GetMapping(value = "/createCurrentAccountReceiptPdf/{id}")
    public void currentReceiptPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-disposition", "attachment;filename=" + "statementCurrent.pdf");

        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        OutputStream responseOutputStream = response.getOutputStream();
        CurrentAccountTransaction currentAccountTransaction = currentAccountTransactionService.findById(new Long(id)).get();
        CurrentAccountTransaction aCurrentAccountTransaction = currentAccountTransaction;
        String htmlInput = pdfService.generateCurrentTransactionReceiptPDF(aCurrentAccountTransaction, initSystemService.findAll());
        generateByteOutputStream(response, htmlInput);

    }


    @GetMapping(value = "/statementCurrentPDF/{id}")
    public void generateStatementPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Content-disposition", "attachment;filename=" + "currentCurrent.pdf");
//            OutputStream responseOutputStream = response.getOutputStream();
        CurrentAccount currentAccount = currentAccountService.findById(new Long(id)).get();
        CurrentBilanzList currentBilanzByUserList = currentAccountService.
                calculateAccountBilanz(currentAccount.getCurrentAccountTransaction(), false);
        RuntimeSetting runtimeSetting = (RuntimeSetting) request.getSession().getAttribute("runtimeSettings");
        String htmlInput = pdfService.generatePDFCurrentBilanzList(currentBilanzByUserList,
                currentAccount, runtimeSetting.getLogo(),
                initSystemService.findAll());
        generateByteOutputStream(response, htmlInput);

    }


    @PostMapping(value = "/registerCurrentAccountTransactionForm")
    public String registerCurrentAccountTransactionForm(ModelMap model, @ModelAttribute("currentAccountTransaction") CurrentAccountTransaction currentAccountTransaction, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if (null == currentAccountTransaction.getAccountOwner()) {
            currentAccountTransaction.setAccountOwner("false");
        }
        if (StringUtils.isEmpty(currentAccountTransaction.getRepresentative())) {
            currentAccountTransaction.setRepresentative(BVMicroUtils.getFullName(user));
        }
        String createdDate = request.getParameter("createdDate");
//        currentAccountTransaction.setCreatedDate(BVMicroUtils.formatLocaleDate(createdDate));

        String currentAccountId = request.getParameter("currentAccountId");
        CurrentAccount currentAccount = currentAccountService.findById(new Long(currentAccountId)).get();
        currentAccountTransaction.setCurrentAccount(currentAccount);

        currentAccountTransaction.setWithdrawalDeposit(1);
        String error = "";

        if ("CASH".equals(currentAccountTransaction.getModeOfPayment())) {
            if (!checkBillSelectionMatchesEnteredAmount(currentAccountTransaction)) {
                model.put("billSelectionError", "Bills Selection does not match entered amount");
                currentAccountTransaction.setNotes(currentAccountTransaction.getNotes());
                return displayCurrentBilanzNoInterest(new Long(currentAccountId), model, currentAccountTransaction);
            }
        }
        String deposit_withdrawal = request.getParameter("deposit_withdrawal");
        if (StringUtils.isEmpty(currentAccountTransaction.getModeOfPayment())) {
            error = "Select Method of Payment - MOP";
        } else if (StringUtils.isEmpty(deposit_withdrawal)) {
            error = "Select Transaction Type";
        }

        if (deposit_withdrawal.equals("WITHDRAWAL")) {
            currentAccountTransaction.setCurrentAmount(currentAccountTransaction.getCurrentAmount() * -1);
            currentAccountTransaction.setWithdrawalDeposit(-1);
            error = currentAccountService.withdrawalAllowed(currentAccountTransaction);
            //Make sure min amount is not violated at withdrawal
        }
        if (!StringUtils.isEmpty(error)) {
            model.put("billSelectionError", error);
            currentAccountTransaction.setNotes(currentAccountTransaction.getNotes());
            return displayCurrentBilanzNoInterest(new Long(currentAccountId), model, currentAccountTransaction);
        }
        String modeOfPayment = request.getParameter("modeOfPayment");
        currentAccountTransaction.setModeOfPayment(modeOfPayment);
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());

        currentAccountTransaction.setBranch(branchInfo.getId());
        currentAccountTransaction.setBranchCode(branchInfo.getCode());
        currentAccountTransaction.setBranchCountry(branchInfo.getCountry());

        currentAccountService.createCurrentAccountTransaction(currentAccountTransaction, currentAccount);
        if (currentAccount.getCurrentAccountTransaction() != null) {
            currentAccount.getCurrentAccountTransaction().add(currentAccountTransaction);
        } else {
            currentAccount.setCurrentAccountTransaction(new ArrayList<CurrentAccountTransaction>());
            currentAccount.getCurrentAccountTransaction().add(currentAccountTransaction);
        }

        String username = getLoggedInUserName();

        CurrentBilanzList currentBilanzByUserList = currentAccountService.calculateAccountBilanz(currentAccount.getCurrentAccountTransaction(), false);

        callCenterService.saveCallCenterLog(currentAccountTransaction.getReference(),
                username, currentAccount.getAccountNumber(),
                "Current account transaction made " + BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()));


        model.put("name", username);
        model.put("billSelectionInfo", BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()) + " ---- PAYMENT HAS REGISTERED ----- ");
        model.put("currentBilanzList", currentBilanzByUserList);
        request.getSession().setAttribute("currentBilanzList", currentBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        currentAccountTransaction.setCurrentAccount(currentAccount);
        resetCurrentAccountTransaction(currentAccountTransaction); //reset BillSelection and amount
        currentAccountTransaction.setNotes("");
        model.put("currentAccountTransaction", currentAccountTransaction);
        return "currentBilanzNoInterest";

    }

    private void resetCurrentAccountTransaction(CurrentAccountTransaction sat) {
        sat.setCurrentAmount(0);
        sat.setModeOfPayment(null);
        sat.setWithdrawalDeposit(0);
        sat.setCurrentAmount(0);
        sat.setFifty(0);
        sat.setFiveHundred(0);
        sat.setFiveThousand(0);
        sat.setOneHundred(0);
        sat.setOneThousand(0);
        sat.setTenThousand(0);
        sat.setTwentyFive(0);
        sat.setTwoThousand(0);
    }

    private boolean checkBillSelectionMatchesEnteredAmount(CurrentAccountTransaction sat) {
        boolean match = (sat.getCurrentAmount() == (sat.getTenThousand() * 10000) +
                (sat.getFiveThousand() * 5000) +
                (sat.getTwoThousand() * 2000) +
                (sat.getOneThousand() * 1000) +
                (sat.getFiveHundred() * 500) +
                (sat.getOneHundred() * 100) +
                (sat.getFifty() * 50) +
                (sat.getTwentyFive() * 25));
        if (match) {
            sat.setNotes(sat.getNotes()
                    + addBillSelection(sat));
        }
        return match;
    }

    private String addBillSelection(CurrentAccountTransaction sat) {
        String billSelection = " BS \n";
        billSelection = billSelection + concatBillSelection(" 10 000x", sat.getTenThousand()) + "\n";
        billSelection = billSelection + concatBillSelection(" 5 000x", sat.getFiveThousand()) + "\n";
        billSelection = billSelection + concatBillSelection(" 2 000x", sat.getTwoThousand()) + "\n";
        billSelection = billSelection + concatBillSelection(" 1 000x", sat.getOneThousand()) + "\n";
        billSelection = billSelection + concatBillSelection(" 500x", sat.getFiveHundred()) + "\n";
        billSelection = billSelection + concatBillSelection(" 100x", sat.getOneHundred()) + "\n";
        billSelection = billSelection + concatBillSelection(" 50x", sat.getFifty());
        return billSelection;
    }

    private String concatBillSelection(String s, int qty) {
        if (qty == 0) {
            return "";
        }
        s = s + qty;
        return s;
    }

}