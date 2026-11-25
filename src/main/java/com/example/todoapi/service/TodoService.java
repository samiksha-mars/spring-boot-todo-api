package com.example.todoapi.service;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.model.Todo;
import com.example.todoapi.repository.TodoRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAll(Boolean completed) {
        List<Todo> todos = completed == null
                ? repository.findAll()
                : repository.findByCompleted(completed);
        return todos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse findById(Long id) {
        return toResponse(repository.findById(id).orElseThrow());
    }

    @Transactional
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(Boolean.TRUE.equals(request.completed()));
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        repository.save(todo);
        return toResponse(todo);
    }

    @Transactional
    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = repository.findById(id).orElseThrow();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(Boolean.TRUE.equals(request.completed()));
        todo.setUpdatedAt(Instant.now());
        repository.save(todo);
        return toResponse(todo);
    }

    @Transactional
    public TodoResponse setCompleted(Long id, boolean completed) {
        Todo todo = repository.findById(id).orElseThrow();
        todo.setCompleted(completed);
        todo.setUpdatedAt(Instant.now());
        repository.save(todo);
        return toResponse(todo);
    }

    @Transactional
    public void delete(Long id) {
        Todo todo = repository.findById(id).orElseThrow();
        repository.delete(todo);
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
