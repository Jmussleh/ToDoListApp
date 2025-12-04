package org.example.web;

import org.example.entity.ToDo;
import org.example.repo.ToDoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private static final Logger log = LoggerFactory.getLogger(ToDoController.class);

    // EASIEST OPTION: create the repo yourself (no Spring injection)
    private final ToDoRepository repo = new ToDoRepository();

    @GetMapping
    public List<ToDo> getAll() {
        List<ToDo> todos = repo.findAll();
        log.info("Listing all todos (count={})", todos.size());
        return todos;
    }

    @PostMapping
    public ToDo create(@RequestBody ToDoCreateRequest request) {
        log.info("Creating todo title='{}', done={}", request.title(), request.done());
        ToDo created = repo.create(request.title(), request.done());
        log.info("Created todo id={}", created.getId());
        return created;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Deleting todo id={}", id);
        boolean ok = repo.deleteById(id);
        if (!ok) {
            log.warn("Delete failed: todo id={} not found", id);
        }
    }

    // ---- Simple DTOs for JSON request bodies ----
    public static record ToDoCreateRequest(String title, boolean done) {}
    public static record ToDoUpdateRequest(String title) {}
}
