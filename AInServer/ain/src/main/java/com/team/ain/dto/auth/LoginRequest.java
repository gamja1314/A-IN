package com.team.ain.dto.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;

    private String password;
}
