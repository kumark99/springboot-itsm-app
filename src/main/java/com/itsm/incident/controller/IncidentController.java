package com.itsm.incident.controller;

import com.itsm.incident.entity.Incident;
import com.itsm.incident.entity.User;
import com.itsm.incident.repository.CategoryRepository;
import com.itsm.incident.repository.StatusRepository;
import com.itsm.incident.service.IncidentService;
import com.itsm.incident.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class IncidentController {

    private static final Logger logger = LoggerFactory.getLogger(IncidentController.class);

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StatusRepository statusRepository;

    @GetMapping("/incidents")
    public String viewHomePage(Model model) {
        logger.info("Accessing incidents home page");
        List<Incident> listIncidents = incidentService.getAllIncidents();
        model.addAttribute("listIncidents", listIncidents);
        return "incidents";
    }

    @GetMapping("/showNewIncidentForm")
    public String showNewIncidentForm(Model model) {
        logger.info("Showing new incident form");
        Incident incident = new Incident();
        model.addAttribute("incident", incident);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        return "new_incident";
    }

    @PostMapping("/saveIncident")
    public String saveIncident(@ModelAttribute("incident") Incident incident) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        logger.info("Saving incident by user: {}", username);
        User user = userService.findByUsername(username);
        
        if (incident.getId() == null) {
            logger.info("Creating new incident: {}", incident.getTitle());
            incident.setReportedBy(user);
            incidentService.saveIncident(incident);
        } else {
            logger.info("Updating existing incident id: {}", incident.getId());
            Incident existingIncident = incidentService.getIncidentById(incident.getId());
            existingIncident.setTitle(incident.getTitle());
            existingIncident.setDescription(incident.getDescription());
            existingIncident.setPriority(incident.getPriority());
            existingIncident.setStatus(incident.getStatus());
            existingIncident.setCategory(incident.getCategory());
            existingIncident.setAssignedTo(incident.getAssignedTo());
            // Keep original reportedBy and createdAt
            incidentService.saveIncident(existingIncident);
        }
        return "redirect:/incidents";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Showing update form for incident id: {}", id);
        Incident incident = incidentService.getIncidentById(id);
        model.addAttribute("incident", incident);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("agents", userService.findByRole("ROLE_AGENT"));
        return "update_incident";
    }

    @GetMapping("/deleteIncident/{id}")
    public String deleteIncident(@PathVariable(value = "id") Long id) {
        logger.info("Deleting incident id: {}", id);
        this.incidentService.deleteIncidentById(id);
        return "redirect:/incidents";
    }
}
