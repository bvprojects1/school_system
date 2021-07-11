package com.bitsvalley.micro.services;

import com.bitsvalley.micro.domain.CallCenter;
import com.bitsvalley.micro.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<CallCenter> getTodosByUser(String user) {
        List<CallCenter> todoList = todoRepository.findByUserName(user);
        return todoList;
    }

    public Optional<CallCenter> getTodoById(long id) {
        return todoRepository.findById(id);
    }

    public void updateTodo(CallCenter todo) {
        todoRepository.save(todo);
    }

    public void addTodo(String name, String desc, Date targetDate, boolean isDone) {
        todoRepository.save(new CallCenter(name, desc, targetDate, isDone));
    }

    public void deleteTodo(long id) {
        Optional<CallCenter> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            todoRepository.delete(todo.get());
        }
    }

    public void saveTodo(CallCenter todo) {
        todoRepository.save(todo);
    }
}
