package org.example.clientapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

    @Value("${client-app.name}")
    private String name;

    @Value("${client-app.number}")
    private int number;

    @Value("${client-app.enabled:true}")
    private boolean enabled;

    @Value("${client-app.tags}")
    private ArrayList<String> tags;

    @GetMapping("/info")
    public String info() {
        return "name: %s, number: %s, enabled: %s, tags: %s".formatted(name, number, enabled, tags);
    }

}
