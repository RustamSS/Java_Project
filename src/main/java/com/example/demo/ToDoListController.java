package com.example.demo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

        // Handle item commits for editing
        taskList.setOnEditCommit(event -> {
            tasks.set(event.getIndex(), event.getNewValue());
        });

        // Enable deletion on pressing DELETE key
        taskList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteTask();
            }
        });
    }
    @FXML
    public void addTask() {
        String task = taskTextField.getText().trim();
        if (!task.isEmpty()) {
            tasks.add(task);
            taskTextField.clear();
        }
    }

    @FXML
    public void deleteTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tasks.remove(selectedIndex);
        }
    }

    @FXML
    public void editTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            taskList.edit(selectedIndex);
        }
    }
}