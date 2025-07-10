package org.example.clientapp.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clientapp.properties.ClientAppProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomEventListeners {

    private final ClientAppProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Application is ready. Properties are: {}", properties.toString());
    }

}
