package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void createUser(User user) {

        user.setCreated(LocalDateTime.now());
        user.setAccountExpiredDate(LocalDateTime.now().plusYears(99));
        user.setAccountBlockedDate(LocalDateTime.now().plusYears(99));
        user.setLastUpdated(LocalDateTime.now());

        user.setAccountExpired(false);
        user.setAccountLocked(false);

        List<UserRole> userRoleList = new ArrayList<UserRole>();
        userRoleList.add(insureCustomerRoleExists());// create a new customer role if none exists
        user.setUserRole(userRoleList);
        userRepository.save(user);
    }

    /*
        Insure a role 'CUSTOMER' exists in USER_ROLE table and use it. We are making sure a 'CUSTOMER' role
        exists if not create one
     */
    private UserRole insureCustomerRoleExists() {
        UserRole userRole = userRoleService.findUserRoleByName(com.bitsvaley.micro.utils.UserRole.CUSTOMER.name());
        if( null == userRole ){
            UserRole newUserRole = new UserRole();
            newUserRole.setName("CUSTOMER");
            userRoleService.saveUserRole(newUserRole);
            return newUserRole;
        }
        return userRole;
    }

}
