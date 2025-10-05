package org.example.repo;

import org.example.config.HibernateUtility;
import org.example.entity.ToDo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ToDoRepository {

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
        ToDo todo = new ToDo(title, done);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(todo);
            tx.commit();
            return todo;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // READ by id
    public Optional<ToDo> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(ToDo.class, id));
        }
    }

    // READ all
    public List<ToDo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from ToDo order by id", ToDo.class).list();
        }
    }

    // UPDATE title
    public boolean updateTitle(Long id, String newTitle) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) {
                tx.rollback();
                return false;
            }
            t.setTitle(newTitle);
            session.merge(t);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // DELETE by id
    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) {
                tx.rollback();
                return false;
            }
            session.remove(t);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

