package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.AccountTypeRepository;
import com.bitsvalley.micro.repositories.GeneralLedgerRepository;
import com.bitsvalley.micro.repositories.LedgerAccountRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.utils.GeneralLedgerType;
import com.bitsvalley.micro.webdomain.GeneralLedgerBilanz;
import com.bitsvalley.micro.webdomain.GeneralLedgerWeb;
import com.bitsvalley.micro.webdomain.LedgerEntryDTO;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class GeneralLedgerService extends SuperService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private GeneralLedgerRepository generalLedgerRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    @Autowired
    private LedgerAccountRepository ledgerAccountRepository;

    public List<GeneralLedger> findByAccountNumber(String accountNumber) {
        return generalLedgerRepository.findByAccountNumber(accountNumber);
    }

    public GeneralLedgerBilanz findByReference(String reference) {

        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(generalLedgerRepository.findByReference(reference));
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;

    }

//    public void updateSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
//        GeneralLedger generalLedger = savingAccountGLMapper(savingAccountTransaction);
//        generalLedgerRepository.save(generalLedger);
//    }

//    public void updateCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {
//        GeneralLedger generalLedger = currentAccountGLMapper(currentAccountTransaction);
//        generalLedgerRepository.save(generalLedger);
//    }

    public void updateGLWithCurrentLoanAccountTransaction(LoanAccountTransaction loanAccountTransaction) {
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.LOAN, BVMicroUtils.DEBIT, loanAccountTransaction.getLoanAmount(), true);
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.CURRENT, BVMicroUtils.CREDIT, loanAccountTransaction.getLoanAmount(), true);
    }


    public void updateGLAfterSharePurchaseFromCurrent(ShareAccountTransaction shareAccountTransaction) {
        shareAccountTransaction.setNotes(BVMicroUtils.CURRENT);
        updateGeneralLedger(shareAccountTransaction, BVMicroUtils.SHARE_GL_5004, BVMicroUtils.CREDIT, shareAccountTransaction.getShareAmount(), 5, true);
        shareAccountTransaction.setNotes(BVMicroUtils.SHARE);
        updateGeneralLedger(shareAccountTransaction, BVMicroUtils.CURRENT_GL_3004, BVMicroUtils.DEBIT, shareAccountTransaction.getShareAmount(), 3, true);
    }


    public void updateGLAfterLoanAccountCASHRepayment(LoanAccountTransaction loanAccountTransaction) {
//      GeneralLedger generalLedger = null;

        //DEBIT CASH RECEIVED
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.CASH, BVMicroUtils.DEBIT, loanAccountTransaction.getAmountReceived(), true);

        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.LOAN_INTEREST, BVMicroUtils.CREDIT, loanAccountTransaction.getInterestPaid(), true);

        //CREDIT VAT PAID
        if(loanAccountTransaction.getLoanAccount().isVatOption()){
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.VAT, BVMicroUtils.CREDIT, loanAccountTransaction.getVatPercent(), true);
        }

        loanAccountTransaction.setNotes(BVMicroUtils.CASH_GL_5001 + " " + loanAccountTransaction.getNotes());

        //PRINCIPAL PAID
        double amount = loanAccountTransaction.getAmountReceived() - loanAccountTransaction.getInterestPaid();
        if(loanAccountTransaction.getLoanAccount().isVatOption()){
            amount = loanAccountTransaction.getAmountReceived() - loanAccountTransaction.getInterestPaid() - loanAccountTransaction.getVatPercent();
        }
//        LedgerAccount ledgerAccount;
        if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "41")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.SHORT_TERM_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "42")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.CONSUMPTION_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "43")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.AGRICULTURE_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "44")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.BUSINESS_INVESTMENT_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "45")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.SCHOOL_FEES_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "46")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.REAL_ESTATE_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "47")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.SCHOOL_FEES_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else if (StringUtils.equals(
                loanAccountTransaction.getLoanAccount().getAccountType().getNumber(), "48")) {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.REAL_ESTATE_LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        } else {
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.LOAN, loanAccountTransaction.getAmountReceived() > 0 ? "CREDIT" : "DEBIT", amount, true);
        }
    }



    public void updateGLAfterLoanAccountTransferRepayment(LoanAccountTransaction loanAccountTransaction) {
//        GeneralLedger generalLedger = null;

        //DEBIT CASH RECEIVED
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.CURRENT, BVMicroUtils.DEBIT, loanAccountTransaction.getAmountReceived(), true);
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.LOAN_INTEREST, BVMicroUtils.CREDIT, loanAccountTransaction.getInterestPaid(), true);

        //CREDIT VAT PAID
        if(loanAccountTransaction.getLoanAccount().isVatOption()){
            updateGeneralLedger(loanAccountTransaction, BVMicroUtils.VAT, BVMicroUtils.CREDIT, loanAccountTransaction.getVatPercent(), true);
        }
        double amount = 0;
        //PRINCIPAL PAID
        if(loanAccountTransaction.getLoanAccount().isVatOption()){
            amount = loanAccountTransaction.getAmountReceived() - loanAccountTransaction.getInterestPaid() - loanAccountTransaction.getVatPercent();
        }else{
            amount = loanAccountTransaction.getAmountReceived() - loanAccountTransaction.getInterestPaid();
        }
        updateGeneralLedger(loanAccountTransaction, BVMicroUtils.LOAN, BVMicroUtils.CREDIT, amount, true);
    }

    private void updateGeneralLedger(ShareAccountTransaction shareAccountTransaction, String ledgerAccount, String creditDebit,
                                     double amount, int classNumber, boolean generalGL) {
        GeneralLedger generalLedger;//CREDIT INTEREST PAID
        generalLedger = shareAccountGLMapper(shareAccountTransaction, generalGL);
        LedgerAccount interestGL = ledgerAccountRepository.findByCode(ledgerAccount);
        if (generalGL) {
            generalLedger.setLedgerAccount(interestGL);
        }
        generalLedger.setType(creditDebit);
        generalLedger.setAmount(amount);

        extracted(generalLedger);

        generalLedger.setGlClass(classNumber);
        generalLedgerRepository.save(generalLedger);
    }

    private LedgerAccount updateGeneralLedger(SavingAccountTransaction savingAccountTransaction, String accountLedger, String creditDebit,
                                              double amount, boolean generalGL) {
        GeneralLedger generalLedger;//CREDIT INTEREST PAID
        generalLedger = savingAccountGLMapper(savingAccountTransaction);
        LedgerAccount ledgerAccount = ledgerAccountRepository.findByName(accountLedger);
        if (generalGL) {
            generalLedger.setLedgerAccount(ledgerAccount);
        }
        generalLedger.setType(creditDebit);
        generalLedger.setAmount(amount);
        extractClassCodeFromCode(generalLedger, ledgerAccount);
        generalLedgerRepository.save(generalLedger);
        return ledgerAccount;
    }

    private LedgerAccount updateGeneralLedger(LoanAccountTransaction loanAccountTransaction, String ledgerAccount, String creditDebit,
                                              double amount, boolean generalGL) {
        GeneralLedger generalLedger;//CREDIT INTEREST PAID
        generalLedger = loanAccountGLMapper(loanAccountTransaction, generalGL);
        LedgerAccount aLedgerAccount = null;
        if (generalGL) {
            aLedgerAccount = ledgerAccountRepository.findByName(ledgerAccount);
            generalLedger.setLedgerAccount(aLedgerAccount);
        }
        generalLedger.setType(creditDebit);
        generalLedger.setAmount(amount);
        extractClassCodeFromCode(generalLedger, aLedgerAccount);
        generalLedgerRepository.save(generalLedger);

        return aLedgerAccount;
    }

    private void extractClassCodeFromCode(GeneralLedger generalLedger, LedgerAccount aLedgerAccount) {
        String code = aLedgerAccount.getCode();
        String classCode = code.substring(code.length() - 4, code.length() - 3);
        generalLedger.setGlClass(new Integer(classCode));
    }

    private void updateGeneralLedger(CurrentAccountTransaction currentAccountTransaction, String ledgerAccount, String creditDebit,
                                     double amount, int classNumber, boolean generalGL) {
        GeneralLedger generalLedger;//CREDIT INTEREST PAID
        generalLedger = currentAccountGLMapper(currentAccountTransaction, generalGL);
        LedgerAccount currentGL = ledgerAccountRepository.findByName(ledgerAccount);
        if (generalGL) {
            generalLedger.setLedgerAccount(currentGL);
        }
        generalLedger.setType(creditDebit);
        generalLedger.setAmount(amount);
        extractClassCodeFromCode(generalLedger, currentGL);
        generalLedgerRepository.save(generalLedger);
    }

//    public void updateLoanAccountCreation(LoanAccount loanAccount) {
//        GeneralLedger generalLedger = loanAccountGLMapper(loanAccount);
//        generalLedgerRepository.save(generalLedger);
//    }

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

    private GeneralLedger loanAccountGLMapper(LoanAccountTransaction loanAccountTransaction, boolean generalGL) {
        GeneralLedger gl = new GeneralLedger();
        if (!generalGL) {
            gl.setAccountNumber(loanAccountTransaction.getLoanAccount().getAccountNumber());
        } else {
            gl.setAccountNumber(null);
        }
        gl.setAmount(loanAccountTransaction.getAmountReceived());
        Date date = BVMicroUtils.convertToDate(loanAccountTransaction.getCreatedDate());
        gl.setDate(date);
        gl.setCreatedDate(date);

        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(loanAccountTransaction.getNotes());
        gl.setReference(loanAccountTransaction.getReference());
        gl.setLastUpdatedBy(loanAccountTransaction.getCreatedBy());
        gl.setCreatedBy(loanAccountTransaction.getCreatedBy());
        return gl;
    }

    private GeneralLedger shareAccountGLMapper(ShareAccountTransaction shareAccountTransaction, boolean generalGL) {
        GeneralLedger gl = new GeneralLedger();
        if (!generalGL) {
            gl.setAccountNumber(shareAccountTransaction.getShareAccount().getAccountNumber());
        } else {
            gl.setAccountNumber(null);
        }
        gl.setAmount(shareAccountTransaction.getShareAmount());
        Date date = BVMicroUtils.convertToDate(shareAccountTransaction.getCreatedDate());
        gl.setDate(date);
        gl.setCreatedDate(date);

        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(shareAccountTransaction.getNotes());
        gl.setReference(shareAccountTransaction.getReference());
        gl.setLastUpdatedBy(shareAccountTransaction.getCreatedBy());
        gl.setCreatedBy(shareAccountTransaction.getCreatedBy());
        return gl;
    }


    private GeneralLedger currentAccountGLMapper(CurrentAccountTransaction savingAccountTransaction, boolean generalGL) {
        GeneralLedger gl = new GeneralLedger();

        if (!generalGL) {
            gl.setAccountNumber(savingAccountTransaction.getCurrentAccount().getAccountNumber());
        } else {
            gl.setAccountNumber(null);
        }

        gl.setAmount(savingAccountTransaction.getCurrentAmount());
        Date date = new Date();
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(savingAccountTransaction.getNotes());
        gl.setReference(savingAccountTransaction.getReference());
        gl.setLastUpdatedBy(savingAccountTransaction.getCreatedBy());
        gl.setCreatedBy(savingAccountTransaction.getCreatedBy());
        gl.setGlClass(3); //TODO Saving which class in GL ?
        gl.setType(savingAccountTransaction.getCurrentAmount() <= 0 ? "CREDIT" : "DEBIT");
        return gl;
    }

    private GeneralLedger savingAccountGLMapper(SavingAccountTransaction savingAccountTransaction) {
        GeneralLedger gl = new GeneralLedger();
        gl.setAccountNumber(savingAccountTransaction.getSavingAccount().getAccountNumber());
        gl.setAmount(savingAccountTransaction.getSavingAmount());
        Date date = BVMicroUtils.convertToDate(savingAccountTransaction.getCreatedDate());
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(date);
        gl.setNotes(savingAccountTransaction.getNotes());
        gl.setReference(savingAccountTransaction.getReference());
        gl.setLastUpdatedBy(savingAccountTransaction.getCreatedBy());
        gl.setCreatedBy(savingAccountTransaction.getCreatedBy());
        gl.setGlClass(3); //TODO Saving which class in GL ?
        gl.setType(savingAccountTransaction.getSavingAmount() >= 0 ? "CREDIT" : "DEBIT");
        return gl;
    }
//
//    private String getGeneralLedgerType(double amount) {
//            return amount>0?GeneralLedgerType.DEBIT.name():GeneralLedgerType.CREDIT.name();
//    }

    public GeneralLedgerBilanz findAll() {
        Iterable<GeneralLedger> glIterable = generalLedgerRepository.findAll();
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glIterable);
//        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
//        glIterable.forEach(result::add);
        return getGeneralLedgerBilanz(generalLedgerWebs);
    }

    public List<GeneralLedgerWeb> mapperGeneralLedger(Iterable<GeneralLedger> resultGeneralLedger) {
        final Iterator<GeneralLedger> iterator = resultGeneralLedger.iterator();
        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
        while (iterator.hasNext()) {
            GeneralLedger next = iterator.next();
            result.add(extracted(next));
        }
        return result;
    }


    public List<GeneralLedgerWeb> mapperGeneralLedger(List<GeneralLedger> gls) {

        List<GeneralLedgerWeb> result = new ArrayList<GeneralLedgerWeb>();
        for (GeneralLedger next : gls) {
            result.add(extracted(next));
        }
        return result;
    }


    private GeneralLedgerWeb extracted(GeneralLedger next) {
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
        generalLedgerWeb.setLedgerAccount(next.getLedgerAccount());
        return generalLedgerWeb;

    }

    @NotNull
    private GeneralLedgerBilanz getGeneralLedgerBilanz(List<GeneralLedgerWeb> generalLedgerList) {
        double debitTotal = 0.0;
        double creditTotal = 0.0;
        double currentTotal = 0.0;
        GeneralLedgerBilanz bilanz = new GeneralLedgerBilanz();
        for (GeneralLedgerWeb current : generalLedgerList) {

            if (GeneralLedgerType.CREDIT.name().equals(current.getType())) {
                creditTotal = creditTotal + current.getAmount();
                currentTotal = currentTotal + current.getAmount();
            } else if (GeneralLedgerType.DEBIT.name().equals(current.getType())) {
                debitTotal = debitTotal + current.getAmount();
                currentTotal = currentTotal + current.getAmount();
            }
            current.setCurrentTotal(debitTotal - creditTotal);
        }

        bilanz.setTotal(debitTotal - creditTotal);
        bilanz.setDebitTotal(debitTotal);
        bilanz.setCreditTotal(creditTotal);

        Collections.reverse(generalLedgerList);
        bilanz.setGeneralLedgerWeb(generalLedgerList);
        return bilanz;
    }

    public GeneralLedgerBilanz findGLByType(String type) {
        List<GeneralLedger> glByType = generalLedgerRepository.findGLByType(type);
        List<GeneralLedgerWeb> generalLedgerWebList = new ArrayList<GeneralLedgerWeb>();
        for (GeneralLedger aGeneralLedger : glByType) {
            generalLedgerWebList.add(extracted(aGeneralLedger));
        }
        Collections.reverse(generalLedgerWebList);
        return getGeneralLedgerBilanz(generalLedgerWebList);
    }

//    public GeneralLedgerBilanz findGLByAccountLedger(Long ledgerAccountId) {
//        LedgerAccount byId = ledgerAccountRepository.findById(ledgerAccountId).get();
//
//        List<GeneralLedger> glByType = generalLedgerService.findGLByAccountLedger(byId);
//        List<GeneralLedgerWeb> generalLedgerWebList = new ArrayList<GeneralLedgerWeb>();
//        for ( GeneralLedger aGeneralLedger : glByType ) {
//            generalLedgerWebList.add(extracted(aGeneralLedger));
//        }
//        Collections.reverse( generalLedgerWebList );
//        return getGeneralLedgerBilanz( generalLedgerWebList );
//    }

    public GeneralLedgerBilanz searchCriteria(String startDate, String endDate, String agentUsername, long ledgerAccount) {
        List<GeneralLedger> glList = null;
        if (ledgerAccount == -1 && agentUsername.equals("-1")) {
            glList = generalLedgerRepository.searchCriteriaStartEndDate(startDate, endDate);
        } else if (ledgerAccount != -1 && agentUsername.equals("-1")) {
            glList = generalLedgerRepository.searchCriteriaLedger(startDate, endDate, ledgerAccount);
        } else if (ledgerAccount == -1 && !agentUsername.equals("-1")) {
            glList = generalLedgerRepository.searchCriteriaWithCreatedBy(startDate, endDate, agentUsername);
        } else if (ledgerAccount != -1 && !agentUsername.equals("-1")) {
            glList = generalLedgerRepository.searchCriteriaWithCreatedByAndLedgerAccount(startDate, endDate, agentUsername, ledgerAccount);
        }

        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glList);
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;
    }

    public GeneralLedgerBilanz findGLByLedgerAccount(long ledgerAccountId) {

        LedgerAccount ledgerAccount = ledgerAccountRepository.findById(ledgerAccountId).get();
        List<GeneralLedger> glList = ledgerAccount.getGeneralLedger();

//        List<GeneralLedger> glList = generalLedgerRepository.searchCriteriaWithLedgerAccount(  ledgerAccountId );
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glList);
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;
    }

    @Transactional
    public void updateManualAccountTransaction(LedgerEntryDTO ledgerEntryDTO) {
        Date date = new Date();
        String loggedInUserName = getLoggedInUserName();
        GeneralLedger generalLedger = new GeneralLedger();
        generalLedger.setType(ledgerEntryDTO.getCreditOrDebit());

        generalLedger.setNotes(ledgerEntryDTO.getNotes());
        generalLedger.setReference(BVMicroUtils.getSaltString());

        generalLedger.setDate(date);
        generalLedger.setLastUpdatedBy(loggedInUserName);
        generalLedger.setCreatedBy(loggedInUserName);
        generalLedger.setAmount(ledgerEntryDTO.getLedgerAmount());
        generalLedger.setCreatedDate(date);
        generalLedger.setLastUpdatedDate(date);

        LedgerAccount originalAccount = ledgerAccountRepository.findById(ledgerEntryDTO.getOriginLedgerAccount()).get();
        generalLedger.setLedgerAccount(originalAccount);
        generalLedger.setGlClass(new Integer(originalAccount.getCategory().substring(0, 1)));
        generalLedgerRepository.save(generalLedger);

        //record opposite double entry
        generalLedger = new GeneralLedger();
        generalLedger.setType(BVMicroUtils.getOppositeCreditOrDebit(ledgerEntryDTO.getCreditOrDebit()));
        generalLedger.setGlClass(4);
        generalLedger.setNotes(ledgerEntryDTO.getNotes());
        generalLedger.setReference(BVMicroUtils.getSaltString());

        generalLedger.setDate(date);
        generalLedger.setLastUpdatedBy(loggedInUserName);
        generalLedger.setCreatedBy(loggedInUserName);
        generalLedger.setAmount(ledgerEntryDTO.getLedgerAmount());
        generalLedger.setCreatedDate(date);
        generalLedger.setLastUpdatedDate(date);

        LedgerAccount destinetionAccount = ledgerAccountRepository.findById(ledgerEntryDTO.getDestinationLedgerAccount()).get();
        generalLedger.setLedgerAccount(destinetionAccount);
        generalLedger.setGlClass(new Integer(destinetionAccount.getCategory().substring(0, 1)));
        generalLedgerRepository.save(generalLedger);

    }

    public void updateGLAfterSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        savingAccountTransaction.getNotes();
        LedgerAccount ledgerAccount;
//        if(savingAccountTransaction.getSavingAmount()<0){
//            savingAccountTransaction.setSavingAmount(savingAccountTransaction.getSavingAmount()*-1);
//        }
        if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CASH)) {
            savingAccountTransaction.setNotes(BVMicroUtils.CASH_GL_5001 + " " + savingAccountTransaction.getNotes());

            if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "11")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.GENERAL_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());

            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "12")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.RETIREMENT_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "13")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.DAILY_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "14")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.MEDICAL_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "15")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.BUSINESS_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "16")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SOCIAL_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "17")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.CHILDREN_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "13")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.EDUCATION_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "13")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.REAL_ESTATE_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            }

            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.CASH, savingAccountTransaction.getSavingAmount() > 0 ? "DEBIT" : "CREDIT", savingAccountTransaction.getSavingAmount(), true);
        }
    }

    public void updateGLAfterCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {

        currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CASH)) {
            currentAccountTransaction.setNotes(BVMicroUtils.CASH_GL_5001 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT, currentAccountTransaction.getCurrentAmount() > 0 ? "CREDIT" : "DEBIT", currentAccountTransaction.getCurrentAmount(), 3, true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CASH_GL_5001, currentAccountTransaction.getCurrentAmount() > 0 ? "DEBIT" : "CREDIT", currentAccountTransaction.getCurrentAmount(), 3, true);
        }
    }

    public void updateGLAfterCurrentCurrentTransfer(CurrentAccountTransaction currentAccountTransaction) {

        currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CURRENT_CURRENT_TRANSFER)) {
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT_GL_3004, "DEBIT", currentAccountTransaction.getCurrentAmount(), 3, true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT_GL_3004, "CREDIT", currentAccountTransaction.getCurrentAmount(), 3, true);
        }
    }

    public void updateGLAfterCurrentDebitTransfer(CurrentAccountTransaction currentAccountTransaction) {

        currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CURRENT_DEBIT_TRANSFER)) {
            currentAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT_GL_3004, "DEBIT", currentAccountTransaction.getCurrentAmount(), 3, true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.SAVINGS_GL_3003, "CREDIT", currentAccountTransaction.getCurrentAmount(), 3, true);
        }
    }

    public void updateGLAfterDebitDebitTransfer(SavingAccountTransaction savingAccountTransaction) {

        savingAccountTransaction.getNotes();
        if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.DEBIT_DEBIT_TRANSFER)) {
            savingAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + savingAccountTransaction.getNotes());
            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SAVINGS_GL_3003, "DEBIT", savingAccountTransaction.getSavingAmount(), true);
            savingAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + savingAccountTransaction.getNotes());
            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SAVINGS_GL_3003, "CREDIT", savingAccountTransaction.getSavingAmount(), true);
        }
    }

    public void updateGLAfterDebitCurrentTransfer(SavingAccountTransaction savingAccountTransaction) {

        savingAccountTransaction.getNotes();
        if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.DEBIT_CURRENT_TRANSFER)) {
            savingAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + savingAccountTransaction.getNotes());
            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.CURRENT_GL_3004, "DEBIT", savingAccountTransaction.getSavingAmount(), true);
            savingAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + savingAccountTransaction.getNotes());
            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SAVINGS_GL_3003, "CREDIT", savingAccountTransaction.getSavingAmount(), true);
        }
    }

    @Transactional
    public void updateGLAfterLedgerAccountMultipleAccountEntry(LedgerEntryDTO newLedgerEntryDTO) {
        List<String> paramValueString = newLedgerEntryDTO.getParamValueString();
        String accountNumber = "";
        String accountAmount = "";
        for (String aString : paramValueString) {
            String[] s = aString.split("_");
            accountNumber = s[0];
            accountAmount = s[1];

            determineLedgerAccount(accountNumber);

        }

        int result = 0;
    }

    public LedgerAccount determineLedgerAccount(String accountNumber) {
        String productCode = accountNumber.substring(2, 4);
        if (productCode.equals("20")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.CURRENT);
        } else if (productCode.equals("11")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.GENERAL_SAVINGS);
        } else if (productCode.equals("12")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.RETIREMENT_SAVINGS);
        } else if (productCode.equals("13")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.DAILY_SAVINGS);
        } else if (productCode.equals("14")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.MEDICAL_SAVINGS);
        } else if (productCode.equals("15")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.SOCIAL_SAVINGS);
        } else if (productCode.equals("16")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.BUSINESS_SAVINGS);
        } else if (productCode.equals("17")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.CHILDREN_SAVINGS);
        } else if (productCode.equals("18")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.REAL_ESTATE_SAVINGS);
        } else if (productCode.equals("19")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.EDUCATION_SAVINGS);
        } else if (productCode.equals("41")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.SHORT_TERM_LOAN);
        } else if (productCode.equals("42")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.CONSUMPTION_LOAN);
        } else if (productCode.equals("43")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.AGRICULTURE_LOAN);
        } else if (productCode.equals("44")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.BUSINESS_INVESTMENT_LOAN);
        } else if (productCode.equals("45")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.SCHOOL_FEES_LOAN);
        } else if (productCode.equals("46")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.REAL_ESTATE_LOAN);
        } else if (productCode.equals("47")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.REAL_ESTATE_SAVINGS);
        } else if (productCode.equals("48")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.OVERDRAFT_LOAN);
        } else if (productCode.equals("45")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.NJANGI_FINANCING);
        }
        return ledgerAccountRepository.findByName(BVMicroUtils.NO_NAME);
    }

}
