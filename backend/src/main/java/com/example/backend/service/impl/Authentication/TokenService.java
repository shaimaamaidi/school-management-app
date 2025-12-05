package com.example.backend.service.impl.Authentication;

import com.example.backend.entity.Admin;
import com.example.backend.entity.Token;
import com.example.backend.enumeration.TokenType;
import com.example.backend.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void saveUserToken(Admin admin, String jwtToken) {
        var token = Token.builder()
                .admin(admin)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    public void revokeAllUserTokens(Admin admin) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(admin.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public Token getByToken(String token){
        return tokenRepository.findByToken(token).orElse(null);
    }
    public void deleTokenByClient(Long id){
        var validUserTokens = tokenRepository.findAllByUser(id);
        if (validUserTokens.isEmpty())
            return;
        tokenRepository.deleteAll(validUserTokens);
    }
}
