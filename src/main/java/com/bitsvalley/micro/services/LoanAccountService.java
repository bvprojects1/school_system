package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.*;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.Amortization;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.LoanBilanz;
import com.bitsvalley.micro.webdomain.LoanBilanzList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanAccountService extends SuperService {

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private LoanAccountTransactionService loanAccountTransactionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private InterestService interestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private CallCenterRepository callCenterRepository;

    @Autowired
    private GeneralLedgerService generalLedgerService;

    public SavingAccount findByAccountNumber(String accountNumber) {
        return savingAccountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public void createLoanAccount(LoanAccount loanAccount, User user) {
        int allCount = loanAccountRepository.findAllCount();

        String loggedInUserName = getLoggedInUserName();
        User aUser = userRepository.findByUserName(loggedInUserName);
        loanAccount.setCountry(aUser.getBranch().getCountry());
        loanAccount.setBranchCode(aUser.getBranch().getCode());

        loanAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(loanAccount.getCountry(),
                loanAccount.getProductCode(), loanAccount.getBranchCode(), allCount)); //TODO: Collision
        loanAccount.setAccountStatus(AccountStatus.ACTIVE);

        loanAccount.setCreatedBy(loggedInUserName);
        loanAccount.setCreatedDate(new Date(System.currentTimeMillis()));
        loanAccount.setLastUpdatedBy(loggedInUserName);
        loanAccount.setLastUpdatedDate(new Date(System.currentTimeMillis()));

        AccountType accountType = accountTypeRepository.findByNumber(loanAccount.getProductCode());
        loanAccount.setAccountType(accountType);
        user = userRepository.findById(user.getId()).get();
        loanAccount.setUser(user);
        loanAccountRepository.save(loanAccount);

        user.getLoanAccount().add(loanAccount);
        userService.saveUser(user);

        //Create a initial loan transaction of borrowed amount
        LoanAccountTransaction loanAccountTransaction = loanAccountTransactionService.createLoanAccountTransaction(loanAccount);

        generalLedgerService.updateLoanAccountTransaction(loanAccountTransaction);

        callCenterService.saveCallCenterLog(loanAccount.getUser().getFirstName(), loanAccount.getUser().getLastName(),
                loanAccount.getAccountNumber(), loanAccount.getAccountType().getName(), loanAccount.getLoanAmount() + "");
    }


    public LoanBilanzList getLoanBilanzByUser(User user, boolean calculateInterest) {
        User aUser = null;
        if (null != user.getUserName()) {
            aUser = userRepository.findByUserName(user.getUserName());
        } else {
            aUser = userRepository.findById(user.getId()).get();
        }
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(aUser);
        return calculateUsersInterest(userList, calculateInterest);

    }


    public Optional<LoanAccount> findById(long id) {
        Optional<LoanAccount> loanAccount = loanAccountRepository.findById(id);
        return loanAccount;
    }

    public void save(LoanAccount save) {
        loanAccountRepository.save(save);
    }


    public LoanBilanzList calculateAccountBilanz(
            List<LoanAccountTransaction> loanAccountTransactions,
            boolean calculateInterest) {
        double totalSaved = 0.0;
        double currentSaved = 0.0;
        double loanAccountTransactionInterest = 0.0;

        LoanBilanzList loanBilanzsList = new LoanBilanzList();

        for (int k = 0; k < loanAccountTransactions.size(); k++) {
            final LoanAccountTransaction loanAccountTransaction = loanAccountTransactions.get(k);
            LoanBilanz loanBilanz = new LoanBilanz();
            loanBilanz = calculateInterest(loanAccountTransaction, calculateInterest);
            currentSaved = currentSaved + loanAccountTransaction.getLoanAmount();
            loanBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
            loanBilanzsList.getLoanBilanzList().add(loanBilanz);
            totalSaved = totalSaved + loanAccountTransaction.getLoanAmount();
            if (calculateInterest) {
                LoanAccount loanAccount = loanAccountTransaction.getLoanAccount();
                loanBilanzsList.setTotalLoanInterest("0000"); //TODO set total interest
            }
        }
        loanBilanzsList.setTotalLoan(BVMicroUtils.formatCurrency(totalSaved));
        Collections.reverse(loanBilanzsList.getLoanBilanzList());
        return loanBilanzsList;
    }


    private LoanBilanzList calculateUsersInterest(ArrayList<User> users, boolean calculateInterest) {
        double totalRepayment = 0.0;
        double currentSaved = 0.0;
        double loanAccountTransactionInterest = 0.0;
        LoanBilanzList loanBilanzsList = new LoanBilanzList();
        for (int i = 0; i < users.size(); i++) {
            List<LoanAccount> loanAccounts = users.get(i).getLoanAccount();

            List<LoanAccountTransaction> loanAccountTransactions = new ArrayList<LoanAccountTransaction>();
            for (int j = 0; j < loanAccounts.size(); j++) {
                LoanAccount loanAccount = loanAccounts.get(j);
                boolean defaultedPayments = checkDefaultLogic(loanAccount);
                loanAccount.setDefaultedPayment(defaultedPayments); //TODO:defaultLogic
                loanAccountTransactions = loanAccount.getLoanAccountTransaction();
                for (int k = 0; k < loanAccountTransactions.size(); k++) {
                    final LoanAccountTransaction loanAccountTransaction = loanAccountTransactions.get(k);
                    if (loanAccountTransaction.getLoanAmount() <= 0)
                        continue;
//                    LocalDateTime createdDate = savingAccountTransaction.getCreatedDate();
//                    if (LocalDateTime.now().minusMonths(1).isAfter(createdDate)) {
                    LoanBilanz loanBilanz = calculateInterest(loanAccountTransaction, calculateInterest);
                    currentSaved = currentSaved + loanAccountTransaction.getLoanAmount();
                    loanBilanz.setCurrentBalance(BVMicroUtils.formatCurrency(currentSaved));
                    loanBilanzsList.getLoanBilanzList().add(loanBilanz);
                    totalRepayment = totalRepayment + loanAccountTransaction.getLoanAmount();
                    double monthlyPayments = interestService.monthlyPaymentAmortisedPrincipal(
                            loanAccount.getInterestRate(),
                                loanAccount.getTermOfLoan(),
                                    loanAccount.getLoanAmount());
                }

//                loanAccount.setAccountBalance(totalRepayment); TODO: set balance
                if (checkMinBalanceLogin(currentSaved, loanAccount)) {
                    loanAccount.setDefaultedPayment(true);// Minimum balance check
                }
                loanAccountRepository.save(loanAccount);
            }
        }
        loanBilanzsList.setTotalLoan(BVMicroUtils.formatCurrency(totalRepayment));
        loanBilanzsList.setTotalLoanInterest(BVMicroUtils.formatCurrency(loanAccountTransactionInterest));
        Collections.reverse(loanBilanzsList.getLoanBilanzList());
        return loanBilanzsList;
    }


    private boolean checkMinBalanceLogin(double currentSaved, LoanAccount savingAccount) {

//        if(savingAccount.getAccountMinBalance() > currentSaved){
//            CallCenter callCenter = new CallCenter();
//            callCenter.setDate(new Date(System.currentTimeMillis()));
//            callCenter.setNotes("Minimum Balance not met for this account");
//            callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " " + savingAccount.getUser().getLastName());
//            callCenter.setAccountNumber(savingAccount.getAccountNumber());
//            callCenterRepository.save(callCenter);
//            return true;
//        }

        return false;
    }


    private LoanBilanz calculateInterest(LoanAccountTransaction loanAccountTransaction, boolean calculateInterest) {
        LoanBilanz loanBilanz = new LoanBilanz();
        loanBilanz.setId("" + loanAccountTransaction.getId());
        LoanAccount loanAccount = loanAccountTransaction.getLoanAccount();
        loanBilanz.setAccountType(loanAccount.getAccountType().getName());
        loanBilanz.setCreatedBy(loanAccountTransaction.getCreatedBy());
        loanBilanz.setReference(loanAccountTransaction.getReference());
        loanBilanz.setAgent(loanAccountTransaction.getCreatedBy());
        loanBilanz.setInterestRate("" + loanAccount.getInterestRate());
        loanBilanz.setLoanAmount(BVMicroUtils.formatCurrency(loanAccountTransaction.getLoanAmount()));
        loanBilanz.setCreatedDate(BVMicroUtils.formatDateTime(loanAccountTransaction.getCreatedDate()));
        loanBilanz.setNotes(loanAccountTransaction.getNotes());
        loanBilanz.setAccountNumber(loanAccount.getAccountNumber());
        loanBilanz.setNoOfDays(calculateNoOfDays(loanAccountTransaction.getCreatedDate()));
        loanBilanz.setModeOfPayment(loanAccountTransaction.getModeOfPayment());
        loanBilanz.setAccountOwner(loanAccountTransaction.getAccountOwner());
        loanBilanz.setBranch(loanAccount.getBranchCode());
        loanBilanz.setMonthYearOfLastPayment(calculateMonthOfLastPayment(loanAccount.getCreatedDate(),
                loanAccount.getTermOfLoan()));

        double monthlyPayment = interestService.monthlyPaymentAmortisedPrincipal(
                loanAccount.getInterestRate(),
                loanAccount.getTermOfLoan(),
                loanAccount.getLoanAmount());

        loanBilanz.setMonthlyPayment(loanAccount.getMonthlyPayment()+"");
        Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
                loanAccount.getInterestRate()*.01,
                loanAccount.getTermOfLoan()/12);
        String report = amortization.getReport(monthlyPayment);

        loanBilanz.setSetAmortizationSchedule(report);
        if (calculateInterest) {
                double monthlyPayments = monthlyPayment;

        }

        return loanBilanz;
    }

    private String calculateMonthOfLastPayment(Date createdDate, int termOfLoan) {
        LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(createdDate) );
        localDate = localDate.plusMonths(termOfLoan);
        return localDate.getMonthValue()+"/"+localDate.getYear();
    }

    private String calculateNoOfDays(LocalDateTime createdDate) {
        long noOfDays = createdDate.until(LocalDateTime.now(), ChronoUnit.DAYS);
        return "" + noOfDays;
    }

//    private double calculateInterestAccruedMonthCompounded(LoanAccountTransaction loanAccountTransaction) {
////        = P [(1 + i/12)pow of NoOfMonths – 1]
////        P = principal, i = nominal annual interest rate in percentage terms, n = number of compounding periods
//        double interestPlusOne = (loanAccountTransaction.getLoanAccount().getInterestRate() * .01 * .0833333) + 1;
//        double temp = Math.pow(interestPlusOne, getNumberOfMonths(loanAccountTransaction.getCreatedDate()));
//        temp = temp - 1;
//        return loanAccountTransaction.getLoanAmount() * temp;
//    }

//    private Amortization calculateInterestAccruedMonthCompounded(LoanAccount loanAccount) {
//            Amortization amortization = new Amortization(loanAccount.getLoanAmount(),
//                    loanAccount.getInterestRate(),
//                        loanAccount.getTermOfLoan());
//
//            return amortization;
//    }

//    private double calculateInterestAccruedYearCompounded(SavingAccountTransaction savingAccountTransaction){
////        = P [(1 + i)pow of n – 1]
////        P = principal, i = nominal annual interest rate in percentage terms, n = number of compounding periods
//        double interestPlusOne = (savingAccountTransaction.getSavingAccount().getInterestRate()*.01) + 1;
//        double temp = Math.pow(interestPlusOne,getNumberOfMonths(savingAccountTransaction.getCreatedDate()));
//        temp = temp - 1;
//        return savingAccountTransaction.getSavingAmount() * temp;
//    }

    private double getNumberOfMonths(LocalDateTime cretedDateInput) {
        double noOfMonths = 0.0;
        Duration diff = Duration.between(cretedDateInput, LocalDateTime.now());
        noOfMonths = diff.toDays() / 30;
        return Math.floor(noOfMonths);
    }

    public boolean checkDefaultLogic(LoanAccount savingAccount) {

//        if(savingAccount.getAccountSavingType().getName().equals("GENERAL SAVINGS")){
//            List<SavingAccountTransaction> savingAccountTransactionList = savingAccount.getSavingAccountTransaction();
//
//            Date createdDate = savingAccount.getCreatedDate();
//            Date currentDate = new Date(System.currentTimeMillis());
//
//            Calendar currentDateCal = GregorianCalendar.getInstance();
//            currentDateCal.setTime(currentDate);
//
//            Calendar createdCalenderCal = GregorianCalendar.getInstance();
//            createdCalenderCal.setTime(createdDate);
//
//            long monthsBetween = ChronoUnit.MONTHS.between(
//                    YearMonth.from(LocalDate.parse(createdCalenderCal.get(GregorianCalendar.YEAR)+"-"+padding(createdCalenderCal.get(GregorianCalendar.MONTH))+"-"+padding(createdCalenderCal.get(GregorianCalendar.DAY_OF_MONTH)))),
//                    YearMonth.from(LocalDate.parse(currentDateCal.get(GregorianCalendar.YEAR)+"-"+padding(currentDateCal.get(GregorianCalendar.MONTH))+"-"+padding(currentDateCal.get(GregorianCalendar.DAY_OF_MONTH)))));
//
//            if (monthsBetween >= savingAccountTransactionList.size()){
//                CallCenter callCenter = new CallCenter();
//                callCenter.setNotes(BVMicroUtils.REGULAR_MONTHLY_PAYMENT_MISSING);
//                callCenter.setDate(new Date(System.currentTimeMillis()));
//                callCenter.setAccountHolderName(savingAccount.getUser().getFirstName() + " "+ savingAccount.getUser().getLastName());
//                callCenter.setAccountNumber(savingAccount.getAccountNumber());
//                callCenterRepository.save(callCenter);
//                return true;
//            }
//
//        }
        return false;
    }

    //
    private String padding(int i) {
        if (i < 10)
            return "" + 0 + 1;
        return "" + i;
    }
//
//    public String withdrawalAllowed(SavingAccountTransaction savingTransaction) {
//       String error = "";
//       error = minimumSavingRespected(savingTransaction);
//       return error;
//    }
//
//    private String minimumSavingRespected(SavingAccountTransaction savingTransaction) {
//        double futureBalance = getAccountBalance(savingTransaction.getSavingAccount()) + savingTransaction.getSavingAmount();
//        if(savingTransaction.getSavingAccount().getAccountMinBalance() > futureBalance ){
//            return "Account will fall below Minimum Savings amount";
//        }
//        return null;
//    }
//
//    public double getAccountBalance(SavingAccount savingAccount) {
//        double total = 0.0;
//        List<SavingAccountTransaction> savingAccountTransactions = savingAccount.getSavingAccountTransaction();
//        for (SavingAccountTransaction tran: savingAccountTransactions) {
//            total = tran.getSavingAmount() + total;
//        }
//        return total;
//    }
}
