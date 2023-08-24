package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.AuthenticationRequestDTO;
import com.kubsu.userService.controller.dto.AuthenticationResponseDTO;
import com.kubsu.userService.model.User;
import com.kubsu.userService.repository.*;
import com.kubsu.userService.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public AuthenticationResponseDTO authenticationUser(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        User user = new User();

        if (!userRepository.existsByUsername(authenticationRequestDTO.getUsername())) {
            userService.registration(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword());
        }

        List<String> tokenAndIdAndRoles = userService.authorization(authenticationRequestDTO.getUsername(),
                                                                    authenticationRequestDTO.getPassword());

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();

        authenticationResponseDTO.setToken(tokenAndIdAndRoles.get(0));
        authenticationResponseDTO.setUserId(tokenAndIdAndRoles.get(1));
        authenticationResponseDTO.setUsername(authenticationRequestDTO.getUsername());
        authenticationResponseDTO.setRoles(new ArrayList<>(tokenAndIdAndRoles.subList(2, tokenAndIdAndRoles.size())));

        return authenticationResponseDTO;
    }

}
