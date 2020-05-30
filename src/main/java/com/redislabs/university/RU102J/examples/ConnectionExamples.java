package com.redislabs.university.RU102J.examples;

import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

public class ConnectionExamples {

    // Create a basic connection to Redis. Not threadsafe!
    public static Jedis getJedis(String host, Integer port, int timeout,
                                 String password) {
        Jedis jedis = new Jedis(host, port);
        jedis.auth(password);

        return jedis;
    }

    // Create a pool of connections to Redis. This is threadsafe.
    public static JedisPool getPool(String host, Integer port,
                                    int maxConnections, int timeout,
                                    String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);

        return new JedisPool(config, host, port, timeout, password);
    }

    // Connect to a Redis Sentinel deployment. Pooled and threadsafe.
    public static JedisSentinelPool getJedisSentinelPool(int maxConnections,
                                                         int timeout,
                                                         String password) {
        String masterName = "redisMaster";
        Set<String> sentinels = new HashSet<>();
        sentinels.add("localhost:6379");
        sentinels.add("localhost:7379");

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);

        return new JedisSentinelPool(masterName, sentinels, config, timeout,
                password);
    }

    /** Connect to a Redis Cluster deployment. Uses a pool connection under
    the hood and is threadsafe.
    */
    public static JedisCluster getClusterConnection(int timeout, int maxAttempts,
                                                    int maxConnections) {
        Set<HostAndPort> nodes = new HashSet<>();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);

        return new JedisCluster(nodes, timeout, maxAttempts, config);
    }
}
