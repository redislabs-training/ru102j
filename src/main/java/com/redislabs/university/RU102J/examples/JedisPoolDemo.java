package com.redislabs.university.RU102J.examples;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolDemo {
    public static void main(String[] args) {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(64);
        poolConfig.setMaxIdle(64);
        JedisPool jedisPool = new JedisPool(poolConfig, "redis.enterprise", 6379);
    }
}
