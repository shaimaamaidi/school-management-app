package com.example.backend.service;

import com.example.backend.dto.AdminDTO;
import com.example.backend.dto.Authentication.AuthenticationRequest;
import com.example.backend.dto.Authentication.AuthenticationResponse;
import com.example.backend.entity.Admin;
import com.example.backend.exceptions.ForbiddenException;
import com.example.backend.service.impl.AdminService;
import com.example.backend.service.impl.Authentication.AuthenticationService;
import com.example.backend.service.impl.Authentication.JWTService;
import com.example.backend.service.impl.Authentication.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AdminService adminService;

    @Mock
    private JWTService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistreAdmin_Success() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin1");
        request.setPassword("pass123");

        Admin savedAdmin = new Admin();
        savedAdmin.setUsername("admin1");
        savedAdmin.setPassword("pass123");

        when(adminService.addAdmin(any(AdminDTO.class))).thenReturn(savedAdmin);
        when(jwtService.generateToken(savedAdmin)).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(savedAdmin)).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.registreAdmin(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(tokenService).saveAdminToken(savedAdmin, "jwtToken");
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin");
        request.setPassword("password");

        Admin admin = new Admin();
        admin.setUsername("admin");

        // Mocks
        when(adminService.findByUsername(anyString())).thenReturn(admin);
        when(jwtService.generateToken(any(Admin.class))).thenReturn("mocked-access-token");
        when(jwtService.generateRefreshToken(any(Admin.class))).thenReturn("mocked-refresh-token");

        // DoNothing only for void methods
        doNothing().when(tokenService).revokeAllAdminTokens(any(Admin.class));
        doNothing().when(tokenService).saveAdminToken(any(Admin.class), anyString());

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-access-token", response.getAccessToken());
        assertEquals("mocked-refresh-token", response.getRefreshToken());
    }

    @Test
    void testAuthenticate_BadCredentials() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin1");
        request.setPassword("wrong");

        doThrow(BadCredentialsException.class).when(authManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(ForbiddenException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testRefreshToken_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer refreshToken");

        Admin admin = new Admin();
        admin.setUsername("admin1");

        when(jwtService.extractUserName("refreshToken")).thenReturn("admin1");
        when(adminService.findByUsername("admin1")).thenReturn(admin);
        when(jwtService.isValidToken("refreshToken", admin)).thenReturn(true);
        when(jwtService.isRefreshToken("refreshToken")).thenReturn(true);
        when(jwtService.generateToken(admin)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(admin)).thenReturn("newRefreshToken");

        AuthenticationResponse response = authenticationService.refreshToken(request);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());

        verify(tokenService).revokeAllAdminTokens(admin);
        verify(tokenService).saveAdminToken(admin, "newAccessToken");
    }
}
