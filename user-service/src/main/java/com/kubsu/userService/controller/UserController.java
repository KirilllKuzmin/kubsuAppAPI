package com.kubsu.userService.controller;

import com.kubsu.userService.controller.dto.GroupResponseDTO;
import com.kubsu.userService.controller.dto.StudentResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public UserResponseDTO profile() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new UserResponseDTO(userService.getUserById(userDetails.getId()));
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public CompletableFuture<List<UserResponseDTO>> allProfiles() {
        CompletableFuture<List<User>> usersFuture = userService.getAllUsers();

        return usersFuture.thenApplyAsync(users -> users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("groups")
    public List<GroupResponseDTO> allGroups(@RequestParam(required = false) List<Long> groupId) {
        return userService.getAllGroups(groupId)
                .stream()
                .map(GroupResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("students")
    public List<StudentResponseDTO> allStudents(@RequestParam List<Long> Id) {
        return userService.getAllStudents(Id)
                .stream()
                .map(StudentResponseDTO::new)
                .collect(Collectors.toList());
    }
}
