package com.expensetracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для JwtTokenProvider
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void generateAccessToken_CreatesValidToken() {
        // Arrange
        Long userId = 123L;
        String email = "test@example.com";

        // Act
        String token = jwtTokenProvider.generateAccessToken(userId, email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateRefreshToken_CreatesValidToken() {
        // Arrange
        Long userId = 123L;

        // Act
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        Long userId = 123L;
        String email = "test@example.com";
        String token = jwtTokenProvider.generateAccessToken(userId, email);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getUserIdFromToken_ReturnsCorrectUserId() {
        // Arrange
        Long userId = 123L;
        String email = "test@example.com";
        String token = jwtTokenProvider.generateAccessToken(userId, email);

        // Act
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // Assert
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getUserIdFromToken_DifferentUserIds_ReturnCorrectIds() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 999L;
        String token1 = jwtTokenProvider.generateAccessToken(userId1, "user1@example.com");
        String token2 = jwtTokenProvider.generateAccessToken(userId2, "user2@example.com");

        // Act
        Long extracted1 = jwtTokenProvider.getUserIdFromToken(token1);
        Long extracted2 = jwtTokenProvider.getUserIdFromToken(token2);

        // Assert
        assertEquals(userId1, extracted1);
        assertEquals(userId2, extracted2);
        assertNotEquals(extracted1, extracted2);
    }

    @Test
    void accessTokenAndRefreshToken_AreDifferent() {
        // Arrange
        Long userId = 123L;
        String email = "test@example.com";

        // Act
        String accessToken = jwtTokenProvider.generateAccessToken(userId, email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // Assert
        assertNotEquals(accessToken, refreshToken);
    }

    @Test
    void multipleTokensForSameUser_AreDifferent() {
        // Arrange
        Long userId = 123L;
        String email = "test@example.com";

        // Act
        String token1 = jwtTokenProvider.generateAccessToken(userId, email);
        // Небольшая задержка для разных timestamp
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String token2 = jwtTokenProvider.generateAccessToken(userId, email);

        // Assert
        assertNotEquals(token1, token2);
        
        // Оба токена должны быть валидны
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));
        
        // Оба токена должны содержать одинаковый userId
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token1));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token2));
    }
}
