package kr.tilto.cc2backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyController {
    @GetMapping({"/", "/index", "/api", "/api/", "/api/index"})
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping({"/healthy", "/api/healthy"})
    public Healthy healthy() {
        return new Healthy("I'm healthy!");
    }
}
