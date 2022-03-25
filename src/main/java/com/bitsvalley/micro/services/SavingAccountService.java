package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SavingAccountService extends SuperService {


    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

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

    @Autowired
    private CurrentAccountService currentAccountService;

    @Autowired
    private CurrentAccountTransactionRepository currentAccountTransactionRepository;

    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public void createSavingAccount(SavingAccount savingAccount, User user) {

        ArrayList<UserRole> userRoleList = new ArrayList<UserRole>();
        UserRole customer = userRoleService.findUserRoleByName("ROLE_CUSTOMER");
        userRoleList.add(customer);
        ArrayList<User> customerList = userService.findAllByUserRoleIn(userRoleList);

        int countNumberOfProductsInBranch = savingAccountRepository.countNumberOfProductsCreatedInBranch(user.getBranch().getCode());
        savingAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(savingAccount.getCountry(),
                savingAccount.getProductCode(), countNumberOfProductsInBranch, user.getCustomerNumber(), savingAccount.getBranchCode())); //TODO: Collision

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
        callCenterService.callCenterSavingAccount(savingAccount);

        user = userRepository.findById(user.getId()).get();
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);

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
        savingAccountTransaction.setAccountBalance(calculateAccountBalance(savingAccountTransaction.getSavingAmount(), savingAccountTransaction.getSavingAccount()));

        savingAccountTransactionRepository.save(savingAccountTransaction);
//        generalLedgerService.updateGLAfterSavingAccountTransaction(savingAccountTransaction);
    }

    public double calculateAccountBalance(double savingAmount, SavingAccount savingAccount) {

        Double balance = 0.0;
        for (SavingAccountTransaction transaction : savingAccount.getSavingAccountTransaction()) {
            balance = transaction.getSavingAmount() + balance;
        }
        return savingAmount + balance;
    }

    @Transactional
    public void createCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {
        //Get id of savingAccount transaction
        currentAccountTransaction.setReference(BVMicroUtils.getSaltString()); //Collision
        currentAccountTransaction.setCreatedBy(getLoggedInUserName());
        currentAccountTransaction.setCreatedDate(LocalDateTime.now());
        currentAccountTransactionRepository.save(currentAccountTransaction);
        // generalLedgerService.updateSavingAccountTransaction(savingAccountTransaction);
    }

    @Transactional
    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction, SavingAccount savingAccount) {
        //Get id of savingAccount transactions
        createSavingAccountTransaction(savingAccountTransaction);
        if (savingAccount.getSavingAccountTransaction() != null) {
            savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        } else {
            savingAccount.setSavingAccountTransaction(new ArrayList<SavingAccountTransaction>());
            savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        }
        save(savingAccount);
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
            savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
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

//        Collections.reverse(savingBilanzsList.getSavingBilanzList());
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
                }
                savingAccount.setAccountBalance(accountTotalSaved);
                if (!defaultedPayments) {
                    boolean minBalance = checkMinBalanceLogin(savingAccount) ? true : false;
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

    private boolean checkMinBalanceLogin(SavingAccount savingAccount) {

        if (savingAccount.getAccountMinBalance() > savingAccount.getAccountBalance()) {
            callCenterService.saveCallCenterLog("", savingAccount.getUser().getUserName(), savingAccount.getAccountNumber(), "Minimum Balance not met for this account");
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
        savingBilanz.setSavingAmount(savingAccountTransaction.getSavingAmount());
        savingBilanz.setRepresentative(savingAccountTransaction.getRepresentative());

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

        LocalDateTime currentDateCal = LocalDateTime.now();

        Date input = savingAccount.getCreatedDate();
        LocalDate createdLocalDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(LocalDate.of(createdLocalDate.getYear(), createdLocalDate.getMonth(), createdLocalDate.getDayOfMonth())),
                YearMonth.from(LocalDate.of(currentDateCal.getYear(), currentDateCal.getMonth(), currentDateCal.getDayOfMonth())));

        if (monthsBetween >= savingAccountTransactionList.size()) {
            CallCenter callCenter = new CallCenter();
            callCenter.setNotes(BVMicroUtils.REGULAR_MONTHLY_PAYMENT_MISSING);
            callCenter.setDate(new Date(System.currentTimeMillis()));
            callCenter.setReference(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getLastName());
            callCenter.setAccountNumber(savingAccount.getAccountNumber());
            return true;
        }

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

    @Transactional
    public String transferFromCurrentToLoan(CurrentAccount fromCurrentAccount,
                                            LoanAccount loanAccount,
                                            double transferAmount,
                                            String notes) {
        LocalDateTime now = LocalDateTime.now();
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        CurrentAccountTransaction currentAccountTransaction = getCurrentAccountTransaction(notes, branchInfo, fromCurrentAccount, transferAmount * -1, BVMicroUtils.CURRENT_LOAN_TRANSFER);

        fromCurrentAccount.getCurrentAccountTransaction().add(currentAccountTransaction);
        currentAccountRepository.save(fromCurrentAccount);
        if (!StringUtils.equals(loanAccount.getAccountStatus().name(), AccountStatus.ACTIVE.name())) {
            return BVMicroUtils.LOAN_MUST_BE_IN_ACTIVE_STATE;
        }
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        loanAccountTransaction.setLoanAccount(loanAccount);

        loanAccountTransaction.setCreatedDate(now);
        loanAccountTransaction.setCreatedBy(loggedInUserName);
        loanAccountTransaction.setNotes(notes);

        loanAccountTransaction.setBranch(branchInfo.getId());
        loanAccountTransaction.setBranchCode(branchInfo.getCode());
        loanAccountTransaction.setBranchCountry(branchInfo.getCountry());
        loanAccountTransaction.setAmountReceived(transferAmount);
//        loanAccountTransaction.setAccountOwner(loanAccount.getUser().getLastName() +", "loanAccount.getUser().getLastName());
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
        loanAccount = loanAccountService.createLoanAccountTransaction(loanAccountTransaction, loanAccount, BVMicroUtils.TRANSFER);

//

        //Update shortee accounts min balance
        if (loanAccount.isBlockBalanceQuarantor()) {
            List<ShorteeAccount> shorteeAccounts = loanAccount.getShorteeAccounts();
            ShorteeAccount shorteeAccount = shorteeAccounts.get(0);
            SavingAccount shorteeSavingAccount = shorteeAccount.getSavingAccount();
            shorteeSavingAccount.setAccountMinBalance(shorteeSavingAccount.getAccountMinBalance() - loanAccountTransaction.getAmountReceived());
            savingAccountRepository.save(shorteeSavingAccount);
        }


        generalLedgerService.updateGLAfterLoanAccountTransferRepayment(loanAccountTransaction);
        return "true";
    }


    @NotNull
    public SavingAccountTransaction getSavingAccountTransaction(String notes, Branch branchInfo, SavingAccount savingAccount, double v, String modeOfPayment) {
        SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
        savingAccountTransaction.setNotes(notes);
        savingAccountTransaction.setSavingAccount(savingAccount);
        savingAccountTransaction.setSavingAmount(v);
        savingAccountTransaction.setModeOfPayment(modeOfPayment);
        savingAccountTransaction.setBranch(branchInfo.getId());
        savingAccountTransaction.setBranchCode(branchInfo.getCode());
        savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createSavingAccountTransaction(savingAccountTransaction);
        return savingAccountTransaction;
    }

    @NotNull
    public CurrentAccountTransaction getCurrentAccountTransaction(String notes, Branch branchInfo, CurrentAccount currentAccount, double v, String transferMode) {
        CurrentAccountTransaction currentAccountTransaction = new CurrentAccountTransaction();
        currentAccountTransaction.setNotes(notes);
        currentAccountTransaction.setCurrentAccount(currentAccount);
        currentAccountTransaction.setCurrentAmount(v);
        currentAccountTransaction.setModeOfPayment(transferMode);
        currentAccountTransaction.setBranch(branchInfo.getId());
        currentAccountTransaction.setBranchCode(branchInfo.getCode());
        currentAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createCurrentAccountTransaction(currentAccountTransaction);
        return currentAccountTransaction;
    }

    public SavingAccount transferFromDebitToDebit(String fromAccountNumber,
                                                  String toAccountNumber,
                                                  double transferAmount,
                                                  String notes) {

        SavingAccount toSavingAccount = findByAccountNumber(toAccountNumber);
        if (toSavingAccount == null) {
            return null;
        }
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        SavingAccountTransaction fromSavingAccountTransaction = getSavingAccount(fromAccountNumber, -1 * transferAmount, notes, branchInfo, BVMicroUtils.DEBIT_DEBIT_TRANSFER);

        SavingAccountTransaction toSavingAccountTransaction = getSavingAccountTransaction(notes, branchInfo, toSavingAccount, transferAmount, BVMicroUtils.DEBIT_DEBIT_TRANSFER);
        toSavingAccount.getSavingAccountTransaction().add(toSavingAccountTransaction);
        savingAccountRepository.save(toSavingAccount);

        generalLedgerService.updateGLAfterDebitDebitTransfer(toSavingAccountTransaction, fromSavingAccountTransaction);

        callCenterService.saveCallCenterLog(fromSavingAccountTransaction.getReference(),
                loggedInUserName, fromSavingAccountTransaction.getSavingAccount().getAccountNumber(),
                "TRANSFER FROM: Saving account transaction made to " + toSavingAccountTransaction.getSavingAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(toSavingAccountTransaction.getSavingAmount()));

        callCenterService.saveCallCenterLog(toSavingAccountTransaction.getReference(),
                loggedInUserName, toSavingAccountTransaction.getSavingAccount().getAccountNumber(),
                "TRANSFER TO: Saving account transaction made from " + fromSavingAccountTransaction.getSavingAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(toSavingAccountTransaction.getSavingAmount()));

        return fromSavingAccountTransaction.getSavingAccount();
    }

    public SavingAccount transferFromDebitToCurrent(String fromAccountNumber,
                                                    String toAccountNumber,
                                                    double transferAmount,
                                                    String notes) {

        CurrentAccount toCurrentAccount = currentAccountService.findByAccountNumber(toAccountNumber);
        if (toCurrentAccount == null) {
            return null;
        }

        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        SavingAccountTransaction savingAccountTransaction = getSavingAccount(fromAccountNumber, transferAmount * -1, notes, branchInfo, BVMicroUtils.DEBIT_CURRENT_TRANSFER);

        CurrentAccountTransaction toCurrentAccountTransaction = getCurrentAccountTransaction(notes, branchInfo, toCurrentAccount, transferAmount, BVMicroUtils.DEBIT_CURRENT_TRANSFER);
        toCurrentAccount.getCurrentAccountTransaction().add(toCurrentAccountTransaction);
        currentAccountRepository.save(toCurrentAccount);

        generalLedgerService.updateGLAfterDebitCurrentTransfer(savingAccountTransaction,toCurrentAccountTransaction);

        callCenterService.saveCallCenterLog(savingAccountTransaction.getReference(),
                loggedInUserName, savingAccountTransaction.getSavingAccount().getAccountNumber(),
                "TRANSFER FROM: Current account transaction made to " + toCurrentAccountTransaction.getCurrentAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()));

        callCenterService.saveCallCenterLog(toCurrentAccountTransaction.getReference(),
                loggedInUserName, toCurrentAccountTransaction.getCurrentAccount().getAccountNumber(),
                "TRANSFER TO: Saving account transaction made from " + savingAccountTransaction.getSavingAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(toCurrentAccountTransaction.getCurrentAmount()));

        return savingAccountTransaction.getSavingAccount();
    }

    public CurrentAccount transferFromCurrentToDebit(String fromCurrentAccountNumber,
                                                     String toDebitAccountNumber,
                                                     double transferAmount,
                                                     String notes) {
        CurrentAccount fromCurrentAccount = currentAccountService.findByAccountNumber(fromCurrentAccountNumber);
        if (fromCurrentAccount == null) {
            return null;
        }
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);
        SavingAccountTransaction savingAccountTransaction = getSavingAccount(toDebitAccountNumber, transferAmount, notes, branchInfo, BVMicroUtils.CURRENT_DEBIT_TRANSFER);

        CurrentAccountTransaction fromCurrentAccountTransaction = getCurrentAccountTransaction(notes, branchInfo, fromCurrentAccount, -1 * transferAmount, BVMicroUtils.CURRENT_DEBIT_TRANSFER);

        fromCurrentAccount.getCurrentAccountTransaction().add(fromCurrentAccountTransaction);
        currentAccountRepository.save(fromCurrentAccount);

        generalLedgerService.updateGLAfterCurrentDebitTransfer(fromCurrentAccountTransaction, savingAccountTransaction);

        callCenterService.saveCallCenterLog(savingAccountTransaction.getReference(),
                loggedInUserName, savingAccountTransaction.getSavingAccount().getAccountNumber(),
                "TRANSFER FROM: Current account transaction made to " + fromCurrentAccountTransaction.getCurrentAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()));

        callCenterService.saveCallCenterLog(savingAccountTransaction.getReference(),
                loggedInUserName, fromCurrentAccountTransaction.getCurrentAccount().getAccountNumber(),
                "TRANSFER TO: Saving account transaction made from " + savingAccountTransaction.getSavingAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(fromCurrentAccountTransaction.getCurrentAmount()));

        return fromCurrentAccount;
    }

    public CurrentAccount transferFromCurrentToCurrent(String fromAccountNumber,
                                                       String toAccountNumber,
                                                       double transferAmount,
                                                       String notes) {

        CurrentAccount fromCurrentAccount = currentAccountService.findByAccountNumber(toAccountNumber);
        if (fromCurrentAccount == null) {
            return null;
        }
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

        CurrentAccountTransaction currentAccountTransaction = getCurrentAccount(fromAccountNumber, transferAmount, notes, branchInfo, BVMicroUtils.CURRENT_CURRENT_TRANSFER);

        CurrentAccountTransaction toCurrentAccountTransaction = getCurrentAccountTransaction(notes, branchInfo, fromCurrentAccount, transferAmount, BVMicroUtils.CURRENT_CURRENT_TRANSFER);
        fromCurrentAccount.getCurrentAccountTransaction().add(toCurrentAccountTransaction);
        currentAccountRepository.save(fromCurrentAccount);

        generalLedgerService.updateGLAfterCurrentCurrentTransfer(toCurrentAccountTransaction);

        callCenterService.saveCallCenterLog(currentAccountTransaction.getReference(),
                loggedInUserName, currentAccountTransaction.getCurrentAccount().getAccountNumber(),
                "TRANSFER FROM: Current account transaction made to " + toCurrentAccountTransaction.getCurrentAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(currentAccountTransaction.getCurrentAmount()));

        callCenterService.saveCallCenterLog(toCurrentAccountTransaction.getReference(),
                loggedInUserName, toCurrentAccountTransaction.getCurrentAccount().getAccountNumber(),
                "TRANSFER TO: Current account transaction made from " + currentAccountTransaction.getCurrentAccount().getAccountNumber() + " " + BVMicroUtils.formatCurrency(toCurrentAccountTransaction.getCurrentAmount()));
        return fromCurrentAccount;
    }

//    @NotNull
//    private SavingAccountTransaction getToSavingAccount(String fromAccountNumber, double transferAmount, String notes, Branch branchInfo, String transferMode) {
//        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
//        SavingAccountTransaction savingAccountTransaction = getSavingAccountTransaction(notes, branchInfo, savingAccount, transferAmount, transferMode);
//        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
//        savingAccountRepository.save(savingAccount);
//        return savingAccountTransaction;
//    }

    @NotNull
    private SavingAccountTransaction getSavingAccount(String fromAccountNumber, double transferAmount, String notes, Branch branchInfo, String transferMode) {
        SavingAccount savingAccount = findByAccountNumber(fromAccountNumber);
        SavingAccountTransaction savingAccountTransaction = getSavingAccountTransaction(notes, branchInfo, savingAccount, transferAmount, transferMode);
        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        savingAccountRepository.save(savingAccount);
        return savingAccountTransaction;
    }

    @NotNull
    private CurrentAccountTransaction getCurrentAccount(String fromAccountNumber, double transferAmount, String notes, Branch branchInfo, String transactionType) {
        CurrentAccount currentAccount = currentAccountService.findByAccountNumber(fromAccountNumber);
        CurrentAccountTransaction currentAccountTransaction = getCurrentAccountTransaction(notes, branchInfo, currentAccount, transferAmount * -1, transactionType);
        currentAccount.getCurrentAccountTransaction().add(currentAccountTransaction);
        currentAccountRepository.save(currentAccount);
        return currentAccountTransaction;
    }

}
