package com.factusapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FactusappApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactusappApplication.class, args);
        System.out.println("\n===============================================");
        System.out.println("  FACTUSAPP - Backend iniciado correctamente");
        System.out.println("  API Docs: http://localhost:8080/swagger-ui.html");
        System.out.println("===============================================\n");
    }
}
