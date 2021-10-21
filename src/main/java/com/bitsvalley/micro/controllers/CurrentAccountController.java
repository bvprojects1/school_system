package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.CurrentBilanzList;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import com.bitsvalley.micro.webdomain.TransferBilanz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    BranchService branchService;


    @GetMapping(value = "/registerCurrentAccount")
    public String registerSaving(ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if (user == null) {
            return "findCustomer";
        }
        CurrentAccount currentAccount = new CurrentAccount();
        model.put("currentAccount", currentAccount);
        return "currentAccount";
    }


    @PostMapping(value = "/registerCurrentAccountForm")
    public String registerSavingAccount(@ModelAttribute("current") CurrentAccount currentAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());//TODO Create branch repo
        currentAccount.setBranchCode(branchInfo.getCode());
        currentAccount.setCountry(branchInfo.getCountry());
        currentAccountService.createCurrentAccount(currentAccount, user);
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

//    @GetMapping(value = "/statementPDF/{id}")
//    public void generateStatementPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        response.setHeader("Content-disposition","attachment;filename="+ "statementSavingPDF.pdf");
//            OutputStream responseOutputStream = response.getOutputStream();
//            Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(id));
//            SavingBilanzList savingBilanzByUserList = savingAccountService.
//                    calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(),false);
//            RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
//            String htmlInput = pdfService.generatePDFSavingBilanzList(savingBilanzByUserList, savingAccount.get(),runtimeSetting.getLogo(), initSystemService.findAll() );
//            generateByteOutputStream(response, htmlInput);
//    }
//
//
//    @GetMapping(value = "/transferFromSavingToLoanAccountsForm")
//    public String transferBetweenAccounts(ModelMap model,
//                                        HttpServletRequest request,
//                                        HttpServletResponse response) {
//        TransferBilanz transferBilanz = new TransferBilanz();
//        transferBilanz.setTransferType(BVMicroUtils.DEBIT_LOAN_TRANSFER);
//        model.put("transferBilanz", transferBilanz);
//        return "transfer";
//    }
//
//
//    @GetMapping(value = "/transferFromDebitToDebitForm")
//    public String transferFromDebitToDebitForm(ModelMap model,
//                                               HttpServletRequest request) {
//
//        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
//        if (user == null ) {
//            model.addAttribute("user", new User());
//            return "findCustomer";
//        }
//        try{
//            user.getSavingAccount().size();
//        }catch (RuntimeException exp){
//            model.addAttribute("user", new User());
//            return "findCustomer";
//        }
//        TransferBilanz transferBilanz = new TransferBilanz();
//        transferBilanz.setTransferType(BVMicroUtils.DEBIT_DEBIT_TRANSFER);
//        model.put("transferBilanz", transferBilanz );
//        return "transferDebitToDebit";
//    }
//
//
//    @PostMapping(value = "/transferFromSavingToLoanAccountsForm")
//    public String transferFromSavingToLoanAccountsForm(ModelMap model,
//                                                       @ModelAttribute("transferBilanz") TransferBilanz transferBilanz) {
//        //Validate transfer amount is available
//
//        model.put("fromTransferText",transferBilanz.getTransferFromAccount() );
//        model.put("toTransferText",transferBilanz.getTransferToAccount() );
//        model.put("transferAmount",BVMicroUtils.formatCurrency(transferBilanz.getTransferAmount()) );
//        model.put("notes", transferBilanz.getNotes());
//
//        if(transferBilanz.getTransferType().equals(BVMicroUtils.DEBIT_LOAN_TRANSFER)) {
//            savingAccountService.transferFromSavingToLoan(transferBilanz.getTransferFromAccount(),
//                    transferBilanz.getTransferToAccount(),
//                    transferBilanz.getTransferAmount(), transferBilanz.getNotes());
//        }else{
//            SavingAccount savingAccount = savingAccountService.transferFromDebitToDebit(transferBilanz.getTransferFromAccount(),
//                    transferBilanz.getTransferToAccount(),
//                    transferBilanz.getTransferAmount(), transferBilanz.getNotes());
//
//        }
//
//        return "transferConfirm";
//    }
//
//
//    @PostMapping(value = "/transferFromDebitToDebitFormReview")
//    public String transferFromDebitToDebitFormReview(ModelMap model,
//                                                             @ModelAttribute("transferBilanz") TransferBilanz transferBilanz) {
//
//        SavingAccount toAccount = savingAccountService.findByAccountNumber(transferBilanz.getTransferToAccount());
//        if(null==toAccount){
//            model.put("invalidToAccount","Please make sure Account Number is valid" );
//            return "transferDebitToDebit";
//        }
//        model.put("transferBilanz", transferBilanz);
//        SavingAccount fromAccount = savingAccountService.findByAccountNumber(transferBilanz.getTransferFromAccount());
//
//        model.put("transferType", transferBilanz.getTransferType());
//        model.put("fromTransferText",fromAccount.getAccountType().getName() +" --- Balance " + BVMicroUtils.formatCurrency(fromAccount.getAccountBalance()) +"--- Minimum Balance "+ BVMicroUtils.formatCurrency(fromAccount.getAccountMinBalance()) );
//        model.put("toTransferText",toAccount.getAccountType().getName() +" --- Balance " + BVMicroUtils.formatCurrency(toAccount.getAccountBalance()) +"--- Minimum Balance "+ BVMicroUtils.formatCurrency(toAccount.getAccountMinBalance()) );
//        model.put("transferAmount", BVMicroUtils.formatCurrency(transferBilanz.getTransferAmount()));
//        model.put("notes", transferBilanz.getNotes());
//        return "transferReview";
//    }
//
//    @PostMapping(value = "/transferFromSavingToLoanAccountsFormReview")
//    public String transferFromSavingToLoanAccountsFormReview(ModelMap model,
//                                                       @ModelAttribute("transferBilanz") TransferBilanz transferBilanz) {
//        model.put("transferBilanz", transferBilanz);
//        SavingAccount fromAccount= savingAccountService.findByAccountNumber(transferBilanz.getTransferFromAccount());
//        LoanAccount toAccount = loanAccountService.findByAccountNumber(transferBilanz.getTransferToAccount());
//
//        model.put("transferType", BVMicroUtils.DEBIT_LOAN_TRANSFER);
//        model.put("fromTransferText", fromAccount.getAccountType().getName() +" --- Balance " + BVMicroUtils.formatCurrency(fromAccount.getAccountBalance()) +"--- Minimum Balance "+ BVMicroUtils.formatCurrency(fromAccount.getAccountMinBalance()) );
//        model.put("toTransferText", toAccount.getAccountType().getName() +" --- Balance " + BVMicroUtils.formatCurrency(toAccount.getCurrentLoanAmount()) +"--- Initial Loan "+ BVMicroUtils.formatCurrency(toAccount.getLoanAmount()) );
//        model.put("transferAmount", transferBilanz.getTransferAmount() );
//        model.put("notes", transferBilanz.getNotes());
//
//        return "transferReview";
//    }
//
//
//
    @PostMapping(value = "/registerCurrentAccountTransactionForm")
    public String registerCurrentAccountTransactionForm(ModelMap model, @ModelAttribute("currentAccountTransaction") CurrentAccountTransaction currentAccountTransaction, HttpServletRequest request) {
        String currentAccountId = request.getParameter("currentAccountId");
        Optional<CurrentAccount> currentAccount = currentAccountService.findById(new Long(currentAccountId));
        currentAccountTransaction.setCurrentAccount(currentAccount.get());
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);

//        if(currentAccountTransaction.getCurrentAmount() < currentAccountTransaction.getCurrentAmount().getMinimumPayment()){
//            model.put("billSelectionError", "Please make minimum payment of "+ BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAccount().getMinimumPayment()));
//            currentAccountTransaction.setNotes(currentAccountTransaction.getNotes());
//            return displaySavingBilanzNoInterest(new Long(savingAccountId), model, currentAccountTransaction);
//        }

//        if ("CASH".equals(currentAccountTransaction.getModeOfPayment())) {
//            if (!checkBillSelectionMatchesEnteredAmount(currentAccountTransaction)) {
//                model.put("billSelectionError", "Bills Selection does not match entered amount");
//                currentAccountTransaction.setNotes(currentAccountTransaction.getNotes());
//                return displayCurrentBilanzNoInterest(new Long(currentAccountId), model, currentAccountTransaction);
//            }
//        }

        if (request.getParameter("deposit_withdrawal").equals("WITHDRAWAL")) {
            currentAccountTransaction.setCurrentAmount(currentAccountTransaction.getCurrentAmount() * -1);
            String error = currentAccountService.withdrawalAllowed(currentAccountTransaction);
            //Make sure min amount is not violated at withdrawal
            if (!(error == null)) {
                model.put("billSelectionError", error);
                currentAccountTransaction.setNotes(currentAccountTransaction.getNotes());
                return displayCurrentBilanzNoInterest(new Long(currentAccountId), model, currentAccountTransaction);
            }
        }
        String modeOfPayment = request.getParameter("modeOfPayment");
        currentAccountTransaction.setModeOfPayment(modeOfPayment);
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());

        currentAccountTransaction.setBranch(branchInfo.getId());
        currentAccountTransaction.setBranchCode(branchInfo.getCode());
        currentAccountTransaction.setBranchCountry(branchInfo.getCountry());

        currentAccountService.createCurrentAccountTransaction(currentAccountTransaction);
        if (currentAccount.get().getCurrentAccountTransaction() != null) {
            currentAccount.get().getCurrentAccountTransaction().add(currentAccountTransaction);
        } else {
            currentAccount.get().setCurrentAccountTransaction(new ArrayList<CurrentAccountTransaction>());
            currentAccount.get().getCurrentAccountTransaction().add(currentAccountTransaction);
        }
        currentAccountService.save(currentAccount.get());
        String username = getLoggedInUserName();
        callCenterService.saveCallCenterLog(currentAccountTransaction.getReference(),
                username, currentAccount.get().getAccountNumber(),
                "Saving account transaction made "+ BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()));

        CurrentBilanzList savingBilanzByUserList = currentAccountService.calculateAccountBilanz(currentAccount.get().getCurrentAccountTransaction(), false);
        model.put("name", username );
        model.put("billSelectionInfo", BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()) + " ---- PAYMENT HAS REGISTERED ----- ");
        model.put("savingBilanzList", savingBilanzByUserList);
        request.getSession().setAttribute("savingBilanzList", savingBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        currentAccountTransaction.setCurrentAccount(currentAccount.get());
        resetCurrentAccountTransaction(currentAccountTransaction); //reset BillSelection and amount
        currentAccountTransaction.setNotes("");
        model.put("savingAccountTransaction", currentAccountTransaction);
        return "savingBilanzNoInterest";

    }
//
//    @GetMapping(value = "/showUserSavingBilanz/{id}")
//    public String showUserSavingBilanz(@PathVariable("id") long id, ModelMap model, HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
//        SavingBilanzList savingBilanzByUserList = savingAccountService.getSavingBilanzByUser(user, true);
//        model.put("name", getLoggedInUserName());
//        model.put("savingBilanzList", savingBilanzByUserList);
//        return "savingBilanz";
//    }
//
//    @GetMapping(value = "/showSavingAccountBilanz/{accountId}")
//    public String showSavingAccountBilanz(@PathVariable("accountId") long accountId, ModelMap model, HttpServletRequest request) {
//        Optional<SavingAccount> byId = savingAccountService.findById(accountId);
//        List<SavingAccountTransaction> savingAccountTransaction = byId.get().getSavingAccountTransaction();
//        SavingBilanzList savingBilanzByUserList = savingAccountService.calculateAccountBilanz(savingAccountTransaction, true);
//        model.put("name", getLoggedInUserName());
//        model.put("savingBilanzList", savingBilanzByUserList);
//        return "savingBilanz";
//    }
//
//
    private void resetCurrentAccountTransaction(CurrentAccountTransaction sat) {
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
//
//    private boolean checkBillSelectionMatchesEnteredAmount(SavingAccountTransaction sat) {
//        boolean match = (sat.getSavingAmount() == (sat.getTenThousand() * 10000) +
//                (sat.getFiveThousand() * 5000) +
//                (sat.getTwoThousand() * 2000) +
//                (sat.getOneThousand() * 1000) +
//                (sat.getFiveHundred() * 500) +
//                (sat.getOneHundred() * 100) +
//                (sat.getFifty() * 50) +
//                (sat.getTwentyFive() * 25));
//        if (match) {
//            sat.setNotes(sat.getNotes()
//                    + addBillSelection(sat));
//        }
//        return match;
//    }
//
//    private String addBillSelection(SavingAccountTransaction sat) {
//        String billSelection = " BS \n";
//        billSelection = billSelection + concatBillSelection(" 10 000x", sat.getTenThousand()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 5 000x", sat.getFiveThousand()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 2 000x", sat.getTwoThousand()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 1 000x", sat.getOneThousand()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 500x", sat.getFiveHundred()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 100x", sat.getOneHundred()) + "\n";
//        billSelection = billSelection + concatBillSelection(" 50x", sat.getFifty());
//        return billSelection;
//    }
//
//    private String concatBillSelection(String s, int qty) {
//        if (qty == 0) {
//            return "";
//        }
//        s = s + qty;
//        return s;
//    }



}