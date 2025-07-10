package org.example.clientapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clientapp.properties.ClientAppProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientAppProperties properties;

    @GetMapping("/info")
    public String info() {
        return "name: %s, number: %s, enabled: %s, tags: %s".formatted(properties.getName(), properties.getNumber(), properties.isEnabled(), properties.getTags());
    }

}
