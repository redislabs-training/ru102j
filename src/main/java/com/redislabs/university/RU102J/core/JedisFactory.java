package com.redislabs.university.RU102J.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {
    @JsonProperty
    private String host;

    @JsonProperty
    private Integer port;

    @JsonProperty
    private String password;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public JedisPool build(Environment environment) {
        String password = getPassword();
        JedisPool pool;

        if (password.length() > 0) {
            pool = new JedisPool(new JedisPoolConfig(), getHost(), getPort(), 2000, getPassword());
        } else {
            pool = new JedisPool(new JedisPoolConfig(), getHost(), getPort());
        }

        return pool;
    }
}
