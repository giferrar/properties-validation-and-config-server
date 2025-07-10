package org.example.clientapp.properties;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@Validated
@ConfigurationProperties(prefix = "client-app")
public class ClientAppProperties {
        @NotEmpty
        private String name;

        @Min(0)
        @Max(100)
        private int number;

        @NotNull
        private boolean enabled;

        @Size(max = 5)
        private ArrayList<String> tags;
}
