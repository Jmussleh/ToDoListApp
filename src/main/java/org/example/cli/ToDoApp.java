package org.example.cli;

import org.example.config.HibernateUtility;
import org.example.entity.ToDo;
import org.example.repo.ToDoRepository;

import java.util.List;
import java.util.Scanner;
//Main method for app
public class ToDoApp {

    private static final Scanner in = new Scanner(System.in);
    private static final ToDoRepository repo = new ToDoRepository();

    public static void main(String[] args) {
        System.out.println("*** To-Do List App ***");
        boolean running = true;
        //While running do this:
        while (running) {
            printMenu();
            String input = prompt("Enter choice: ");

            int choice;
            try { choice = Integer.parseInt(input.trim()); }
            catch (NumberFormatException e) { println("Please enter a valid number."); continue; }

            switch (choice) {
                //Choices based on case from the CLI menu
                case 1 -> addTask();
                case 2 -> removeTask();
                case 3 -> listTasks();
                case 4 -> updateTitle();
                case 5 -> { println("Exiting application..."); running = false; }
                default -> println("Invalid choice. Try again.");
            }
        }

        in.close();
        HibernateUtility.shutdown();
    }

    //Actions from the CLI list

    //Add a task to the list
    private static void addTask() {
        String task = prompt("Enter task: ");
        if (task.isBlank()) { println("Task cannot be empty."); return; }
        ToDo created = repo.create(task.trim(), false);
        println("Added: [" + created.getId() + "] " + created.getTitle());
    }

    //Remove a task from the list
    private static void removeTask() {
        Long id = readId("Enter the ID of the task to delete: ");
        if (id == null) return;
        boolean ok = repo.deleteById(id);
        println(ok ? "Removed task id " + id : "Task not found.");
    }

    //Lists all tasks
    private static void listTasks() {
        List<ToDo> tasks = repo.findAll();
        if (tasks.isEmpty()) {
            println("There are no tasks to view...");
            return;
        }
        //printing the to-do list
        println("To-Do List");
        for (ToDo t : tasks) {
            String box = t.isDone() ? "[x]" : "[ ]";
            System.out.printf("%-4s %-3s %s%n", t.getId() + ")", box, t.getTitle());
        }
    }

    //Update name of task
    private static void updateTitle() {
        Long id = readId("Enter the ID of the task to rename: ");
        if (id == null) return;
        String title = prompt("Enter new task: ");
        if (title.isBlank()) { println("Title cannot be empty."); return; }
        boolean ok = repo.updateTitle(id, title.trim());
        println(ok ? "Updated task " + id : "Task not found.");
    }


    //Helpers
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

    private static void println(String s) { System.out.println(s); }

    private static Long readId(String msg) {
        String raw = prompt(msg);
        try { return Long.parseLong(raw.trim()); }
        catch (NumberFormatException e) { println("Invalid input. Please enter a numeric ID."); return null; }
    }
}
