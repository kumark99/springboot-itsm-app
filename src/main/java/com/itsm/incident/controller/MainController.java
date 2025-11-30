package com.itsm.incident.controller;

import com.itsm.incident.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private IncidentService incidentService;

    @GetMapping("/login")
    public String login() {
        logger.info("Accessing login page");
        return "login";
    }

    @GetMapping("/")
    public String home(Model model) {
        logger.info("Accessing dashboard");
        model.addAttribute("totalIncidents", incidentService.getTotalIncidents());
        model.addAttribute("newIncidents", incidentService.getCountByStatus("NEW"));
        model.addAttribute("inProgressIncidents", incidentService.getCountByStatus("IN_PROGRESS"));
        model.addAttribute("resolvedIncidents", incidentService.getCountByStatus("RESOLVED"));
        model.addAttribute("closedIncidents", incidentService.getCountByStatus("CLOSED"));
        return "index";
    }
}
