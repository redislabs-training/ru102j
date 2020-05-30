package com.redislabs.university.RU102J.examples;

import com.redislabs.university.RU102J.HostPort;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MultiKeyTest {

    private Jedis jedis;
    private String statusKey;
    private String availableKey;

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Before
    public void setUp() {
        this.jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (HostPort.getRedisPassword().length() > 0) {
            this.jedis.auth(HostPort.getRedisPassword());
        }

        this.statusKey = "test:sites:status";
        this.availableKey = "test:sites:available";
    }

    @After
    public void tearDown() {
        jedis.del(statusKey);
        jedis.del(availableKey);
        jedis.del("a");
        jedis.del("b");
        jedis.del("c");
        jedis.close();
    }

    @Test
    public void testPipeline() {
        Long siteId = 1L;
        Pipeline p = jedis.pipelined();

        Response<Long> hsetResponse = p.hset(statusKey, "available", "true");
        Response<Long> expireResponse = p.expire(statusKey, 1000);
        Response<Long> saddResponse = p.sadd(availableKey,
                String.valueOf(siteId));

        p.sync();

        assertThat(hsetResponse.get(), is(1L));
        assertThat(expireResponse.get(), is(1L));
        assertThat(saddResponse.get(), is(1L));
    }

    @Test
    public void testTransaction() {
        Long siteId = 1L;
        Transaction t = jedis.multi();

        Response<Long> hsetResponse = t.hset(statusKey, "available", "true");
        Response<Long> expireResponse = t.expire(statusKey, 1000);
        Response<Long> saddResponse = t.sadd(availableKey,
                String.valueOf(siteId));

        t.exec();

        assertThat(hsetResponse.get(), is(1L));
        assertThat(expireResponse.get(), is(1L));
        assertThat(saddResponse.get(), is(1L));
    }

    @Test public void testTransactionWithErrors() {
        jedis.set("a", "foo");
        jedis.set("c", "bar");
        Transaction t = jedis.multi();

        Response<String> r1 = t.set("b", "1");
        Response<Long> r2 = t.incr("a");
        Response<String> r3 = t.set("c", "100");

        t.exec();
        assertThat(r1.get(), is("OK"));
        assertThat(r3.get(), is("OK"));

        exceptionGrabber.expect(JedisDataException.class);
        r2.get();
    }
}
