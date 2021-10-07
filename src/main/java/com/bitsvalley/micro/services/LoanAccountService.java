package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.Amortization;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LoanBilanz;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanAccountService extends SuperService {

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private LoanAccountTransactionService loanAccountTransactionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private InterestService interestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private LoanAccountTransactionRepository loanAccountTransactionRepository;

    @Autowired
    private SavingAccountService savingAccountService;

    public LoanAccount findByAccountNumber(String accountNumber) {
        return loanAccountRepository.findByAccountNumber(accountNumber);
    }

    @Autowired
    private ShorteeAccountRepository shorteeAccountRepository;

    @Autowired
    BranchService branchService;

    @NotNull
    @Transactional
    public LoanAccount createLoanAccount(User user, LoanAccount loanAccount,
                                         SavingAccount savingAccountGuarantor) {
        Date createdDate = new Date();
        String loggedInUserName = getLoggedInUserName();

        ShorteeAccount shorteeAccount = new ShorteeAccount();
        SavingAccount shorteeSavingAccount = savingAccountService.findByAccountNumber(
                savingAccountGuarantor.getAccountNumber());
        shorteeAccount.setSavingAccount(shorteeSavingAccount);

        shorteeSavingAccount.setAccountMinBalance(
                shorteeSavingAccount.getAccountMinBalance() + loanAccount.getGuarantor1Amount1());
        shorteeSavingAccount.setLastUpdatedDate(createdDate);
        shorteeSavingAccount.setLastUpdatedBy(loggedInUserName);
        shorteeSavingAccount.setAccountStatus(AccountStatus.ACTIVE);
        shorteeSavingAccount.setAccountLocked(true);
        savingAccountService.save(shorteeSavingAccount);

        callCenterService.callCenterShorteeUpdate(shorteeSavingAccount, loanAccount.getGuarantor1Amount1());

        shorteeAccount.setAmountShortee(loanAccount.getGuarantor1Amount1());
        shorteeAccount.setCreatedDate(createdDate);
        shorteeAccount.setLastUpdatedDate(createdDate);

        shorteeAccount.setCreatedBy(loggedInUserName);
        shorteeAccount.setLastUpdatedBy(loggedInUserName);
        shorteeAccountRepository.save(shorteeAccount);

        ArrayList<ShorteeAccount> listShorteeAccount = new ArrayList<ShorteeAccount>();
        listShorteeAccount.add(shorteeAccount);
        loanAccount.setShorteeAccounts(listShorteeAccount);
        double payment = interestService.monthlyPaymentAmortisedPrincipal(loanAccount.getInterestRate(),
                loanAccount.getTermOfLoan(), loanAccount.getLoanAmount());
        loanAccount.setMonthlyPayment(payment);

        createLoanAccount(loanAccount, user);
        return loanAccount;
    }


    @Transactional
    public void createLoanAccount(LoanAccount loanAccount, User user) {
        int allCount = loanAccountRepository.findAllCount();

        String loggedInUserName = getLoggedInUserName();
        User aUser = userRepository.findByUserName(loggedInUserName);
        loanAccount.setCreatedBy(loggedInUserName);
        loanAccount.setLastUpdatedBy(loggedInUserName);

        loanAccount.setCountry(aUser.getBranch().getCountry());
        loanAccount.setBranchCode(aUser.getBranch().getCode());
        loanAccount.setCurrentLoanAmount(loanAccount.getLoanAmount());
        loanAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(loanAccount.getCountry(),
                loanAccount.getProductCode(), loanAccount.getBranchCode(), allCount)); //TODO: Collision
        loanAccount.setAccountStatus(AccountStatus.ACTIVE);

        Date dateNow = new Date(System.currentTimeMillis());
        loanAccount.setCreatedDate(dateNow);
        loanAccount.setLastUpdatedDate(dateNow);
        loanAccount.setLastPaymentDate(dateNow);

        AccountType accountType = accountTypeRepository.findByNumber(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);

        user = userRepository.findById(user.getId()).get();
        loanAccount.setUser(user);
        loanAccountRepository.save(loanAccount);

        user.getLoanAccount().add(loanAccount);
        userService.saveUser(user);

        //Create a initial loan transaction of borrowed amount
        LoanAccountTransaction loanAccountTransaction =
                loanAccountTransactionService.createLoanAccountTransaction(loanAccount);

        // Update new loan account transaction
        generalLedgerService.updateLoanAccountTransaction(loanAccountTransaction);

        //Trace
        callCenterService.saveCallCenterLog("",
                loanAccount.getAccountNumber(), loanAccount.getAccountType().getName(), loanAccount.getLoanAmount() + "");
    }


    public void createLoanAccountTransaction(LoanAccountTransaction loanAccountTransaction, LoanAccount aLoanAccount, String modeOfPayment) {
        loanAccountTransaction.setModeOfPayment(modeOfPayment);
        Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());

        loanAccountTransaction.setBranch(branchInfo.getId());
        loanAccountTransaction.setBranchCode(branchInfo.getCode());
        loanAccountTransaction.setBranchCountry(branchInfo.getCountry());

        loanAccountTransaction.setLoanAccount(aLoanAccount);
        if (aLoanAccount.getLoanAccountTransaction() != null) {
            aLoanAccount.getLoanAccountTransaction().add(loanAccountTransaction);
        } else {
            aLoanAccount.setLoanAccountTransaction(new ArrayList<LoanAccountTransaction>());
            aLoanAccount.getLoanAccountTransaction().add(loanAccountTransaction);
        }
        updateInterestOwedPayment(aLoanAccount, loanAccountTransaction);

        save(aLoanAccount);

        callCenterService.saveCallCenterLog(loanAccountTransaction.getReference(), aLoanAccount.getUser().getUserName(), aLoanAccount.getAccountNumber(),
                "Loan account Payment received Amount: "+ loanAccountTransaction.getAmountReceived());

        generalLedgerService.updateLoanAccountTransaction(loanAccountTransaction);
    }

    public LoanBilanzList getLoanBilanzByUser(User user, boolean calculateInterest) {
        User aUser = null;
        if (null != user.getUserName()) {
            aUser = userRepository.findByUserName(user.getUserName());
        } else {
            aUser = userRepository.findById(user.getId()).get();
        }
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(aUser);
        return calculateUsersInterest(userList, calculateInterest);
    }


    public Optional<LoanAccount> findById(long id) {
        Optional<LoanAccount> loanAccount = loanAccountRepository.findById(id);
        return loanAccount;
    }

    public void save(LoanAccount save) {
        loanAccountRepository.save(save);
    }


    public LoanAccount updateInterestOwedPayment(LoanAccount loanAccount, LoanAccountTransaction loanAccountTransaction) {
        LocalDateTime transactionDate = loanAccountTransaction.getCreatedDate();
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(loanAccount.getLastPaymentDate().getTime()), ZoneId.systemDefault());
        long days = date.until(transactionDate, ChronoUnit.DAYS);
        double interestOwed = loanAccount.getCurrentLoanAmount() * days * (loanAccount.getInterestRate() * .01 / 365);
        if (interestOwed <= loanAccountTransaction.getAmountReceived()) {

            loanAccountTransaction.setCurrentLoanAmount(
                    loanAccount.getCurrentLoanAmount() - (loanAccountTransaction.getAmountReceived() - interestOwed));
            loanAccountTransaction.setInterestPaid(interestOwed);

            loanAccount.setCurrentLoanAmount(loanAccountTransaction.getCurrentLoanAmount());
            loanAccount.setTotalInterestOnLoan(loanAccount.getTotalInterestOnLoan() + interestOwed);
            Date lastPaymentDate = new Date();
            loanAccount.setLastPaymentDate(lastPaymentDate);
            loanAccount.setLastUpdatedDate(lastPaymentDate);
        }
//        else{ TODO: Negative scenario if amt paid is less than interest accrued
//            loanAccountTransaction.setCurrentLoanAmount(
//                    loanAccountTransaction.getAmountReceived() - interestOwed);
//            loanAccountTransaction.setInterestPaid(interestOwed);
//        }
        loanAccountTransactionRepository.save(loanAccountTransaction);
        return loanAccount;
    }


    public LoanBilanzList calculateAccountBilanz(
            List<LoanAccountTransaction> loanAccountTransactions,
            boolean calculateInterest) {
        double totalLoan = 0.0;
        String currentLoanBalance = "";
        double totalLoanAccountTransactionInterest = 0.0;

        LoanBilanzList loanBilanzsList = new LoanBilanzList();

        for (int k = 0; k < loanAccountTransactions.size(); k++) {
            final LoanAccountTransaction loanAccountTransaction = loanAccountTransactions.get(k);
            LoanBilanz loanBilanz = new LoanBilanz();
            loanBilanz = calculateInterest(loanAccountTransaction, calculateInterest);
            currentLoanBalance = loanBilanz.getCurrentBalance();
            loanBilanzsList.getLoanBilanzList().add(loanBilanz);
            totalLoan = totalLoan + loanAccountTransaction.getLoanAmount();
            if (calculateInterest) {
                totalLoanAccountTransactionInterest = totalLoanAccountTransactionInterest + loanAccountTransaction.getInterestPaid();
            }
        }
        loanBilanzsList.setTotalLoanInterest(BVMicroUtils.formatCurrency(totalLoanAccountTransactionInterest)); //TODO set total interest
        loanBilanzsList.setTotalLoan(BVMicroUtils.formatCurrency(totalLoan));
        loanBilanzsList.setCurrentLoanBalance(currentLoanBalance);
        Collections.reverse(loanBilanzsList.getLoanBilanzList());
        return loanBilanzsList;
    }


    private LoanBilanzList calculateUsersInterest(ArrayList<User> users, boolean calculateInterest) {
        double totalCurrentLoan = 0.0;
        double totalLoanAmount = 0.0;
        double loanAccountTransactionInterest = 0.0;
        LoanBilanzList loanBilanzsList = new LoanBilanzList();
        double currentLoanBalanceAllUserLoans = 0.0;
        for (int i = 0; i < users.size(); i++) {
            List<LoanAccount> loanAccounts = users.get(i).getLoanAccount();
            double currentLoanBalanceUserLoans = 0.0;
            List<LoanAccountTransaction> loanAccountTransactions = new ArrayList<LoanAccountTransaction>();
            for (int j = 0; j < loanAccounts.size(); j++) {
                LoanAccount loanAccount = loanAccounts.get(j);
                boolean defaultedPayments = checkDefaultLogic(loanAccount);
                loanAccount.setDefaultedPayment(defaultedPayments); //TODO:defaultLogic
                loanAccountTransactions = loanAccount.getLoanAccountTransaction();
                double currentLoanBalance = 0.0;
                for (int k = 0; k < loanAccountTransactions.size(); k++) {
                    final LoanAccountTransaction loanAccountTransaction = loanAccountTransactions.get(k);
//                    if (loanAccountTransaction.getLoanAmount() == 0)
//                        continue;
                    LoanBilanz loanBilanz = calculateInterest(loanAccountTransaction, calculateInterest);
                    totalLoanAmount = totalLoanAmount + loanAccountTransaction.getLoanAmount();
                    loanBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(totalLoanAmount));
                    loanBilanzsList.getLoanBilanzList().add(loanBilanz);
                    currentLoanBalance = loanAccountTransaction.getCurrentLoanAmount();
                }
//                loanAccount.setCurrentLoanAmount(totalCurrentLoan);
//                if (checkMinBalanceLogin(totalLoanAmount, loanAccount)) {
//                    loanAccount.setDefaultedPayment(true);// Minimum balance check
//                }
                currentLoanBalanceUserLoans = currentLoanBalanceUserLoans + currentLoanBalance;
                loanAccount.setCurrentLoanAmount(currentLoanBalance);
                loanAccountRepository.save(loanAccount);
            }
            currentLoanBalanceAllUserLoans = currentLoanBalanceAllUserLoans + currentLoanBalanceUserLoans;
            loanBilanzsList.setCurrentLoanBalance(BVMicroUtils.formatCurrency(currentLoanBalanceUserLoans));
        }
        loanBilanzsList.setTotalLoan(BVMicroUtils.formatCurrency(totalLoanAmount));
//        loanBilanzsList.setCurrentLoanBalanceAllUserLoans(BVMicroUtils.formatCurrency(currentLoanBalanceAllUserLoans));
        loanBilanzsList.setTotalLoanInterest(BVMicroUtils.formatCurrency(loanAccountTransactionInterest));
        Collections.reverse(loanBilanzsList.getLoanBilanzList());
        return loanBilanzsList;
    }


    private boolean checkMinBalanceLogin(double currentSaved, LoanAccount savingAccount) {

//        if(savingAccount.getAccountMinBalance() > currentSaved){
//            CallCenter callCenter = new CallCenter();
//            callCenter.setDate(new Date(System.currentTimeMillis()));
//            callCenter.setNotes("Minimum Balance not met for this account");
//            callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getLastName());
//            callCenter.setAccountNumber(savingAccount.getAccountNumber());
//            callCenterRepository.save(callCenter);
//            return true;
//        }

        return false;
    }


    private LoanBilanz calculateInterest(LoanAccountTransaction loanAccountTransaction, boolean calculateInterest) {
        LoanBilanz loanBilanz = new LoanBilanz();
        loanBilanz.setId("" + loanAccountTransaction.getId());
        LoanAccount loanAccount = loanAccountTransaction.getLoanAccount();
        loanBilanz.setAccountType(loanAccount.getAccountType().getName());
        loanBilanz.setCreatedBy(loanAccountTransaction.getCreatedBy());
        loanBilanz.setReference(loanAccountTransaction.getReference());
        loanBilanz.setAgent(loanAccountTransaction.getCreatedBy());
        loanBilanz.setInterestRate("" + loanAccount.getInterestRate());
        loanBilanz.setLoanAmount(BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()));
        loanBilanz.setCreatedDate(BVMicroUtils.formatDateTime(loanAccountTransaction.getCreatedDate()));
        loanBilanz.setNotes(loanAccountTransaction.getNotes());
        loanBilanz.setAccountNumber(loanAccount.getAccountNumber());
        loanBilanz.setNoOfDays(calculateNoOfDays(loanAccountTransaction.getCreatedDate()));
        loanBilanz.setModeOfPayment(loanAccountTransaction.getModeOfPayment());
        loanBilanz.setAccountOwner(loanAccountTransaction.getAccountOwner());
        loanBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(loanAccountTransaction.getCurrentLoanAmount()));
        loanBilanz.setBranch(loanAccount.getBranchCode());
        loanBilanz.setMonthYearOfLastPayment(calculateMonthOfLastPayment(loanAccount.getCreatedDate(),
                loanAccount.getTermOfLoan()));
        loanBilanz.setInterestAccrued(BVMicroUtils.formatCurrency(loanAccountTransaction.getInterestPaid()));
        loanBilanz.setAmountReceived(BVMicroUtils.formatCurrency(loanAccountTransaction.getAmountReceived()));

//        double monthlyPayment = interestService.monthlyPaymentAmortisedPrincipal(
//                loanAccount.getInterestRate(),
//                loanAccount.getTermOfLoan(),
//                loanAccount.getLoanAmount());

        loanBilanz.setMonthlyPayment(loanAccount.getMonthlyPayment() + "");
//        Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
//                loanAccount.getInterestRate()*.01,
//                loanAccount.getTermOfLoan(),monthlyPayment);

//        loanBilanz.setSetAmortizationSchedule(amortization.getAmortizationReport());
//        if (calculateInterest) {
//                double monthlyPayments = monthlyPayment;
//        }
        return loanBilanz;
    }

    private String calculateMonthOfLastPayment(Date createdDate, int termOfLoan) {
        LocalDate localDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(createdDate));
        localDate = localDate.plusMonths(termOfLoan);
        return localDate.getMonthValue() + "/" + localDate.getYear();
    }

    private String calculateNoOfDays(LocalDateTime createdDate) {
        long noOfDays = createdDate.until(LocalDateTime.now(), ChronoUnit.DAYS);
        return "" + noOfDays;
    }

//    private double calculateInterestAccruedMonthCompounded(LoanAccountTransaction loanAccountTransaction) {
////        = P [(1 + i/12)pow of NoOfMonths – 1]
////        P = principal, i = nominal annual interest rate in percentage terms, n = number of compounding periods
//        double interestPlusOne = (loanAccountTransaction.getLoanAccount().getInterestRate() * .01 * .0833333) + 1;
//        double temp = Math.pow(interestPlusOne, getNumberOfMonths(loanAccountTransaction.getCreatedDate()));
//        temp = temp - 1;
//        return loanAccountTransaction.getLoanAmount() * temp;
//    }

//    private Amortization calculateInterestAccruedMonthCompounded(LoanAccount loanAccount) {
//            Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
//                    loanAccount.getInterestRate(),
//                        loanAccount.getTermOfLoan());
//
//            return amortization;
//    }

//    private double calculateInterestAccruedYearCompounded(SavingAccountTransaction savingAccountTransaction){
////        = P [(1 + i)pow of n – 1]
////        P = principal, i = nominal annual interest rate in percentage terms, n = number of compounding periods
//        double interestPlusOne = (savingAccountTransaction.getSavingAccount().getInterestRate()*.01) + 1;
//        double temp = Math.pow(interestPlusOne,getNumberOfMonths(savingAccountTransaction.getCreatedDate()));
//        temp = temp - 1;
//        return savingAccountTransaction.getSavingAmount() * temp;
//    }

    private double getNumberOfMonths(LocalDateTime cretedDateInput) {
        double noOfMonths = 0.0;
        Duration diff = Duration.between(cretedDateInput, LocalDateTime.now());
        noOfMonths = diff.toDays() / 30;
        return Math.floor(noOfMonths);
    }

    public boolean checkDefaultLogic(LoanAccount savingAccount) {

//        if(savingAccount.getAccountSavingType().getName().equals("GENERAL SAVINGS")){
//            List<SavingAccountTransaction> savingAccountTransactionList = savingAccount.getSavingAccountTransaction();
//
//            Date createdDate = savingAccount.getCreatedDate();
//            Date currentDate = new Date(System.currentTimeMillis());
//
//            Calendar currentDateCal = GregorianCalendar.getInstance();
//            currentDateCal.setTime(currentDate);
//
//            Calendar createdCalenderCal = GregorianCalendar.getInstance();
//            createdCalenderCal.setTime(createdDate);
//
//            long monthsBetween = ChronoUnit.MONTHS.between(
//                    YearMonth.from(LocalDate.parse(createdCalenderCal.get(GregorianCalendar.YEAR)+"-"+padding(createdCalenderCal.get(GregorianCalendar.MONTH))+"-"+padding(createdCalenderCal.get(GregorianCalendar.DAY_OF_MONTH)))),
//                    YearMonth.from(LocalDate.parse(currentDateCal.get(GregorianCalendar.YEAR)+"-"+padding(currentDateCal.get(GregorianCalendar.MONTH))+"-"+padding(currentDateCal.get(GregorianCalendar.DAY_OF_MONTH)))));
//
//            if (monthsBetween >= savingAccountTransactionList.size()){
//                CallCenter callCenter = new CallCenter();
//                callCenter.setNotes(BVMicroUtils.REGULAR_MONTHLY_PAYMENT_MISSING);
//                callCenter.setDate(new Date(System.currentTimeMillis()));
//                callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " "+ savingAccount.getUser().getLastName());
//                callCenter.setAccountNumber(savingAccount.getAccountNumber());
//                callCenterRepository.save(callCenter);
//                return true;
//            }
//
//        }
        return false;
    }

    //
    private String padding(int i) {
        if (i < 10)
            return "" + 0 + 1;
        return "" + i;
    }
//
//    public String withdrawalAllowed(SavingAccountTransaction savingTransaction) {
//       String error = "";
//       error = minimumSavingRespected(savingTransaction);
//       return error;
//    }
//
//    private String minimumSavingRespected(SavingAccountTransaction savingTransaction) {
//        double futureBalance = getAccountBalance(savingTransaction.getSavingAccount()) + savingTransaction.getSavingAmount();
//        if(savingTransaction.getSavingAccount().getAccountMinBalance() > futureBalance ){
//            return "Account will fall below Minimum Savings amount";
//        }
//        return null;
//    }
//
//    public double getAccountBalance(SavingAccount savingAccount) {
//        double total = 0.0;
//        List<SavingAccountTransaction> savingAccountTransactions = savingAccount.getSavingAccountTransaction();
//        for (SavingAccountTransaction tran: savingAccountTransactions) {
//            total = tran.getSavingAmount() + total;
//        }
//        return total;
//    }
}
