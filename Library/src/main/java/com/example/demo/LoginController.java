package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class LoginController {

    // --- Login fields ---
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginErrorLabel;

    // --- Register fields (hidden by default) ---
    @FXML private VBox registerPanel;
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private Label registerErrorLabel;

    // --- Forgot password fields (hidden by default) ---
    @FXML private VBox forgotPanel;
    @FXML private TextField forgotEmailField;
    @FXML private Label forgotMessageLabel;

    // --- Librarian login fields ---
    @FXML private VBox librarianPanel;
    @FXML private TextField libUsernameField;
    @FXML private PasswordField libPasswordField;
    @FXML private Label libErrorLabel;

    private final HttpClient http = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    @FXML
    public void initialize() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        forgotPanel.setVisible(false);
        forgotPanel.setManaged(false);
        librarianPanel.setVisible(false);
        librarianPanel.setManaged(false);
    }

    // --- Student Login ---

    @FXML
    public void handleStudentLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please enter email and password.");
            return;
        }

        String body = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}", email, password
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/User/login/student"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse username from response and open student UI
                String username = parseJsonField(response.body(), "username");
                openStudentUI(username, email);
            } else {
                loginErrorLabel.setText("Login failed. Check your credentials.");
            }
        } catch (Exception e) {
            loginErrorLabel.setText("Connection error. Is the server running?");
        }
    }

    // --- Librarian Login ---

    @FXML
    public void showLibrarianPanel() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        forgotPanel.setVisible(false);
        forgotPanel.setManaged(false);
        
        boolean isVisible = librarianPanel.isVisible();
        librarianPanel.setVisible(!isVisible);
        librarianPanel.setManaged(!isVisible);
    }

    @FXML
    public void handleLibrarianLogin() {
        String username = libUsernameField.getText().trim();
        String password = libPasswordField.getText();

        String body = String.format(
            "{\"username\":\"%s\",\"password\":\"%s\"}", username, password
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/User/login/librarian"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                openLibrarianUI(username);
            } else {
                libErrorLabel.setText("Invalid librarian credentials.");
            }
        } catch (Exception e) {
            libErrorLabel.setText("Connection error.");
        }
    }

    // --- Register ---

    @FXML
    public void showRegisterPanel() {
    	   forgotPanel.setVisible(false);
    	    forgotPanel.setManaged(false);
    	    librarianPanel.setVisible(false);
    	    librarianPanel.setManaged(false);

        boolean isVisible = registerPanel.isVisible();
        registerPanel.setVisible(!isVisible);
        registerPanel.setManaged(!isVisible);
    }

    @FXML
    public void handleRegister() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerErrorLabel.setText("All fields are required.");
            return;
        }

        if (password.length() < 8) {
            registerErrorLabel.setText("Password must be at least 8 characters.");
            return;
        }

        String body = String.format(
            "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
            username, email, password
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/User/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                registerErrorLabel.setStyle("-fx-text-fill: green;");
                registerErrorLabel.setText("Account created! You can now log in.");
                registerPanel.setVisible(false);
                registerPanel.setManaged(false);
            } else {
                registerErrorLabel.setText("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            registerErrorLabel.setText("Connection error.");
        }
    }

    // --- Forgot Password ---

    @FXML
    public void showForgotPanel() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        librarianPanel.setVisible(false);
        librarianPanel.setManaged(false);

        // Toggle forgot panel
        boolean isVisible = forgotPanel.isVisible();
        forgotPanel.setVisible(!isVisible);
        forgotPanel.setManaged(!isVisible);
    }

    @FXML
    public void handleForgotPassword() {
        String email = forgotEmailField.getText().trim();
        if (email.isEmpty()) {
            forgotMessageLabel.setText("Please enter your email.");
            return;
        }

        String body = String.format("{\"email\":\"%s\"}", email);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/User/forgot-password"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString());

            forgotMessageLabel.setStyle("-fx-text-fill: green;");
            forgotMessageLabel.setText("Reset email sent! Check your inbox.");
        } catch (Exception e) {
            forgotMessageLabel.setText("Error sending email.");
        }
    }

    // --- Navigation helpers ---

    private void openStudentUI(String username, String email) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/student_main.fxml")
        );
        // Pass logged-in user info to StudentController
        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene scene = new Scene(loader.load(), 1200, 750);

        StudentController ctrl = loader.getController();
        ctrl.initUser(username, email);

        stage.setScene(scene);
    }

    private void openLibrarianUI(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/librarian_main.fxml")
        );
        Stage stage = (Stage) libUsernameField.getScene().getWindow();
        Scene scene = new Scene(loader.load(), 1200, 750);

        LibrarianController ctrl = loader.getController();
        ctrl.initLibrarian(username);

        stage.setScene(scene);
    }

    // Minimal JSON field extractor (avoids adding a JSON library just for this)
    private String parseJsonField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key) + key.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    // Panel swapping
    
}