package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    UserRole findByName(String name);
}
