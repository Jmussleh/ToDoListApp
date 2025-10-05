import org.example.entity.ToDo;
import org.example.repo.ToDoRepository;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoRepositoryTest {

    private SessionFactory sessionFactory;
    private ToDoRepository repo;

    @BeforeEach
    void setup() {
        // New in-memory DB + schema for EVERY test
        sessionFactory = TestHibernate.buildSessionFactory();
        repo = new ToDoRepository(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void testCreateAndFindAll() {
        repo.create("Buy groceries", false);
        repo.create("Clean the house", true);

        List<ToDo> todos = repo.findAll();
        assertEquals(2, todos.size());
        assertNotNull(todos.get(0).getId());
        assertNotNull(todos.get(1).getId());
    }

    @Test
    void testFindById() {
        ToDo todo = repo.create("Learn Hibernate", false);
        var found = repo.findById(todo.getId());
        assertTrue(found.isPresent());
        assertEquals("Learn Hibernate", found.get().getTitle());
        assertFalse(found.get().isDone());
    }

    @Test
    void testUpdateTitle() {
        ToDo todo = repo.create("Old title", false);
        boolean updated = repo.updateTitle(todo.getId(), "New title");
        assertTrue(updated);

        var after = repo.findById(todo.getId());
        assertTrue(after.isPresent());
        assertEquals("New title", after.get().getTitle());
    }

    @Test
    void testDeleteById() {
        ToDo todo = repo.create("Temporary task", false);
        boolean deleted = repo.deleteById(todo.getId());
        assertTrue(deleted);

        var found = repo.findById(todo.getId());
        assertTrue(found.isEmpty());
    }
}

