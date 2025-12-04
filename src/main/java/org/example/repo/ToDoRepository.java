package org.example.repo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.HibernateUtility;
import org.example.entity.ToDo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ToDoRepository {

    private static final Logger log = LogManager.getLogger(ToDoRepository.class);

    private final SessionFactory sessionFactory;

    // Keeps current behavior (uses your HibernateUtility)
    public ToDoRepository() {
        this(HibernateUtility.getSessionFactory());
    }

    // Allows tests to inject a custom SessionFactory (e.g., H2)
    public ToDoRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // CREATE
    public ToDo create(String title, boolean done) {
        log.debug("create(title='{}', done={})", title, done);

        ToDo todo = new ToDo(title, done);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(todo);
            tx.commit();
            log.info("Persisted ToDo id={} title='{}'", todo.getId(), todo.getTitle());
            return todo;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Error creating ToDo title='{}'", title, e);
            throw e;
        }
    }

    // READ by id
    public Optional<ToDo> findById(Long id) {
        log.debug("findById(id={})", id);
        try (Session session = sessionFactory.openSession()) {
            ToDo result = session.get(ToDo.class, id);
            if (result == null) {
                log.info("ToDo not found id={}", id);
            } else {
                log.info("Found ToDo id={} title='{}'", result.getId(), result.getTitle());
            }
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.error("Error finding ToDo by id={}", id, e);
            throw e;
        }
    }

    // READ all
    public List<ToDo> findAll() {
        log.debug("findAll()");
        try (Session session = sessionFactory.openSession()) {
            List<ToDo> list = session
                    .createQuery("from ToDo order by id", ToDo.class)
                    .list();
            log.info("Retrieved {} ToDo records", list.size());
            return list;
        } catch (Exception e) {
            log.error("Error retrieving all ToDo records", e);
            throw e;
        }
    }

    // UPDATE title
    public boolean updateTitle(Long id, String newTitle) {
        log.debug("updateTitle(id={}, newTitle='{}')", id, newTitle);

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) {
                log.info("No ToDo found to update id={}", id);
                tx.rollback();
                return false;
            }
            t.setTitle(newTitle);
            session.merge(t);
            tx.commit();
            log.info("Updated ToDo id={} newTitle='{}'", id, newTitle);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Error updating ToDo id={} newTitle='{}'", id, newTitle, e);
            throw e;
        }
    }

    // DELETE by id
    public boolean deleteById(Long id) {
        log.debug("deleteById(id={})", id);

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) {
                log.info("No ToDo found to delete id={}", id);
                tx.rollback();
                return false;
            }
            session.remove(t);
            tx.commit();
            log.info("Deleted ToDo id={}", id);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Error deleting ToDo id={}", id, e);
            throw e;
        }
    }
}


