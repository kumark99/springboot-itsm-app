package com.itsm.incident.config;

import com.itsm.incident.entity.*;
import com.itsm.incident.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedCategories();
        seedStatuses();
        seedUsers();
        seedIncidents();
    }

    private void seedRoles() {
        createRoleIfNotFound("ROLE_USER", "Standard user permissions");
        createRoleIfNotFound("ROLE_AGENT", "Support agent permissions");
        createRoleIfNotFound("ROLE_MANAGER", "Manager permissions");
        createRoleIfNotFound("ROLE_ADMIN", "Administrator permissions");
    }

    private void createRoleIfNotFound(String name, String permissions) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
    }

    private void seedCategories() {
        createCategoryIfNotFound("Hardware");
        createCategoryIfNotFound("Software");
        createCategoryIfNotFound("Network");
        createCategoryIfNotFound("Access");
    }

    private void createCategoryIfNotFound(String name) {
        Category category = categoryRepository.findByName(name);
        if (category == null) {
            category = new Category();
            category.setName(name);
            categoryRepository.save(category);
        }
    }

    private void seedStatuses() {
        createStatusIfNotFound("New");
        createStatusIfNotFound("In Progress");
        createStatusIfNotFound("Resolved");
        createStatusIfNotFound("Closed");
    }

    private void createStatusIfNotFound(String name) {
        Status status = statusRepository.findByName(name);
        if (status == null) {
            status = new Status();
            status.setName(name);
            statusRepository.save(status);
        }
    }

    private void seedUsers() {
        createUserIfNotFound("user", "User Name", "user@example.com", "password", "ROLE_USER", "IT");
        createUserIfNotFound("agent", "Agent Name", "agent@example.com", "password", "ROLE_AGENT", "Support");
        createUserIfNotFound("manager", "Manager Name", "manager@example.com", "password", "ROLE_MANAGER", "Management");
        createUserIfNotFound("admin", "Admin Name", "admin@example.com", "password", "ROLE_ADMIN", "Administration");
    }

    private void createUserIfNotFound(String username, String fullName, String email, String password, String roleName, String department) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setDepartment(department);
            user.setEnabled(true);
            
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                user.setRoles(Collections.singletonList(role));
            }
            
            userRepository.save(user);
        }
    }

    private void seedIncidents() {
        if (incidentRepository.count() == 0) {
            User user = userRepository.findByUsername("user");
            User agent = userRepository.findByUsername("agent");
            Category hardware = categoryRepository.findByName("Hardware");
            Category software = categoryRepository.findByName("Software");
            Status newStatus = statusRepository.findByName("New");
            Status inProgress = statusRepository.findByName("In Progress");

            createIncident("Laptop Screen Flicker", "Screen flickers intermittently.", "MEDIUM", hardware, newStatus, user, null);
            createIncident("VPN Connection Issue", "Cannot connect to VPN.", "HIGH", software, inProgress, user, agent);
            createIncident("Printer Jam", "Printer on 2nd floor is jammed.", "LOW", hardware, newStatus, user, null);
        }
    }

    private void createIncident(String title, String description, String priority, Category category, Status status, User reportedBy, User assignedTo) {
        Incident incident = new Incident();
        incident.setTitle(title);
        incident.setDescription(description);
        incident.setPriority(priority);
        incident.setCategory(category);
        incident.setStatus(status);
        incident.setReportedBy(reportedBy);
        incident.setAssignedTo(assignedTo);
        incidentRepository.save(incident);
    }
}
