package com.picbank.authservice.services;

public interface EmailService {
    void sendEmail(String recipient, String subject, String body);
}
