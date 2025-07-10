package org.example.clientapp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;

@ConfigurationProperties(prefix = "client-app")
public class ClientAppProperties {

    private String name;

    private int number;

    private boolean enabled;

    private ArrayList<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
