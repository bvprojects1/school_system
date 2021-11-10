package com.bitsvalley.micro.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class ShareAccountService extends SuperService{

    @Autowired
    private CallCenterService callCenterService;

}
