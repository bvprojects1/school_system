package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.LoanAccount;
import com.bitsvalley.micro.domain.LoanAccountTransaction;
import com.bitsvalley.micro.domain.SavingAccountTransaction;
import com.bitsvalley.micro.repositories.LoanAccountRepository;
import com.bitsvalley.micro.repositories.LoanAccountTransactionRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public LoanAccountTransaction createLoanAccountTransaction(LoanAccount loanAccount) {
        //Get id of savingAccount transaction
        LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
        loanAccountTransaction.setAccountOwner(loanAccount.getUser().getLastName()
                + ", " + loanAccount.getUser().getFirstName());
        loanAccountTransaction.setLoanAmount(loanAccount.getLoanAmount());
        loanAccountTransaction.setCurrentLoanAmount(loanAccount.getLoanAmount());
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

    public Optional<LoanAccountTransaction> findById(long id){
        Optional<LoanAccountTransaction> loanAccountTransaction = loanAccountTransactionRepository.findById(id);
        return loanAccountTransaction;
    }

    public Optional<LoanAccountTransaction> findByReference(String id){
        Optional<LoanAccountTransaction> loanAccountTransaction = loanAccountTransactionRepository.findByReference(id);
        return loanAccountTransaction;
    }

}
