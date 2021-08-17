package com.bitsvalley.micro.impl;

import com.bitsvalley.micro.domain.RuntimeProperties;
import com.bitsvalley.micro.domain.SavingAccountType;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.RuntimePropertiesRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.services.UserRoleService;
import com.bitsvalley.micro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    RuntimePropertiesRepository runtimePropertiesRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username);

        if(null == user && username.equals("admin")){
            Set<GrantedAuthority> grantedAuthority = new HashSet<GrantedAuthority>();
            grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//one role
            return new org.springframework.security.core.userdetails.User("admin", "admin", grantedAuthority);
        }
        else if(null == user ) throw new UsernameNotFoundException(username);
        Set<GrantedAuthority> grantedAuthority = new HashSet<GrantedAuthority>();
        for(UserRole userRole : user.getUserRole()){
            grantedAuthority.add(new SimpleGrantedAuthority(userRole.getName()));
        }
        grantedAuthority.add(new SimpleGrantedAuthority(user.getUserRole().get(0).getName()));//one role
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),true, true, true,!user.isAccountLocked(), grantedAuthority);
//        public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
//            this(username, password, true, true, true, true, authorities);
    }



}
