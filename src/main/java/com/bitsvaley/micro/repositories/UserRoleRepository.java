package com.bitsvaley.micro.repositories;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String name);
}
