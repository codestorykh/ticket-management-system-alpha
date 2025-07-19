package com.codestorykh.user.repository;

import com.codestorykh.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findFirstById(Long id);

    Optional<User> findFirstByUsernameAndStatus(String username, String status);

    List<User> findAllByIdIn(Set<Long> ids);
}
