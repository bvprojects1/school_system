package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.Amortization;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import sun.jvm.hotspot.utilities.CStringUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Controller
public class LoanAccountController extends SuperController {


    @Autowired
    UserService userService;

    @Autowired
    LoanAccountService loanAccountService;

    @Autowired
    CurrentAccountService currentAccountService;

    @Autowired
    LoanAccountTransactionService loanAccountTransactionService;

    @Autowired
    SavingAccountService savingAccountService;

    @Autowired
    AccountTypeService accountTypeService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InterestService interestService;

    @Autowired
    ShorteeService shorteeService;

    @Autowired
    CallCenterService callCenterService;

    @Autowired
    GeneralLedgerService generalLedgerService;

    @Autowired
    InitSystemService initSystemService;

    @Autowired
    PdfService pdfService;

    @Autowired
    BranchService branchService;

    @GetMapping(value = "/registerLoanAccount")
    public String registerLoanAccount(ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        if (user == null) {
            return "findCustomer";
        }
        LoanAccount loanAccount = new LoanAccount();

        model.put("loanAccount", loanAccount);
        return "loanAccount";
    }


    @PostMapping(value = "/generatePaymentSchedule")
    public String generatePaymentSchedule(@ModelAttribute("loanAccount") LoanAccount loanAccount,
                                          ModelMap model, HttpServletRequest request) {
        double payment = interestService.monthlyPaymentAmortisedPrincipal(loanAccount.getInterestRate(), loanAccount.getTermOfLoan(), loanAccount.getLoanAmount());
        String report = "";
        Amortization amortization = new Amortization(loanAccount.getLoanAmount(),loanAccount.getInterestRate(),loanAccount.getTermOfLoan(),payment);
        model.put("amortization",amortization );
        request.getSession().setAttribute("amortization",amortization);
        return "amortizationReport";
    }

    @GetMapping(value = "/amortizationPDF")
    public void generatePaymentSchedule(@SessionAttribute("amortization") Amortization amortization,
                                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
        String htmlInput = pdfService.generateAmortizationPDF(amortization, runtimeSetting, getLoggedInUserName());
        response.setHeader("Content-disposition", "attachment;filename=" + "amortizationPDF.pdf");
        generateByteOutputStream(response, htmlInput);
    }


    @PostMapping(value = "/registerLoanAccountForm")
    public String registerLoanAccountForm(@ModelAttribute("loanAccount") LoanAccount loanAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();

        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());
        loanAccount.setBranchCode(new Long(branchInfo.getId()).toString());
        loanAccount.setBranchCode(branchInfo.getCode());
        loanAccount.setCountry(branchInfo.getCountry());

//        loanAccount.setTotalInterestOnLoan( );

        double monthlyPayment = interestService.monthlyPaymentAmortisedPrincipal(loanAccount.getInterestRate(),
        loanAccount.getTermOfLoan(),loanAccount.getLoanAmount());

//        Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
//                loanAccount.getInterestRate()*.01,
//                loanAccount.getTermOfLoan(),monthlyPayment);

        loanAccount.setMonthlyPayment(new Double(monthlyPayment).intValue());

//         interestService.calculateInterestAccruedMonthCompounded(
//              loanAccount.getInterestRate(),
//                 loanAccount.getTermOfLoan(),
//                 loanAccount.getLoanAmount()));

//        currentAccountTransaction.getCurrentAccount().getInterestRate(),
//                currentAccountTransaction.getCreatedDate(),
//                currentAccountTransaction.getCurrentAmount());

        AccountType accountType = accountTypeService.getAccountTypeByProductCode(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);

        String error = "";

        if(!StringUtils.isEmpty(loanAccount.getGuarantorAccountNumber1())){
            SavingAccount byAccountNumber1 = savingAccountService.findByAccountNumber(loanAccount.getGuarantorAccountNumber1());
            if(null == byAccountNumber1){
                error = "Guarantor account number not valid";
                model.put("loanAccount", loanAccount);
                model.put("error", error );
                return "loanAccount";
            }
            request.getSession().setAttribute("guarantor1",byAccountNumber1);
        }
        if(!StringUtils.isEmpty(loanAccount.getGuarantorAccountNumber2())){
            SavingAccount byAccountNumber2 = savingAccountService.findByAccountNumber(loanAccount.getGuarantorAccountNumber2());
            request.getSession().setAttribute("guarantor2",byAccountNumber2);
        }
        if(!StringUtils.isEmpty(loanAccount.getGuarantorAccountNumber3())){
            SavingAccount byAccountNumber3 = savingAccountService.findByAccountNumber(loanAccount.getGuarantorAccountNumber3());
            request.getSession().setAttribute("guarantor3",byAccountNumber3);
        }

        request.getSession().setAttribute("loanAccount",loanAccount);
        model.put("loanAccount", loanAccount);
        return "loanShorteeAccounts";
    }


    @GetMapping(value = "/registerLoanAccountTransaction/{id}")
    public String registerLoanAccountTransaction(@PathVariable("id") long id, ModelMap model) {
        LoanAccount loanAccount = loanAccountService.findById(id).get();
        if(!loanAccount.getAccountStatus().name().equals(BVMicroUtils.ACTIVE)){
            model.put("loanMustBeInActiveState",BVMicroUtils.LOAN_MUST_BE_IN_ACTIVE_STATE);
            return "userHome";
        }
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        return displayLoanBilanzNoInterest(id, model, loanAccountTransaction);
    }

    @GetMapping(value = "/approveLoan/{id}")
    public String approveLoan(@PathVariable("id") long id, ModelMap model) {
        LoanAccount byId = loanAccountService.findById(id).get();
        if(byId.getCreatedBy().equals(getLoggedInUserName())){
            model.put("error","Get another authorized Person to approve loan");
        }else{
            byId.setAccountStatus(AccountStatus.PENDING_PAYOUT);
            byId.setApprovedBy(getLoggedInUserName());
            byId.setApprovedDate(new Date());
            callCenterService.saveCallCenterLog("PENDING PAYOUT", getLoggedInUserName(), byId.getAccountNumber(),"LOAN ACCOUNT APPROVED"); //TODO ADD DATE
            loanAccountService.save(byId);
        }
        model.put("loan",byId);
        return "loanDetails";
    }


    @PostMapping(value = "/loanShorteeAccountsForm")
    public String loanGuarantorForm(@ModelAttribute("loanAccount") LoanAccount loanAccount,
                                    ModelMap model,
                                    HttpServletRequest request) {

        LoanAccount loanAccountSession = (LoanAccount)request.getSession().getAttribute("loanAccount");
        loanAccountSession.setGuarantor1Amount1(loanAccount.getGuarantor1Amount1());
        loanAccountSession.setGuarantorAccountNumber1(loanAccount.getGuarantorAccountNumber1());
        String loanShorteeMessage = getLoanShorteeMessage(loanAccountSession);

        request.getSession().setAttribute("loanAccount", loanAccountSession);
        model.put("loanAccount", loanAccountSession);

        if(!StringUtils.isEmpty(loanShorteeMessage)){
            model.put("errorShorteeAmount",loanShorteeMessage);
            return "loanShorteeAccounts";
        }
        return "loanShorteeReview";
    }

    @NotNull
    private String getLoanShorteeMessage(LoanAccount loanAccount) {
        if(loanAccount.getLoanAmount() > loanAccount.getGuarantor1Amount1() +
                                            loanAccount.getGuarantor1Amount2() +
                                                loanAccount.getGuarantor1Amount3()){
            return "Shortee amount is LESS than loan Amount";
        }else if (loanAccount.getLoanAmount() < loanAccount.getGuarantor1Amount1() +
                    loanAccount.getGuarantor1Amount2() +
                        loanAccount.getGuarantor1Amount3()){
            return "Shortee amount is MORE than loan Amount";
        }
        return "";
    }

    @PostMapping(value = "/createLoanAccountForm")
    public String createLoanAccount(@ModelAttribute("loanAccount") LoanAccount loanAccount,
                                    ModelMap model,
                                    HttpServletRequest request) {

        LoanAccount loanAccountSession = (LoanAccount)request.getSession().getAttribute("loanAccount");
        SavingAccount savingAccountGuarantor1Session = (SavingAccount)request.getSession().getAttribute("guarantor1");

//        TODO:  create shortee, update minimum acc. balance on guarantor, call center log, GL entry
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        loanAccountService.createLoanAccount(user,
                loanAccountSession, savingAccountGuarantor1Session);

        return "loanCreated";
    }



    private String displayLoanBilanzNoInterest(long id, ModelMap model, LoanAccountTransaction loanAccountTransaction) {
        Optional<LoanAccount> loanAccount = loanAccountService.findById(id);
        LoanAccount aLoanAccount = loanAccount.get();
        List<LoanAccountTransaction> loanAccountTransactionList = aLoanAccount.getLoanAccountTransaction();
        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccountTransactionList, true);
        model.put("name", getLoggedInUserName());
        model.put("loanBilanzList", loanBilanzByUserList);
        loanAccountTransaction.setLoanAccount(aLoanAccount);
        model.put("loanAccountTransaction", loanAccountTransaction);
        return "loanBilanzNoInterest";
    }


    @GetMapping(value = "/createLoanAccountReceiptPdf/{id}")
    public void loanReceiptPDF(@PathVariable("id") long id, ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Optional<LoanAccountTransaction> aLoanAccountTransaction = loanAccountTransactionService.findById(new Long(id));
        LoanAccountTransaction LoanAccountTransaction = aLoanAccountTransaction.get();
        response.setHeader("Content-disposition", "attachment;filename=" + "ReceiptLoan_"+LoanAccountTransaction.getReference()+".pdf");
        String htmlInput = pdfService.generateLoanTransactionReceiptPDF( LoanAccountTransaction, initSystemService.findAll() );
        generateByteOutputStream(response, htmlInput);

    }


    @GetMapping(value = "/statementLoanPDF/{id}")
    public void generateStatementLoanPDF(@PathVariable("id") long id, ModelMap model,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        Optional<LoanAccount> loanAccount = loanAccountService.findById(new Long(id));
        LoanAccount loanAccount1 = loanAccount.get();
        response.setHeader("Content-disposition","attachment;filename="+ "Loan_("+loanAccount1.getAccountNumber()+").pdf");

        LoanBilanzList loanBilanzByUserList = loanAccountService.
                calculateAccountBilanz(loanAccount1.getLoanAccountTransaction(),true);
        RuntimeSetting runtimeSetting = (RuntimeSetting)request.getSession().getAttribute("runtimeSettings");
        String htmlInput = pdfService.generatePDFLoanBilanzList(loanBilanzByUserList, loanAccount1,
                runtimeSetting.getLogo(), initSystemService.findAll());
        generateByteOutputStream(response, htmlInput);
    }


    @PostMapping(value = "/registerLoanAccountTransactionForm")
    public String registerLoanAccountTransactionForm(ModelMap model, @ModelAttribute("loanAccountTransaction")
            LoanAccountTransaction loanAccountTransaction, HttpServletRequest request) {
        loanAccountTransaction.setWithdrawalDeposit(1);
        String loanAccountId = request.getParameter("loanAccountId");
        Optional<LoanAccount> loanAccount = loanAccountService.findById(new Long(loanAccountId));
        LoanAccount aLoanAccount = loanAccount.get();

        loanAccountTransaction.setLoanAccount(aLoanAccount);
        loanAccountTransaction.setCreatedDate(LocalDateTime.now());
        loanAccountTransaction.setCreatedBy(getLoggedInUserName());
        loanAccountTransaction.setAccountOwner(aLoanAccount.getUser().getLastName() +", "+
                aLoanAccount.getUser().getLastName());
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        String error = "";

        if(StringUtils.isEmpty(loanAccountTransaction.getModeOfPayment() ) ){
            error = "Select Method of Payment - MOP";
            model.put("billSelectionError", error);
            loanAccountTransaction.setNotes(loanAccountTransaction.getNotes());
            return displayLoanBilanzNoInterest(new Long(loanAccountId), model, loanAccountTransaction);
        }

        if(loanAccountTransaction.getLoanAmount()<loanAccountTransaction.getLoanAccount().getMinimumPayment()){
            model.put("billSelectionError", "Please make minimum payment of "+ BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAccount().getMinimumPayment()));
            loanAccountTransaction.setNotes(loanAccountTransaction.getNotes());
            return displayLoanBilanzNoInterest(new Long(loanAccountId), model, loanAccountTransaction);
        }


        if ("CASH".equals(loanAccountTransaction.getModeOfPayment())) {
//            if (!checkBillSelectionMatchesEnteredAmount(loanAccountTransaction)) {
//                model.put("billSelectionError", "Bills Selection does not match entered amount");
//                loanAccountTransaction.setNotes(loanAccountTransaction.getNotes());
//                return displayLoanBilanzNoInterest(new Long(savingAccountId), model, loanAccountTransaction);
//            }

        }

        String modeOfPayment = request.getParameter("modeOfPayment");
        loanAccountService.createLoanAccountTransaction(loanAccountTransaction, aLoanAccount, modeOfPayment);

        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(aLoanAccount.getLoanAccountTransaction(), true);
        model.put("name", getLoggedInUserName());
        model.put("billSelectionInfo", BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) + " ---- PAYMENT HAS REGISTERED ----- ");
        model.put("loanBilanzList", loanBilanzByUserList);
        request.getSession().setAttribute("loanBilanzList", loanBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        loanAccountTransaction.setLoanAccount(aLoanAccount);
        resetLoansAccountTransaction(loanAccountTransaction); //reset BillSelection and amount
        loanAccountTransaction.setNotes("");

        request.getSession().setAttribute("loanAccountTransaction", loanAccountTransaction);
        model.put("loanAccountTransaction", loanAccountTransaction);
        return "loanBilanzNoInterest";
    }


    private void resetLoansAccountTransaction(LoanAccountTransaction sat) {
        sat.setLoanAmount(0);
        sat.setFifty(0);
        sat.setFiveHundred(0);
        sat.setFiveThousand(0);
        sat.setOneHundred(0);
        sat.setOneThousand(0);
        sat.setTenThousand(0);
        sat.setTwentyFive(0);
        sat.setTwoThousand(0);
    }

    private String addBillSelection(SavingAccountTransaction sat) {
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

    @GetMapping(value = "/showLoanAccountBilanz/{accountId}")
    public String showLoanAccountBilanz(@PathVariable("accountId") long accountId, ModelMap model) {
        LoanAccount loanAccount = loanAccountService.findById(accountId).get();
        if(!loanAccount.getAccountStatus().name().equals(BVMicroUtils.ACTIVE)){
            model.put("loanMustBeInActiveState", BVMicroUtils.LOAN_MUST_BE_IN_ACTIVE_STATE);
            return "userHome";
        }
        List<LoanAccountTransaction> loanAccountTransaction = loanAccount.getLoanAccountTransaction();
        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccountTransaction, true);
        model.put("name", getLoggedInUserName());
        model.put("loanBilanzList", loanBilanzByUserList);
        return "loanBilanz";
    }

    @GetMapping(value = "/loansPendingAction")
    public String loansPendingAction( ModelMap model ) {
        List<LoanAccount> loansPendingAction = loanAccountService.findLoansPendingAction();
        model.put("loansList", loansPendingAction);
        return "loans";
    }


    @GetMapping(value = "/loanDetails/{id}")
    public String loanDetails( @PathVariable("id") long id, ModelMap model ) {
        Optional<LoanAccount> byId = loanAccountService.findById(id);
        model.put("loan", byId.get());
        return "loanDetails";
    }


    @GetMapping(value = "/transferToCurrent/{id}")
    public String transferToCurrent(@PathVariable("id") long id, ModelMap model) {
        LoanAccount loanAccount = loanAccountService.findById(id).get();
        loanAccount.setAccountStatus(AccountStatus.ACTIVE);
        loanAccount.setApprovedBy(getLoggedInUserName());
        loanAccount.setApprovedDate(new Date());

        List<CurrentAccount> currentAccounts = loanAccount.
                getUser().getCurrentAccount();
        CurrentAccount currentAccount = null;
        if(null == currentAccounts || currentAccounts.size() == 0){

            model.put("error","PLEASE CREATE A CURRENT ACCOUNT FOR THIS CUSTOMER");
        }else{
            currentAccount = currentAccounts.get(0);
            currentAccountService.createCurrentAccountTransactionFromLoan(currentAccount, loanAccount);
            model.put("loanDetailsInfo","THIS LOAN ACCOUNT IS NOW ACTIVE. SUCCESSFULLY  TRANSFERRED FUNDS to CURRENT ACCOUNT. ");
        }
        model.put("loan",loanAccount);
        return "loanDetails";
    }



    @PostMapping(value = "/updateTrxDateForm")
    public String updateTrxDateForm(ModelMap model, @ModelAttribute("loanAccountTransaction")
            LoanAccountTransaction loanAccountTransaction, HttpServletRequest request) {
        String transaction = request.getParameter("transactionID");
        String dateChange = request.getParameter("dateChange");
        LoanAccountTransaction loanAccountTransaction1 = loanAccountTransactionService.updateDateForTest(transaction, dateChange);



        return showLoanAccountBilanz(loanAccountTransaction1.getLoanAccount().getId(), model);
    }

}