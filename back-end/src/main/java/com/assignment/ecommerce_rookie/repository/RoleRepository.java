package com.assignment.ecommerce_rookie.repository;

import com.assignment.ecommerce_rookie.model.AppRole;
import com.assignment.ecommerce_rookie.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
