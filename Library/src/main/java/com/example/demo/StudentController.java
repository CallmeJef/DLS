package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class StudentController {

    // Top bar
    @FXML private TextField searchBar;
    @FXML private Label welcomeLabel;

    // Left panel (subject filter)
    @FXML private ListView<String> subjectListView;

    // Main book grid
    @FXML private FlowPane bookGrid;

    private String loggedInUsername;
    private String loggedInEmail;

    private final HttpClient http = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    public void initUser(String username, String email) {
        this.loggedInUsername = username;
        this.loggedInEmail = email;
        welcomeLabel.setText("Welcome, " + username);
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
            showAlert("Error", "Could not load books: " + e.getMessage());
        }
    }

    private void loadSubjects() {
        // Populate subject filter — in a real app, fetch distinct subjects from DB
        subjectListView.getItems().addAll(
            "All", "Science", "Mathematics", "Literature",
            "History", "Technology", "Arts", "Philosophy"
        );
        subjectListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> {
                if (selected == null || selected.equals("All")) {
                    loadAllBooks();
                } else {
                    filterBySubject(selected);
                }
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

    /**
     * Renders a JSON array of books as cards in the FlowPane.
     * Each card shows: cover image | title | author | date | borrowed indicator
     */
    private void renderBooks(String json) {
        bookGrid.getChildren().clear();
        // NOTE: In production, use Jackson ObjectMapper to parse JSON properly.
        // This simplified split approach works for well-formed responses.
        String[] entries = json.split("\\},\\{");
        for (String entry : entries) {
            String title = extractField(entry, "title");
            String author = extractField(entry, "author");
            String subject = extractField(entry, "subject");
            String date = extractField(entry, "date");
            String imageUrl = extractField(entry, "imageUrl");
            boolean isBorrowed = entry.contains("\"isBorrowed\":true");
            String id = extractField(entry, "id");

            VBox card = buildBookCard(id, title, author, subject, date, imageUrl, isBorrowed);
            bookGrid.getChildren().add(card);
        }
    }

    private VBox buildBookCard(String id, String title, String author,
                                String subject, String date,
                                String imageUrl, boolean isBorrowed) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; "
            + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        card.setPrefWidth(200);

        // Cover image
        ImageView cover = new ImageView();
        cover.setFitWidth(110);
        cover.setFitHeight(220);
        cover.setPreserveRatio(true);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            cover.setImage(new Image(imageUrl, true)); // true = background loading
        }

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        titleLabel.setWrapText(true);

        // Author (smaller)
        Label authorLabel = new Label(author);
        authorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Date
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");

        // Borrowed indicator
        Label statusLabel = new Label(isBorrowed ? "Borrowed" : "Available");
        statusLabel.setStyle(isBorrowed
            ? "-fx-text-fill: #e74c3c; -fx-font-size: 11px;"
            : "-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        card.getChildren().addAll(cover, titleLabel, authorLabel, dateLabel, statusLabel);

        // Click → open book detail popup
        card.setOnMouseClicked(e ->
            openBookDetail(id, title, author, subject, date, imageUrl, isBorrowed)
        );

        return card;
    }

    private void openBookDetail(String id, String title, String author,
                                 String subject, String date,
                                 String imageUrl, boolean isBorrowed) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #fafafa;");
    
      
        ImageView cover = new ImageView();
        cover.setFitWidth(200);
        cover.setFitHeight(280);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            cover.setImage(new Image(imageUrl, true));
        }

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label authorLbl = new Label("By: " + author);
        Label subjectLbl = new Label("Subject: " + subject);
        Label dateLbl = new Label("Published: " + date);

        root.getChildren().addAll(cover, titleLbl, authorLbl, subjectLbl, dateLbl);

        if (isBorrowed) {
            Label borrowedMsg = new Label("This book is currently borrowed.");
            borrowedMsg.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            root.getChildren().add(borrowedMsg);
        } else {
            Button borrowBtn = new Button("Borrow this Book");
            borrowBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 8 20;");
            borrowBtn.setOnAction(e -> {
                dialog.close();
                openBorrowForm(id, title);
            });
            root.getChildren().add(borrowBtn);
        }

        dialog.setScene(new Scene(root, 360, 520)); 
        dialog.setWidth(660);
        dialog.setHeight(520);
        dialog.setResizable(false); 
        dialog.showAndWait();
    }

    private void openBorrowForm(String bookId, String bookTitle) {
        Stage form = new Stage();
        form.initModality(Modality.APPLICATION_MODAL);
        form.setTitle("Borrow Book");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setPrefWidth(380);
        root.setPrefHeight(320);
        root.setMaxWidth(680);
        root.setMaxHeight(320);  
        
        Label titleLbl = new Label("Borrowing: " + bookTitle);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Auto-filled fields
        TextField emailField = new TextField(loggedInEmail);
        emailField.setPromptText("Email");
        emailField.setEditable(false);

        TextField nameField = new TextField(loggedInUsername);
        nameField.setPromptText("Student Name");
        nameField.setEditable(false);

        // Student number must be entered manually
        TextField studentNumField = new TextField();
        studentNumField.setPromptText("Student Number");

        // Due date auto-set to next week
        java.time.LocalDate due = java.time.LocalDate.now().plusWeeks(1);
        Label dueLabel = new Label("Due Date: " + due);
        dueLabel.setStyle("-fx-text-fill: #555;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button confirmBtn = new Button("Confirm Borrow");
        confirmBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        confirmBtn.setOnAction(e -> {
            String studentNum = studentNumField.getText().trim();
            if (studentNum.isEmpty()) {
                errorLabel.setText("Please enter your student number.");
                return;
            }

            String body = String.format(
                "{\"bookId\":\"%s\",\"studentEmail\":\"%s\","
                + "\"studentName\":\"%s\",\"studentNumber\":\"%s\"}",
                bookId, loggedInEmail, loggedInUsername, studentNum
            );

            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/Borrow/borrow"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() == 200) {
                    form.close();
                    showAlert("Success", "Book borrowed! Due: " + due);
                    loadAllBooks(); // refresh grid
                } else {
                    errorLabel.setText("Borrow failed: " + resp.body());
                }
            } catch (Exception ex) {
                errorLabel.setText("Connection error.");
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> form.close());

        HBox buttons = new HBox(10, confirmBtn, cancelBtn);

        root.getChildren().addAll(titleLbl, emailField, nameField,
            studentNumField, dueLabel, errorLabel, buttons);

        form.setScene(new Scene(root, 380, 320));
        form.setWidth(680);   
        form.setHeight(320); 
        form.setResizable(false);
 
        form.showAndWait();
    }

    @FXML
    public void handleSearch() {
        String query = searchBar.getText().trim();
        if (query.isEmpty()) {
            loadAllBooks();
            return;
        }
        // Default search by title; the flyout category selector can call other endpoints
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

    // Minimal field extractor for quick JSON parsing
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