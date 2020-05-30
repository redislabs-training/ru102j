package com.redislabs.university.RU102J.examples;

import redis.clients.jedis.Jedis;

public class Hello {

    private final String host;
    private final Integer port;

    public Hello(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void say() {
        Jedis jedis = new Jedis(host, port);
        String response = jedis.set("hello", "world");
        String saying = jedis.get("hello");
        System.out.println("Hello, " + saying);
        jedis.close();
    }
}
