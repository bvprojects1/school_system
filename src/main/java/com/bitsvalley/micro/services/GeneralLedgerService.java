package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.CallCenterRepository;
import com.bitsvalley.micro.repositories.GeneralLedgerRepository;
import com.bitsvalley.micro.repositories.SavingAccountTypeRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.utils.GeneralLedgerType;
import com.bitsvalley.micro.webdomain.GeneralLedgerBilanz;
import com.bitsvalley.micro.webdomain.RuntimeSetting;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class GeneralLedgerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private GeneralLedgerRepository generalLedgerRepository;

    @Autowired
    private SavingAccountTypeRepository savingAccountTypeRepository;

    public List<GeneralLedger> findByAccountNumber(String accountNumber) {
        return generalLedgerRepository.findByAccountNumber(accountNumber);
    }

    public void updateSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        GeneralLedger generalLedger = savingAccountGLMapper(savingAccountTransaction);
        generalLedgerRepository.save(generalLedger);
    }

    private GeneralLedger savingAccountGLMapper(SavingAccountTransaction savingAccountTransaction) {
        GeneralLedger gl = new GeneralLedger();
        gl.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        gl.setAmount(savingAccountTransaction.getSavingAmount());
        gl.setDate(new Date(System.currentTimeMillis()));
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(savingAccountTransaction.getNotes());
        gl.setReference(savingAccountTransaction.getReference());
        gl.setLastUpdatedBy(BVMicroUtils.SYSTEM);
        gl.setCreatedBy(BVMicroUtils.SYSTEM);
        gl.setType(getGeneralLedgetType(savingAccountTransaction.getSavingAmount()));
        return gl;
    }

    private String getGeneralLedgetType(int amount) {
            return amount>0?GeneralLedgerType.DEBIT.name():GeneralLedgerType.CREDIT.name();
    }

    public GeneralLedgerBilanz findAll() {
            Iterable<GeneralLedger> glIterable = generalLedgerRepository.findAll();
        List<GeneralLedger> result = new ArrayList<GeneralLedger>();
        glIterable.forEach(result::add);
        return getGeneralLedgerBilanz( result);
    }

    @NotNull
    private GeneralLedgerBilanz getGeneralLedgerBilanz( List<GeneralLedger> generalLedgerList) {
        int debitTotal = 0;
        int creditTotal = 0;
        GeneralLedgerBilanz bilanz = null;
        for (GeneralLedger current: generalLedgerList ) {
                bilanz = new GeneralLedgerBilanz();
                if (GeneralLedgerType.CREDIT.equals(current.getType())) {
                    creditTotal = creditTotal + current.getAmount();
                } else if (GeneralLedgerType.DEBIT.equals(current.getType())) {
                    debitTotal = debitTotal + current.getAmount();
                }
            }
        bilanz.setTotal(debitTotal+creditTotal);
        bilanz.setDebitTotal(debitTotal);
        bilanz.setCreditTotal(creditTotal);
        bilanz.setGeneralLedger(generalLedgerList);
        return bilanz;
    }

    
    public GeneralLedgerBilanz findGLByType(String type) {
        List<GeneralLedger> glByType = generalLedgerRepository.findGLByType(type);
        return getGeneralLedgerBilanz(glByType);
    }

}
