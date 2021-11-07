package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
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

    @Autowired
    RuntimePropertiesRepository runtimePropertiesRepository;

    @Autowired
    private ShorteeAccountRepository shorteeAccountRepository;

    @Autowired
    BranchService branchService;

    public LoanAccount findByAccountNumber(String accountNumber) {
        return loanAccountRepository.findByAccountNumber(accountNumber);
    }

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
        shorteeSavingAccount.setAccountStatus(AccountStatus.SHORTEE_ACCOUNT);
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
        loanAccount.setAccountStatus(AccountStatus.PENDING_APPROVAL);

        Date dateNow = new Date(System.currentTimeMillis());
        loanAccount.setCreatedDate(dateNow);
        loanAccount.setApprovedDate(new Date(0));
        loanAccount.setApprovedBy( AccountStatus.PENDING_APPROVAL.name() );
        loanAccount.setLastUpdatedDate(dateNow);
        loanAccount.setLastPaymentDate(dateNow);

        AccountType accountType = accountTypeRepository.findByNumber(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);

        user = userRepository.findById(user.getId()).get();
        loanAccount.setUser(user);
        loanAccountRepository.save(loanAccount);

        user.getLoanAccount().add(loanAccount);
        userService.saveUser(user);

//        //Create a initial loan transaction of borrowed amount
//        LoanAccountTransaction loanAccountTransaction =
//                loanAccountTransactionService.createLoanAccountTransaction(loanAccount);

//        // Update new loan account transaction
//        generalLedgerService.updateLoanAccountTransaction(loanAccountTransaction);

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
        // ro
        save(aLoanAccount);

        callCenterService.saveCallCenterLog(loanAccountTransaction.getReference(), aLoanAccount.getUser().getUserName(), aLoanAccount.getAccountNumber(),
                "Loan account Payment received Amount: "+ loanAccountTransaction.getAmountReceived());

        generalLedgerService.updateGLWithLoanAccountRepayment(loanAccountTransaction,BVMicroUtils.DEBIT);
//        generalLedgerService.updateLoanRepayment();

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


    public LoanAccountTransaction updateInterestOwedPayment(LoanAccount loanAccount, LoanAccountTransaction loanAccountTransaction) {
        LocalDateTime transactionDate = loanAccountTransaction.getCreatedDate();
        LocalDateTime date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(loanAccount.getLastPaymentDate().getTime()), ZoneId.systemDefault());
        long days = date.until(transactionDate, ChronoUnit.DAYS);
        double interestOwed = loanAccount.getCurrentLoanAmount() * days * (loanAccount.getInterestRate() * .01 / 365);
        if (interestOwed <= loanAccountTransaction.getAmountReceived()) {

            loanAccountTransaction.setInterestPaid(interestOwed);
            loanAccountTransaction.setVatPercent( 0.195*interestOwed );

            loanAccountTransaction.setCurrentLoanAmount(
                    loanAccount.getCurrentLoanAmount() - (loanAccountTransaction.getAmountReceived() - interestOwed - loanAccountTransaction.getVatPercent()));

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


        return loanAccountTransaction;
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
            loanBilanz = calculateInterest(loanAccountTransaction, calculateInterest );
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
//        double totalCurrentLoan = 0.0;
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
        loanBilanz.setBranch(loanAccount.getBranchCode());
        loanBilanz.setMonthYearOfLastPayment(calculateMonthOfLastPayment(loanAccount.getCreatedDate(),
                loanAccount.getTermOfLoan()));
        loanBilanz.setInterestAccrued(BVMicroUtils.formatCurrency(loanAccountTransaction.getInterestPaid()));
        loanBilanz.setVatPercent(BVMicroUtils.formatCurrency(loanAccountTransaction.getVatPercent()));// Configure
        loanBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(loanAccountTransaction.getCurrentLoanAmount()));
        loanBilanz.setAmountReceived(BVMicroUtils.formatCurrency(loanAccountTransaction.getAmountReceived()));
        loanBilanz.setMonthlyPayment(loanAccount.getMonthlyPayment() + "");
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

    private String padding(int i) {
        if (i < 10)
            return "" + 0 + 1;
        return "" + i;
    }

    public List<LoanAccount> findLoansPendingAction() {
        List<LoanAccount> byStatusNotActive = loanAccountRepository.findByStatusNotActive();
        return byStatusNotActive;
    }

}
