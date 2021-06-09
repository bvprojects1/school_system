package com.bitsvaley.micro.services;

import com.bitsvaley.micro.domain.Todo;
import com.bitsvaley.micro.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getTodosByUser(String user) {
        List<Todo> todoList = todoRepository.findByUserName(user);
        return todoList;
    }

    public Optional<Todo> getTodoById(long id) {
        return todoRepository.findById(id);
    }

    public void updateTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public void addTodo(String name, String desc, Date targetDate, boolean isDone) {
        todoRepository.save(new Todo(name, desc, targetDate, isDone));
    }

    public void deleteTodo(long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            todoRepository.delete(todo.get());
        }
    }

    public void saveTodo(Todo todo) {
        todoRepository.save(todo);
    }
}
