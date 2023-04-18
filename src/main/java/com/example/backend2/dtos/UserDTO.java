package com.example.backend2.dtos;

import com.example.backend2.models.UserModel.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Setter
@Schema(description = "Objeto UserDTO que representa os dados de um usuário")
public class UserDTO {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome de usuário", example = "Caio Rolando da Rocha", required = true)
    private String name;


    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(description = "E-mail do usuário", example = "caio@example.com")
    private String email;


    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Senha do usuário", example = "mypassword")
    private String password;


    @Schema(description = "Data de cadastro do usuário", example = "2023-01-01")
    private LocalDate registrationDate;

    @Schema(description = "Nível de acesso do usuário", example = "ADMINISTRATOR")
    private AccessLevel accessLevel;
}
