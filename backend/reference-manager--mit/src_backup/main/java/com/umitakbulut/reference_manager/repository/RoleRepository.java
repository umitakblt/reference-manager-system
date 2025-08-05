package com.umitakbulut.reference_manager.repository;

import com.umitakbulut.reference_manager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Object> findByName(String name);
}
