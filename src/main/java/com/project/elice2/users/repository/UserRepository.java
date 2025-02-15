package com.project.elice2.users.repository;

import com.project.elice2.users.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long>, UserRepositoryCustom {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    boolean existsByEmail(String email);
}
