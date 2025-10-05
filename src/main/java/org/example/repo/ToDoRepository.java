package org.example.repo;

import org.example.config.HibernateUtility;
import org.example.entity.ToDo;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ToDoRepository {

    // CREATE
    public ToDo create(String title, boolean done) {
        ToDo todo = new ToDo(title, done);
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(todo);
            tx.commit();
        }
        return todo;
    }

    // READ by id
    public Optional<ToDo> findById(Long id) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(ToDo.class, id));
        }
    }

    // READ all
    public List<ToDo> findAll() {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            return session.createQuery("from ToDo order by id", ToDo.class).list();
        }
    }

    // UPDATE title
    public boolean updateTitle(Long id, String newTitle) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) { tx.rollback(); return false; }
            t.setTitle(newTitle);
            tx.commit();
            return true;
        }
    }


    // DELETE by id
    public boolean deleteById(Long id) {
        try (Session session = HibernateUtility.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            ToDo t = session.get(ToDo.class, id);
            if (t == null) { tx.rollback(); return false; }
            session.remove(t);
            tx.commit();
            return true;
        }
    }
}
