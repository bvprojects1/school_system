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
        user.setAccountExpiredDate(LocalDateTime.now().plusYears(99));
        user.setAccountBlockedDate(LocalDateTime.now().plusYears(99));
        user.setLastUpdated(LocalDateTime.now());

        user.setAccountExpired(false);
        user.setAccountLocked(false);
        insureRolesExists();
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

    private void insureRolesExists(){
        SavingAccountType school_saving = savingAccountTypeRepository.findByName("SCHOOL SAVING");
//        Iterable<UserRole> all = userRoleService.findAll();
        if (school_saving == null) {
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
            schoolSavingAccountType.setName("SCHOOL SAVING");
            savingAccountTypeList.add(schoolSavingAccountType);

            SavingAccountType autoSavingAccountType = new SavingAccountType();
            autoSavingAccountType.setName("AUTO SAVING");
            savingAccountTypeList.add(autoSavingAccountType);

            SavingAccountType vacationSavingAccountType = new SavingAccountType();
            vacationSavingAccountType.setName("VACATION SAVING");
            savingAccountTypeList.add(vacationSavingAccountType);

            SavingAccountType constructionSavingAccountType = new SavingAccountType();
            constructionSavingAccountType.setName("CONSTRUCTION SAVING");
            savingAccountTypeList.add(constructionSavingAccountType);

            SavingAccountType familySavingAccountType = new SavingAccountType();
            familySavingAccountType.setName("FAMILY SAVING");
            savingAccountTypeList.add(familySavingAccountType);

            SavingAccountType otherSavingAccountType = new SavingAccountType();
            otherSavingAccountType.setName("OTHER SAVING");
            savingAccountTypeList.add(otherSavingAccountType);

            SavingAccountType yearlySavingAccountType = new SavingAccountType();
            yearlySavingAccountType.setName("YEARLY SAVING");
            savingAccountTypeList.add(yearlySavingAccountType);

            SavingAccountType monthlSavingAccountType = new SavingAccountType();
            monthlSavingAccountType.setName("MONTHLY SAVING");
            savingAccountTypeList.add(monthlSavingAccountType);

            SavingAccountType dailySavingAccountType = new SavingAccountType();
            dailySavingAccountType.setName("DAILY SAVING");
            savingAccountTypeList.add(dailySavingAccountType);

            Iterable<SavingAccountType> savingAccountTypeListIterable = savingAccountTypeList;
            savingAccountTypeRepository.saveAll(savingAccountTypeListIterable);
        }
    }



    public ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findAllByUserRoleIn(userRole);
    }

}
