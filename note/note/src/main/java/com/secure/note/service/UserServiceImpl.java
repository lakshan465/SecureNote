package com.secure.note.service;


import com.secure.note.dto.UserDTO;
import com.secure.note.entity.PasswordResetToken;
import com.secure.note.enums.AppRole;
import com.secure.note.entity.Role;
import com.secure.note.entity.User;
import com.secure.note.repo.PasswordResetTokenRepository;
import com.secure.note.repo.RoleRepository;
import com.secure.note.repo.UserRepository;
import com.secure.note.service.interfac3.UserService;
import com.secure.note.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetTokenRepository pwTokenRepository;

    @Value("${frontend.url}")
    String frontendUrl;

    @Autowired
    EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }
    // UserServiceImpl.java
    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }
    @Override
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }

    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("user not found"));
        String token = UUID.randomUUID().toString();
        Instant exDate = Instant.now().plus(24, ChronoUnit.DAYS);
        PasswordResetToken passwordResetToken =   PasswordResetToken
                .builder()
                .token(token)
                .expiryDate(exDate)
                .user(user)
                .build();
        pwTokenRepository.save(passwordResetToken);
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        //send email
        emailService.sendPasswordResetEmail(email, resetUrl);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken pwdresetTkn = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Token not found"));

        if(pwdresetTkn.isUsed())
            throw new RuntimeException("Password reset is used");

        if(pwdresetTkn.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Password reset is expired");
        User user = pwdresetTkn.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        pwdresetTkn.setUsed(true);
        passwordResetTokenRepository.save(pwdresetTkn);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(User newUser) {
        if(newUser.getPassword() != null)
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }
}
