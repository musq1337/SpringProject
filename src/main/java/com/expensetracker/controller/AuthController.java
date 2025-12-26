package com.expensetracker.controller;

import com.expensetracker.dto.request.LoginRequest;
import com.expensetracker.dto.request.RegisterRequest;
import com.expensetracker.dto.response.AuthResponse;
import com.expensetracker.dto.response.UserResponse;
import com.expensetracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * контроллер для аутентификации
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Tag(name = "Authentication", description = "endpoints для регистрации и входа")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    /**
     * регистрация нового пользователя
     */
    @PostMapping("/register")
    @Operation(summary = "регистрация", description = "создание нового пользователя")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Получен запрос на регистрацию: email={}, firstName={}, currency={}", 
            request.getEmail(), request.getFirstName(), request.getDefaultCurrency());
        try {
            UserResponse response = authService.register(request);
            logger.info("Пользователь успешно зарегистрирован: id={}", response.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Ошибка при регистрации: ", e);
            throw e;
        }
    }
    
    /**
     * вход в систему
     */
    @PostMapping("/login")
    @Operation(summary = "вход", description = "аутентификация пользователя и получение токенов")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * обновление токена
     */
    @PostMapping("/refresh")
    @Operation(summary = "обновление токена", description = "получение нового access токена по refresh токену")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
