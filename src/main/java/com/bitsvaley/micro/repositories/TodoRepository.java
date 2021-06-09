package com.bitsvaley.micro.repositories;

import java.util.List;

import com.bitsvaley.micro.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository <Todo, Long> {
    List <Todo> findByUserName(String user);
}
