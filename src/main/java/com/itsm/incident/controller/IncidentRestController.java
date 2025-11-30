package com.itsm.incident.controller;

import com.itsm.incident.entity.Incident;
import com.itsm.incident.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentRestController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        incidentService.saveIncident(incident);
        return new ResponseEntity<>(incident, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(@PathVariable Long id, @RequestBody Incident incidentDetails) {
        Incident incident = incidentService.getIncidentById(id);
        
        incident.setTitle(incidentDetails.getTitle());
        incident.setDescription(incidentDetails.getDescription());
        incident.setPriority(incidentDetails.getPriority());
        incident.setStatus(incidentDetails.getStatus());
        incident.setCategory(incidentDetails.getCategory());
        incident.setAssignedTo(incidentDetails.getAssignedTo());
        incident.setResolutionNotes(incidentDetails.getResolutionNotes());
        incident.setSlaDueDate(incidentDetails.getSlaDueDate());
        
        incidentService.saveIncident(incident);
        return ResponseEntity.ok(incident);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncidentById(id);
        return ResponseEntity.noContent().build();
    }
}
