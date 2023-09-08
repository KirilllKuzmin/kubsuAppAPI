package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.AuthenticationRequestDTO;
import com.kubsu.userService.controller.dto.AuthenticationResponseDTO;
import com.kubsu.userService.repository.*;
import com.kubsu.userService.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/users")
public class AuthenticationController {
    private final UserRepository userRepository;

    private final UserService userService;

    public AuthenticationController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/authentication")
    public CompletableFuture<AuthenticationResponseDTO> authenticationUser(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {

        if (!userRepository.existsByUsername(authenticationRequestDTO.getUsername())) {
            userService.registration(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword());
        }

        /*
         * Временный метод до тех пор, пока не будет интеграции с БД/AD КубГУ либо парсинг данных с офф. сайта
         */
        if (authenticationRequestDTO.getIsLecturer() != null && authenticationRequestDTO.getIsLecturer())
            userService.registrationLecturer(authenticationRequestDTO.getUsername(),
                    authenticationRequestDTO.getFullName(),
                    authenticationRequestDTO.getEmail(),
                    authenticationRequestDTO.getPassword());

        CompletableFuture<List<String>> tokenAndIdAndRolesAsync = userService.authorization(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword());

        return tokenAndIdAndRolesAsync.thenApplyAsync(strings -> new AuthenticationResponseDTO(
                strings.get(0),
                strings.get(1),
                authenticationRequestDTO.getUsername(),
                new ArrayList<>(strings.subList(2, strings.size()))
        ));
    }

}
