package com.itsm.incident.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itsm.incident.entity.Category;
import com.itsm.incident.entity.Incident;
import com.itsm.incident.entity.Status;
import com.itsm.incident.entity.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IncidentRestClient {

    private static final String BASE_URL = "http://localhost:8081/api/incidents";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static void main(String[] args) {
        try {
            System.out.println("Starting Incident REST Client Test...");

            // 1. GET All Incidents
            System.out.println("\n--- 1. GET All Incidents ---");
            getAllIncidents();

            // 2. POST Create New Incident
            System.out.println("\n--- 2. POST Create New Incident ---");
            Incident newIncident = createSampleIncident();
            Incident createdIncident = createIncident(newIncident);
            System.out.println("Created Incident ID: " + createdIncident.getId());

            // 3. GET Incident By ID
            System.out.println("\n--- 3. GET Incident By ID ---");
            getIncidentById(createdIncident.getId());

            // 4. PUT Update Incident
            System.out.println("\n--- 4. PUT Update Incident ---");
            createdIncident.setTitle("Updated Title via REST Client");
            createdIncident.setPriority("CRITICAL");
            updateIncident(createdIncident.getId(), createdIncident);

            // 5. DELETE Incident
            System.out.println("\n--- 5. DELETE Incident ---");
            deleteIncident(createdIncident.getId());

            // 6. Verify Deletion
            System.out.println("\n--- 6. Verify Deletion ---");
            try {
                getIncidentById(createdIncident.getId());
            } catch (RuntimeException e) {
                System.out.println("Expected error: " + e.getMessage());
            }

            System.out.println("\nTest Completed Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getAllIncidents() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", getBasicAuthHeader())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
    }

    private static Incident createIncident(Incident incident) throws Exception {
        String json = objectMapper.writeValueAsString(incident);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", getBasicAuthHeader())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
             return objectMapper.readValue(response.body(), Incident.class);
        } else {
            throw new RuntimeException("Failed to create incident: " + response.statusCode());
        }
    }

    private static void getIncidentById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Authorization", getBasicAuthHeader())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get incident: " + response.statusCode());
        }
    }

    private static void updateIncident(Long id, Incident incident) throws Exception {
        String json = objectMapper.writeValueAsString(incident);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Authorization", getBasicAuthHeader())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
    }

    private static void deleteIncident(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Authorization", getBasicAuthHeader())
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printResponse(response);
    }

    private static String getBasicAuthHeader() {
        String auth = USERNAME + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private static void printResponse(HttpResponse<String> response) {
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    private static Incident createSampleIncident() {
        Incident incident = new Incident();
        incident.setTitle("REST Client Test Incident");
        incident.setDescription("This incident was created by the standalone Java REST client.");
        incident.setPriority("HIGH");
        
        // Assuming IDs 1 exist from DataSeeder
        Category category = new Category();
        category.setId(1L); 
        incident.setCategory(category);

        Status status = new Status();
        status.setId(1L);
        incident.setStatus(status);
        
        User user = new User();
        user.setId(1L); // Assuming admin/user has ID 1
        incident.setReportedBy(user);

        return incident;
    }
}
