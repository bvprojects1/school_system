package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.*;
import com.bitsvalley.micro.utils.Amortization;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
    CallCenterRepository callCenterRepository;

    @Autowired
    LoanAccountService loanAccountService;

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
    GeneralLedgerService generalLedgerService;

    @Autowired
    PdfService pdfService;

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
        return "amortizationReport";
    }


        @PostMapping(value = "/registerLoanAccountForm")
    public String registerLoanAccountForm(@ModelAttribute("loanAccount") LoanAccount loanAccount, ModelMap model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);
        user = userRepository.findById(user.getId()).get();

        Branch branchInfo = getBranchInfo(getLoggedInUserName());
        loanAccount.setBranchCode(new Long(branchInfo.getId()).toString());
        loanAccount.setBranchCode(branchInfo.getCode());
        loanAccount.setCountry(branchInfo.getCountry());

        loanAccount.setTotalInterestOnLoan(0);
        double monthlyPayment = interestService.monthlyPaymentAmortisedPrincipal(loanAccount.getInterestRate(),
        loanAccount.getTermOfLoan(),loanAccount.getLoanAmount());

        Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
                loanAccount.getInterestRate()*.01,
                loanAccount.getTermOfLoan(),monthlyPayment);

        loanAccount.setMonthlyPayment(new Double(monthlyPayment).intValue());

//         interestService.calculateInterestAccruedMonthCompounded(
//              loanAccount.getInterestRate(),loanAccount.getTermOfLoan(),loanAccount.getLoanAmount()));

        AccountType accountType = accountTypeService.getAccountTypeByProductCode(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);

        if(!StringUtils.isEmpty(loanAccount.getGuarantorAccountNumber1())){
            SavingAccount byAccountNumber1 = savingAccountService.findByAccountNumber(loanAccount.getGuarantorAccountNumber1());
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
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        return displayLoanBilanzNoInterest(id, model, loanAccountTransaction);
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
        LoanAccount loanAccountReturn = shorteeService.createLoanAccount(user,
                loanAccountSession, savingAccountGuarantor1Session);

        return "loanCreated";
    }



    private String displayLoanBilanzNoInterest(long id, ModelMap model, LoanAccountTransaction loanAccountTransaction) {
        Optional<LoanAccount> loanAccount = loanAccountService.findById(id);
        LoanAccount aLoanAccount = loanAccount.get();
        List<LoanAccountTransaction> loanAccountTransactionList = aLoanAccount.getLoanAccountTransaction();
        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccountTransactionList, false);
        model.put("name", getLoggedInUserName());
        model.put("loanBilanzList", loanBilanzByUserList);
        loanAccountTransaction.setLoanAccount(aLoanAccount);
        model.put("loanAccountTransaction", loanAccountTransaction);
        return "loanBilanzNoInterest";
    }


    @GetMapping(value = "/printLoanAccountDetails/{id}")
    public String printLoanAccountDetails(@PathVariable("id") long id, ModelMap model,
                                            @ModelAttribute("savingAccountTransaction") SavingAccountTransaction savingAccountTransaction,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String username = getLoggedInUserName();
//        Optional<SavingAccount> savingAccount = savingAccountService.findById(new Long(id));
//        SavingBilanzList savingBilanzByUserList = savingAccountService.
//                calculateAccountBilanz(savingAccount.get().getSavingAccountTransaction(), false);
//        String htmlInput = null;
////                pdfService.generatePDFSavingBilanzList(savingBilanzByUserList, username);
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-disposition", "attachment;filename=" + "statementPDF.pdf");
//        ByteArrayOutputStream byteArrayOutputStream = null;
//        ByteArrayInputStream byteArrayInputStream = null;
//        try {
//            OutputStream responseOutputStream = response.getOutputStream();
//            byteArrayOutputStream = pdfService.generatePDF(htmlInput, response);
//            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//            int bytes;
//            while ((bytes = byteArrayInputStream.read()) != -1) {
//                responseOutputStream.write(bytes);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            byteArrayInputStream.close();
//            byteArrayOutputStream.flush();
//            byteArrayOutputStream.close();
//        }
//        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        return "userHome";
    }


    @PostMapping(value = "/registerLoanAccountTransactionForm")
    public String registerLoanAccountTransactionForm(ModelMap model, @ModelAttribute("loanAccountTransaction")
            LoanAccountTransaction loanAccountTransaction, HttpServletRequest request) {
        String loanAccountId = request.getParameter("loanAccountId");


        Optional<LoanAccount> loanAccount = loanAccountService.findById(new Long(loanAccountId));
        loanAccountTransaction.setLoanAccount( loanAccount.get() );
        loanAccountTransaction.setCreatedDate(LocalDateTime.now());
        loanAccountTransaction.setCreatedBy(getLoggedInUserName());
        loanAccountTransaction.setAccountOwner(loanAccount.get().getUser().getLastName() +", "+
                loanAccount.get().getUser().getLastName());
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
        User user = (User) request.getSession().getAttribute(BVMicroUtils.CUSTOMER_IN_USE);

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
        loanAccountTransaction.setModeOfPayment(modeOfPayment);
        Branch branchInfo = getBranchInfo(getLoggedInUserName());

        loanAccountTransaction.setBranch(branchInfo.getId());
        loanAccountTransaction.setBranchCode(branchInfo.getCode());
        loanAccountTransaction.setBranchCountry(branchInfo.getCountry());

        loanAccountTransaction.setLoanAccount(loanAccount.get());
        if (loanAccount.get().getLoanAccountTransaction() != null) {
            loanAccount.get().getLoanAccountTransaction().add(loanAccountTransaction);
        } else {
            loanAccount.get().setLoanAccountTransaction(new ArrayList<LoanAccountTransaction>());
            loanAccount.get().getLoanAccountTransaction().add(loanAccountTransaction);
        }
        loanAccountService.save(loanAccount.get());

        CallCenter callCenter = new CallCenter();
        callCenter.setUserName(loanAccount.get().getUser().getUserName());
        callCenter.setAccountNumber(loanAccount.get().getAccountNumber());
        callCenter.setDate(new Date(System.currentTimeMillis()));
        callCenter.setNotes(loanAccountTransaction.getModeOfPayment() +
                " Payment/ Deposit made into account amount: " + loanAccountTransaction.getLoanAmount());
        callCenterRepository.save(callCenter);

        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccount.get().getLoanAccountTransaction(), false);
        model.put("name", getLoggedInUserName());
        model.put("billSelectionError", BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()) + " ---- PAYMENT HAS REGISTERED ----- ");
        model.put("loanBilanzList", loanBilanzByUserList);
        request.getSession().setAttribute("loanBilanzList", loanBilanzByUserList);
        Optional<User> byId = userRepository.findById(user.getId());
        request.getSession().setAttribute(BVMicroUtils.CUSTOMER_IN_USE, byId.get());
        loanAccountTransaction.setLoanAccount(loanAccount.get());
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

    private boolean checkBillSelectionMatchesEnteredAmount(SavingAccountTransaction sat) {
        boolean match = (sat.getSavingAmount() == (sat.getTenThousand() * 10000) +
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
    public String showLoanAccountBilanz(@PathVariable("accountId") long accountId, ModelMap model, HttpServletRequest request) {
        Optional<LoanAccount> byId = loanAccountService.findById(accountId);
        List<LoanAccountTransaction> loanAccountTransaction = byId.get().getLoanAccountTransaction();
        LoanBilanzList loanBilanzByUserList = loanAccountService.calculateAccountBilanz(loanAccountTransaction, true);
        model.put("name", getLoggedInUserName());
        model.put("loanBilanzList", loanBilanzByUserList);
        return "loanBilanz";
    }

}