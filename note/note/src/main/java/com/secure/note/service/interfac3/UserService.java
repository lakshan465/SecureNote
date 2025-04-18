package com.secure.note.service.interfac3;

import com.secure.note.dto.UserDTO;
import com.secure.note.entity.User;

import java.util.List;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);

}

