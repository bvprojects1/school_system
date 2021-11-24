package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.ShareAccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ShareAccountTransactionRepository extends CrudRepository<ShareAccountTransaction, Long> {

    List<ShareAccountTransaction> findByShareAccount(String shareAccount);

    Optional<ShareAccountTransaction> findByReference(String reference);

}
