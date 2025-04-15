package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;

import java.io.*;

public class ToDoListController {

    @FXML
    private TextField taskTextField;

    @FXML
    private ListView<String> taskList;

    private ObservableList<String> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        taskList.setItems(tasks);
        taskList.setEditable(true);
        taskList.setCellFactory(TextFieldListCell.forListView());


        // Load tasks when the application starts
        loadTasks();

        // Handle item commits for editing
        taskList.setOnEditCommit(event -> {
            tasks.set(event.getIndex(), event.getNewValue());
            saveTasks(); // Save tasks after editing
            updateTaskDisplay(); // Update display after editing
        });

        // Enable deletion on pressing DELETE key
        taskList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteTask();
            }
        });

        // Enable marking tasks as done on double-click
        taskList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                markTaskAsDone();
            }
        });
    }

    @FXML
    public void addTask() {
        String task = taskTextField.getText().trim();
        if (!task.isEmpty()) {
            if (!tasks.contains(task)) { // Check for duplicates
                tasks.add(task);
                taskTextField.clear();
                taskTextField.requestFocus(); // Focus back to the text field
                saveTasks(); // Save tasks after adding
                updateTaskDisplay(); // Update display after adding
            } else {
                showAlert("Задача уже существует!", "Эта задача уже в списке.");
            }
        } else {
            showAlert("Ошибка", "Введите задачу перед добавлением.");
        }
    }

    @FXML
    public void deleteTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tasks.remove(selectedIndex);
            saveTasks(); // Save tasks after deletion
            updateTaskDisplay(); // Update display after deletion
        } else {
            showAlert("Ошибка", "Выберите задачу для удаления.");
        }
    }

    @FXML
    public void editTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String currentTask = tasks.get(selectedIndex);
            taskTextField.setText(currentTask); // Set the text field to the current task for editing

            // Remove the current task temporarily to avoid duplicates during editing
            tasks.remove(selectedIndex);

            // Focus on the text field for immediate editing
            taskTextField.requestFocus();

            // Add a listener to handle when the user presses Enter to confirm editing
            taskTextField.setOnAction(event -> {
                String newTask = taskTextField.getText().trim();
                if (!newTask.isEmpty()) {
                    if (!tasks.contains(newTask)) { // Check for duplicates before adding back
                        tasks.add(newTask);
                        saveTasks(); // Save tasks after editing
                        updateTaskDisplay(); // Update display after editing
                    } else {
                        showAlert("Задача уже существует!", "Эта задача уже в списке.");
                        tasks.add(currentTask); // Re-add the original task if duplicate found
                    }
                    taskTextField.clear(); // Clear the text field after editing
                    taskTextField.setOnAction(null); // Remove listener to prevent multiple actions on Enter key press.
                } else {
                    showAlert("Ошибка", "Введите задачу перед сохранением.");
                    tasks.add(currentTask); // Re-add original task if input is empty.
                    taskTextField.clear();
                    taskTextField.setOnAction(null);
                }
            });

        } else {
            showAlert("Ошибка", "Выберите задачу для редактирования.");
        }
    }

    @FXML
    public void markTaskAsDone() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String currentTask = tasks.get(selectedIndex);

            if (!currentTask.startsWith("[✓] ")) {  // Check if it's not already marked as done.
                currentTask = "[✓] " + currentTask;  // Mark as done by prefixing with "[✓] "
                tasks.set(selectedIndex, currentTask);  // Update the list with marked done status.
                saveTasks();  // Save updated list.
                updateTaskDisplay();  // Update display.
            } else {
                showAlert("Информация", "Задача уже выполнена.");
            }

        } else {
            showAlert("Ошибка", "Выберите задачу для пометки как выполненную.");
        }
    }

    @FXML
    public void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось сохранить задачи.");
        }
    }

    @FXML
    public void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!tasks.contains(line)) { // Prevent duplicates when loading
                    tasks.add(line);
                }
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить задачи.");
        }

        updateTaskDisplay();  // Update display after loading.
    }

    private void updateTaskDisplay() {
        ObservableList<String> numberedTasks = FXCollections.observableArrayList();
        for (int i = 0; i < tasks.size(); i++) {
            numberedTasks.add((i + 1) + ". " + tasks.get(i));
        }
        taskList.setItems(numberedTasks);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}