package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello! Application is running securely."))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }
    
    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("secure-app"));
    }
    
    @Test
    public void testGreet() throws Exception {
        mockMvc.perform(get("/greet/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("Hello, John!"));
    }
    
    @Test
    public void testInfo() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app").value("Secure App Demo"));
    }
}
