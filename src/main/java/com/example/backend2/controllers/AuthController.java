package com.example.backend2.controllers;

import com.example.backend2.dtos.LoginDTO;
import com.example.backend2.models.UserModel;
import com.example.backend2.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth Controller",
        description = "Controlador responsável por gerenciar operações relacionadas à autenticação")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    @Operation(summary = "Efetuar login do usuário",
            description = "Realiza o login do usuário utilizando as informações fornecidas no LoginDTO e retorna um token JWT.\n" +
                    "\n" +
                    "@param login objeto LoginDTO enviado pelo cliente via HTTP request body.\n" +
                    "@return string representando o token JWT gerado.\n")
    public String login(@RequestBody LoginDTO login) {

        // Cria um objeto UsernamePasswordAuthenticationToken com os dados de login fornecidos.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(login.getLogin(), login.getPassword());

        // Autentica o usuário com o authenticationManager.
        Authentication authenticate = this.authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);

        // Obtém o objeto UserModel autenticado (principal).
        var user = (UserModel) authenticate.getPrincipal();

        // Gera e retorna o token JWT usando o TokenService.
        return tokenService.generateToken(user);
    }
}
