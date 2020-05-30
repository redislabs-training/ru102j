package com.redislabs.university.RU102J;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedisConfig {
    public static final String DEFAULT_HOST = "localhost";
    public static final Integer DEFAULT_PORT = 6379;
    public static final String DEFAULT_PASSWORD = "";
    public String host;
    public Integer port;
    public String password;

    @JsonProperty
    public String getHost() {
        if (host == null) {
            return DEFAULT_HOST;
        } else {
            return host;
        }
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public Integer getPort() {
        if (port == null) {
            return DEFAULT_PORT;
        } else {
            return port;
        }
    }

    @JsonProperty
    public void setPort(Integer port) {
        this.port = port;
    }

    @JsonProperty
    public String getPassword() {
        if (password == null) {
            return DEFAULT_PASSWORD;
        } else {
            return password;
        }
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
