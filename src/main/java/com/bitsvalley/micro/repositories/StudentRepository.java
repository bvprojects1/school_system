package com.bitsvalley.micro.repositories;

import com.bitsvalley.micro.webdomain.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student,Long> {

}
