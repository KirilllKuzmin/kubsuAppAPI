package com.kubsu.accounting.service;

import com.kubsu.accounting.repository.RoleRepository;
import com.kubsu.accounting.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public AccountingService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
}
