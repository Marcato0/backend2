package com.example.backend2.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.backend2.models.UserModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    /**
     * Gera um token JWT para o usuário informado.
     *
     * @param user O usuário para o qual o token será gerado.
     * @return Um token JWT assinado.
     */
    public String generateToken(UserModel user) {
        return JWT.create()
                .withIssuer("api.trevo.com")
                .withSubject(user.getUsername())
                .withClaim("id", user.getId().toString())
                .withExpiresAt(LocalDateTime.now()
                                .plusMinutes(2)
//                        .plusSeconds(30)
                                .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256("secret"));
    }

    /**
     * Extrai o "subject" (nome de usuário) do token JWT fornecido.
     *
     * @param token O token JWT do qual o "subject" será extraído.
     * @return O "subject" (nome de usuário) contido no token.
     */
    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256("secret"))
                .withIssuer("api.trevo.com")
                .build().verify(token).getSubject();

    }
}
