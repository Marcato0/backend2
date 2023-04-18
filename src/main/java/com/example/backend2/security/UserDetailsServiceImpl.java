package com.example.backend2.security;

import com.example.backend2.exceptions.ResourceNotFoundException;
import com.example.backend2.models.UserModel;
import com.example.backend2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel userModel = userRepository.findByEmailIgnoreCase(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        return userModel;
    }
}
