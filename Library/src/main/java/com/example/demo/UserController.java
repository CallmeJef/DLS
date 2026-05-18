package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/User")
public class UserController {

    private final UserService userService;

    // Librarian credentials loaded 
    @Value("${librarian.username}")
    private String librarianUsername;

    @Value("${librarian.password}")
    private String librarianPassword;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Student registration
    @PostMapping("/register")
    public UserAccount register(@RequestBody Map<String, String> body) {
        return userService.register(
            body.get("username"),
            body.get("email"),
            body.get("password")
        );
    }

    // Student login 
    @PostMapping("/login/student")
    public UserAccount studentLogin(@RequestBody Map<String, String> body) {
        return userService.login(body.get("email"), body.get("password"));
    }

    @PostMapping("/login/librarian")
    public Map<String, String> librarianLogin(@RequestBody Map<String, String> body) {
        String user = body.get("username");
        String pass = body.get("password");

        if (librarianUsername.equals(user) && librarianPassword.equals(pass)) {
            return Map.of("role", "LIBRARIAN", "username", user);
        } else {
            throw new RuntimeException("Invalid librarian credentials.");
        }
    }

    // Forgot password sends reset email
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody Map<String, String> body) {
        userService.sendPasswordResetEmail(body.get("email"));
        return Map.of("message", "Reset email sent to " + body.get("email"));
    }

    // Reset password using token received via email
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("token"), body.get("newPassword"));
        return Map.of("message", "Password reset successfully.");
    }
}