package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.*;
import com.bitsvaley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SavingAccountTypeRepository savingsAccountTypeRepository;

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
        insureRolesExists(user.getUserRole().get(0).getName());
        User save = userRepository.save(user);
        return save;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    private UserRole insureRolesExists(String name) {
        UserRole role = userRoleService.findUserRoleByName(name);
        Iterable<UserRole> all = userRoleService.findAll();
        if (role == null) {
//            role = new UserRole();
//            role.setName(name);
//            userRoleService.saveUserRole(role);

            List<UserRole> userRolesList = new ArrayList<UserRole>();
            UserRole customer = new UserRole();
            customer.setName("CUSTOMER");
            userRolesList.add(customer);

            UserRole admin = new UserRole();
            admin.setName("ADMIN");
            userRolesList.add(admin);

            UserRole manager = new UserRole();
            manager.setName("MANAGER");
            userRolesList.add(manager);

            UserRole agent = new UserRole();
            agent.setName("AGENT");
            userRolesList.add(agent);

            UserRole auditor = new UserRole();
            auditor.setName("AUDITOR");
            userRolesList.add(auditor);
            Iterable<UserRole> iterable = userRolesList;
            userRoleService.saveAllRole(iterable);
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

            Iterable<SavingAccountType> savingAccountTypeListIterable = savingAccountTypeList;
            savingsAccountTypeRepository.saveAll(savingAccountTypeListIterable);
        }
        return role;
    }



    public ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findAllByUserRoleIn(userRole);
    }

}
