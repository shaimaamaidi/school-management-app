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

    public void saveAdminToken(Admin admin, String jwtToken) {
        var token = Token.builder()
                .admin(admin)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    public void revokeAllAdminTokens(Admin admin) {
        var validAdminTokens = tokenRepository.findAllValidTokenByAdmin(admin.getId());
        if (validAdminTokens.isEmpty())
            return;
        validAdminTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validAdminTokens);
    }
    public Token getByToken(String token){
        return tokenRepository.findByToken(token).orElse(null);
    }
    public void deleTokenByClient(Long id){
        var validUAdminTokens = tokenRepository.findAllByAdmin(id);
        if (validUAdminTokens.isEmpty())
            return;
        tokenRepository.deleteAll(validUAdminTokens);
    }
}
