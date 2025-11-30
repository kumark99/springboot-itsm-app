package com.itsm.incident.service;

import com.itsm.incident.entity.Role;
import com.itsm.incident.entity.User;
import com.itsm.incident.exception.UserAlreadyExistsException;
import com.itsm.incident.repository.RoleRepository;
import com.itsm.incident.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User save(User user) {
        logger.info("Attempting to save user: {}", user.getUsername());
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            logger.warn("User already exists with username: {}", user.getUsername());
            throw new UserAlreadyExistsException("User already exists with username: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
             Role userRole = roleRepository.findByName("ROLE_USER");
             if(userRole == null) {
                 logger.info("Creating default ROLE_USER");
                 userRole = new Role("ROLE_USER");
                 roleRepository.save(userRole);
             }
             user.setRoles(Arrays.asList(userRole));
        }
        User savedUser = userRepository.save(user);
        logger.info("Successfully saved user: {}", savedUser.getUsername());
        return savedUser;
    }

    public User findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public java.util.List<User> findByRole(String roleName) {
        logger.debug("Finding users by role: {}", roleName);
        Role role = roleRepository.findByName(roleName);
        if (role != null) {
            return userRepository.findByRolesContaining(role);
        }
        logger.warn("Role not found: {}", roleName);
        return java.util.Collections.emptyList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user details for username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
