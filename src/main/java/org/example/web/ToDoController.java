package org.example.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.ToDo;
import org.example.repo.ToDoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private static final Logger log = LogManager.getLogger(ToDoController.class);

    private final ToDoRepository repo;

    public ToDoController() {
        // Reuse your existing Hibernate-based repository
        this.repo = new ToDoRepository();
    }

    // GET /api/todos -> list all
    @GetMapping
    public List<ToDo> getAll() {
        List<ToDo> list = repo.findAll();
        log.info("HTTP GET /api/todos - returned {} tasks", list.size());
        return list;
    }

    // GET /api/todos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ToDo> getOne(@PathVariable Long id) {
        return repo.findById(id)
                .map(todo -> {
                    log.info("HTTP GET /api/todos/{} - found", id);
                    return ResponseEntity.ok(todo);
                })
                .orElseGet(() -> {
                    log.info("HTTP GET /api/todos/{} - not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // POST /api/todos with JSON { "title": "...", "done": false }
    @PostMapping
    public ResponseEntity<ToDo> create(@RequestBody ToDoCreateRequest body) {
        if (body.title() == null || body.title().isBlank()) {
            log.warn("HTTP POST /api/todos - missing title");
            return ResponseEntity.badRequest().build();
        }
        ToDo created = repo.create(body.title().trim(), body.done());
        log.info("HTTP POST /api/todos - created id={} title='{}'",
                created.getId(), created.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/todos/{id} with JSON { "title": "New title" }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTitle(@PathVariable Long id,
                                            @RequestBody ToDoUpdateRequest body) {
        if (body.title() == null || body.title().isBlank()) {
            log.warn("HTTP PUT /api/todos/{} - empty title", id);
            return ResponseEntity.badRequest().build();
        }
        boolean ok = repo.updateTitle(id, body.title().trim());
        if (ok) {
            log.info("HTTP PUT /api/todos/{} - updated title to '{}'", id, body.title().trim());
            return ResponseEntity.noContent().build();
        } else {
            log.info("HTTP PUT /api/todos/{} - not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/todos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = repo.deleteById(id);
        if (ok) {
            log.info("HTTP DELETE /api/todos/{} - deleted", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("HTTP DELETE /api/todos/{} - not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Simple DTOs for JSON request bodies
    public record ToDoCreateRequest(String title, boolean done) {}
    public record ToDoUpdateRequest(String title) {}
}
