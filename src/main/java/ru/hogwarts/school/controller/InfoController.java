package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Value("${server.port}")
    private int serverPort;

    private final Environment environment;

    public InfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/port")
    public String getPort() {
        String activeProfile = environment.getActiveProfiles().length > 0 ?
                environment.getActiveProfiles()[0] : "default";
        return "Current profile: " + activeProfile + ", Port: " + serverPort;
    }

    @GetMapping("/sum")
    public long calculateSum() {
        // Используем формулу суммы арифметической прогрессии
        // S = n*(a1 + an) / 2
        // Где n = 1_000_000, a1 = 1, an = 1_000_000
        return (1_000_000L * (1L + 1_000_000L)) / 2;
    }
}
