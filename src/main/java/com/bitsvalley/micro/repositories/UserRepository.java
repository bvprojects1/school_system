package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUserName(String userName);

    ArrayList<User> findAllByUserRoleIn(ArrayList<UserRole> userRole);

    ArrayList<User> findDistintAllByUserRoleNotIn(ArrayList<UserRole> userRole);


}
