package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public Optional<UserRole> findUserRoleById(long id) {
        return userRoleRepository.findById(id);
    }
    public UserRole findUserRoleByName(String name) {
        return userRoleRepository.findByName(name);
    }

    public void saveUserRole(UserRole userRole){
        userRoleRepository.save(userRole);
    }

    public void saveAllRole(Iterable<UserRole> iterableUserRole){
        Iterable<UserRole> iterable = userRoleRepository.saveAll(iterableUserRole);
    }

    public Iterable<UserRole>  findAll(){
        Iterable<UserRole> all = userRoleRepository.findAll();
        return all;
    }

}
