package com.example.backend2.controllers;

import com.example.backend2.dtos.UserDTO;
import com.example.backend2.exceptions.DuplicateObjException;
import com.example.backend2.exceptions.ResourceNotFoundException;
import com.example.backend2.models.UserModel;
import com.example.backend2.services.UserService;


import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Controlador responsável por gerenciar operações relacionadas aos usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Cria um novo usuário",
            description = "Cria um novo usuário usando os dados fornecidos no UserDTO e salva no banco de dados.\n" +
                    "\n" +
                    "@param userDto objeto UserDTO enviado pelo cliente via HTTP request body.\n" +
                    "@return objeto ResponseEntity com status HTTP CREATED e corpo da resposta contendo o objeto UserModel criado.\n" +
                    "@throws DuplicateObjException se já existir um usuário com o mesmo e-mail.\n")
    public ResponseEntity<UserModel> createUser(@RequestBody UserDTO userDto) throws DuplicateObjException {
        UserModel user = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    @GetMapping
    @Operation(summary = "Obter todos os usuários",
            description = "Retorna uma lista de todos os usuários registrados no banco de dados.\n" +
                    "\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a lista de objetos UserModel.\n")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }


    @GetMapping("/search/{name}")
    @Operation(summary = "Buscar usuários por nome",
            description = "Retorna uma lista de usuários cujo nome contém o valor fornecido.\n" +
                    "\n" +
                    "@param name valor a ser pesquisado no campo 'name' dos objetos UserModel.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a lista de objetos UserModel.\n" +
                    "@throws ResourceNotFoundException se nenhum usuário for encontrado com o nome fornecido.\n")
    public ResponseEntity<List<UserModel>> findByNameContaining(@PathVariable String name) throws ResourceNotFoundException {
        List<UserModel> users = userService.findByNameContaining(name);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }




    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário por ID",
            description = "Exclui um usuário com base no ID fornecido.\n" +
                    "\n" +
                    "@param id ID do objeto UserModel a ser removido.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a mensagem 'User deleted successfully.'.\n" +
                    "@throws ResourceNotFoundException se nenhum usuário for encontrado com o ID fornecido.\n")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) throws ResourceNotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário por ID",
            description = "Atualiza os dados de um usuário com base no ID fornecido e nas informações fornecidas no UserDTO.\n" +
                    "\n" +
                    "@param id ID do objeto UserModel a ser atualizado.\n" +
                    "@param userDTO objeto UserDTO enviado pelo cliente via HTTP request body.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo o objeto UserModel atualizado.\n" +
                    "@throws ResourceNotFoundException se nenhum usuário for encontrado com o ID fornecido.\n")
    public ResponseEntity<UserModel> updateUser(@PathVariable UUID id,
                                                      @RequestBody UserDTO userDTO)throws ResourceNotFoundException {

        UserModel user = userService.updateUser(id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
