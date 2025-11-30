package com.itsm.incident.repository;

import com.itsm.incident.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByReportedByUsername(String username);
    List<Incident> findByAssignedToUsername(String username);
    long countByStatusName(String statusName);
}
