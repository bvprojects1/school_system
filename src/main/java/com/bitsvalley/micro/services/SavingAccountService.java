package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.SavingAccount;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.SavingAccountRepository;
import com.bitsvalley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.SavingBilanz;
import com.bitsvalley.micro.webdomain.SavingBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SavingAccountService extends SuperService{

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private double minimumSaving;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public int findAllSavingAccountCount() {
        return savingAccountRepository.findAllCount();
    }

    public void createSavingAccount(SavingAccount savingAccount, User user) {

//        User user = userService.findUserByUserName("admin");
//        request.getSession().getAttribute("customerInUse");
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "CM"));
//        savingAccount.setAccountMinBalance(new Double(formatter.format(savingAccount.getAccountMinBalance())));

        savingAccount.setAccountNumber(BVMicroUtils.getSaltString()); //Collision
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(new Date(System.currentTimeMillis()));
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        savingAccount.setSavingAccountType(insureAccountSavingTypeExists());
        savingAccount.setUser(user); //TODO:Add User
        savingAccountRepository.save(savingAccount);
        user = userRepository.findById(user.getId()).get();//TODO handle optional
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);
    }

    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction, User user) {
        //Get id of savingAccount transaction

        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        savingAccountTransactionRepository.save(savingAccountTransaction);
//        savingAccount.getSavingAccount().add(savingAccount);
//        savingAccountRepository.get(savingAccountTransaction);
    }


    private com.bitsvalley.micro.domain.SavingAccountType insureAccountSavingTypeExists() {
        com.bitsvalley.micro.domain.SavingAccountType savingAccountType = savingAccountTypeRepository.findByName(com.bitsvalley.micro.utils.SavingAccountType.MONTHLY_SAVING.name());
        if( null == savingAccountType ){
            savingAccountType = new com.bitsvalley.micro.domain.SavingAccountType();
            savingAccountType.setName(com.bitsvalley.micro.utils.SavingAccountType.MONTHLY_SAVING.name());
            savingAccountTypeRepository.save(savingAccountType);
            return savingAccountType;
        }
        return savingAccountType;
    }

    public Optional<SavingAccount> findById(long id){
        Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
        return savingAccount;
    }

    public void save(SavingAccount save){
        savingAccountRepository.save(save);
    }


//    public SavingBilanzList getSavingBilanzByUserRole(ArrayList<UserRole> userRole) {
//        ArrayList<User> users = userRepository.findAllByUserRoleIn(userRole);
//        return calculateUsersInterest(users);
//    }


    public SavingBilanzList getSavingBilanzByUser(User user, boolean calculateInterest) {
        User aUser = null;
        if(null != user.getUserName()){
            aUser = userRepository.findByUserName(user.getUserName());
        }else{
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
                    if(savingAccountTransaction.getSavingAmount() <= 0)
                        continue;
                    SavingBilanz savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
                    currentSaved = currentSaved + savingAccountTransaction.getSavingAmount();
                    savingBilanz.setCurrentBalance(formatCurrency(currentSaved));
                    savingBilanzsList.getSavingBilanzList().add(savingBilanz);
                    totalSaved = totalSaved + savingAccountTransaction.getSavingAmount();
                    if(calculateInterest){
                        savingAccountTransactionInterest = savingAccountTransactionInterest +
                                calculateInterestAccruedMonthCompounded(savingAccountTransaction);
                    }
                }
        savingBilanzsList.setTotalSaving(formatCurrency(totalSaved));
//        savingBilanzsList.setTotalSavingInterest(formatCurrency(savingAccountTransactionInterest));
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
//            savingBilanzsList.setSavingsAccount(savingAccounts);
            List<SavingAccountTransaction> savingAccountTransactions = new ArrayList<SavingAccountTransaction>();
            for (int j = 0; j < savingAccounts.size(); j++) {
                savingAccountTransactions = savingAccounts.get(j).getSavingAccountTransaction();
                for (int k = 0; k < savingAccountTransactions.size(); k++) {
                    final SavingAccountTransaction savingAccountTransaction = savingAccountTransactions.get(k);
                    if(savingAccountTransaction.getSavingAmount() <= 0)
                        continue;
//                    LocalDateTime createdDate = savingAccountTransaction.getCreatedDate();
//                    if (LocalDateTime.now().minusMonths(1).isAfter(createdDate)) {
                        SavingBilanz savingBilanz = calculateInterest(savingAccountTransaction, calculateInterest);
                        currentSaved = currentSaved + savingAccountTransaction.getSavingAmount();
                        savingBilanz.setCurrentBalance(formatCurrency(currentSaved));
                        savingBilanzsList.getSavingBilanzList().add(savingBilanz);
                        totalSaved = totalSaved + savingAccountTransaction.getSavingAmount();
                        savingAccountTransactionInterest = savingAccountTransactionInterest +
                                calculateInterestAccruedMonthCompounded(savingAccountTransaction);
//                    }
                }
            }
        }
        savingBilanzsList.setTotalSaving(formatCurrency(totalSaved));
        savingBilanzsList.setTotalSavingInterest(formatCurrency(savingAccountTransactionInterest));
        Collections.reverse(savingBilanzsList.getSavingBilanzList());
        return savingBilanzsList;
    }


    private String formatCurrency(double totalSaved) {
        Locale locale = new Locale("en", "CM");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        String total = fmt.format(totalSaved);
        return total.substring(3,total.length());
    }


    private SavingBilanz calculateInterest(SavingAccountTransaction savingAccountTransaction, boolean calculateInterest) {
        SavingBilanz savingBilanz = new SavingBilanz();

        savingBilanz.setAccountType(savingAccountTransaction.getSavingAccount().getAccountSavingType().getName());
        savingBilanz.setAccountMinimumBalance(formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setMinimumBalance(formatCurrency(savingAccountTransaction.getSavingAccount().getAccountMinBalance()));
        savingBilanz.setCreatedBy(savingAccountTransaction.getCreatedBy());

        savingBilanz.setAgent(savingAccountTransaction.getCreatedBy());
        savingBilanz.setInterestRate(""+savingAccountTransaction.getSavingAccount().getInterestRate());
        savingBilanz.setSavingAmount(formatCurrency(savingAccountTransaction.getSavingAmount()));
        savingBilanz.setCreatedDate(BVMicroUtils.formatDateTime(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setNotes(savingAccountTransaction.getNotes());
        savingBilanz.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        savingBilanz.setNoOfDays(calculateNoOfDays(savingAccountTransaction.getCreatedDate()));
        savingBilanz.setModeOfPayment(savingAccountTransaction.getModeOfPayment());
        if(calculateInterest){
            savingBilanz.setInterestAccrued(formatCurrency(calculateInterestAccruedMonthCompounded(savingAccountTransaction)));
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

}
