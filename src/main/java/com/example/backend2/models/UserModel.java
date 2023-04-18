package com.example.backend2.models;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Setter
@Entity
@Table(name = "users")
@Schema(description = "Objeto UserModel que representa os dados de um usuário no banco de dados")
public class UserModel implements UserDetails, Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Schema(description = "ID único do usuário")
    private UUID id;


    @Column(nullable = false)
    @Schema(description = "Nome completo do usuário", example = "Caio Rolando da Rocha")
    private String name;


    @Column(nullable = false, unique = true)
    @Schema(description = "E-mail do usuário", example = "caio@example.com")
    private String email;


    @Column(nullable = false)
    @Schema(description = "Senha do usuário", example = "mypassword")
    private String password;


    @Column(name = "registration_date", nullable = false)
    @Schema(description = "Data de cadastro do usuário", example = "2023-01-01")
    private LocalDate registrationDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    @Schema(description = "Nível de acesso do usuário", example = "ADMINISTRATOR")
    private AccessLevel accessLevel;



    @Override
    @Schema(description = "A autoridade concedida ao usuário", example = "ROLE_ADMINISTRATOR")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessLevel.name()));
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    @Schema(description = "E-mail do usuário",
            example = "caio@example.com")
    public String getUsername() {
        return this.email;
    }

    @Override
    @Schema(description = "Indica se a conta do usuário não está expirada.")
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(description = "Indica se a conta do usuário não está bloqueada.")
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Schema(description = "Indica se as credenciais do usuário não estão expiradas.")
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(description = "Indica se a conta do usuário está habilitada.")
    public boolean isEnabled() {
        return true;
    }

    public enum AccessLevel {
        ADMINISTRATOR,
        COLLABORATOR
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

}
