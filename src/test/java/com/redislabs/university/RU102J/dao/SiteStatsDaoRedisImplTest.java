package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.HostPort;
import com.redislabs.university.RU102J.TestKeyManager;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.SiteStats;
import org.junit.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.ZonedDateTime;

public class SiteStatsDaoRedisImplTest {
    private static JedisPool jedisPool;
    private static Jedis jedis;
    private static TestKeyManager keyManager;

    @BeforeClass
    public static void setUp() throws Exception {
        String password = HostPort.getRedisPassword();

        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(), HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
        } else {
            jedisPool = new JedisPool(HostPort.getRedisHost(), HostPort.getRedisPort());
        }

        jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (password.length() > 0) {
            jedis.auth(password);
        }

        keyManager = new TestKeyManager("test");
    }

    @AfterClass
    public static void tearDown() {
        jedisPool.destroy();
        jedis.close();
    }

    @After
    public void flush() {
        keyManager.deleteKeys(jedis);
    }

    @Test
    public void findById() {
        MeterReading r1 = generateMeterReading(1);
        SiteStatsDao dao = new SiteStatsDaoRedisImpl(jedisPool);
        dao.update(r1);
        SiteStats stats = dao.findById(1);
        assertThat(stats.getMeterReadingCount(), is(1L));
        assertThat(stats.getMinWhGenerated(), is(r1.getWhGenerated()));
        assertThat(stats.getMaxWhGenerated(), is(r1.getWhGenerated()));
    }

    // Challenge #3
    @Test
    public void testUpdate() {
        SiteStatsDao dao = new SiteStatsDaoRedisImpl(jedisPool);
        MeterReading r1 = generateMeterReading(1);
        r1.setWhGenerated(1.0);
        r1.setWhUsed(0.0);
        MeterReading r2 = generateMeterReading(1);
        r2.setWhGenerated(2.0);
        r2.setWhUsed(0.0);

        dao.update(r1);
        dao.update(r2);
        SiteStats stats = dao.findById(1L, r1.getDateTime());
        assertThat(stats.getMaxWhGenerated(), is(2.0));
        assertThat(stats.getMinWhGenerated(), is(1.0));
        assertThat(stats.getMaxCapacity(), is(2.0));
    }

    private MeterReading generateMeterReading(long siteId) {
        MeterReading reading = new MeterReading();
        reading.setSiteId(siteId);
        reading.setDateTime(ZonedDateTime.now());
        reading.setTempC(15.0);
        reading.setWhGenerated(0.025);
        reading.setWhUsed(0.015);
        return reading;
    }
}