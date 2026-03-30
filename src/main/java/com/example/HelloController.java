package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello! Application is running securely.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "secure-app");
        return status;
    }
    
    @GetMapping("/greet/{name}")
    public Map<String, String> greet(@PathVariable String name) {
        Map<String, String> greeting = new HashMap<>();
        greeting.put("greeting", "Hello, " + name + "!");
        greeting.put("timestamp", LocalDateTime.now().toString());
        return greeting;
    }
    
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("app", "Secure App Demo");
        info.put("description", "Demo application with security scanning");
        info.put("java_version", System.getProperty("java.version"));
        info.put("os", System.getProperty("os.name"));
        return info;
    }
}
