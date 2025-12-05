package com.example.backend.service.impl.Authentication;

import com.example.backend.dto.AdminDTO;
import com.example.backend.dto.Authentication.AuthenticationRequest;
import com.example.backend.dto.Authentication.AuthenticationResponse;
import com.example.backend.entity.Admin;
import com.example.backend.exceptions.ForbiddenException;
import com.example.backend.service.impl.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AdminService adminService;
    private final JWTService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthenticationResponse registreAdmin(AuthenticationRequest request) {
        AdminDTO adminDTO=new AdminDTO();
        adminDTO.setUsername(request.getUsername());
        adminDTO.setPassword(request.getPassword());
        Admin savedAdmin=adminService.addAdmin(adminDTO);

        var jwtToken=jwtService.generateToken(savedAdmin);
        var refreshToken=jwtService.generateRefreshToken(savedAdmin);
        tokenService.saveUserToken(savedAdmin,jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest){
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ForbiddenException("Le mot de passe est incorrect.");
        }
        var user =adminService.findByUsername(authRequest.getUsername());


        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

    }
    @Transactional
    public AuthenticationResponse refreshToken(HttpServletRequest request){
        final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader==null || !authHeader.startsWith("Bearer "))
            throw new BadCredentialsException("En-tête d'autorisation manquant ou mal formé.");

        final String refreshToken=authHeader.substring(7);
        final String userName=jwtService.extractUserName(refreshToken);

        if (userName == null)
                throw new BadCredentialsException("Le token semble incorrecte.");

        var user = adminService.findByUsername(userName);

        if (!jwtService.isValidToken(refreshToken, user) || !jwtService.isRefreshToken(refreshToken))
            throw new BadCredentialsException("Token JWT est invalide ou corrompu.");

        var accessToken=jwtService.generateToken(user);
        var newRefreshToken= jwtService.generateRefreshToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user,accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    @Transactional
    public void logOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        String jwt = authHeader.substring(7);
        var storedToken = tokenService.getByToken(jwt);
        if (storedToken != null) {
            tokenService.revokeAllUserTokens(storedToken.getAdmin());
            SecurityContextHolder.clearContext();
        }
    }
}
