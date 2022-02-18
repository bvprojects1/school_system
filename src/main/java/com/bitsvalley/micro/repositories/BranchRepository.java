package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.domain.Branch;
import com.bitsvalley.micro.domain.CallCenter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BranchRepository extends CrudRepository<Branch, Long> {

    List<Branch> findByName(String accountNumber);

    Branch findByCode(String code);



}
