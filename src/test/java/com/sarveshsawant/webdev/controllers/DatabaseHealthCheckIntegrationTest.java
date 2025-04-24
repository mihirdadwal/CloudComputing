package com.sarveshsawant.webdev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DatabaseHealthCheckIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    DatabaseHealthCheckIntegrationTest(MockMvc mockMvc){
        this.mockMvc = mockMvc;
    }

    // Test to check if the database is healthy
    @Test
    public void testDatabaseHealthCheck200Ok() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/healthz")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }
}
