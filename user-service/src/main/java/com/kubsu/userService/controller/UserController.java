package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.UserResponseDTO;
import com.kubsu.userService.model.User;
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
import java.util.concurrent.CompletableFuture;
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
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public CompletableFuture<UserResponseDTO> profile() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        CompletableFuture<User> userFuture = userService.getUserById(userDetails.getId());
        return userFuture.thenApplyAsync(UserResponseDTO::new);
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public CompletableFuture<List<UserResponseDTO>> allProfiles() {
        CompletableFuture<List<User>> usersFuture = userService.getAllUsers();

        return usersFuture.thenApplyAsync(users -> users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList()));
    }
}
