package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.SavingAccount;
import com.bitsvaley.micro.domain.SavingAccountTransaction;
import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

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
//        insureUserRolesExists(user.getUserRole().get(0).getName());
        User save = userRepository.save(user);
        return save;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    private UserRole insureUserRolesExists(String name) {
        UserRole role = userRoleService.findUserRoleByName(name);
        Iterable<UserRole> all = userRoleService.findAll();
        if (role == null) {
            role = new UserRole();
            role.setName(name);
            userRoleService.saveUserRole(role);
        }
        return role;
    }



    public ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole) {
        return userRepository.findAllByUserRoleIn(userRole);
    }

}
