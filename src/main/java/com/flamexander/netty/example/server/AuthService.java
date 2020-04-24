package com.flamexander.netty.example.server;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password);
}
