package com.bitsvalley.micro.repositories;

import java.util.List;

import com.bitsvalley.micro.domain.CallCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository <CallCenter, Long> {
    List <CallCenter> findByUserName(String user);
}
