package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.domain.SavingAccountType;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.domain.UserRole;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class CallCenterService {

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
