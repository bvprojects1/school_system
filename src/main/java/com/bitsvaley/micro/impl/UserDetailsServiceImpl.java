package com.bitsvaley.micro.impl;

import com.bitsvaley.micro.domain.User;
import com.bitsvaley.micro.domain.UserRole;
import com.bitsvaley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if(null == user) throw new UsernameNotFoundException(username);
        Set<GrantedAuthority> grantedAuthority = new HashSet<GrantedAuthority>();
        for(UserRole userRole : user.getUserRole()){
            grantedAuthority.add(new SimpleGrantedAuthority(userRole.getName()));
        }
        grantedAuthority.add(new SimpleGrantedAuthority(user.getUserRole().get(0).getName()));//one role
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), grantedAuthority);
    }
}
