package com.epam.gymcrm.restController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> getTestMessage() {
        return ResponseEntity.ok(Map.of(
                "message", "Hello from Spring Framework (no Boot)!",
                "status", "success"
        ));
    }
}
