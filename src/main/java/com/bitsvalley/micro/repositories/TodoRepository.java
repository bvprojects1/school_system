package com.bitsvalley.micro.repositories;

import java.util.List;

import com.bitsvalley.micro.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository <Todo, Long> {
    List <Todo> findByUserName(String user);
}
