package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.domain.SavingAccountType;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    private CallCenterRepository callCenterRepository;

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }
    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User createUser(User user) {

        user.setCreated(LocalDateTime.now());
        user.setAccountExpiredDate(LocalDateTime.now().plusMonths(6));
        user.setAccountBlockedDate(LocalDateTime.now().plusMonths(6));
        user.setLastUpdated(LocalDateTime.now());

        user.setAccountExpired(false);
        user.setAccountLocked(false);
        insureSavingAccountExists();
        User save = userRepository.save(user);

        // - Update callcenter
        CallCenter cc = new CallCenter();
        cc.setUserName(user.getUserName());
        cc.setDate(new Date(System.currentTimeMillis()));
        cc.setAccountHolderName(user.getFirstName() +", "+ user.getLastName());
        cc.setNotes("Login account created by "+ user.getCreatedBy() + " on " + user.getCreated() );
        callCenterRepository.save(cc);

        return save;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    private void insureSavingAccountExists(){
        SavingAccountType genral_saving = savingAccountTypeRepository.findByName("GENERAL SAVINGS");
//        Iterable<UserRole> all = userRoleService.findAll();
        if (genral_saving == null) {
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

            List<SavingAccountType> savingAccountTypeList = new ArrayList<SavingAccountType>();
            SavingAccountType schoolSavingAccountType = new SavingAccountType();
            schoolSavingAccountType.setNumber("11");
            schoolSavingAccountType.setName("GENERAL SAVINGS");
            savingAccountTypeList.add(schoolSavingAccountType);

            SavingAccountType autoSavingAccountType = new SavingAccountType();
            autoSavingAccountType.setName("RETIREMENT SAVINGS");
            autoSavingAccountType.setNumber("12");
            savingAccountTypeList.add(autoSavingAccountType);

            SavingAccountType vacationSavingAccountType = new SavingAccountType();
            vacationSavingAccountType.setName("DAILY SAVINGS");
            vacationSavingAccountType.setNumber("13");
            savingAccountTypeList.add(vacationSavingAccountType);

            SavingAccountType constructionSavingAccountType = new SavingAccountType();
            constructionSavingAccountType.setName("MEDICAL SAVINGS");
            constructionSavingAccountType.setNumber("14");
            savingAccountTypeList.add(constructionSavingAccountType);

            SavingAccountType familySavingAccountType = new SavingAccountType();
            familySavingAccountType.setName("SOCIAL SAVINGS");
            familySavingAccountType.setNumber("15");
            savingAccountTypeList.add(familySavingAccountType);

            SavingAccountType otherSavingAccountType = new SavingAccountType();
            otherSavingAccountType.setName("BUSINESS SAVINGS");
            otherSavingAccountType.setNumber("16");
            savingAccountTypeList.add(otherSavingAccountType);

            SavingAccountType yearlySavingAccountType = new SavingAccountType();
            yearlySavingAccountType.setName("CHILDREN SAVINGS");
            yearlySavingAccountType.setNumber("17");
            savingAccountTypeList.add(yearlySavingAccountType);

            SavingAccountType monthlSavingAccountType = new SavingAccountType();
            monthlSavingAccountType.setName("REAL ESTATE SAVINGS");
            monthlSavingAccountType.setNumber("18");
            savingAccountTypeList.add(monthlSavingAccountType);

            SavingAccountType dailySavingAccountType = new SavingAccountType();
            dailySavingAccountType.setName("EDUCATION SAVINGS");
            dailySavingAccountType.setNumber("19");
            savingAccountTypeList.add(dailySavingAccountType);

            Iterable<SavingAccountType> savingAccountTypeListIterable = savingAccountTypeList;
            savingAccountTypeRepository.saveAll(savingAccountTypeListIterable);
        }
    }



    public ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findAllByUserRoleIn(userRole);
    }

}
