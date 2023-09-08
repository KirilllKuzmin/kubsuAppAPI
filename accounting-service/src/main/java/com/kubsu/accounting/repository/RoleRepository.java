package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.ERole;
import com.kubsu.accounting.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
