package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.UserResponseDTO;
import com.kubsu.userService.service.UserDetailsImpl;
import com.kubsu.userService.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/access")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER')")
    public UserResponseDTO profile() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("username: {}", userDetails.getUsername() + userDetails.getId());
        return new UserResponseDTO(userService.getUserById(userDetails.getId()));
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<UserResponseDTO> allProfiles() {
        return userService
                .getAllUsers()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }
}
