package com.bitsvalley.micro.impl;

import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username);

        if(null == user && username.equals("admin")){
            Set<GrantedAuthority> grantedAuthority = new HashSet<GrantedAuthority>();
            grantedAuthority.add(new SimpleGrantedAuthority("ADMIN"));//one role
            return new org.springframework.security.core.userdetails.User("admin", "admin", grantedAuthority);
        }
        else if(null == user ) throw new UsernameNotFoundException(username);
        Set<GrantedAuthority> grantedAuthority = new HashSet<GrantedAuthority>();
        for(UserRole userRole : user.getUserRole()){
            grantedAuthority.add(new SimpleGrantedAuthority(userRole.getName()));
        }
        grantedAuthority.add(new SimpleGrantedAuthority(user.getUserRole().get(0).getName()));//one role
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), grantedAuthority);

    }


//    private void init(){
//        insureCustomerRolesExists();
//        User user = new User();
//        user.setUserName("bitsvalley");
//        user.setPassword("bitsvalley");
//        user.setFirstName("Ignatius");
//        user.setLastName("Fonji");
//        user.setEmail("info@bitsvalley.com");
//        user.setBeneficiary("beneficiary");
//        user.setDateOfBirth(LocalDateTime.now().toString());
//        user.setAddress("LaChance Garage");
//        user.setTelephone1("671 78 992");
//        user.setTelephone2("671 98 966");
//
//        UserRole admin = new UserRole();
//        admin.setName("ADMIN");
//        List<UserRole> userRoleList = new ArrayList<UserRole>();
//        userRoleList.add(admin);
//        user.setUserRole(userRoleList);
//        User u = userService.createUser(user);
//        int i = 0;
//    }
//
//    private void insureCustomerRolesExists() {
////        UserRole userRole = userRoleService.findUserRoleByName(com.bitsvaley.micro.utils.UserRole.CUSTOMER.name());
//        Iterable<UserRole> all = userRoleService.findAll();
//        if(all ==null || !all.iterator().hasNext()){
//
//            UserRole customer = new UserRole();
//            customer .setName("CUSTOMER");
////            userRoleService.saveUserRole(customer);
//
//            UserRole agent = new UserRole();
//            agent.setName("AGENT");
////            userRoleService.saveUserRole(agent);
//
//            UserRole manager = new UserRole();
//            manager.setName("MANAGER");
////            userRoleService.saveUserRole(manager);
//
//            UserRole board = new UserRole();
//            board.setName("BOARD_MEMBER");
////            userRoleService.saveUserRole(board);
//
//            UserRole auditor = new UserRole();
//            auditor.setName("AUDITOR");
////            userRoleService.saveUserRole(auditor);
//
////            UserRole admin = new UserRole();
////            admin.setName("ADMIN");
////            userRoleService.saveUserRole(admin);
//
//            UserRole userRoles[] = { customer, agent, manager, board, auditor};
//            userRoleService.saveAllRole(Arrays.asList(userRoles));
//        }
//
//    }

}
