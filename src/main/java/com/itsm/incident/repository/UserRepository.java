package com.itsm.incident.repository;

import com.itsm.incident.entity.Role;
import com.itsm.incident.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByRolesContaining(Role role);
}
