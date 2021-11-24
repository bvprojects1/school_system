package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.CurrentBilanz;
import com.bitsvalley.micro.webdomain.CurrentBilanzList;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CurrentAccountService extends SuperService {


    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    CurrentAccountTransactionRepository currentAccountTransactionRepository;

    @Autowired
    CurrentAccountRepository currentAccountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private InterestService interestService;

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private LoanAccountService loanAccountService;

    @Autowired
    private BranchService branchService;

    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public int findAllSavingAccountCount() {
        return savingAccountRepository.findAllCount();
    }

    public void createCurrentAccount(CurrentAccount currentAccount, User user) {

        long count = currentAccountRepository.count();

        currentAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(currentAccount.getCountry(),
                currentAccount.getProductCode(), currentAccount.getBranchCode(), count)); //TODO: Collision
        currentAccount.setAccountStatus(AccountStatus.ACTIVE);
        currentAccount.setCreatedBy(getLoggedInUserName());
        currentAccount.setCreatedDate(new Date(System.currentTimeMillis()));
        currentAccount.setLastUpdatedBy(getLoggedInUserName());
        currentAccount.setAccountLocked(false);
        currentAccount.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        AccountType savingAccountType = accountTypeRepository.findByNumber(currentAccount.getProductCode());
        currentAccount.setAccountType(savingAccountType);

        currentAccount.setUser(user);
        currentAccountRepository.save(currentAccount);

        user = userRepository.findById(user.getId()).get();
        user.getCurrentAccount().add(currentAccount);
        userService.saveUser(user);
//
//        //TODO: Move to callCenter service
//        callCenterService.callCenterUpdate(savingAccount);

    }


    public CurrentBilanzList getCurrentAccountByUser(User user, boolean calculateInterest) {
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

    @Transactional
    public void createCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {

        currentAccountTransaction.setCreatedBy(getLoggedInUserName());
        currentAccountTransaction.setCreatedDate(LocalDateTime.now());
        currentAccountTransaction.setReference(BVMicroUtils.getSaltString());

        currentAccountTransactionRepository.save(currentAccountTransaction);

//        generalLedgerService.updateCurrentAccountTransaction(currentAccountTransaction);
    }

    @Transactional
    public void createCurrentAccountTransaction(LoanAccountTransaction loanAccountTransaction) {

        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        List<CurrentAccount> currentAccounts = loanAccountTransaction.getLoanAccount().
                getUser().getCurrentAccount();
        CurrentAccount currentAccount = currentAccounts.get(0);

        CurrentAccountTransaction currentAccountTransaction = new CurrentAccountTransaction();
        currentAccountTransaction.setCurrentAmount(loanAccountTransaction.getLoanAmount());
        currentAccountTransaction.setCurrentAmountInLetters(BVMicroUtils.TRANSFER);
        currentAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
        currentAccountTransaction.setBranch(branchInfo.getId());
        currentAccountTransaction.setBranchCode(branchInfo.getCode());
        currentAccountTransaction.setBranchCountry(branchInfo.getCountry());
        currentAccountTransaction.setCurrentAccount(currentAccount);
        currentAccountTransaction.setNotes("Transfer, loan init payment "+ loanAccountTransaction.getLoanAccount().getAccountNumber());
        currentAccountTransaction.setReference(BVMicroUtils.getSaltString());
        currentAccountTransaction.setCreatedBy(getLoggedInUserName());
        currentAccountTransaction.setCreatedDate(LocalDateTime.now());
        currentAccountTransactionRepository.save(currentAccountTransaction);
        currentAccount.getCurrentAccountTransaction().add(currentAccountTransaction);
        currentAccountRepository.save(currentAccount);

//        generalLedgerService.updateCurrentAccountTransaction(currentAccountTransaction);?

    }


    public Optional<CurrentAccount> findById(long id) {
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findById(id);
        return currentAccount;
    }

    public void save(CurrentAccount save) {
        currentAccountRepository.save(save);
    }


    public CurrentBilanzList getCurrentBilanzByUser(User user, boolean calculateInterest) {
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


    public CurrentBilanzList calculateAccountBilanz(
            List<CurrentAccountTransaction> currentAccountTransactions,
            boolean calculateInterest) {
        double totalSaved = 0.0;
        double currentSaved = 0.0;
        double savingAccountTransactionInterest = 0.0;

        CurrentBilanzList currentBilanzsList = new CurrentBilanzList();

        for (int k = 0; k < currentAccountTransactions.size(); k++) {
            final CurrentAccountTransaction currentAccountTransaction = currentAccountTransactions.get(k);
            CurrentBilanz currentBilanz = new CurrentBilanz();
//                    if(savingAccountTransaction.getSavingAmount() <= 0){
//                        //calculate negative saving interest
//                    }else{
            currentBilanz = calculateInterest(currentAccountTransaction, calculateInterest);
//                    }
            currentSaved = currentSaved + currentAccountTransaction.getCurrentAmount();
            currentBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
            currentBilanzsList.getCurrentBilanzList().add(currentBilanz);
            totalSaved = totalSaved + currentAccountTransaction.getCurrentAmount();
            if (calculateInterest) {
                savingAccountTransactionInterest = savingAccountTransactionInterest +
                        interestService.calculateInterestAccruedMonthCompounded(
                                currentAccountTransaction.getCurrentAccount().getInterestRate(),
                                currentAccountTransaction.getCreatedDate(),
                                currentAccountTransaction.getCurrentAmount());
                currentBilanzsList.setTotalCurrentInterest(BVMicroUtils.formatCurrency(savingAccountTransactionInterest));
            }
        }
        currentBilanzsList.setTotalCurrent(BVMicroUtils.formatCurrency(totalSaved));

//        Collections.reverse(currentBilanzsList.getCurrentBilanzList());
        return currentBilanzsList;
    }


    private CurrentBilanzList calculateUsersInterest(ArrayList<User> users, boolean calculateInterest) {
        double totalSaved = 0.0;
        double currentSaved = 0.0;
        double currentAccountTransactionInterest = 0.0;
        CurrentBilanzList currentBilanzList = new CurrentBilanzList();
        for (int i = 0; i < users.size(); i++) {
            List<CurrentAccount> currentAccounts = users.get(i).getCurrentAccount();
            List<CurrentAccountTransaction> currentAccountTransactions = new ArrayList<CurrentAccountTransaction>();
            for (int j = 0; j < currentAccounts.size(); j++) {
                CurrentAccount currentAccount = currentAccounts.get(j);

//                boolean defaultedPayments = checkDefaultLogic(currentAccount);
//                currentAccount.setDefaultedPayment(defaultedPayments); //TODO:defaultLogic

                currentAccountTransactions = currentAccount.getCurrentAccountTransaction();
                double accountTotalSaved = 0.0;
                for (int k = 0; k < currentAccountTransactions.size(); k++) {
                    final CurrentAccountTransaction currentAccountTransaction = currentAccountTransactions.get(k);
//                    if (savingAccountTransaction.getSavingAmount() <= 0)
//                        continue;
                    CurrentBilanz currentBilanz = calculateInterest(currentAccountTransaction, calculateInterest);
                    currentSaved = currentSaved + currentAccountTransaction.getCurrentAmount();
                    currentBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
                    currentBilanzList.getCurrentBilanzList().add(currentBilanz);
                    totalSaved = totalSaved + currentAccountTransaction.getCurrentAmount();
                    accountTotalSaved = accountTotalSaved + currentAccountTransaction.getCurrentAmount();
                    currentAccountTransactionInterest = currentAccountTransactionInterest +
                            interestService.calculateInterestAccruedMonthCompounded(
                                    currentAccountTransaction.getCurrentAccount().getInterestRate(),
                                    currentAccountTransaction.getCreatedDate(),
                                    currentAccountTransaction.getCurrentAmount());
//                    }
                }
                currentAccount.setAccountBalance(accountTotalSaved);
//                if(!defaultedPayments){
//                    boolean minBalance = checkMinBalanceLogin(currentAccount)?true:false;
//                        currentAccount.setDefaultedPayment(minBalance);// Minimum balance check
//                }
                currentAccountRepository.save(currentAccount);
            }
        }

        currentBilanzList.setTotalCurrent(BVMicroUtils.formatCurrency(totalSaved));
        currentBilanzList.setTotalCurrentInterest(BVMicroUtils.formatCurrency(currentAccountTransactionInterest));
//        Collections.reverse(currentBilanzList.getCurrentBilanzList());
        return currentBilanzList;
    }

    private boolean checkMinBalanceLogin( SavingAccount savingAccount) {

        if (savingAccount.getAccountMinBalance() > savingAccount.getAccountBalance()) {
            callCenterService.saveCallCenterLog("",savingAccount.getUser().getUserName(),savingAccount.getAccountNumber(),"Minimum Balance not met for this account");
            return true;
        }

        return false;
    }


    private CurrentBilanz calculateInterest(CurrentAccountTransaction currentAccountTransaction, boolean calculateInterest) {
        CurrentBilanz currentBilanz = new CurrentBilanz();
        currentBilanz.setId("" + currentAccountTransaction.getId());
        currentBilanz.setCreatedBy(currentAccountTransaction.getCreatedBy());
        currentBilanz.setReference(currentAccountTransaction.getReference());
        currentBilanz.setAgent(currentAccountTransaction.getCreatedBy());
        currentBilanz.setInterestRate("" + currentAccountTransaction.getCurrentAccount().getInterestRate());
        currentBilanz.setCurrentAmount(BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()));
        currentBilanz.setCreatedDate(BVMicroUtils.formatDateTime(currentAccountTransaction.getCreatedDate()));
        currentBilanz.setNotes(currentAccountTransaction.getNotes());
        currentBilanz.setAccountNumber(currentAccountTransaction.getCurrentAccount().getAccountNumber());
        currentBilanz.setNoOfDays(calculateNoOfDays(currentAccountTransaction.getCreatedDate()));
        currentBilanz.setModeOfPayment(currentAccountTransaction.getModeOfPayment());
        currentBilanz.setAccountOwner(currentAccountTransaction.getAccountOwner());
        currentBilanz.setBranch(currentAccountTransaction.getCurrentAccount().getBranchCode());

        if (calculateInterest) {
            currentBilanz.setInterestAccrued(
                    BVMicroUtils.formatCurrency(
                            interestService.calculateInterestAccruedMonthCompounded(
                                    currentAccountTransaction.getCurrentAccount().getInterestRate(),
                                    currentAccountTransaction.getCreatedDate(),
                                    currentAccountTransaction.getCurrentAmount())));
        }
        return currentBilanz;
    }

    private String calculateNoOfDays(LocalDateTime createdDate) {
        long noOfDays = createdDate.until(LocalDateTime.now(), ChronoUnit.DAYS);
        return "" + noOfDays;
    }


    public boolean checkDefaultLogic(SavingAccount savingAccount) {

//        if(savingAccount.getAccountSavingType().getName().equals("GENERAL SAVINGS")){
        List<SavingAccountTransaction> savingAccountTransactionList = savingAccount.getSavingAccountTransaction();

        Date createdDate = savingAccount.getCreatedDate();
        Date currentDate = new Date(System.currentTimeMillis());

        Calendar currentDateCal = GregorianCalendar.getInstance();
        currentDateCal.setTime(currentDate);

        Calendar createdCalenderCal = GregorianCalendar.getInstance();
        createdCalenderCal.setTime(createdDate);

        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(LocalDate.parse(createdCalenderCal.get(GregorianCalendar.YEAR) + "-" + padding(createdCalenderCal.get(GregorianCalendar.MONTH)) + "-" + padding(createdCalenderCal.get(GregorianCalendar.DAY_OF_MONTH)))),
                YearMonth.from(LocalDate.parse(currentDateCal.get(GregorianCalendar.YEAR) + "-" + padding(currentDateCal.get(GregorianCalendar.MONTH)) + "-" + padding(currentDateCal.get(GregorianCalendar.DAY_OF_MONTH)))));

        if (monthsBetween >= savingAccountTransactionList.size()) {
            CallCenter callCenter = new CallCenter();
            callCenter.setNotes(BVMicroUtils.REGULAR_MONTHLY_PAYMENT_MISSING);
            callCenter.setDate(new Date(System.currentTimeMillis()));
            callCenter.setReference(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getLastName());
            callCenter.setAccountNumber(savingAccount.getAccountNumber());
            callCenterService.callCenterSavingAccount(savingAccount);
            return true;
        }

/*            if (savingAccount.getAccountMinBalance() > totalSaved){
                CallCenter callCenter = new CallCenter();
                callCenter.setNotes("Minimum payment not met");
                callCenter.setDate(new Date(System.currentTimeMillis()));
                callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " "+ savingAccount.getUser().getLastName());
                callCenter.setAccountNumber(savingAccount.getAccountNumber());
                callCenterRepository.save(callCenter);
                return true;
            }*/

//        }
        return false;
    }

    private String padding(int i) {
        if (i < 10)
            return "" + 0 + 1;
        return "" + i;
    }

    public String withdrawalAllowed(CurrentAccountTransaction currentTransaction) {
        String error = "";
        error = minimumSavingRespected(currentTransaction);
        return error;
    }

    private String minimumSavingRespected(CurrentAccountTransaction currentTransaction) {
        double futureBalance = getAccountBalance(currentTransaction.getCurrentAccount()) + currentTransaction.getCurrentAmount();
        if (currentTransaction.getCurrentAccount().getAccountMinBalance() > futureBalance) {
            return "Account will fall below Minimum Savings amount";
        }
        return null;
    }

    public double getAccountBalance(CurrentAccount savingAccount) {
        double total = 0.0;
        List<CurrentAccountTransaction> savingAccountTransactions = savingAccount.getCurrentAccountTransaction();
        for (CurrentAccountTransaction tran : savingAccountTransactions) {
            total = tran.getCurrentAmount() + total;
        }
        return total;
    }

//    public void transferFromSavingToLoan(String fromAccountNumber,
//                                         String toAccountNumber,
//                                         double transferAmount,
//                                         String notes) {
//        LocalDateTime now = LocalDateTime.now();
//        String loggedInUserName = getLoggedInUserName();
//        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);
//
//        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
//        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
//        savingAccountTransaction.setNotes(notes);
//        savingAccountTransaction.setSavingAccount(savingAccount);
//        savingAccountTransaction.setSavingAmount(transferAmount*-1);
//        savingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
//        savingAccountTransaction.setBranch(branchInfo.getId());
//        savingAccountTransaction.setBranchCode(branchInfo.getCode());
//        savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
//        createSavingAccountTransaction(savingAccountTransaction);
//
//        LoanAccount loanAccount = loanAccountService.findByAccountNumber(toAccountNumber);
//        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
//        loanAccountTransaction.setLoanAccount(loanAccount);
//
//        loanAccountTransaction.setCreatedDate(now);
//        loanAccountTransaction.setCreatedBy(loggedInUserName);
//        loanAccountTransaction.setNotes(notes);
//
//        loanAccountTransaction.setBranch(branchInfo.getId());
//        loanAccountTransaction.setBranchCode(branchInfo.getCode());
//        loanAccountTransaction.setBranchCountry(branchInfo.getCountry());
//        loanAccountTransaction.setAmountReceived(transferAmount);
//        loanAccountTransaction.setAccountOwner(loanAccount.getUser().getLastName() +", "+
//                loanAccount.getUser().getLastName());
//        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
//        loanAccountService.createLoanAccountTransaction(loanAccountTransaction, loanAccount, BVMicroUtils.TRANSFER);
//
//        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
//        savingAccountRepository.save(savingAccount);
//
//        //Update shortee accounts min balance
//        List<ShorteeAccount> shorteeAccounts = loanAccount.getShorteeAccounts();
//        ShorteeAccount shorteeAccount = shorteeAccounts.get(0);
//        SavingAccount shorteeSavingAccount = shorteeAccount.getSavingAccount();
//        shorteeSavingAccount.setAccountMinBalance(shorteeSavingAccount.getAccountMinBalance()-loanAccountTransaction.getAmountReceived());
//        savingAccountRepository.save(shorteeSavingAccount);
//
//    }

//    public SavingAccount transferFromDebitToDebit(String fromAccountNumber,
//                                         String toAccountNumber,
//                                         double transferAmount,
//                                         String notes) {
//
//        SavingAccount toSavingAccount = findByAccountNumber(toAccountNumber);
//        if(toSavingAccount == null ){
//            return null;
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        String loggedInUserName = getLoggedInUserName();
//        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);
//
//        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
//        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
//        savingAccountTransaction.setNotes(notes);
//        savingAccountTransaction.setSavingAccount(savingAccount);
//        savingAccountTransaction.setSavingAmount(transferAmount*-1);
//        savingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
//        savingAccountTransaction.setBranch(branchInfo.getId());
//        savingAccountTransaction.setBranchCode(branchInfo.getCode());
//        savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
//        createSavingAccountTransaction(savingAccountTransaction);
//        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
//        savingAccountRepository.save(savingAccount);
//
//        SavingAccountTransaction toSavingAccountTransaction = new SavingAccountTransaction();
//        toSavingAccountTransaction.setNotes(notes);
//        toSavingAccountTransaction.setSavingAccount(toSavingAccount);
//        toSavingAccountTransaction.setSavingAmount(transferAmount);
//        toSavingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
//        toSavingAccountTransaction.setBranch(branchInfo.getId());
//        toSavingAccountTransaction.setBranchCode(branchInfo.getCode());
//        toSavingAccountTransaction.setBranchCountry(branchInfo.getCountry());
//        createSavingAccountTransaction(toSavingAccountTransaction);
//        toSavingAccount.getSavingAccountTransaction().add(toSavingAccountTransaction);
//        savingAccountRepository.save(toSavingAccount);
//
//        return savingAccount;
//    }

}
