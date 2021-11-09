package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
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
public class SavingAccountService extends SuperService {


    @Autowired
    private SavingAccountRepository savingAccountRepository;

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

    public void createSavingAccount(SavingAccount savingAccount, User user) {

        long count = savingAccountRepository.count();

        savingAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(savingAccount.getCountry(),
                savingAccount.getProductCode(), savingAccount.getBranchCode(), count)); //TODO: Collision
        savingAccount.setAccountStatus(AccountStatus.ACTIVE);
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(new Date(System.currentTimeMillis()));
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        AccountType savingAccountType = accountTypeRepository.findByNumber(savingAccount.getProductCode());
        savingAccount.setAccountType(savingAccountType);

        savingAccount.setUser(user);
        savingAccountRepository.save(savingAccount);


        user = userRepository.findById(user.getId()).get();
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);
//
//        //TODO: Move to callCenter service
//        callCenterService.callCenterUpdate(savingAccount);

    }


    public SavingBilanzList getSavingAccountByUser(User user, boolean calculateInterest) {
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
    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        //Get id of savingAccount transaction
        savingAccountTransaction.setReference(BVMicroUtils.getSaltString()); //Collision
        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        savingAccountTransactionRepository.save(savingAccountTransaction);
        generalLedgerService.updateSavingAccountTransaction(savingAccountTransaction);

    }


    public Optional<SavingAccount> findById(long id) {
        Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
        return savingAccount;
    }

    public void save(SavingAccount save) {
        savingAccountRepository.save(save);
    }


    public SavingBilanzList getSavingBilanzByUser(User user, boolean calculateInterest) {
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


    public SavingBilanzList calculateAccountBilanz(
            List<SavingAccountTransaction> savingAccountTransactions,
            boolean calculateInterest) {
        double totalSaved = 0.0;
        double currentSaved = 0.0;
        double savingAccountTransactionInterest = 0.0;

        SavingBilanzList savingBilanzsList = new SavingBilanzList();

        for (int k = 0; k < savingAccountTransactions.size(); k++) {
            final SavingAccountTransaction savingAccountTransaction = savingAccountTransactions.get(k);
            SavingBilanz savingBilanz = new SavingBilanz();
//                    if(savingAccountTransaction.getSavingAmount() <= 0){
//                        //calculate negative saving interest
//                    }else{
            savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
//                    }
            currentSaved = currentSaved + savingAccountTransaction.getSavingAmount();
            savingBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
            savingBilanzsList.getSavingBilanzList().add(savingBilanz);
            totalSaved = totalSaved + savingAccountTransaction.getSavingAmount();
            if (calculateInterest) {
                savingAccountTransactionInterest = savingAccountTransactionInterest +
                        interestService.calculateInterestAccruedMonthCompounded(
                                savingAccountTransaction.getSavingAccount().getInterestRate(),
                                savingAccountTransaction.getCreatedDate(),
                                savingAccountTransaction.getSavingAmount());
                savingBilanzsList.setTotalSavingInterest(BVMicroUtils.formatCurrency(savingAccountTransactionInterest));
            }
        }
        savingBilanzsList.setTotalSaving(BVMicroUtils.formatCurrency(totalSaved));

        Collections.reverse(savingBilanzsList.getSavingBilanzList());
        return savingBilanzsList;
    }


    private SavingBilanzList calculateUsersInterest(ArrayList<User> users, boolean calculateInterest) {
        double totalSaved = 0.0;
        double currentSaved = 0.0;
        double savingAccountTransactionInterest = 0.0;
        SavingBilanzList savingBilanzsList = new SavingBilanzList();
        for (int i = 0; i < users.size(); i++) {
            List<SavingAccount> savingAccounts = users.get(i).getSavingAccount();
            List<SavingAccountTransaction> savingAccountTransactions = new ArrayList<SavingAccountTransaction>();
            for (int j = 0; j < savingAccounts.size(); j++) {
                SavingAccount savingAccount = savingAccounts.get(j);

                boolean defaultedPayments = checkDefaultLogic(savingAccount);
                savingAccount.setDefaultedPayment(defaultedPayments); //TODO:defaultLogic
                savingAccountTransactions = savingAccount.getSavingAccountTransaction();
                double accountTotalSaved = 0.0;
                for (int k = 0; k < savingAccountTransactions.size(); k++) {
                    final SavingAccountTransaction savingAccountTransaction = savingAccountTransactions.get(k);
//                    if (savingAccountTransaction.getSavingAmount() <= 0)
//                        continue;
                    SavingBilanz savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
                    currentSaved = currentSaved + savingAccountTransaction.getSavingAmount();
                    savingBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
                    savingBilanzsList.getSavingBilanzList().add(savingBilanz);
                    totalSaved = totalSaved + savingAccountTransaction.getSavingAmount();
                    accountTotalSaved = accountTotalSaved + savingAccountTransaction.getSavingAmount();
                    savingAccountTransactionInterest = savingAccountTransactionInterest +
                            interestService.calculateInterestAccruedMonthCompounded(
                                    savingAccountTransaction.getSavingAccount().getInterestRate(),
                                    savingAccountTransaction.getCreatedDate(),
                                    savingAccountTransaction.getSavingAmount());
//                    }
                }
                savingAccount.setAccountBalance(accountTotalSaved);
                if(!defaultedPayments){
                    boolean minBalance = checkMinBalanceLogin(savingAccount)?true:false;
                        savingAccount.setDefaultedPayment(minBalance);// Minimum balance check
                }
                savingAccountRepository.save(savingAccount);
            }
        }

        savingBilanzsList.setTotalSaving(BVMicroUtils.formatCurrency(totalSaved));
        savingBilanzsList.setTotalSavingInterest(BVMicroUtils.formatCurrency(savingAccountTransactionInterest));
        Collections.reverse(savingBilanzsList.getSavingBilanzList());
        return savingBilanzsList;
    }

    private boolean checkMinBalanceLogin( SavingAccount savingAccount) {

        if (savingAccount.getAccountMinBalance() > savingAccount.getAccountBalance()) {
            callCenterService.saveCallCenterLog("",savingAccount.getUser().getUserName(),savingAccount.getAccountNumber(),"Minimum Balance not met for this account");
            return true;
        }

        return false;
    }


    private SavingBilanz calculateInterest(SavingAccountTransaction savingAccountTransaction, boolean calculateInterest) {
        SavingBilanz savingBilanz = new SavingBilanz();
        savingBilanz.setId("" + savingAccountTransaction.getId());
        savingBilanz.setAccountType(savingAccountTransaction.getSavingAccount().getAccountSavingType().getName());
        savingBilanz.setAccountMinimumBalance(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setMinimumBalance(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setCreatedBy(savingAccountTransaction.getCreatedBy());
        savingBilanz.setReference(savingAccountTransaction.getReference());
        savingBilanz.setAgent(savingAccountTransaction.getCreatedBy());
        savingBilanz.setInterestRate("" + savingAccountTransaction.getSavingAccount().getInterestRate());
        savingBilanz.setSavingAmount(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()));
        savingBilanz.setCreatedDate(BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setNotes(savingAccountTransaction.getNotes());
        savingBilanz.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        savingBilanz.setNoOfDays(calculateNoOfDays(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setModeOfPayment(savingAccountTransaction.getModeOfPayment());
        savingBilanz.setAccountOwner(savingAccountTransaction.getAccountOwner());
        savingBilanz.setBranch(savingAccountTransaction.getSavingAccount().getBranchCode());

        if (calculateInterest) {
            savingBilanz.setInterestAccrued(
                    BVMicroUtils.formatCurrency(
                            interestService.calculateInterestAccruedMonthCompounded(
                                    savingAccountTransaction.getSavingAccount().getInterestRate(),
                                    savingAccountTransaction.getCreatedDate(),
                                    savingAccountTransaction.getSavingAmount())));
        }
        return savingBilanz;
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

    public String withdrawalAllowed(SavingAccountTransaction savingTransaction) {
        String error = "";
        error = minimumSavingRespected(savingTransaction);
        return error;
    }

    private String minimumSavingRespected(SavingAccountTransaction savingTransaction) {
        double futureBalance = getAccountBalance(savingTransaction.getSavingAccount()) + savingTransaction.getSavingAmount();
        if (savingTransaction.getSavingAccount().getAccountMinBalance() > futureBalance) {
            return "Account will fall below Minimum Savings amount";
        }
        return null;
    }

    public double getAccountBalance(SavingAccount savingAccount) {
        double total = 0.0;
        List<SavingAccountTransaction> savingAccountTransactions = savingAccount.getSavingAccountTransaction();
        for (SavingAccountTransaction tran : savingAccountTransactions) {
            total = tran.getSavingAmount() + total;
        }
        return total;
    }

    public void transferFromSavingToLoan(String fromAccountNumber,
                                         String toAccountNumber,
                                         double transferAmount,
                                         String notes) {
        LocalDateTime now = LocalDateTime.now();
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
        savingAccountTransaction.setNotes(notes);
        savingAccountTransaction.setSavingAccount(savingAccount);
        savingAccountTransaction.setSavingAmount(transferAmount*-1);
        savingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
        savingAccountTransaction.setBranch(branchInfo.getId());
        savingAccountTransaction.setBranchCode(branchInfo.getCode());
        savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createSavingAccountTransaction(savingAccountTransaction);

        LoanAccount loanAccount = loanAccountService.findByAccountNumber(toAccountNumber);
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        loanAccountTransaction.setLoanAccount(loanAccount);

        loanAccountTransaction.setCreatedDate(now);
        loanAccountTransaction.setCreatedBy(loggedInUserName);
        loanAccountTransaction.setNotes(notes);

        loanAccountTransaction.setBranch(branchInfo.getId());
        loanAccountTransaction.setBranchCode(branchInfo.getCode());
        loanAccountTransaction.setBranchCountry(branchInfo.getCountry());
        loanAccountTransaction.setAmountReceived(transferAmount);
        loanAccountTransaction.setAccountOwner(loanAccount.getUser().getLastName() +", "+
                loanAccount.getUser().getLastName());
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
        loanAccountService.createLoanAccountTransaction(loanAccountTransaction, loanAccount, BVMicroUtils.TRANSFER);

        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        savingAccountRepository.save(savingAccount);

        //Update shortee accounts min balance
        List<ShorteeAccount> shorteeAccounts = loanAccount.getShorteeAccounts();
        ShorteeAccount shorteeAccount = shorteeAccounts.get(0);
        SavingAccount shorteeSavingAccount = shorteeAccount.getSavingAccount();
        shorteeSavingAccount.setAccountMinBalance(shorteeSavingAccount.getAccountMinBalance()-loanAccountTransaction.getAmountReceived());
        savingAccountRepository.save(shorteeSavingAccount);

        generalLedgerService.updateGLAfterLoanAccountTransferRepayment(loanAccountTransaction);

    }

    public SavingAccount transferFromDebitToDebit(String fromAccountNumber,
                                         String toAccountNumber,
                                         double transferAmount,
                                         String notes) {

        SavingAccount toSavingAccount = findByAccountNumber(toAccountNumber);
        if(toSavingAccount == null ){
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
        savingAccountTransaction.setNotes(notes);
        savingAccountTransaction.setSavingAccount(savingAccount);
        savingAccountTransaction.setSavingAmount(transferAmount*-1);
        savingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
        savingAccountTransaction.setBranch(branchInfo.getId());
        savingAccountTransaction.setBranchCode(branchInfo.getCode());
        savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createSavingAccountTransaction(savingAccountTransaction);
        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        savingAccountRepository.save(savingAccount);

        SavingAccountTransaction toSavingAccountTransaction = new SavingAccountTransaction();
        toSavingAccountTransaction.setNotes(notes);
        toSavingAccountTransaction.setSavingAccount(toSavingAccount);
        toSavingAccountTransaction.setSavingAmount(transferAmount);
        toSavingAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
        toSavingAccountTransaction.setBranch(branchInfo.getId());
        toSavingAccountTransaction.setBranchCode(branchInfo.getCode());
        toSavingAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createSavingAccountTransaction(toSavingAccountTransaction);
        toSavingAccount.getSavingAccountTransaction().add(toSavingAccountTransaction);
        savingAccountRepository.save(toSavingAccount);

        return savingAccount;
    }

}
