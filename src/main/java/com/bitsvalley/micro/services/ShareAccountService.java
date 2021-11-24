package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.*;
import com.bitsvalley.micro.repositories.SavingAccountRepository;
import com.bitsvalley.micro.repositories.ShareAccountRepository;
import com.bitsvalley.micro.repositories.ShareAccountTransactionRepository;
import com.bitsvalley.micro.repositories.UserRepository;
import com.bitsvalley.micro.utils.AccountStatus;
import com.bitsvalley.micro.utils.BVMicroUtils;
import com.bitsvalley.micro.webdomain.ShareAccountBilanz;
import com.bitsvalley.micro.webdomain.ShareAccountBilanzList;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Fru Chifen
 * 11.06.2021
 */
@Service
public class ShareAccountService extends SuperService{

    @Autowired
    private CallCenterService callCenterService;

    @Autowired
    private ShareAccountRepository shareAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private ShareAccountTransactionRepository shareAccountTransactionRepository;

    @Autowired
    SavingAccountService savingAccountService;

    public void createShareAccount(ShareAccount shareAccount, User user) {

        long count = shareAccountRepository.count();
        shareAccount.setAccountNumber(BVMicroUtils.getCobacSavingsAccountNumber(shareAccount.getCountry(),
                shareAccount.getProductCode(), shareAccount.getBranchCode(), count));
        shareAccount.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        shareAccount.setCreatedBy(getLoggedInUserName());
        Date date = new Date(System.currentTimeMillis());
        shareAccount.setLastUpdatedDate(date);
        shareAccount.setCreatedDate(date);
        shareAccount.setLastUpdatedBy(getLoggedInUserName());
        shareAccount.setAccountBalance(shareAccount.getUnitSharePrice() * shareAccount.getQuantity());
        shareAccount.setCountry(user.getBranch().getCountry());
        shareAccount.setBranchCode(user.getBranch().getCode());

        shareAccount.setUser(user);
        shareAccountRepository.save(shareAccount);

        user = userRepository.findById(user.getId()).get();
        user.getShareAccount().add(shareAccount);
        userRepository.save(user);
    }

    public ShareAccountBilanzList getShareAccountBilanzByUser(User user) {
        User aUser = null;
        if (null != user.getUserName()) {
            aUser = userRepository.findByUserName(user.getUserName());
        } else {
            aUser = userRepository.findById(user.getId()).get();
        }
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(aUser);
        return calculateUsersInterest(userList);
    }

    private ShareAccountBilanzList calculateUsersInterest(ArrayList<User> users) {
        double totalSaved = 0.0;
        ShareAccountBilanzList shareAccountBilanzList = new ShareAccountBilanzList();
        for (int i = 0; i < users.size(); i++) {
            List<ShareAccount> shareAccounts = users.get(i).getShareAccount();
            List<ShareAccountTransaction> shareAccountTransactions = new ArrayList<ShareAccountTransaction>();
            ShareAccountBilanz shareAccountBilanz = new ShareAccountBilanz();
            for (int j = 0; j < shareAccounts.size(); j++) {
                ShareAccount shareAccount = shareAccounts.get(j);
                List<ShareAccountTransaction> shareAccountTransaction = shareAccount.getShareAccountTransaction();
                double accountTotalSaved = 0.0;
                shareAccountRepository.save(shareAccount);
//                shareAccountBilanz.
            }
        }

        shareAccountBilanzList.setTotalCurrent(BVMicroUtils.formatCurrency(totalSaved));
        Collections.reverse(shareAccountBilanzList.getShareAccountBilanz());
        return shareAccountBilanzList;
    }


    public void transferFromSavingToShareAccount(SavingAccount savingAccount,
                                        ShareAccount shareAccount,
                                        double transferAmount,
                                        String notes) {
        LocalDateTime now = LocalDateTime.now();
        String loggedInUserName = getLoggedInUserName();
        Branch branchInfo = branchService.getBranchInfo(loggedInUserName);

//        ShareAccount shareAccount = shareAccountRepository.findByAccountNumber(toAccountNumber);
        ShareAccountTransaction shareAccountTransaction = getShareAccountTransaction(transferAmount, notes, branchInfo, shareAccount);
        shareAccount.getShareAccountTransaction().add( shareAccountTransaction );
        shareAccountRepository.save(shareAccount);

//        SavingAccount savingAccount = savingAccountService.findByAccountNumber(fromAccountNumber);
        SavingAccountTransaction savingAccountTransaction = savingAccountService.getSavingAccountTransaction(notes, branchInfo, savingAccount, transferAmount * -1);
        savingAccount.getSavingAccountTransaction().add(savingAccountTransaction);
        savingAccountRepository.save(savingAccount);

//        generalLedgerService.updateGLAfterLoanAccountTransferRepayment(loanAccountTransaction);

    }

    @NotNull
    private ShareAccountTransaction getShareAccountTransaction(double transferAmount, String notes, Branch branchInfo, ShareAccount shareAccount) {
        ShareAccountTransaction shareAccountTransaction = new ShareAccountTransaction();
        shareAccountTransaction.setNotes(notes);

        shareAccountTransaction.setShareAccount(shareAccount);
        shareAccountTransaction.setShareAmount(transferAmount);
        shareAccountTransaction.setModeOfPayment(BVMicroUtils.TRANSFER);
        shareAccountTransaction.setBranch(branchInfo.getId());
        shareAccountTransaction.setBranchCode(branchInfo.getCode());
        shareAccountTransaction.setBranchCountry(branchInfo.getCountry());
        createShareAccountTransaction( shareAccountTransaction );
        return shareAccountTransaction;
    }

    @Transactional
    public void createShareAccountTransaction(ShareAccountTransaction savingAccountTransaction) {
        //Get id of savingAccount transaction
        savingAccountTransaction.setReference(BVMicroUtils.getSaltString()); //Collision
        savingAccountTransaction.setCreatedBy(getLoggedInUserName());
        savingAccountTransaction.setCreatedDate(LocalDateTime.now());
        shareAccountTransactionRepository.save(savingAccountTransaction);
    }
}
