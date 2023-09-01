package com.kubsu.userService.repository;

import com.kubsu.userService.model.ERole;
import com.kubsu.userService.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
