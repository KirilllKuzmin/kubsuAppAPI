package com.kubsu.userService.repository;

import com.kubsu.userService.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
