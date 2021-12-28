package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.AccountType;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }
    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User createUser(User user) {
        Date now = new Date();
        user.setCreated(now);
        user.setAccountExpiredDate(LocalDateTime.now().plusMonths(6));
        user.setAccountBlockedDate(LocalDateTime.now().plusMonths(6));
        user.setLastUpdated(now);

        user.setAccountExpired(false);
        user.setAccountLocked(false);
        insureAccountExists();
        User save = userRepository.save(user);

        // - Update callcenter
        callCenterService.callCenterUserAccount(user,"Login account created by "+ user.getCreatedBy() + " on " + user.getCreated());
        return save;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    private void insureAccountExists(){ // init system
        AccountType general_saving = accountTypeRepository.findByName("GENERAL SAVINGS");
//        Iterable<UserRole> all = userRoleService.findAll();
        if (general_saving == null) {
//
////            role = new UserRole();
////            role.setName(name);
////            userRoleService.saveUserRole(role);
//
//            List<UserRole> userRolesList = new ArrayList<UserRole>();
//            UserRole customer = new UserRole();
//            customer.setName("CUSTOMER");
////            userRoleService.saveUserRole(customer);
//            userRolesList.add(customer);
//
//            UserRole admin = new UserRole();
//            admin.setName("ADMIN");
////            userRoleService.saveUserRole(admin);
//            userRolesList.add(admin);
//
//            UserRole manager = new UserRole();
//            manager.setName("MANAGER");
//            userRolesList.add(manager);
//
//            UserRole agent = new UserRole();
//            agent.setName("AGENT");
//            userRolesList.add(agent);
//
//            UserRole auditor = new UserRole();
//            auditor.setName("AUDITOR");
//            userRolesList.add(auditor);
//            Iterable<UserRole> iterable = userRolesList;
//            userRoleService.saveAllRole(iterable);
// ----------------------------------------------------------------------------------------------

            List<AccountType> savingAccountTypeList = new ArrayList<AccountType>();
            AccountType schoolSavingAccountType = new AccountType();
            schoolSavingAccountType.setNumber("11");
            schoolSavingAccountType.setName("GENERAL SAVINGS");
            savingAccountTypeList.add(schoolSavingAccountType);

            AccountType autoSavingAccountType = new AccountType();
            autoSavingAccountType.setName("RETIREMENT SAVINGS");
            autoSavingAccountType.setNumber("12");
            savingAccountTypeList.add(autoSavingAccountType);

            AccountType vacationSavingAccountType = new AccountType();
            vacationSavingAccountType.setName("DAILY SAVINGS");
            vacationSavingAccountType.setNumber("13");
            savingAccountTypeList.add(vacationSavingAccountType);

            AccountType constructionSavingAccountType = new AccountType();
            constructionSavingAccountType.setName("MEDICAL SAVINGS");
            constructionSavingAccountType.setNumber("14");
            savingAccountTypeList.add(constructionSavingAccountType);

            AccountType familySavingAccountType = new AccountType();
            familySavingAccountType.setName("SOCIAL SAVINGS");
            familySavingAccountType.setNumber("15");
            savingAccountTypeList.add(familySavingAccountType);

            AccountType otherSavingAccountType = new AccountType();
            otherSavingAccountType.setName("BUSINESS SAVINGS");
            otherSavingAccountType.setNumber("16");
            savingAccountTypeList.add(otherSavingAccountType);

            AccountType yearlySavingAccountType = new AccountType();
            yearlySavingAccountType.setName("CHILDREN SAVINGS");
            yearlySavingAccountType.setNumber("17");
            savingAccountTypeList.add(yearlySavingAccountType);

            AccountType monthlSavingAccountType = new AccountType();
            monthlSavingAccountType.setName("REAL ESTATE SAVINGS");
            monthlSavingAccountType.setNumber("18");
            savingAccountTypeList.add(monthlSavingAccountType);

            AccountType dailySavingAccountType = new AccountType();
            dailySavingAccountType.setName("EDUCATION SAVINGS");
            dailySavingAccountType.setNumber("19");
            savingAccountTypeList.add(dailySavingAccountType);



            AccountType shortTermLoanType = new AccountType();
            shortTermLoanType.setName("SHORT TERM LOAN");
            shortTermLoanType.setNumber("41");
            savingAccountTypeList.add(shortTermLoanType);

            AccountType consumptionType = new AccountType();
            consumptionType.setName("CONSUMPTION LOAN");
            consumptionType.setNumber("42");
            savingAccountTypeList.add(consumptionType);

            AccountType agricultureLoanType = new AccountType();
            agricultureLoanType.setName("AGRICULTURE LOAN");
            agricultureLoanType.setNumber("43");
            savingAccountTypeList.add(agricultureLoanType);

            AccountType businessLoanType = new AccountType();
            businessLoanType.setName("BUSINESS INVESTMENT LOAN");
            businessLoanType.setNumber("44");
            savingAccountTypeList.add(businessLoanType);

            AccountType schoolFeesType = new AccountType();
            schoolFeesType.setName("SCHOOL FEES LOAN");
            schoolFeesType.setNumber("45");
            savingAccountTypeList.add(schoolFeesType);

            AccountType realEstateType = new AccountType();
            realEstateType.setName("REAL INVESTMENT LOAN");
            realEstateType.setNumber("46");
            savingAccountTypeList.add(realEstateType);

            AccountType overdraftType = new AccountType();
            overdraftType.setName("OVER DRAFT LOAN");
            overdraftType.setNumber("47");
            savingAccountTypeList.add(overdraftType);

            AccountType njangiType = new AccountType();
            njangiType.setName("NJANGI FINANCING LOAN");
            njangiType.setNumber("48");
            savingAccountTypeList.add(njangiType);

            Iterable<AccountType> savingAccountTypeListIterable = savingAccountTypeList;
            accountTypeRepository.saveAll(savingAccountTypeListIterable);
        }
    }



    public ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findAllByUserRoleIn(userRole);
    }

    public ArrayList<User> findAllByUserNotRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findDistintAllByUserRoleNotIn(userRole);
    }
}
