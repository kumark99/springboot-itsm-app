package com.itsm.incident.controller;

import com.itsm.incident.exception.UserAlreadyExistsException;
import com.itsm.incident.entity.User;
import com.itsm.incident.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

    @Autowired
    private UserService userService;

    @ModelAttribute("user")
    public User userRegistrationDto() {
        return new User();
    }

    @GetMapping
    public String showRegistrationForm() {
        logger.info("Showing registration form");
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") User user, Model model) {
        logger.info("Registering new user account: {}", user.getUsername());
        try {
            userService.save(user);
        } catch (UserAlreadyExistsException e) {
            logger.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
        return "redirect:/registration?success";
    }
}
