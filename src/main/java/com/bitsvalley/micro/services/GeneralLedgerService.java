package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.GeneralLedgerRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.utils.GeneralLedgerType;
import com.bitsvalley.micro.webdomain.GLSearchDTO;
import com.bitsvalley.micro.webdomain.GeneralLedgerBilanz;
import com.bitsvalley.micro.webdomain.GeneralLedgerWeb;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class GeneralLedgerService extends SuperService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private GeneralLedgerRepository generalLedgerRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public List<GeneralLedger> findByAccountNumber(String accountNumber) {
        return generalLedgerRepository.findByAccountNumber(accountNumber);
    }

    public void updateSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        GeneralLedger generalLedger = savingAccountGLMapper(savingAccountTransaction);
        generalLedgerRepository.save(generalLedger);
    }

    public void updateCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {
        GeneralLedger generalLedger = currentAccountGLMapper(currentAccountTransaction);
        generalLedgerRepository.save(generalLedger);
    }

    public void updateGLWithLoanAccountTransaction(LoanAccountTransaction loanAccountTransaction, String gLType) {
        GeneralLedger generalLedger = loanAccountGLMapper(loanAccountTransaction, gLType);
        generalLedgerRepository.save(generalLedger);
    }

    public void updateLoanAccountCreation(LoanAccount loanAccount) {
        GeneralLedger generalLedger = loanAccountGLMapper(loanAccount);
        generalLedgerRepository.save(generalLedger);
    }

    private GeneralLedger loanAccountGLMapper(LoanAccount loanAccount) {
        GeneralLedger gl = new GeneralLedger();
        Date aDate = new Date();
        String loggedInUserName = getLoggedInUserName();
        gl.setAccountNumber(loanAccount.getAccountNumber());
        gl.setAmount(loanAccount.getLoanAmount());
        gl.setDate(aDate);
        gl.setLastUpdatedDate(aDate);
        gl.setNotes(loanAccount.getNotes());
//        gl.setReference(loanAccount.getReference());
        gl.setLastUpdatedBy(loggedInUserName);
        gl.setCreatedBy(loggedInUserName);

        gl.setGlClass(4); //TODO Saving which class in GL ?
        gl.setType(GeneralLedgerType.CREDIT.name());
        return gl;
    }

    private GeneralLedger loanAccountGLMapper(LoanAccountTransaction loanAccountTransaction, String GLType) {
        GeneralLedger gl = new GeneralLedger();
        gl.setAccountNumber(loanAccountTransaction.getLoanAccount().getAccountNumber());
        gl.setAmount(loanAccountTransaction.getAmountReceived());
        Date date = new Date();
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(loanAccountTransaction.getNotes());
        gl.setReference(loanAccountTransaction.getReference());
        gl.setLastUpdatedBy(BVMicroUtils.SYSTEM);
        gl.setCreatedBy(BVMicroUtils.SYSTEM);
        gl.setGlClass(4); //TODO Saving which class in GL ?
        gl.setType(getGeneralLedgerType(loanAccountTransaction.getLoanAmount()));
        gl.setType(GLType);
        return gl;
    }


    private GeneralLedger currentAccountGLMapper(CurrentAccountTransaction savingAccountTransaction) {
        GeneralLedger gl = new GeneralLedger();
        gl.setAccountNumber(savingAccountTransaction.getCurrentAccount().getAccountNumber());
        gl.setAmount(savingAccountTransaction.getCurrentAmount());
        Date date = new Date();
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(savingAccountTransaction.getNotes());
        gl.setReference(savingAccountTransaction.getReference());
        gl.setLastUpdatedBy(BVMicroUtils.SYSTEM);
        gl.setCreatedBy(BVMicroUtils.SYSTEM);
        gl.setGlClass(3); //TODO Saving which class in GL ?
        gl.setType(savingAccountTransaction.getCurrentAmount()<=0?"CREDIT":"DEBIT");
        return gl;
    }

    private GeneralLedger savingAccountGLMapper(SavingAccountTransaction savingAccountTransaction) {
        GeneralLedger gl = new GeneralLedger();
        gl.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        gl.setAmount(savingAccountTransaction.getSavingAmount());
        Date date = new Date();
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(savingAccountTransaction.getNotes());
        gl.setReference(savingAccountTransaction.getReference());
        gl.setLastUpdatedBy(BVMicroUtils.SYSTEM);
        gl.setCreatedBy(BVMicroUtils.SYSTEM);
        gl.setGlClass(3); //TODO Saving which class in GL ?
        gl.setType(savingAccountTransaction.getSavingAmount()>=0?"CREDIT":"DEBIT");
        return gl;
    }

    private String getGeneralLedgerType(double amount) {
            return amount>0?GeneralLedgerType.DEBIT.name():GeneralLedgerType.CREDIT.name();
    }

    public GeneralLedgerBilanz findAll() {
            Iterable<GeneralLedger> glIterable = generalLedgerRepository.findAll();
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glIterable);
//        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
//        glIterable.forEach(result::add);
        return getGeneralLedgerBilanz( generalLedgerWebs );
    }

    public List<GeneralLedgerWeb> mapperGeneralLedger(Iterable<GeneralLedger> resultGeneralLedger){
        final Iterator<GeneralLedger> iterator = resultGeneralLedger.iterator();
        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
        while (iterator.hasNext()) {
            GeneralLedger next = iterator.next();
            result.add(extracted( next));
        }
        return result;
    }


    public List<GeneralLedgerWeb> mapperGeneralLedger(List<GeneralLedger> gls){

        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
        for (GeneralLedger next : gls ) {
            result.add(extracted( next));
        }
        return result;
    }


    private GeneralLedgerWeb  extracted( GeneralLedger next) {
        GeneralLedgerWeb generalLedgerWeb = new GeneralLedgerWeb();
        generalLedgerWeb.setCreatedDate(next.getCreatedDate());
        generalLedgerWeb.setAccountNumber(next.getAccountNumber());
        generalLedgerWeb.setCreatedBy(next.getCreatedBy());
        generalLedgerWeb.setGlClass(next.getGlClass());
        generalLedgerWeb.setLastUpdatedDate(next.getLastUpdatedDate());
        generalLedgerWeb.setNotes(next.getNotes());
        generalLedgerWeb.setAmount(next.getAmount());
        generalLedgerWeb.setLastUpdatedBy(next.getLastUpdatedBy());
        generalLedgerWeb.setType(next.getType());
        generalLedgerWeb.setReference(next.getReference());
        return generalLedgerWeb;

    }

    @NotNull
    private GeneralLedgerBilanz getGeneralLedgerBilanz( List<GeneralLedgerWeb> generalLedgerList) {
        double debitTotal = 0.0;
        double creditTotal = 0.0;
        double currentTotal = 0.0;
        GeneralLedgerBilanz bilanz = new GeneralLedgerBilanz();
        for (GeneralLedgerWeb current: generalLedgerList ) {

                if (GeneralLedgerType.CREDIT.name().equals(current.getType())) {
                    creditTotal = creditTotal + current.getAmount();
                } else if (GeneralLedgerType.DEBIT.name().equals(current.getType())) {
                    debitTotal = debitTotal + current.getAmount();
                }
                currentTotal = currentTotal + current.getAmount();
            current.setCurrentTotal(currentTotal);
        }

        bilanz.setTotal(debitTotal + creditTotal);
        bilanz.setDebitTotal(debitTotal);
        bilanz.setCreditTotal(creditTotal);

        Collections.reverse(generalLedgerList);
        bilanz.setGeneralLedgerWeb(generalLedgerList);
        return bilanz;
    }

    public GeneralLedgerBilanz findGLByType(String type) {
        List<GeneralLedger> glByType = generalLedgerRepository.findGLByType(type);
        List<GeneralLedgerWeb> generalLedgerWebList = new ArrayList<GeneralLedgerWeb>();
        for ( GeneralLedger aGeneralLedger: glByType ) {
            generalLedgerWebList.add(extracted(aGeneralLedger));
        }
        Collections.reverse( generalLedgerWebList );
        return getGeneralLedgerBilanz( generalLedgerWebList );
    }

    public GeneralLedgerBilanz searchCriteria(String startDate, String endDate, String type, String accountNumber) {
        List<GeneralLedger> glList = null;
        if(StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(accountNumber) ){
            if(type.equals("ALL")){
                glList = generalLedgerRepository.searchCriteriaWithAccountNumber( startDate, endDate, accountNumber );
            }
            else {
                glList = generalLedgerRepository.searchCriteriaWithAccountNumberAndType(type, startDate, endDate, accountNumber );
            }
        }else if("ALL".equals(type) && StringUtils.isEmpty(accountNumber) ){
            glList = generalLedgerRepository.searchCriteria( startDate, endDate );
        }else if( accountNumber != null){
            glList = generalLedgerRepository.searchCriteriaWithAccountNumber( startDate, endDate , accountNumber);
        }
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glList);
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;
    }

}
