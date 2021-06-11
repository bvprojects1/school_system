package com.bitsvaley.micro.controllers;

import com.bitsvaley.micro.domain.Todo;
import com.bitsvaley.micro.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class TodoController extends SuperController{

    @Autowired
    private TodoService todoService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping(value = "/list-todos")
    public String showTodos(ModelMap model) {
        String name = getLoggedInUserName();
        model.put("todos", todoService.getTodosByUser(name));
        return "list-todos";
    }


    @GetMapping(value = "/add-todo")
    public String showAddTodoPage(ModelMap model) {
        model.addAttribute("todo", new Todo());
        return "todo";
    }

    @GetMapping(value = "/delete-todo")
    public String deleteTodo(@RequestParam long id) {
        todoService.deleteTodo(id);
        return "redirect:/list-todos";
    }

    @GetMapping(value = "/update-todo")
    public String showUpdateTodoPage(@RequestParam long id, ModelMap model) {
        Todo todo = todoService.getTodoById(id).get();
        model.put("todo", todo);
        return "todo";
    }

    @PostMapping(value = "/update-todo")
    public String updateTodo(ModelMap model, Todo todo, BindingResult result) {

        if (result.hasErrors()) {
            return "todo";
        }

        todo.setUserName(getLoggedInUserName());
        todoService.updateTodo(todo);
        return "redirect:/list-todos";
    }

    @PostMapping(value = "/add-todo")
    public String addTodo(ModelMap model, Todo todo, BindingResult result) {

        if (result.hasErrors()) {
            return "todo";
        }

        todo.setUserName(getLoggedInUserName());
        todoService.saveTodo(todo);
        return "redirect:/list-todos";
    }
}
