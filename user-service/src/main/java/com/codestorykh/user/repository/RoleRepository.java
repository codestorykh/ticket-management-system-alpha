package com.codestorykh.user.repository;

import com.codestorykh.user.entity.Role;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    List<Role> findAllByNameIn(Set<String> roles);

    boolean existsByName(String name);

    List<Role> findAllByStatus(String status);

    Optional<Role> findFirstByName(String name);

    Optional<Role> findFirstById(Long id);

    List<Role> findAllByIdIn(Set<Long> ids);

    Optional<Role> findFirstByNameAndStatus(String name, String status);

    List<Role> findByNameIn(Set<String> roles);
}
