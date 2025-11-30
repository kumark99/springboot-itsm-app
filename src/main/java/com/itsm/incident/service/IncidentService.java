package com.itsm.incident.service;

import com.itsm.incident.entity.Incident;
import com.itsm.incident.exception.ResourceNotFoundException;
import com.itsm.incident.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);

    @Autowired
    private IncidentRepository incidentRepository;

    public List<Incident> getAllIncidents() {
        logger.info("Fetching all incidents");
        return incidentRepository.findAll();
    }

    public void saveIncident(Incident incident) {
        logger.info("Saving incident: {}", incident.getTitle());
        incidentRepository.save(incident);
    }

    public Incident getIncidentById(Long id) {
        logger.debug("Fetching incident by id: {}", id);
        return incidentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Incident not found for id: {}", id);
                    return new ResourceNotFoundException("Incident not found for id :: " + id);
                });
    }

    public void deleteIncidentById(Long id) {
        logger.info("Deleting incident by id: {}", id);
        if (!incidentRepository.existsById(id)) {
            logger.error("Cannot delete. Incident not found for id: {}", id);
            throw new ResourceNotFoundException("Cannot delete. Incident not found for id :: " + id);
        }
        this.incidentRepository.deleteById(id);
    }

    public long getTotalIncidents() {
        logger.debug("Counting total incidents");
        return incidentRepository.count();
    }

    public long getCountByStatus(String status) {
        logger.debug("Counting incidents by status: {}", status);
        return incidentRepository.countByStatusName(status);
    }
}
