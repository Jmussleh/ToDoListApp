package org.example.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.example.config.HibernateUtility;
import org.example.entity.ToDo;
import org.example.repo.ToDoRepository;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

// Main method for app
public class ToDoApp {

    private static final Logger log = LogManager.getLogger(ToDoApp.class);

    private static final Scanner in = new Scanner(System.in);
    private static final ToDoRepository repo = new ToDoRepository();

    public static void main(String[] args) {
        // Context that will show up in every JSON log entry
        ThreadContext.put("userId", "cli-user");
        ThreadContext.put("sessionId", UUID.randomUUID().toString());

        log.info("Application started");

        System.out.println("*** To-Do List App ***");
        boolean running = true;

        // While running do this:
        while (running) {
            printMenu();
            String input = prompt("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid menu input for choice: '{}'", input);
                println("Please enter a valid number.");
                continue;
            }

            log.info("User selected menu option {}", choice);

            switch (choice) {
                // Choices based on case from the CLI menu
                case 1 -> addTask();
                case 2 -> removeTask();
                case 3 -> listTasks();
                case 4 -> updateTitle();
                case 5 -> {
                    log.info("User chose to exit application");
                    println("Exiting application...");
                    running = false;
                }
                default -> {
                    log.warn("Invalid menu choice: {}", choice);
                    println("Invalid choice. Try again.");
                }
            }
        }

        log.info("Application shutting down");

        in.close();
        HibernateUtility.shutdown();
        ThreadContext.clearAll();
    }

    // Actions from the CLI list

    // Add a task to the list
    private static void addTask() {
        String task = prompt("Enter task: ");
        if (task.isBlank()) {
            log.warn("Attempted to create task with empty title");
            println("Task cannot be empty.");
            return;
        }

        try {
            ToDo created = repo.create(task.trim(), false);
            log.info("Task created id={} title='{}'", created.getId(), created.getTitle());
            println("Added: [" + created.getId() + "] " + created.getTitle());
        } catch (Exception e) {
            log.error("Error while creating task with title='{}'", task, e);
            println("An error occurred while creating the task. Check logs for details.");
        }
    }

    // Remove a task from the list
    private static void removeTask() {
        Long id = readId("Enter the ID of the task to delete: ");
        if (id == null) return;

        try {
            boolean ok = repo.deleteById(id);
            if (ok) {
                log.info("Task deleted id={}", id);
                println("Removed task id " + id);
            } else {
                log.info("Delete requested for non-existent task id={}", id);
                println("Task not found.");
            }
        } catch (Exception e) {
            log.error("Error while deleting task id={}", id, e);
            println("An error occurred while deleting the task. Check logs for details.");
        }
    }

    // Lists all tasks
    private static void listTasks() {
        try {
            List<ToDo> tasks = repo.findAll();
            log.info("Listing tasks, count={}", tasks.size());

            if (tasks.isEmpty()) {
                println("There are no tasks to view...");
                return;
            }
            // printing the to-do list
            println("To-Do List");
            for (ToDo t : tasks) {
                String box = t.isDone() ? "[x]" : "[ ]";
                System.out.printf("%-4s %-3s %s%n", t.getId() + ")", box, t.getTitle());
            }
        } catch (Exception e) {
            log.error("Error while listing tasks", e);
            println("An error occurred while listing tasks. Check logs for details.");
        }
    }

    // Update name of task
    private static void updateTitle() {
        Long id = readId("Enter the ID of the task to rename: ");
        if (id == null) return;

        String title = prompt("Enter new task: ");
        if (title.isBlank()) {
            log.warn("Attempted to update task id={} with empty title", id);
            println("Title cannot be empty.");
            return;
        }

        try {
            boolean ok = repo.updateTitle(id, title.trim());
            if (ok) {
                log.info("Task updated id={} newTitle='{}'", id, title.trim());
                println("Updated task " + id);
            } else {
                log.info("Update requested for non-existent task id={}", id);
                println("Task not found.");
            }
        } catch (Exception e) {
            log.error("Error while updating task id={} newTitle='{}'", id, title, e);
            println("An error occurred while updating the task. Check logs for details.");
        }
    }

    // Helpers
    private static void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Add Task");
        System.out.println("2. Remove Task");
        System.out.println("3. List All Tasks");
        System.out.println("4. Update Task Title");
        System.out.println("5. Exit Application");
    }

    private static String prompt(String msg) {
        System.out.print(msg);
        return in.nextLine();
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static Long readId(String msg) {
        String raw = prompt(msg);
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid numeric ID input: '{}'", raw);
            println("Invalid input. Please enter a numeric ID.");
            return null;
        }
    }
}
