package com.example.backend2.repositories;

import com.example.backend2.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    List<UserModel> findByNameContainingIgnoreCase(String name);

    Optional<UserModel> findByEmailIgnoreCase(String email);
}
