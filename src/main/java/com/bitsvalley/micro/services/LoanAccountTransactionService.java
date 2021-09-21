package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.LoanAccount;
import com.bitsvalley.micro.domain.LoanAccountTransaction;
import com.bitsvalley.micro.domain.User;
import com.bitsvalley.micro.repositories.LoanAccountRepository;
import com.bitsvalley.micro.repositories.LoanAccountTransactionRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class LoanAccountTransactionService extends SuperService {

    @Autowired
    private LoanAccountTransactionRepository loanAccountTransactionRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private LoanAccountRepository loanAccountRepository;


    @Transactional
    public void createLoanAccountTransaction(LoanAccountTransaction loanAccountTransaction) {
        //Get id of savingAccount transaction
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString()); //Collision
        loanAccountTransaction.setCreatedBy(getLoggedInUserName());
        loanAccountTransaction.setCreatedDate(LocalDateTime.now());
        loanAccountTransactionRepository.save(loanAccountTransaction);
        generalLedgerService.updateLoanAccountTransaction(loanAccountTransaction);
    }

    @Transactional
    public LoanAccountTransaction createLoanAccountTransaction(LoanAccount loanAccount) {
        //Get id of savingAccount transaction
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        loanAccountTransaction.setAccountOwner(loanAccount.getUser().getLastName()
                + ", " + loanAccount.getUser().getFirstName());
        loanAccountTransaction.setLoanAmount(loanAccount.getLoanAmount()*-1);
        Optional<LoanAccount> byId = loanAccountRepository.findById(loanAccount.getId());
        loanAccount = byId.get();
        loanAccountTransaction.setLoanAccount(loanAccount);
        loanAccountTransaction.setCreatedDate(LocalDateTime.now());
        loanAccountTransaction.setLoanAmountInLetters(" In letters " + loanAccount.getLoanAmount());
        loanAccountTransaction.setBranchCode(loanAccount.getBranchCode());
        loanAccountTransaction.setCreatedBy(loanAccount.getCreatedBy());
        loanAccountTransaction.setBranchCountry(loanAccount.getCountry());
        loanAccountTransaction.setNotes(loanAccount.getNotes());
        loanAccountTransaction.setReference(BVMicroUtils.getSaltString());
        loanAccountTransaction.setModeOfPayment("RECEIPT");
        loanAccountTransactionRepository.save(loanAccountTransaction);
        if (loanAccount.getLoanAccountTransaction() != null) {
            loanAccount.getLoanAccountTransaction().add(loanAccountTransaction);
        } else {
            ArrayList<LoanAccountTransaction> arrayList = new ArrayList<LoanAccountTransaction>();
            arrayList.add(loanAccountTransaction);
            loanAccount.setLoanAccountTransaction(arrayList);
        }
        return loanAccountTransaction;
    }

}
