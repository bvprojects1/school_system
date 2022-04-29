package com.bitsvalley.micro.controllers;

import com.bitsvalley.micro.repositories.StudentRepository;
import com.bitsvalley.micro.services.StudentService;
import com.bitsvalley.micro.webdomain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/studentList")
    public String studentList(Model model){
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students_class_list";
    }

    @GetMapping("/studentForm")
    public String registrationForm(Model model){
        Student student = new Student();
        model.addAttribute("student",student);
        return "students_registration_form";
    }

    @PostMapping("/saveStudent")
    public String saveStudent(Student student){
        studentService.saveStudent(student);
        return "students_class_List";
    }
}
