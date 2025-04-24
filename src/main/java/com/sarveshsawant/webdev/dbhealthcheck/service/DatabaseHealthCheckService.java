package com.sarveshsawant.webdev.dbhealthcheck.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheckService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Check if database is healthy
    public boolean isDatabaseHealthy() {
            logger.info("Connecting to database");
            try {
                jdbcTemplate.execute("SELECT 1");
            } catch (Exception e) {
                return false;
            }
            logger.info("Connected to database successfully");
            return true;
    }
}
