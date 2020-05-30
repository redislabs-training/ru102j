package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.HostPort;
import com.redislabs.university.RU102J.JedisDaoTestBase;
import com.redislabs.university.RU102J.TestKeyManager;
import com.redislabs.university.RU102J.api.MeterReading;
import org.junit.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FeedDaoRedisImplTest {

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

    // Challenge #6
    @Ignore
    @Test
    public void testBasicInsertReturnsRecent() {
        FeedDao dao = new FeedDaoRedisImpl(jedisPool);
        MeterReading reading0 = generateMeterReading(1L, ZonedDateTime.now());
        MeterReading reading1 = generateMeterReading(1L,
                ZonedDateTime.now().minusMinutes(1));
        dao.insert(reading0);
        dao.insert(reading1);
        List<MeterReading> globalList = dao.getRecentGlobal(100);
        assertThat(globalList.size(), is(2));
        assertThat(globalList.get(0), is(reading1));
        assertThat(globalList.get(1), is(reading0));

        List<MeterReading> siteList = dao.getRecentForSite(1, 100);
        assertThat(siteList.size(), is(2));
        assertThat(siteList.get(0), is(reading1));
        assertThat(siteList.get(1), is(reading0));
    }

    private MeterReading generateMeterReading(long siteId, ZonedDateTime dateTime) {
        MeterReading reading = new MeterReading();
        reading.setSiteId(siteId);
        reading.setDateTime(dateTime);
        reading.setTempC(15.0);
        reading.setWhGenerated(0.025);
        reading.setWhUsed(0.015);
        return reading;
    }
}