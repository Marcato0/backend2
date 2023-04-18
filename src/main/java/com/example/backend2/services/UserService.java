package com.example.backend2.services;

import com.example.backend2.dtos.UserDTO;
import com.example.backend2.exceptions.*;
import com.example.backend2.models.UserModel;
import com.example.backend2.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Cria um novo usuário com base no DTO fornecido e o salva no banco de dados.
     * A senha do usuário será criptografada antes de ser salva no banco de dados.
     *
     * @param userDto o DTO que contém as informações do usuário a ser criado
     * @return o usuário criado e salvo no banco de dados
     * @throws DuplicateObjException se já existir um usuário com o mesmo e-mail
     */

    public UserModel createUser(UserDTO userDto) {

        // Remove espaços em branco no início e no fim do nome
        userDto.setName(userDto.getName().trim());

        // Verifica se o e-mail está vazio ou nulo
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("E-mail cannot be null or empty!");
        }

        // Verifica se já existe um usuário com o mesmo e-mail
        if (userRepository.findByEmailIgnoreCase(userDto.getEmail()).isPresent()) {
            throw new DuplicateObjException("E-mail is already in use!");
        }

        // Cria um usuário com base no DTO
        var user = new UserModel();
        BeanUtils.copyProperties(userDto, user);
        user.setRegistrationDate(LocalDate.now());

        // Criptografa a senha do usuário antes de salvá-la
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Salva o usuário no banco de dados
        return userRepository.save(user);
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Retorna uma lista de todos os usuários salvos no banco de dados.
     *
     * @return uma lista de todos os usuários salvos no banco de dados
     */
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }


    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Retorna uma lista de usuários com nome correspondente à pesquisa.
     *
     * @param name o nome a ser pesquisado
     * @return uma lista de usuários com nome correspondente à pesquisa
     */
    public List<UserModel> findByNameContaining(String name) throws ResourceNotFoundException {

        // Remove os espaços em branco no início e no final da string
        String trimmedName = name.trim();

        //busca o user pelo nome
        List<UserModel> optionalUser = userRepository.findByNameContainingIgnoreCase(trimmedName);


        // verifica se user existe
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found with name containing: " + name);
        }

        return optionalUser;
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Exclui o user com o id fornecido.
     * @param id o id do user a ser excluída.
     * @throws ResourceNotFoundException se não houver user com o id fornecido.
     */
    public void deleteUser(UUID id) throws ResourceNotFoundException {

        //busca o user pelo id
        Optional<UserModel> optionalUser = userRepository.findById(id);

        // verifica se user existe
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        // exclui o user encontrado
        UserModel user = optionalUser.get();
        userRepository.deleteById(user.getId());
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Atualiza os dados do user com o id fornecido com base nos dados fornecidos.
     * Se a senha fornecida for diferente da senha armazenada, ela será criptografada antes de ser salva no banco de dados.
     *
     * @param id o id do user a ser atualizada.
     * @param userDTO um objeto UserDTO contendo os novos dados de user.
     * @return um objeto UserModel representando o user atualizado.
     * @throws ResourceNotFoundException se não houver user com o id fornecido.
     */

    public UserModel updateUser(UUID id, UserDTO userDTO) throws ResourceNotFoundException {

        Optional<UserModel> optionalUser = userRepository.findById(id);

        // verifica se o user existe
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        UserModel existingUser = optionalUser.get();

        // Verifica se o e-mail fornecido já está em uso por outro usuário
        Optional<UserModel> userWithEmail = userRepository.findByEmailIgnoreCase(userDTO.getEmail());
        if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(existingUser.getId())) {
            throw new DuplicateObjException("E-mail is already in use!");
        }

        // Criar um user com base no DTO
        var user = new UserModel();
        BeanUtils.copyProperties(userDTO, user);
        user.setRegistrationDate(existingUser.getRegistrationDate()); // Use a data de registro original
        user.setId(existingUser.getId()); // Definir o ID do usuário

        // Verificar se a senha fornecida no DTO é diferente da senha armazenada
        if (!passwordEncoder.matches(userDTO.getPassword(), existingUser.getPassword())) {
            // Criptografar a nova senha antes de atualizar o usuário
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword()); // Manter a senha existente
        }

        // salva no banco de dados
        return userRepository.save(user);
    }

}
