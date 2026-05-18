package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

@Component
public class LibrarianController {

    @FXML private TextField searchBar;
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> subjectListView;
    @FXML private FlowPane bookGrid;

    private String librarianUsername;
    private final HttpClient http = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    public void initLibrarian(String username) {
        this.librarianUsername = username;
        welcomeLabel.setText("Librarian: " + username);
        loadAllBooks();
        loadSubjects();
    }

    private void loadAllBooks() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/BookInformation"))
                .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            renderBooks(resp.body());
        } catch (Exception e) {
            showAlert("Error", "Could not load books.");
        }
    }

    private void loadSubjects() {
        subjectListView.getItems().addAll(
            "All", "Science", "Mathematics", "Literature",
            "History", "Technology", "Arts", "Philosophy"
        );
        subjectListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> {
                if (selected == null || selected.equals("All")) loadAllBooks();
                else filterBySubject(selected);
            }
        );
    }

    private void filterBySubject(String subject) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/BookInformation/search/Subject?subject=" + subject))
                .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            renderBooks(resp.body());
        } catch (Exception e) {
            showAlert("Error", "Could not filter books.");
        }
    }

    private void renderBooks(String json) {
        bookGrid.getChildren().clear();
        String[] entries = json.split("\\},\\{");
        for (String entry : entries) {
            String id = extractField(entry, "id");
            String title = extractField(entry, "title");
            String author = extractField(entry, "author");
            String subject = extractField(entry, "subject");
            String date = extractField(entry, "date");
            String imageUrl = extractField(entry, "imageUrl");

            VBox card = buildBookCard(id, title, author, subject, date, imageUrl);
            bookGrid.getChildren().add(card);
        }
    }

    private VBox buildBookCard(String id, String title, String author,
                                String subject, String date, String imageUrl) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; "
            + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        card.setPrefWidth(200);

        ImageView cover = new ImageView();
        cover.setFitWidth(180);
        cover.setFitHeight(240);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            cover.setImage(new Image(imageUrl, true));
        }

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        titleLbl.setWrapText(true);

        Label authorLbl = new Label(author);
        authorLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        card.getChildren().addAll(cover, titleLbl, authorLbl);

        // Click → edit book details
        card.setOnMouseClicked(e ->
            openEditDialog(id, title, author, subject, date, imageUrl)
        );

        return card;
    }

    private void openEditDialog(String id, String title, String author,
                                 String subject, String date, String imageUrl) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Book Information");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(420);
        root.setPrefHeight(560);
        root.setMaxWidth(420);
        root.setMaxHeight(560);

        TextField idField = new TextField(id);
        idField.setPromptText("Classification ID");
        idField.setEditable(false); // ID is read-only once set

        TextField titleField = new TextField(title);
        titleField.setEditable(false);

        TextField authorField = new TextField(author);
        authorField.setEditable(false);

        TextField subjectField = new TextField(subject);
        subjectField.setEditable(false);

        TextField dateField = new TextField(date);
        dateField.setEditable(false);

        TextField imageField = new TextField(imageUrl);
        imageField.setEditable(false);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button editBtn = new Button("Edit");
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);

        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        editBtn.setOnAction(e -> {
            // Enable all fields for editing
            titleField.setEditable(true);
            authorField.setEditable(true);
            subjectField.setEditable(true);
            dateField.setEditable(true);
            imageField.setEditable(true);
            editBtn.setVisible(false);
            saveBtn.setVisible(true);
            cancelBtn.setVisible(true);
        });

        cancelBtn.setOnAction(e -> {
            // Restore original values and disable editing
            titleField.setText(title);
            authorField.setText(author);
            subjectField.setText(subject);
            dateField.setText(date);
            imageField.setText(imageUrl);
            titleField.setEditable(false);
            authorField.setEditable(false);
            subjectField.setEditable(false);
            dateField.setEditable(false);
            imageField.setEditable(false);
            editBtn.setVisible(true);
            saveBtn.setVisible(false);
            cancelBtn.setVisible(false);
            errorLabel.setText("");
        });

        saveBtn.setOnAction(e -> {
            String body = String.format(
                "{\"author\":\"%s\",\"title\":\"%s\",\"subject\":\"%s\","
                + "\"date\":\"%s\",\"imageUrl\":\"%s\"}",
                authorField.getText(), titleField.getText(),
                subjectField.getText(), dateField.getText(), imageField.getText()
            );

            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/BookInformation/update/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() == 200) {
                    dialog.close();
                    showAlert("Saved", "Book updated successfully.");
                    loadAllBooks();
                } else {
                    errorLabel.setText("Update failed: " + resp.body());
                }
            } catch (Exception ex) {
                errorLabel.setText("Connection error.");
            }
        });

        HBox buttons = new HBox(10, editBtn, saveBtn, cancelBtn);

        root.getChildren().addAll(
            new Label("ID:"), idField,
            new Label("Title:"), titleField,
            new Label("Author:"), authorField,
            new Label("Subject:"), subjectField,
            new Label("Date (YYYY-MM-DD):"), dateField,
            new Label("Image URL:"), imageField,
            errorLabel, buttons
        );

        dialog.setScene(new Scene(root, 420, 560));
        dialog.setWidth(550);
        dialog.setHeight(520);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    @FXML
    public void openCreateBookDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Book");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(400);
        root.setPrefHeight(480);
        root.setMaxWidth(400);
        root.setMaxHeight(480);

        TextField idField = new TextField();
        idField.setPromptText("Classification ID (e.g. 001.23)");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        TextField dateField = new TextField(LocalDate.now().toString());
        dateField.setPromptText("Date (YYYY-MM-DD)");

        TextField imageField = new TextField();
        imageField.setPromptText("Image URL (from Supabase Storage)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());

        saveBtn.setOnAction(e -> {
            if (idField.getText().isEmpty() || titleField.getText().isEmpty()) {
                errorLabel.setText("ID and Title are required.");
                return;
            }

            String body = String.format(
                "{\"id\":\"%s\",\"title\":\"%s\",\"author\":\"%s\","
                + "\"subject\":\"%s\",\"date\":\"%s\",\"imageUrl\":\"%s\"}",
                idField.getText(), titleField.getText(), authorField.getText(),
                subjectField.getText(), dateField.getText(), imageField.getText()
            );

            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/BookInformation/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() == 200) {
                    dialog.close();
                    showAlert("Created", "Book added to database.");
                    loadAllBooks();
                } else {
                    errorLabel.setText("Failed: " + resp.body());
                }
            } catch (Exception ex) {
                errorLabel.setText("Connection error.");
            }
        });

        HBox buttons = new HBox(10, saveBtn, cancelBtn);

        root.getChildren().addAll(
            new Label("Classification ID:"), idField,
            new Label("Title:"), titleField,
            new Label("Author:"), authorField,
            new Label("Subject:"), subjectField,
            new Label("Date:"), dateField,
            new Label("Image URL:"), imageField,
            errorLabel, buttons
        );

        dialog.setScene(new Scene(root, 400, 480));
        dialog.setWidth(600);
        dialog.setHeight(520);
        dialog.setResizable(false);
        dialog.showAndWait();
    }


    @FXML
    public void openDuesDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Borrowed Books — Due Checker");
        dialog.setWidth(700);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(700);
        root.setPrefHeight(500);
        root.setMaxWidth(700);
        root.setMaxHeight(500);
        
        ScrollPane scrollPane = new ScrollPane();
        VBox recordList = new VBox(8);
        scrollPane.setContent(recordList);
        scrollPane.setFitToWidth(true);

        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/Borrow/active"))
                .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            // Parse and sort: overdue first (marked red), then non-overdue
            String[] records = resp.body().split("\\},\\{");

            // Sort by overdue status — overdue records first
            java.util.List<String> overdue = new java.util.ArrayList<>();
            java.util.List<String> current = new java.util.ArrayList<>();

            for (String r : records) {
                LocalDate due = LocalDate.parse(extractField(r, "dueDate"));
                if (LocalDate.now().isAfter(due)) overdue.add(r);
                else current.add(r);
            }

            // Render overdue first
            for (String r : overdue) renderBorrowRecord(r, recordList, true);
            for (String r : current) renderBorrowRecord(r, recordList, false);

        } catch (Exception e) {
            recordList.getChildren().add(new Label("Could not load borrow records."));
        }

        root.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        dialog.setScene(new Scene(root, 700, 500));
        dialog.setWidth(700);
        dialog.setHeight(500);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private void renderBorrowRecord(String json, VBox container, boolean isOverdue) {
        String borrowId = extractField(json, "borrowId");
        String bookTitle = extractField(json, "bookTitle");
        String studentName = extractField(json, "studentName");
        String studentEmail = extractField(json, "studentEmail");
        String studentNum = extractField(json, "studentNumber");
        String dueDate = extractField(json, "dueDate");

        VBox card = new VBox(4);
        card.setPadding(new Insets(10));
        String bg = isOverdue ? "#ffe6e6" : "#f0fff0";
        String border = isOverdue ? "#e74c3c" : "#27ae60";
        card.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border
            + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label bookLbl = new Label("📖 " + bookTitle);
        bookLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label studentLbl = new Label("Student: " + studentName
            + "  |  " + studentEmail + "  |  #" + studentNum);
        Label dueLbl = new Label("Due: " + dueDate
            + (isOverdue ? " ⚠ OVERDUE" : ""));
        dueLbl.setStyle(isOverdue ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" : "");

        Button extendBtn = new Button("Extend 1 Week");
        Button completeBtn = new Button("Mark as Returned");
        completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        extendBtn.setOnAction(e -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/Borrow/extend/" + borrowId))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
                http.send(req, HttpResponse.BodyHandlers.ofString());
                showAlert("Extended", "Due date extended by one week.");
                container.getChildren().remove(card);
            } catch (Exception ex) {
                showAlert("Error", "Could not extend due.");
            }
        });

        completeBtn.setOnAction(e -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/Borrow/complete/" + borrowId))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
                http.send(req, HttpResponse.BodyHandlers.ofString());
                showAlert("Completed", "Book marked as returned. Now available.");
                container.getChildren().remove(card);
                loadAllBooks();
            } catch (Exception ex) {
                showAlert("Error", "Could not complete due.");
            }
        });

        HBox actions = new HBox(10, extendBtn, completeBtn);
        card.getChildren().addAll(bookLbl, studentLbl, dueLbl, actions);
        container.getChildren().add(card);
    }

    @FXML
    public void handleSearch() {
        String query = searchBar.getText().trim();
        if (query.isEmpty()) { loadAllBooks(); return; }
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/BookInformation/search/Title?title=" + query))
                .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            renderBooks(resp.body());
        } catch (Exception e) {
            showAlert("Error", "Search failed.");
        }
    }

    @FXML
    public void handleLogout() throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/login.fxml")
        );
        Stage stage = (Stage) searchBar.getScene().getWindow();
        stage.setScene(new Scene(loader.load(), 900, 600));
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? "" : json.substring(start, end);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}