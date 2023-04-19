package com.example.backend2.security;

import com.example.backend2.repositories.UserRepository;
import com.example.backend2.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FilterToken extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Filtro personalizado para autenticar o usuário com base no token JWT.
     *
     * @param request A requisição recebida.
     * @param response A resposta a ser enviada.
     * @param filterChain A cadeia de filtros.
     * @throws ServletException Se ocorrer um erro na servlet.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            var authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null) {

                String token = authorizationHeader.replace("Bearer ", "");
                var subject = this.tokenService.getSubject(token);
                var user = this.userRepository.findByEmailIgnoreCase(subject);

                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.get().getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            buildErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Converte a mensagem para um JSON.
     *
     * @param message A mensagem a ser convertida.
     * @return A mensagem convertida em formato JSON.
     */
    private String convertToJson(String message) {
        return String.format("{\"message\": \"%s\"}", message);
    }

    /**
     * Cria e envia uma resposta de erro.
     *
     * @param response A resposta a ser enviada.
     * @param statusCode O código de status HTTP da resposta.
     * @param message A mensagem de erro.
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    private void buildErrorResponse(HttpServletResponse response,
                                    int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(convertToJson(message));
    }
}
