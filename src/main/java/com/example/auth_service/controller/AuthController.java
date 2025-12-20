package com.example.auth_service.controller;

import com.example.auth_service.dto.*;
import com.example.auth_service.model.RefreshToken;
import com.example.auth_service.model.User;
import com.example.auth_service.security.JwtProvider;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());

        User user = refreshToken.getUser();

        String newAccessToken = jwtProvider.generateToken(user);

        return new LoginResponse(
                newAccessToken,
                refreshToken.getToken(),
                "Bearer"
        );
    }

    @PostMapping("/logout")
    public void logout(@RequestBody @Valid LogoutRequest request) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
    }
}
