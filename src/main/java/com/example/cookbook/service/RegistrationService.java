package com.example.cookbook.service;

import com.example.cookbook.domain.Role;
import com.example.cookbook.domain.User;
import com.example.cookbook.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public final class RegistrationService {

    @Autowired
    private UserRepo userRepo;

    public boolean isUserExists(User user) {
        final User userFromDb = userRepo.findByUsername(user.getUsername());
        return userFromDb != null;
    }

    public void addUser(User user) {
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);
    }
}
