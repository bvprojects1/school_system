package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }
    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public void createUser(User user) {
        user.setCreated(LocalDateTime.now());
        user.setAccountExpired(LocalDateTime.now().plusYears(99));
        user.setLastUpdated(LocalDateTime.now());
        user.setAccountLocked(false);
        UserRole userRole = new UserRole();
        userRole.setName(com.bitsvaley.micro.utils.UserRole.CUSTOMER.name());
        List<UserRole> userRoleList = new ArrayList<UserRole>();
        userRoleList.add(userRole);
        user.setUserRole(userRoleList);
        userRepository.save(user);
    }
}
