package org.example.clientapp.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Validated
@ConfigurationProperties(prefix = "client-app")
public record ClientAppProperties (
        @NotEmpty
        String name,

        @Min(0)
        @Max(100)
        int number,

        @NotNull
        boolean enabled,

        @Size(max = 5)
        ArrayList<String> tags
) {
}
