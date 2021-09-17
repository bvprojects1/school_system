package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanAccountService extends SuperService{

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;


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
    private CallCenterService callCenterService;

    @Autowired
    private GeneralLedgerService generalLedgerService;


    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public int findAllSavingAccountCount() {
        return savingAccountRepository.findAllCount();
    }

    public void createLoanAccount(LoanAccount loanAccount, User user) {
        int allCount = loanAccountRepository.findAllCount();

        String loggedInUserName = getLoggedInUserName();
        User aUser = userRepository.findByUserName(loggedInUserName);
        loanAccount.setCountry(aUser.getBranch().getCountry());
        loanAccount.setBranchCode(aUser.getBranch().getCode());

        loanAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(loanAccount.getCountry(),
        loanAccount.getProductCode(),loanAccount.getBranchCode(), allCount )); //TODO: Collision
        loanAccount.setAccountStatus(AccountStatus.ACTIVE);

        loanAccount.setCreatedBy(loggedInUserName);
        loanAccount.setCreatedDate(new Date(System.currentTimeMillis()));
        loanAccount.setLastUpdatedBy(loggedInUserName);
        loanAccount.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        AccountType accountType = accountTypeRepository.findByNumber(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);
        user = userRepository.findById(user.getId()).get();
        loanAccount.setUser(user);
        loanAccountRepository.save(loanAccount);

        user.getLoanAccount().add(loanAccount);
        userService.saveUser(user);

        callCenterService.saveCallCenterLog(loanAccount.getUser().getFirstName(),loanAccount.getUser().getLastName(),
                loanAccount.getAccountNumber(), loanAccount.getAccountType().getName(), loanAccount.getLoanAmount()+"");

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


    public Optional<SavingAccount> findById(long id){
        Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
        return savingAccount;
    }

    public void save(SavingAccount save){
        savingAccountRepository.save(save);
    }


//    public SavingBilanzList getSavingBilanzByUser(User user, boolean calculateInterest) {
//        User aUser = null;
//        if(null != user.getUserName()){
//            aUser = userRepository.findByUserName(user.getUserName());
//        }else{
//            aUser = userRepository.findById(user.getId()).get();
//        }
//        ArrayList<User> userList = new ArrayList<User>();
//        userList.add(aUser);
//        return calculateUsersInterest(userList, calculateInterest);
//
//    }


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
                    if(calculateInterest){
                        savingAccountTransactionInterest = savingAccountTransactionInterest +
                                calculateInterestAccruedMonthCompounded(savingAccountTransaction);
                        savingBilanzsList.setTotalSavingInterest(BVMicroUtils.formatCurrency(savingAccountTransactionInterest));
                    }
                }
        savingBilanzsList.setTotalSaving(BVMicroUtils.formatCurrency(totalSaved));

        Collections.reverse(savingBilanzsList.getSavingBilanzList());
        return savingBilanzsList;
    }


//    private SavingBilanzList calculateUsersInterest(ArrayList<User> users, boolean calculateInterest) {
//        double totalSaved = 0.0;
//        double currentSaved = 0.0;
//        double savingAccountTransactionInterest = 0.0;
//        SavingBilanzList savingBilanzsList = new SavingBilanzList();
//        for (int i = 0; i < users.size(); i++) {
//            List<SavingAccount> savingAccounts = users.get(i).getSavingAccount();
//
//            List<SavingAccountTransaction> savingAccountTransactions = new ArrayList<SavingAccountTransaction>();
//            for (int j = 0; j < savingAccounts.size(); j++) {
//                SavingAccount savingAccount = savingAccounts.get(j);
//                boolean defaultedPayments = checkDefaultLogic(savingAccount);
//                savingAccount.setDefaultedPayment(defaultedPayments); //TODO:defaultLogic
//                savingAccountTransactions = savingAccount.getSavingAccountTransaction();
//                for (int k = 0; k < savingAccountTransactions.size(); k++) {
//                    final SavingAccountTransaction savingAccountTransaction = savingAccountTransactions.get(k);
//                    if(savingAccountTransaction.getSavingAmount() <= 0)
//                        continue;
////                    LocalDateTime createdDate = savingAccountTransaction.getCreatedDate();
////                    if (LocalDateTime.now().minusMonths(1).isAfter(createdDate)) {
//                        SavingBilanz savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
//                        currentSaved = currentSaved + savingAccountTransaction.getSavingAmount();
//                        savingBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
//                        savingBilanzsList.getSavingBilanzList().add(savingBilanz);
//                        totalSaved = totalSaved + savingAccountTransaction.getSavingAmount();
//                        savingAccountTransactionInterest = savingAccountTransactionInterest +
//                                calculateInterestAccruedMonthCompounded(savingAccountTransaction);
////                    }
//                }
//                savingAccount.setAccountBalance(totalSaved);
//                if(checkMinBalanceLogin(currentSaved, savingAccount)){
//                    savingAccount.setDefaultedPayment(true);// Minimum balance check
//                }
//            }
//        }
//        savingBilanzsList.setTotalSaving(BVMicroUtils.formatCurrency(totalSaved));
//        savingBilanzsList.setTotalSavingInterest(BVMicroUtils.formatCurrency(savingAccountTransactionInterest));
//        Collections.reverse(savingBilanzsList.getSavingBilanzList());
//        return savingBilanzsList;
//    }

//    private boolean checkMinBalanceLogin(double currentSaved, SavingAccount savingAccount) {
//
//        if(savingAccount.getAccountMinBalance() > currentSaved){
//            CallCenter callCenter = new CallCenter();
//            callCenter.setDate(new Date(System.currentTimeMillis()));
//            callCenter.setNotes("Minimum Balance not met for this account");
//            callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getLastName());
//            callCenter.setAccountNumber(savingAccount.getAccountNumber());
//            callCenterRepository.save(callCenter);
//            return true;
//        }
//
//        return false;
//    }





    private SavingBilanz calculateInterest(SavingAccountTransaction savingAccountTransaction, boolean calculateInterest) {
        SavingBilanz savingBilanz = new SavingBilanz();
        savingBilanz.setId(""+savingAccountTransaction.getId());
        savingBilanz.setAccountType(savingAccountTransaction.getSavingAccount().getAccountSavingType().getName());
        savingBilanz.setAccountMinimumBalance(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setMinimumBalance(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setCreatedBy(savingAccountTransaction.getCreatedBy());
        savingBilanz.setReference(savingAccountTransaction.getReference());
        savingBilanz.setAgent(savingAccountTransaction.getCreatedBy());
        savingBilanz.setInterestRate(""+savingAccountTransaction.getSavingAccount().getInterestRate());
        savingBilanz.setSavingAmount(BVMicroUtils.formatCurrency(savingAccountTransaction.getSavingAmount()));
        savingBilanz.setCreatedDate(BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setNotes(savingAccountTransaction.getNotes());
        savingBilanz.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        savingBilanz.setNoOfDays(calculateNoOfDays(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setModeOfPayment(savingAccountTransaction.getModeOfPayment());
        savingBilanz.setAccountOwner(savingAccountTransaction.getAccountOwner());
        savingBilanz.setBranch(savingAccountTransaction.getSavingAccount().getBranchCode());

        if(calculateInterest){
            savingBilanz.setInterestAccrued(BVMicroUtils.formatCurrency(calculateInterestAccruedMonthCompounded(savingAccountTransaction)));
        }
        return savingBilanz;
    }

    private String calculateNoOfDays(LocalDateTime createdDate) {
        long noOfDays = createdDate.until(LocalDateTime.now(), ChronoUnit.DAYS);
        return ""+noOfDays;
    }


    private double calculateInterestAccruedMonthCompounded(SavingAccountTransaction savingAccountTransaction){
//        = P [(1 + i/12)pow of NoOfMonths – 1]
//        P = principal, i = nominal annual interest rate in percentage terms, n = number of compounding periods
        double interestPlusOne = (savingAccountTransaction.getSavingAccount().getInterestRate()*.01*.0833333) + 1;
        double temp = Math.pow(interestPlusOne,getNumberOfMonths(savingAccountTransaction.getCreatedDate()));
        temp = temp - 1;
        return savingAccountTransaction.getSavingAmount() * temp;
    }

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
        Duration diff = Duration.between(cretedDateInput, LocalDateTime.now() );
        noOfMonths = diff.toDays() / 30;
        return Math.floor(noOfMonths);
    }

//    public boolean checkDefaultLogic(SavingAccount savingAccount){
//
////        if(savingAccount.getAccountSavingType().getName().equals("GENERAL SAVINGS")){
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
////            if (savingAccount.getAccountMinBalance() > totalSaved){
////                CallCenter callCenter = new CallCenter();
//////                callCenter.setUserName(savingAccount.getUser().getUserName());
////                callCenter.setNotes("Minimum payment not met");
////                callCenter.setDate(new Date(System.currentTimeMillis()));
////                callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " "+ savingAccount.getUser().getLastName());
////                callCenter.setAccountNumber(savingAccount.getAccountNumber());
////                callCenterRepository.save(callCenter);
////                return true;
////            }
//        }
//        return false;
//    }
//
//    private String padding(int i) {
//        if (i < 10)
//            return ""+0+1;
//        return ""+i;
//    }
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
