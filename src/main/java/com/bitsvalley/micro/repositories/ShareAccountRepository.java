package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.ShareAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ShareAccountRepository extends CrudRepository<ShareAccount, Long> {

    ShareAccount findByAccountNumber(String accountNumber);

    @Query(value = "SELECT COUNT(*) AS numberOfShareAccount FROM ShareAccount sa where sa.branchCode = :branchCode")
    int countNumberOfProductsCreatedInBranch(String branchCode);

}
