package com.example.todoapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.todoapi.model.Todo;
import com.example.todoapi.repository.TodoRepository;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    private Todo save(String title, boolean completed) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setCompleted(completed);
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        return repository.save(todo);
    }

    @Test
    void createReturns201WithTimestamps() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Write README", "description": "Document the API", "completed": false}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write README"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void completedDefaultsToFalseWhenOmitted() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Buy milk"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getReturnsTodoById() throws Exception {
        Todo todo = save("Buy milk", false);

        mockMvc.perform(get("/api/todos/{id}", todo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy milk"));
    }

    @Test
    void listFiltersByCompletion() throws Exception {
        save("Open", false);
        save("Done", true);

        mockMvc.perform(get("/api/todos"))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/todos").param("completed", "true"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Done"));
    }

    @Test
    void updateReplacesFields() throws Exception {
        Todo todo = save("Buy milk", false);

        mockMvc.perform(put("/api/todos/{id}", todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Buy oat milk", "description": "2 litres", "completed": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy oat milk"))
                .andExpect(jsonPath("$.description").value("2 litres"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void patchSetsCompletionStatus() throws Exception {
        Todo todo = save("Buy milk", false);

        mockMvc.perform(patch("/api/todos/{id}/completed", todo.getId()).param("value", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
        assertThat(repository.findById(todo.getId()).orElseThrow().isCompleted()).isTrue();
    }

    @Test
    void deleteReturns204() throws Exception {
        Todo todo = save("Buy milk", false);

        mockMvc.perform(delete("/api/todos/{id}", todo.getId()))
                .andExpect(status().isNoContent());
        assertThat(repository.existsById(todo.getId())).isFalse();
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
        mockMvc.perform(patch("/api/todos/{id}/completed", 999).param("value", "true"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/todos/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void blankTitleIsRejected() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "completed": false}
                                """))
                .andExpect(status().isBadRequest());
        assertThat(repository.count()).isZero();
    }
}
