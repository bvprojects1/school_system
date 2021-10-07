package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.BranchRepository;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class BranchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private BranchRepository branchRepository;

    public List<CallCenter> findByAccountNumber(String accountNumber) {
        return callCenterRepository.findByAccountNumber(accountNumber);
    }

    public Branch getBranchInfo(String userName){
        User loggedInUser = userRepository.findByUserName(userName);
        if(loggedInUser==null && userName.equals("admin")){
            Branch branch = new Branch();
            branch.setActiv("TRUE");
            branch.setCountry("admin");
            branch.setNotes("admin");
            branch.setName("admin");
            branch.setEmail("admin");
            branch.setCity("admin");
            branch.setCode("admin");
            branch.setContact("admin");
            branch.setFax("admin");
            branch.setPoBox("admin");
            branch.setStreet("admin");
            branch.setTelephone("admin");
            branchRepository.save(branch);
            return branch;
        }

        final Branch branch = loggedInUser.getBranch();
        return branch;
    }

}
