package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "todos")
public class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=200)
    private String title;

    private boolean done;

    public ToDo() {}
    public ToDo(String title, boolean done) { this.title = title; this.done = done; }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public boolean isDone() { return done; }

    public void setTitle(String title) { this.title = title; }
    public void setDone(boolean done) { this.done = done; }

    @Override public String toString() {
        return "Todo{id=" + id + ", title='" + title + "', done=" + done + '}';
    }
}
