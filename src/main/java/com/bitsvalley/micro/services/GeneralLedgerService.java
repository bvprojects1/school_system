package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.utils.GeneralLedgerType;
import com.bitsvalley.micro.webdomain.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private LoanAccountTransactionRepository loanAccountTransactionRepository;

    @Autowired
    private SavingAccountTransactionRepository savingAccountTransactionRepository;

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private CurrentAccountTransactionRepository currentAccountTransactionRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    LoanAccountService loanAccountService;

    public List<GeneralLedger> findByAccountNumber(String accountNumber) {
        return generalLedgerRepository.findByAccountNumber(accountNumber);
    }

    public GeneralLedgerBilanz findByReference(String reference) {

        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(generalLedgerRepository.findByReference(reference));
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;

    }


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
        LedgerAccount ledgerAccount = determineLedgerAccount(loanAccountTransaction.getLoanAccount().getProductCode());
        updateGeneralLedger(loanAccountTransaction, ledgerAccount.getCode(), BVMicroUtils.CREDIT, amount, true);
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
        if(ledgerAccount == null){
            ledgerAccount = ledgerAccountRepository.findByCode(accountLedger);
        }
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
            if(aLedgerAccount == null){
                aLedgerAccount = ledgerAccountRepository.findByCode(ledgerAccount);
            }
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
                                     double amount, boolean generalGL) {
        GeneralLedger generalLedger;//CREDIT INTEREST PAID
        generalLedger = currentAccountGLMapper(currentAccountTransaction, generalGL);
        LedgerAccount currentGL = ledgerAccountRepository.findByName(ledgerAccount);
        if(currentGL == null){
            currentGL = ledgerAccountRepository.findByCode(ledgerAccount);
        }
        if (generalGL) {
            generalLedger.setLedgerAccount(currentGL);
        }
        generalLedger.setType(creditDebit);
        generalLedger.setAmount(amount);
        extractClassCodeFromCode(generalLedger, currentGL);
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


    private GeneralLedger currentAccountGLMapper(CurrentAccountTransaction currentAccountTransaction, boolean generalGL) {
        GeneralLedger gl = new GeneralLedger();

        if (!generalGL) {
            gl.setAccountNumber(currentAccountTransaction.getCurrentAccount().getAccountNumber());
        } else {
            gl.setAccountNumber(null);
        }

        gl.setAmount(currentAccountTransaction.getCurrentAmount());
        Date date = BVMicroUtils.convertToDate(currentAccountTransaction.getCreatedDate());
        gl.setDate(date);
        gl.setCreatedDate(date);
        gl.setLastUpdatedDate(date);
        gl.setLastUpdatedDate(new Date(System.currentTimeMillis()));
        gl.setNotes(currentAccountTransaction.getNotes());
        gl.setReference(currentAccountTransaction.getReference());
        gl.setLastUpdatedBy(currentAccountTransaction.getCreatedBy());
        gl.setCreatedBy(currentAccountTransaction.getCreatedBy());
        gl.setGlClass(3); //TODO Saving which class in GL ?
        gl.setType(GeneralLedgerType.CREDIT.name());
//        gl.setType(currentAccountTransaction.getCurrentAmount() <= 0 ? "CREDIT" : "DEBIT");
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

    public GeneralLedgerBilanz findAll() {
        Iterable<GeneralLedger> glIterable = generalLedgerRepository.findAllOldestFirst();
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glIterable);
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


    public TrialBalanceBilanz getCurrentTrialBalance(LocalDateTime startDate, LocalDateTime endDate ){

        String aStartDate = BVMicroUtils.formatDateTime(startDate);
        String aEndDate = BVMicroUtils.formatDateTime(endDate);

        TrialBalanceBilanz trialBalanceWeb = getTrialBalanceWebs(aStartDate, aEndDate);
        return trialBalanceWeb;
    }

    @NotNull
    public TrialBalanceBilanz getTrialBalanceWebs(String aStartDate, String aEndDate) {
        List<TrialBalanceWeb> trialBalanceWebList = new ArrayList<TrialBalanceWeb>();
        Iterable<LedgerAccount> all = ledgerAccountRepository.findAll();
        TrialBalanceWeb trialBalanceWeb;
        TrialBalanceBilanz trialBalanceBilanz = new TrialBalanceBilanz();
        double bilanzTotalDifference = 0.0;
        double bilanzTotalDebit = 0.0;
        double bilanzTotalCredit = 0.0;
        for (LedgerAccount aLedgerAccount: all) {
            trialBalanceWeb = new TrialBalanceWeb();
            Double debitTotal = 0.0;
            Double creditTotal = 0.0;
            Double total = 0.0;
            double totalDifference = 0.0;

            debitTotal =
                    generalLedgerRepository.searchCriteriaLedgerType(aStartDate, aEndDate, aLedgerAccount.getId(),BVMicroUtils.DEBIT);

            creditTotal =
                    generalLedgerRepository.searchCriteriaLedgerType(aStartDate, aEndDate, aLedgerAccount.getId(),BVMicroUtils.CREDIT);
            creditTotal = creditTotal==null?new Double(0):creditTotal;
            debitTotal = debitTotal==null?new Double(0):debitTotal;

            trialBalanceWeb.setCreditTotal( creditTotal );
            trialBalanceWeb.setDebitTotal( debitTotal );
            totalDifference = creditTotal - debitTotal;
            trialBalanceWeb.setTotalDifference(totalDifference);
            trialBalanceWeb.setCode( aLedgerAccount.getCode() );
            trialBalanceWeb.setName( aLedgerAccount.getName() );
            trialBalanceWeb.setTotalDifference( totalDifference );
            bilanzTotalDifference = bilanzTotalDifference + totalDifference;
            bilanzTotalCredit = bilanzTotalCredit + creditTotal;
            bilanzTotalDebit = bilanzTotalDebit + debitTotal;
            trialBalanceWebList.add(trialBalanceWeb);
        }

        trialBalanceBilanz.setTrialBalanceWeb(trialBalanceWebList);
        trialBalanceBilanz.setTotalDifference(bilanzTotalDifference);
        trialBalanceBilanz.setCreditTotal(bilanzTotalCredit);
        trialBalanceBilanz.setDebitTotal(bilanzTotalDebit);

        return trialBalanceBilanz;
    }

    public List<GeneralLedger> searchCriteria(LocalDate startDateLocalDate, LocalDate endDateLocalDate) {

        String startDate = BVMicroUtils.formatUSDateOnly(startDateLocalDate);
        String endDate = BVMicroUtils.formatUSDateOnly(endDateLocalDate);
        List<GeneralLedger> glList = generalLedgerRepository.searchCriteriaStartEndDate(startDate, endDate);

        return glList;
    }

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
        Collections.reverse(glList);
        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glList);
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;
    }

    public BillSelectionBilanz searchCriteriaBillSelection(String startDate, String endDate, String userName) {
        List<CurrentAccountTransaction> currentAccountTransactions = new ArrayList<CurrentAccountTransaction>();
        List<SavingAccountTransaction> savingAccountTransactions = new ArrayList<SavingAccountTransaction>();
        List<LoanAccountTransaction> loanAccountTransactions = new ArrayList<LoanAccountTransaction>();


        if(userName.equals("-1")){
            currentAccountTransactions = currentAccountTransactionRepository.searchStartEndDate(startDate, endDate);
            savingAccountTransactions = savingAccountTransactionRepository.searchStartEndDate(startDate, endDate);
            loanAccountTransactions = loanAccountTransactionRepository.searchStartEndDate(startDate, endDate);
        }else{
            currentAccountTransactions = currentAccountTransactionRepository.searchStartEndDate(startDate, endDate, userName);
            savingAccountTransactions = savingAccountTransactionRepository.searchStartEndDate(startDate, endDate, userName);
            loanAccountTransactions = loanAccountTransactionRepository.searchStartEndDate(startDate, endDate, userName);
        }

        BillSelectionBilanz billSelectionBilanz = new BillSelectionBilanz();

        extractCurrentAccountTransactions(currentAccountTransactions, billSelectionBilanz);
        extractSavingAccountTransactions(savingAccountTransactions, billSelectionBilanz);
        extractLoanAccountTransactions(loanAccountTransactions, billSelectionBilanz);


        billSelectionBilanz.setTotal(
                (billSelectionBilanz.getTenThousand()*10000)+
                        (billSelectionBilanz.getFiveThousand()*5000)+
                            (billSelectionBilanz.getTwoThousand()*2000)+
                                (billSelectionBilanz.getOneThousand()*1000)+
                        (billSelectionBilanz.getFiveHundred()*500)+
                        (billSelectionBilanz.getOneHundred()*100)+
                        (billSelectionBilanz.getFifty()*50)+
                        (billSelectionBilanz.getTwentyFive()*25)+
                        (billSelectionBilanz.getTen()*10)+
                        (billSelectionBilanz.getFive()*5));

        return billSelectionBilanz;
    }

    private void extractLoanAccountTransactions(List<LoanAccountTransaction> loanAccountTransactions, BillSelectionBilanz billSelectionBilanz) {

        int tenThousand = 0;
        int fiveThousand = 0;
        int twoThousand = 0;
        int oneThousand = 0;
        int fiveHundred = 0;
        int oneHundred = 0;
        int fifty = 0;
        int twentyFive = 0;
        int ten = 0;
        int five = 0;

        for ( LoanAccountTransaction aTransaction: loanAccountTransactions) {
            if (aTransaction.getWithdrawalDeposit() == -1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() - aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() - aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() - aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() - aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() - aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() - aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() - aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() - aTransaction.getTwentyFive() );

//                    ten = ten - aTransaction.getTen();
//                    five = five - aTransaction.getFiveFrancs();

            } else if (aTransaction.getWithdrawalDeposit() == 1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() + aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() + aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() + aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() + aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() + aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() + aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() + aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() + aTransaction.getTwentyFive() );

//                    ten = ten + aTransaction.getTen();
//                    five = five + aTransaction.getFiveFrancs();

            }

        }

        billSelectionBilanz.setTenThousand( billSelectionBilanz.getTenThousand() + tenThousand);
        billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() + fiveThousand);
        billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() + twoThousand);
        billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() + oneThousand);
        billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() + fiveHundred);
        billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() + oneHundred);
        billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() + fifty);
        billSelectionBilanz.setTwentyFive(billSelectionBilanz.getTwentyFive() + twentyFive);
//        billSelectionBilanz.setTen(billSelectionBilanz.getTen() + ten);
//        billSelectionBilanz.setFive(billSelectionBilanz.getFive() + five);

    }


    private void extractSavingAccountTransactions(List<SavingAccountTransaction> savingAccountTransactions, BillSelectionBilanz billSelectionBilanz) {

        for ( SavingAccountTransaction aTransaction: savingAccountTransactions) {
            if (aTransaction.getWithdrawalDeposit() == -1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() - aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() - aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() - aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() - aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() - aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() - aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() - aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() - aTransaction.getTwentyFive() );

//                    ten = ten - aTransaction.getTen();
//                    five = five - aTransaction.getFiveFrancs();

            } else if (aTransaction.getWithdrawalDeposit() == 1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() + aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() + aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() + aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() + aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() + aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() + aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() + aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() + aTransaction.getTwentyFive() );

//                    ten = ten + aTransaction.getTen();
//                    five = five + aTransaction.getFiveFrancs();
            }

        }

    }

    private void extractCurrentAccountTransactions(List<CurrentAccountTransaction> currentAccountTransactions, BillSelectionBilanz billSelectionBilanz) {


        for ( CurrentAccountTransaction aTransaction: currentAccountTransactions) {
            if (aTransaction.getWithdrawalDeposit() == -1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() - aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() - aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() - aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() - aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() - aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() - aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() - aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() - aTransaction.getTwentyFive() );

//                    ten = ten - aTransaction.getTen();
//                    five = five - aTransaction.getFiveFrancs();

            } else if (aTransaction.getWithdrawalDeposit() == 1) {

                billSelectionBilanz.setTenThousand(billSelectionBilanz.getTenThousand() + aTransaction.getTenThousand());
                billSelectionBilanz.setFiveThousand(billSelectionBilanz.getFiveThousand() + aTransaction.getFiveThousand());
                billSelectionBilanz.setTwoThousand(billSelectionBilanz.getTwoThousand() + aTransaction.getTwoThousand());
                billSelectionBilanz.setOneThousand(billSelectionBilanz.getOneThousand() + aTransaction.getOneThousand());
                billSelectionBilanz.setFiveHundred(billSelectionBilanz.getFiveHundred() + aTransaction.getFiveHundred());
                billSelectionBilanz.setOneHundred(billSelectionBilanz.getOneHundred() + aTransaction.getOneHundred());
                billSelectionBilanz.setFifty(billSelectionBilanz.getFifty() + aTransaction.getFifty());
                billSelectionBilanz.setTwentyFive( billSelectionBilanz.getTwentyFive() + aTransaction.getTwentyFive() );

//                    ten = ten + aTransaction.getTen();
//                    five = five + aTransaction.getFiveFrancs();

            }
        }

    }

    public GeneralLedgerBilanz findGLByLedgerAccount(long ledgerAccountId) {

        LedgerAccount ledgerAccount = ledgerAccountRepository.findById(ledgerAccountId).get();
        List<GeneralLedger> glList = ledgerAccount.getGeneralLedger();

        List<GeneralLedgerWeb> generalLedgerWebs = mapperGeneralLedger(glList);
        GeneralLedgerBilanz generalLedgerBilanz = getGeneralLedgerBilanz(generalLedgerWebs);
        return generalLedgerBilanz;
    }

    @Transactional
    public void updateManualAccountTransaction(LedgerEntryDTO ledgerEntryDTO) {
        Date date = new Date();
        String loggedInUserName = getLoggedInUserName();

        recordGLFirstEntry(ledgerEntryDTO, date, loggedInUserName);

        GeneralLedger generalLedger;

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

    public GeneralLedger recordGLFirstEntry(LedgerEntryDTO ledgerEntryDTO, Date date, String loggedInUserName) {
        GeneralLedger generalLedger = new GeneralLedger();
        generalLedger.setType(ledgerEntryDTO.getCreditOrDebit());

        generalLedger.setNotes(ledgerEntryDTO.getNotes());
        generalLedger.setReference(BVMicroUtils.getSaltString());

        generalLedger.setDate(date);
        generalLedger.setLastUpdatedBy(loggedInUserName);
        generalLedger.setCreatedBy(loggedInUserName);
        generalLedger.setAmount(ledgerEntryDTO.getLedgerAmount());
        generalLedger.setCreatedDate(date);
        generalLedger.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        LedgerAccount originalAccount = ledgerAccountRepository.findById(ledgerEntryDTO.getOriginLedgerAccount()).get();
        generalLedger.setLedgerAccount(originalAccount);
        generalLedger.setGlClass(new Integer(originalAccount.getCategory().substring(0, 1)));
        generalLedgerRepository.save(generalLedger);
        return generalLedger;
    }

    public void updateGLAfterSavingAccountTransaction(SavingAccountTransaction savingAccountTransaction) {
        savingAccountTransaction.getNotes();
        LedgerAccount ledgerAccount;

        if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CASH)) {
            savingAccountTransaction.setNotes(BVMicroUtils.CASH_GL_5001 + " " + savingAccountTransaction.getNotes());
        }else if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.TRANSFER)) {
            savingAccountTransaction.setNotes(BVMicroUtils.TRANSFER + " " + savingAccountTransaction.getNotes());

        }

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
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "19")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.EDUCATION_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else if (StringUtils.equals(
                    savingAccountTransaction.getSavingAccount().getAccountSavingType().getNumber(), "18")) {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.REAL_ESTATE_SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            } else {
                ledgerAccount = updateGeneralLedger(savingAccountTransaction, BVMicroUtils.SAVINGS, savingAccountTransaction.getSavingAmount() > 0 ? "CREDIT" : "DEBIT", savingAccountTransaction.getSavingAmount(), true);
                savingAccountTransaction.setNotes(ledgerAccount.getCode() + " " + savingAccountTransaction.getNotes());
            }

            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.CASH, savingAccountTransaction.getSavingAmount() > 0 ? "DEBIT" : "CREDIT", savingAccountTransaction.getSavingAmount(), true);


    }



    public void updateGLAfterCurrentAccountTransaction(CurrentAccountTransaction currentAccountTransaction) {
        String notes = currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CASH)) {
            currentAccountTransaction.setNotes(BVMicroUtils.CASH_GL_5001 + " " + notes);
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT, currentAccountTransaction.getCurrentAmount() > 0 ? "CREDIT" : "DEBIT", currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + notes);
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CASH, currentAccountTransaction.getCurrentAmount() > 0 ? "DEBIT" : "CREDIT", currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(notes);
        }
    }



    public void updateGLAfterCurrentCurrentTransfer(CurrentAccountTransaction currentAccountTransaction) {
        String notes = currentAccountTransaction.getNotes();
        currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CURRENT_CURRENT_TRANSFER)) {
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT, "DEBIT", currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT, "CREDIT", currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(notes);
        }
    }

    public void updateGLAfterCurrentDebitTransfer(CurrentAccountTransaction currentAccountTransaction,String savingType) {

        LedgerAccount ledgerAccount = determineLedgerAccount(savingType);
        String ledgerCode = ledgerAccount.getCode();

        String notes = currentAccountTransaction.getNotes();
        if (currentAccountTransaction.getModeOfPayment().equals(BVMicroUtils.CURRENT_DEBIT_TRANSFER)) {
            currentAccountTransaction.setNotes(ledgerCode + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, BVMicroUtils.CURRENT, "DEBIT", currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(BVMicroUtils.CURRENT_GL_3004 + " " + currentAccountTransaction.getNotes());
            updateGeneralLedger(currentAccountTransaction, ledgerCode, "CREDIT", -1*currentAccountTransaction.getCurrentAmount(),  true);
            currentAccountTransaction.setNotes(notes);
        }
    }

    public void updateGLAfterDebitDebitTransfer(SavingAccountTransaction fromSavingAccountTransaction, SavingAccountTransaction toSavingAccountTransaction) {
        LedgerAccount fromLedgerAccount = determineLedgerAccount(fromSavingAccountTransaction.getSavingAccount().getProductCode());
        LedgerAccount toLedgerAccount = determineLedgerAccount(toSavingAccountTransaction.getSavingAccount().getProductCode());
        String fromNotes = fromSavingAccountTransaction.getNotes();
        String toNotes = fromSavingAccountTransaction.getNotes();
        if (fromSavingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.DEBIT_DEBIT_TRANSFER)) {
            fromSavingAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + fromSavingAccountTransaction.getNotes());
            updateGeneralLedger(fromSavingAccountTransaction,fromLedgerAccount.getCode() , "DEBIT", fromSavingAccountTransaction.getSavingAmount(), true);
            toSavingAccountTransaction.setNotes(BVMicroUtils.SAVINGS_GL_3003 + " " + toSavingAccountTransaction.getNotes());
            updateGeneralLedger(toSavingAccountTransaction, toLedgerAccount.getCode(), "CREDIT", toSavingAccountTransaction.getSavingAmount(), true);
            fromSavingAccountTransaction.setNotes(fromNotes);
            toSavingAccountTransaction.setNotes(toNotes);
        }
    }

    public void updateGLAfterDebitCurrentTransfer(SavingAccountTransaction savingAccountTransaction) {
        LedgerAccount savingLedgerAccount = determineLedgerAccount(savingAccountTransaction.getSavingAccount().getProductCode());
        String notes = savingAccountTransaction.getNotes();
        if (savingAccountTransaction.getModeOfPayment().equals(BVMicroUtils.DEBIT_CURRENT_TRANSFER)) {
            savingAccountTransaction.setNotes(savingLedgerAccount.getCode() + " " + notes);
            updateGeneralLedger(savingAccountTransaction, BVMicroUtils.CURRENT_GL_3004, "DEBIT", savingAccountTransaction.getSavingAmount(), true);
            savingAccountTransaction.setNotes(BVMicroUtils.CURRENT + " " + notes);
            updateGeneralLedger(savingAccountTransaction, savingLedgerAccount.getCode(), "CREDIT", savingAccountTransaction.getSavingAmount(), true);
            savingAccountTransaction.setNotes(notes);
        }
    }

    @Transactional
    public void updateGLAfterLedgerAccountMultipleAccountEntry(LedgerEntryDTO newLedgerEntryDTO) {

//        String oppositeDirection = newLedgerEntryDTO.getReverse();
        List<String> paramValueString = newLedgerEntryDTO.getParamValueString();
        long originLedgerAccount = newLedgerEntryDTO.getOriginLedgerAccount();
//        LedgerAccount original = ledgerAccountRepository.findById(originLedgerAccount).get();

//        if(newLedgerEntryDTO.get)
        newLedgerEntryDTO.setCreditOrDebit(BVMicroUtils.DEBIT);
        GeneralLedger generalLedger = recordGLFirstEntry(newLedgerEntryDTO, BVMicroUtils.formatDate(newLedgerEntryDTO.getRecordDate()), getLoggedInUserName());

        String accountNumber = "";
        String accountAmount = "";
        int i = 0;
        for (String aString : paramValueString) {
            String[] s = aString.split("_");
            accountNumber = s[0];
            accountAmount = s[1];
            ++i;
            LedgerAccount ledgerAccount = determineLedgerAccount(accountNumber);
            final Integer productCode = new Integer(accountNumber.substring(3, 5));
//            accountNumber = accountNumber.substring(10, 21);
            if( productCode > 9 && productCode < 20){

                SavingAccount byAccountNumber = savingAccountRepository.findByAccountNumber(accountNumber);
                SavingAccountTransaction savingAccountTransaction = new SavingAccountTransaction();
                savingAccountTransaction.setSavingAccount(byAccountNumber);
                savingAccountTransaction.setWithdrawalDeposit(1);
                savingAccountTransaction.setSavingAmount(new Double(accountAmount));
                savingAccountTransaction.setNotes("GL Account to transfer");
                savingAccountTransaction.setCreatedBy(getLoggedInUserName());
                savingAccountTransaction.setReference(generalLedger.getReference()+"_"+i);
                Date date = BVMicroUtils.formatDate(newLedgerEntryDTO.getRecordDate());
                savingAccountTransaction.setCreatedDate(BVMicroUtils.convertToLocalDateTimeViaMilisecond(date));
                savingAccountTransaction.setModeOfPayment(BVMicroUtils.GL_TRANSFER);
                Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());
                savingAccountTransaction.setBranch(branchInfo.getId());
                savingAccountTransaction.setBranchCode(branchInfo.getCode());
                savingAccountTransaction.setBranchCountry(branchInfo.getCountry());
//                savingAccountTransaction.setAccountOwner(byAccountNumber.getUser().getLastName());
                savingAccountTransaction.setSavingAmountInLetters("SYSTEM");
                savingAccountTransactionRepository.save(savingAccountTransaction);
                byAccountNumber.getSavingAccountTransaction().add(savingAccountTransaction);
                savingAccountRepository.save(byAccountNumber);
                updateGeneralLedger(savingAccountTransaction, ledgerAccount.getCode(), BVMicroUtils.CREDIT, savingAccountTransaction.getSavingAmount(), true);

            }else if( productCode == 20){

                    CurrentAccount byAccountNumber = currentAccountRepository.findByAccountNumber(accountNumber);
                    CurrentAccountTransaction currentAccountTransaction = new CurrentAccountTransaction();
                    currentAccountTransaction.setCurrentAccount(byAccountNumber);
                    currentAccountTransaction.setWithdrawalDeposit(1);
                    currentAccountTransaction.setCurrentAmount(new Double(accountAmount));
                    currentAccountTransaction.setNotes("GL Account to transfer");
                    currentAccountTransaction.setCreatedBy(getLoggedInUserName());
                    currentAccountTransaction.setReference(generalLedger.getReference()+"_"+i);
                    Date date = BVMicroUtils.formatDate(newLedgerEntryDTO.getRecordDate());
                    currentAccountTransaction.setCreatedDate(BVMicroUtils.convertToLocalDateTimeViaMilisecond(date));
                    currentAccountTransaction.setModeOfPayment(BVMicroUtils.GL_TRANSFER);
                    Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());
                    currentAccountTransaction.setBranch(branchInfo.getId());
                    currentAccountTransaction.setBranchCode(branchInfo.getCode());
                    currentAccountTransaction.setBranchCountry(branchInfo.getCountry());
//                    currentAccountTransaction.setAccountOwner(byAccountNumber.getUser().getLastName());
                    currentAccountTransaction.setCurrentAmountInLetters("SYSTEM");
                    currentAccountTransactionRepository.save(currentAccountTransaction);
                    byAccountNumber.getCurrentAccountTransaction().add(currentAccountTransaction);
                    currentAccountRepository.save(byAccountNumber);
                    updateGeneralLedger(currentAccountTransaction, ledgerAccount.getCode(), BVMicroUtils.CREDIT, currentAccountTransaction.getCurrentAmount(), true);


            }else if( productCode > 39 && productCode < 50){
                LoanAccount byAccountNumber = loanAccountRepository.findByAccountNumber(accountNumber);
                LoanAccountTransaction loanAccountTransaction = new LoanAccountTransaction();
                loanAccountTransaction.setLoanAccount(byAccountNumber);
                loanAccountTransaction.setWithdrawalDeposit(1);
//                loanAccountTransaction.setLoanAmount(new Double(accountAmount));
                loanAccountTransaction.setAmountReceived(new Double(accountAmount));
                loanAccountTransaction.setNotes(" GL Account to transfer");
                loanAccountTransaction.setCreatedBy(getLoggedInUserName());
                loanAccountTransaction.setReference(generalLedger.getReference()+"_"+i);
                Date date = BVMicroUtils.formatDate(newLedgerEntryDTO.getRecordDate());
                loanAccountTransaction.setCreatedDate(BVMicroUtils.convertToLocalDateTimeViaMilisecond(date));
                loanAccountTransaction.setModeOfPayment(BVMicroUtils.GL_TRANSFER);
                Branch branchInfo = branchService.getBranchInfo(getLoggedInUserName());
                loanAccountTransaction.setBranch(branchInfo.getId());
                loanAccountTransaction.setBranchCode(branchInfo.getCode());
                loanAccountTransaction.setBranchCountry(branchInfo.getCountry());
                loanAccountTransaction.setAccountOwner("false");
                loanAccountTransaction.setLoanAmountInLetters("SYSTEM");
                loanAccountTransaction.setRepresentative("GL TRANSFER" );

                loanAccountService.updateInterestOwedPayment(byAccountNumber,loanAccountTransaction);
                loanAccountService.calculateAccountBilanz(byAccountNumber.getLoanAccountTransaction(), true);

                updateGeneralLedger(loanAccountTransaction, ledgerAccount.getCode(), BVMicroUtils.CREDIT, loanAccountTransaction.getAmountReceived(), true);
                byAccountNumber.getLoanAccountTransaction().add(loanAccountTransaction);
                loanAccountRepository.save(byAccountNumber);
            }
        }
    }

    public LedgerAccount determineLedgerAccount(String accountNumber) {
        String productCode = accountNumber;
        if(StringUtils.isNotEmpty(accountNumber) && accountNumber.length()!=2){
            productCode = accountNumber.substring(3, 5);
        }

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
            return ledgerAccountRepository.findByName(BVMicroUtils.OVERDRAFT_LOAN);
        } else if (productCode.equals("48")) {
            return ledgerAccountRepository.findByName(BVMicroUtils.NJANGI_FINANCING);
        }
        return ledgerAccountRepository.findByName(BVMicroUtils.NO_NAME);
    }

}
