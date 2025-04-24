package com.sarveshsawant.webdev.dbhealthcheck.controllers;

import com.sarveshsawant.webdev.dbhealthcheck.service.DatabaseHealthCheckService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/*
Controller handles database health check requests
*/
@RestController
public class DatabaseHealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheckController.class);

    // Service does the health check
    private DatabaseHealthCheckService databaseHealthCheckService;
    public DatabaseHealthCheckController(DatabaseHealthCheckService databaseHealthCheckService){
        this.databaseHealthCheckService = databaseHealthCheckService;
    }
    @GetMapping(value = {"/healthz", "/cicd"})
    public ResponseEntity<String> checkDatabaseHealth(@RequestBody(required = false) String body,
                                                      @RequestParam Map<String, String> queryParams) throws DataAccessException, BadRequestException {
        // Check if body is present
        if (body != null && !body.isEmpty()) {
            logger.error("Body is not empty ", body);
            throw new BadRequestException();
        }
        // Check if query params are passed
        if (!queryParams.isEmpty()) {
            logger.error("query params is not empty ", queryParams);
            throw new BadRequestException();
        }
        // Check if database is healthyy

        boolean healthyOrNot = databaseHealthCheckService.isDatabaseHealthy();

        if(!healthyOrNot){
            // Return 503 Service Unavailable if database is not healthy
            throw new DataAccessException("Database is not healthy"){};
        }

        // Return 200OK if database is healthy
        return ResponseEntity.ok()
                .build();
    }
}
