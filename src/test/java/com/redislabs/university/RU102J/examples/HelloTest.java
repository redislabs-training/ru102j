package com.redislabs.university.RU102J.examples;

import com.redislabs.university.RU102J.HostPort;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HelloTest {

    @Test
    public void sayHelloBasic() {
        Jedis jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (HostPort.getRedisPassword().length() > 0) {
            jedis.auth(HostPort.getRedisPassword());
        }

        jedis.set("hello", "world");
        String value = jedis.get("hello");

        assertThat(value, is("world"));
    }

    @Test
    public void sayHello() {
        Jedis jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (HostPort.getRedisPassword().length() > 0) {
            jedis.auth(HostPort.getRedisPassword());
        }

        String result = jedis.set("hello", "world");
        assertThat(result, is("OK"));
        String value = jedis.get("hello");
        assertThat(value, is("world"));

        jedis.close();
    }

    @Test
    public void sayHelloThreadSafe() {
        JedisPool jedisPool;

        String password = HostPort.getRedisPassword();

        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(),
                HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
        } else {
            jedisPool = new JedisPool(new JedisPoolConfig(),
                HostPort.getRedisHost(), HostPort.getRedisPort());
        }

        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set("hello", "world");
            assertThat(result, is("OK"));
            String value = jedis.get("hello");
            assertThat(value, is("world"));
        }

        jedisPool.close();
    }
}
