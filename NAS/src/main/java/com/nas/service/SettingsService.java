package com.nas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.persistence.dto.EmailDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.*;
import com.nas.service.login.ForgotPasswordService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SettingsService {

    @NonNull
    @Autowired
    private UserViewRepository userViewRepository;

    @NonNull
    @Autowired
    private UserRepository userRepository;

    @NonNull
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @NonNull
    @Autowired
    private FileRepository fileRepository;

    @NonNull
    @Autowired
    private RoleRepository roleRepository;

    @NonNull
    @Autowired
    private ObjectMapper objectMapper;

    @NonNull
    @Autowired
    private JwtService jwtService;

    @NonNull
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    public List<UserView> showUploadPage(String jwt) throws ExpiredTokenException, InvalidCredentialsException {
        UserView userView = jwtService.validateToken(jwt);
        if(userView.getRoles().contains("ADMIN")) {
            List<UserView> usersView = userViewRepository.findAll();
            usersView.remove(userView);
            return usersView;
        }

        return new ArrayList<>();
    }

    public void resetUsersPassword(String jwt, String users) throws JsonProcessingException, ExpiredTokenException, InvalidCredentialsException {
        if(isAdmin(jwt)) {
            resetUsersPassword(users);
        }
    }

    @Transactional
    public void deleteUsers(String jwt, String users) throws JsonProcessingException, ExpiredTokenException, InvalidCredentialsException {
        if(isAdmin(jwt)) {
            deleteUsers(users);
        }
    }

    public String generateToken(String jwt, String email) throws ExpiredTokenException, InvalidCredentialsException {
        if(isAdmin(jwt)) {
            UserView userView = userViewRepository.findByEmail(email);
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(userView);
            return jwtService.generateToken(userDetailsDTO);
        }

        return "";
    }

    public void updateTheme(String jwt, boolean isDarkTheme) throws ExpiredTokenException, InvalidCredentialsException {
        UserView userView = jwtService.validateToken(jwt);
        User user = new User(userView);
        user.setDarkTheme(isDarkTheme);
        userRepository.save(user);
    }

    private void deleteUsers(String users) throws JsonProcessingException {
        objectMapper.readValue(users, List.class).forEach(userId ->
            deleteUser(Long.valueOf((Integer) userId))
        );
    }

    private void deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            fileRepository.deleteByUser(user);
            roleRepository.deleteByUser(user);
            verificationTokenRepository.deleteByUser(user);
            userRepository.deleteById(userId);
        }
    }

    private void resetUsersPassword(String users) throws JsonProcessingException {
        objectMapper.readValue(users, List.class).forEach(userId ->
            resetUserPassword(Long.valueOf((Integer) userId))
        );
    }

    private void resetUserPassword(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            EmailDTO emailDTO = new EmailDTO(user.get().getEmail());
            forgotPasswordService.forgotPassword(emailDTO);
        }

    }

    private boolean isAdmin(String jwt) throws ExpiredTokenException, InvalidCredentialsException {
        return jwtService.validateToken(jwt).getRoles().contains("ADMIN");
    }
}
