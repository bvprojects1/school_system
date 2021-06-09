package com.bitsvaley.micro.repositories;

import com.bitsvaley.micro.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUserName(String userName);

//    User saveUser(User user);
}
