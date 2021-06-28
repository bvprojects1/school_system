package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.SavingAccountRepository;
import com.bitsvaley.micro.repositories.SavingAccountTransactionRepository;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.repositories.UserRepository;
import com.bitsvaley.micro.utils.BVMicroUtils;
import com.bitsvaley.micro.utils.SavingAccountType;
import com.bitsvaley.micro.webdomain.SavingBilanzList;
import com.bitsvaley.micro.webdomain.SavingsBilanz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    private double minimumSavings;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    public void createSavingAccount(SavingAccount savingAccount, User user) {

//        User user = userService.findUserByUserName("admin");
//        request.getSession().getAttribute("customerInUse");
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "CM"));
//        savingAccount.setAccountMinBalance(new Double(formatter.format(savingAccount.getAccountMinBalance())));

        savingAccount.setAccountNumber(BVMicroUtils.getSaltString()); //Collision
        savingAccount.setCreatedBy(getLoggedInUserName());
        savingAccount.setCreatedDate(LocalDateTime.now());
        savingAccount.setLastUpdatedBy(getLoggedInUserName());
        savingAccount.setAccountLocked(false);
        savingAccount.setLastUpdatedDate(LocalDateTime.now());
        savingAccount.setSavingAccountType(insureAccountSavingsTypeExists());
        savingAccount.setUser(user); //TODO:Add User
        savingAccountRepository.save(savingAccount);
        user = userRepository.findById(user.getId()).get();//TODO handle optional
        user.getSavingAccount().add(savingAccount);
        userService.saveUser(user);
    }

    public void createSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction,User user) {
        //Get id of savingAccount transaction

        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        savingAccountTransactionRepository.save(savingAccountTransaction);
//        savingAccount.getSavingAccount().add(savingAccount);
//        savingAccountRepository.get(savingAccountTransaction);
    }


    private com.bitsvaley.micro.domain.SavingAccountType insureAccountSavingsTypeExists() {
        com.bitsvaley.micro.domain.SavingAccountType savingAccountType = savingAccountTypeRepository.findByName(SavingAccountType.MONTHLY_SAVINGS.name());
        if( null == savingAccountType ){
            savingAccountType = new com.bitsvaley.micro.domain.SavingAccountType();
            savingAccountType.setName(SavingAccountType.MONTHLY_SAVINGS.name());
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


    public SavingBilanzList getSavingsBilanzByUserRole(ArrayList<UserRole> userRole) {
        ArrayList<User> users = userRepository.findAllByUserRoleIn(userRole);
        return calculateUsersInterest(users);
    }


    public SavingBilanzList getSavingsBilanzByUser(User user) {
        User aUser = userRepository.findById(user.getId()).get();
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(aUser);
        return calculateUsersInterest(userList);
    }

    private SavingBilanzList calculateUsersInterest(ArrayList<User> users) {
        double totalSaved = 0.0;
        SavingBilanzList savingsBilanzsList = new SavingBilanzList();
        for (int i = 0; i < users.size(); i++) {
            List<SavingAccount> savingAccounts = users.get(i).getSavingAccount();
            for (int j = 0; j < savingAccounts.size(); j++) {
                List<SavingAccountTransaction> savingAccountTransactions = savingAccounts.get(i).getSavingAccountTransaction();
                for (int k = 0; k < savingAccountTransactions.size(); k++) {
                    final SavingAccountTransaction savingAccountTransaction = savingAccountTransactions.get(k);
                    LocalDateTime createdDate = savingAccountTransaction.getCreatedDate();
//                    if (LocalDateTime.now().minusMonths(1).isAfter(createdDate)) {
                        SavingsBilanz savingsBilanz = calculateInterest(savingAccountTransaction);
                        savingsBilanzsList.getSavingsBilanzList().add(savingsBilanz);
                        totalSaved = totalSaved + savingsBilanz.getSavingsAmount();
//                    }
                }
            }
        }
        savingsBilanzsList.setTotalSaving(formatCurrency(totalSaved));
        return savingsBilanzsList;
    }

    private String formatCurrency(double totalSaved) {
        Locale locale = new Locale("en", "CM");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        String total = fmt.format(totalSaved);
        return total.substring(3,total.length());
    }



    private SavingsBilanz calculateInterest(SavingAccountTransaction savingAccountTransaction) {
        SavingsBilanz savingsBilanz = new SavingsBilanz();
        savingsBilanz.setAgent(savingAccountTransaction.getCreatedBy());
        savingsBilanz.setSavingsAmount(savingAccountTransaction.getSavingAmount());
        savingsBilanz.setCreatedDate(savingAccountTransaction.getCreatedDate().toString());
        savingsBilanz.setNotes(savingAccountTransaction.getNotes());
        savingsBilanz.setNoOfDays(calculateNoOfDays(savingAccountTransaction.getCreatedDate()));
        savingsBilanz.setModeOfPayment(savingAccountTransaction.getModeOfPayment());
        savingsBilanz.setInterestAccrued(calculateInterestAccrued(savingAccountTransaction));
        return savingsBilanz;
    }

    private String calculateNoOfDays(LocalDateTime createdDate) {
        long noOfDays = createdDate.until(LocalDateTime.now(), ChronoUnit.DAYS);
        return ""+noOfDays;
    }

    private int calculateInterestAccrued(SavingAccountTransaction savingAccountTransaction){
        int accrued = 1977;
        return accrued;
    }

}
