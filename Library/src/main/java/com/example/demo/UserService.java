package com.example.demo;

public interface UserService {

    UserAccount register(String username, String email, String rawPassword);
    UserAccount login(String email, String rawPassword);
    void sendPasswordResetEmail(String email);
    void resetPassword(String token, String newRawPassword);
}