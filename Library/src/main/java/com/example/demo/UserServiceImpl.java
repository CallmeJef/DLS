package com.example.demo;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepo, JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }

    @Override
    public UserAccount register(String username, String email, String rawPassword) {

        // password must be at least 8 characters
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters.");
        }

        // Validate Gmail
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            throw new RuntimeException("Students must register with a Gmail address.");
        }

        // email uniqueness
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists.");
        }

        // username uniqueness
        if (userRepo.existsByUsername(username)) {
            throw new RuntimeException("This username is already taken.");
        }

        String hashed = encoder.encode(rawPassword);
        UserAccount account = new UserAccount(username, email, hashed);
        return userRepo.save(account);
    }

    @Override
    public UserAccount login(String email, String rawPassword) {
        UserAccount account = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No account found for: " + email));

        if (!encoder.matches(rawPassword, account.getPasswordHash())) {
            throw new RuntimeException("Incorrect password.");
        }

        return account;
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        UserAccount account = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No account found for: " + email));

        // Generate a one-time token
        String token = UUID.randomUUID().toString();
        account.setResetToken(token);
        userRepo.save(account);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("DLS Library — Password Reset");
        msg.setText("Your password reset token is: " + token
            + "\n\nEnter this in the app to reset your password."
            + "\nIf you did not request this, ignore this email.");
        mailSender.send(msg);
    }

    @Override
    public void resetPassword(String token, String newRawPassword) {
        if (newRawPassword == null || newRawPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters.");
        }

        UserAccount account = userRepo.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));

        account.setPasswordHash(encoder.encode(newRawPassword));
        account.setResetToken(null);  // clear token after use
        userRepo.save(account);
    }
}