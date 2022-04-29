package com.bitsvalley.micro.services;

import com.bitsvalley.micro.repositories.StudentRepository;
import com.bitsvalley.micro.webdomain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        List<Student> students = (List<Student>) studentRepository.findAll();
        return students;
    }
}
