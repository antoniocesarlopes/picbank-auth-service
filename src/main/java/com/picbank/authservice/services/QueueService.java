package com.picbank.authservice.services;

public interface QueueService {
    void sendMessage(String email, String group);
}
