package com.example.backend2.controllers;

import com.example.backend2.dtos.UserDTO;
import com.example.backend2.exceptions.DuplicateObjException;
import com.example.backend2.exceptions.ResourceNotFoundException;
import com.example.backend2.models.UserModel;
import com.example.backend2.repositories.UserRepository;
import com.example.backend2.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Teste para cadastrar usuário com todos os campos")
    void deveCadastrarUsuarioComTodosOsCampos() throws DuplicateObjException {
        // Dados de entrada
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João da Silva");
        userDTO.setEmail("joao.silva@gmail.com");
        userDTO.setPassword("senha_segura");
        userDTO.setAccessLevel(UserModel.AccessLevel.COLLABORATOR);

        // Dados mockados
        UserModel mockUser = new UserModel();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName(userDTO.getName());
        mockUser.setEmail(userDTO.getEmail());
        mockUser.setPassword(userDTO.getPassword());
        mockUser.setRegistrationDate(LocalDate.now());
        mockUser.setAccessLevel(userDTO.getAccessLevel());

        // Configuração do mock do UserService
        when(userService.createUser(any(UserDTO.class))).thenReturn(mockUser);

        // Execução do método a ser testado
        ResponseEntity<UserModel> response = userController.createUser(userDTO);

        // Verificações
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getId(), response.getBody().getId());
        assertEquals(userDTO.getName(), response.getBody().getName());
        assertEquals(userDTO.getEmail(), response.getBody().getEmail());
        assertEquals(userDTO.getPassword(), response.getBody().getPassword());
        assertNotNull(response.getBody().getRegistrationDate());
        assertEquals(userDTO.getAccessLevel(), response.getBody().getAccessLevel());
    }


    @Test
    @DisplayName("Teste para não cadastrar usuário com e-mail já existente")
    void naoDeveCadastrarUsuarioComEmailJaExistente() throws DuplicateObjException {
        // Dados de entrada
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João da Silva");
        userDTO.setEmail("joao.silva@gmail.com");
        userDTO.setPassword("senha_segura");
        userDTO.setAccessLevel(UserModel.AccessLevel.COLLABORATOR);

        // Configuração do mock do UserService
        when(userService.createUser(any(UserDTO.class))).thenThrow(new DuplicateObjException("E-mail already exists"));

        // Execução do método a ser testado e tratamento de exceção esperada
        Exception exception = assertThrows(DuplicateObjException.class, () -> userController.createUser(userDTO));

        // Verificações
        assertEquals("E-mail already exists", exception.getMessage());
    }


    @Test
    @DisplayName("Buscar todos os usuários")
    void deveRetornarTodosOsUsuarios() {
        UserModel user1 = new UserModel();
        user1.setName("João da Silva");
        user1.setEmail("joao.silva@gmail.com");
        user1.setPassword("senha_segura");
        user1.setAccessLevel(UserModel.AccessLevel.COLLABORATOR);

        UserModel user2 = new UserModel();
        user2.setName("Maria Fernandes");
        user2.setEmail("maria.fernandes@gmail.com");
        user2.setPassword("senha_maria");
        user2.setAccessLevel(UserModel.AccessLevel.ADMINISTRATOR);

        List<UserModel> mockUsers = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(mockUsers);

        ResponseEntity<List<UserModel>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserModel> allUsers = response.getBody();
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertEquals(mockUsers.get(0).getName(), allUsers.get(0).getName());
        assertEquals(mockUsers.get(1).getName(), allUsers.get(1).getName());
    }

    @Test
    @DisplayName("este para buscar usuarios por nome")
    void deveBuscarUsuarioPorNome() {
        String searchName = "João da Silva";
        UserModel mockUser = new UserModel();
        mockUser.setName(searchName);
        mockUser.setEmail("joao.silva@gmail.com");
        mockUser.setPassword("senha_segura");
        mockUser.setAccessLevel(UserModel.AccessLevel.COLLABORATOR);

        when(userService.findByNameContaining(searchName)).thenReturn(Collections.singletonList(mockUser));

        ResponseEntity<List<UserModel>> response = userController.findByNameContaining(searchName);
        List<UserModel> foundUsers = response.getBody();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(searchName, foundUsers.get(0).getName());
    }


    @Test
    @DisplayName("Teste para buscar usuario por nome que não existe")
    void deveRetornarResourceNotFoundExceptionAoBuscarUsuarioInexistente() {
        String searchName = "Usuário Inexistente";

        when(userService.findByNameContaining(searchName)).thenThrow(new ResourceNotFoundException("Usuario não encontrado."));


        assertThrows(ResourceNotFoundException.class, () -> userController.findByNameContaining(searchName));
    }
    @Test
    @DisplayName("Teste para excluir usuario pelo id")
    void deveExcluirUsuarioPorId() throws ResourceNotFoundException {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteUser(id);
        ResponseEntity<Object> response = userController.deleteUser(id);
        verify(userService, times(1)).deleteUser(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully.", response.getBody());
    }

    @Test
    @DisplayName("Teste para excluir usuario que não existe pelo id")
    void deveLancarExcecaoAoTentarExcluirUsuarioInexistente() {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Usuario não encontrado.")).when(userService).deleteUser(id);

        assertThrows(ResourceNotFoundException.class, () -> userController.deleteUser(id));
    }

    @Test
    @DisplayName("Teste para atualizar usuário pelo id")
    void deveEditarUsuario() {
        // Cria um usuário para ser atualizado
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João da Silva Junior");
        userDTO.setEmail("joao.silva.junior@gmail.com");
        userDTO.setPassword("senha_segura_atualizada");
        userDTO.setAccessLevel(UserModel.AccessLevel.ADMINISTRATOR);

        UserModel mockUser = new UserModel();
        UUID userId = UUID.randomUUID();
        mockUser.setId(userId);
        mockUser.setName(userDTO.getName());
        mockUser.setEmail(userDTO.getEmail());
        mockUser.setPassword(userDTO.getPassword());
        mockUser.setAccessLevel(userDTO.getAccessLevel());

        // Quando userService.updateUser for chamado com o ID correto, retorne o mockUser
        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(mockUser);

        // Chama o método do controlador para atualizar o usuário
        UserModel updatedUser = userController.updateUser(userId, userDTO).getBody();

        // Verifica se o usuário foi atualizado corretamente
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals(userDTO.getName(), updatedUser.getName());
        assertEquals(userDTO.getEmail(), updatedUser.getEmail());
        assertNotEquals("senha_segura", updatedUser.getPassword());
        assertEquals(userDTO.getAccessLevel(), updatedUser.getAccessLevel());
    }


    @Test
    @DisplayName("Teste para editar usuário e colocar um email de um usuário que já existe")
    void naoDeveEditarUsuarioComEmailDuplicado() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setName("João da Silva Junior");
        userDTO.setEmail("joao.silva.junior@gmail.com");
        userDTO.setPassword("senha_segura_atualizada");
        userDTO.setAccessLevel(UserModel.AccessLevel.ADMINISTRATOR);

        when(userService.updateUser(eq(userId), any(UserDTO.class)))
                .thenThrow(new DuplicateObjException("Já existe um usuário com esse email."));

        assertThrows(DuplicateObjException.class, () -> userController.updateUser(userId, userDTO));
    }


}