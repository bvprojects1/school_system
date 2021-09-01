package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
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
    private SavingAccountTypeRepository savingAccountTypeRepository;

    public List<CallCenter> findByAccountNumber(String accountNumber) {
        return callCenterRepository.findByAccountNumber(accountNumber);
    }

    public List<CallCenter> findUserByFirstLastname(String firstLastName) {
        return callCenterRepository.findByAccountHolderName(firstLastName);
    }

}
