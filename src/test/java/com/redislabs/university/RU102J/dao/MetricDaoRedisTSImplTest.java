package com.redislabs.university.RU102J.dao;

import com.redislabs.redistimeseries.RedisTimeSeries;
import com.redislabs.university.RU102J.HostPort;
import com.redislabs.university.RU102J.TestKeyManager;
import com.redislabs.university.RU102J.api.Measurement;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.MetricUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

@Ignore
public class MetricDaoRedisTSImplTest {

    private ArrayList<MeterReading> readings;
    private Long siteId = 1L;
    private ZonedDateTime startingDate = ZonedDateTime.now(ZoneOffset.UTC);
    private TestKeyManager keyManager;
    private RedisTimeSeries rts;
    private JedisPool jedisPool;

    @Before
    public void setUp() {
        String password = HostPort.getRedisPassword();

        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(), HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
        } else {
            jedisPool = new JedisPool(HostPort.getRedisHost(), HostPort.getRedisPort());
        }

        keyManager = new TestKeyManager("test");
    }

    @After
    public void tearDown() {
        String gKey = RedisSchema.getTSKey(siteId, MetricUnit.WHGenerated);
        String uKey = RedisSchema.getTSKey(siteId, MetricUnit.WHUsed);
        String tKey = RedisSchema.getTSKey(siteId, MetricUnit.TemperatureCelsius);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(gKey);
            jedis.del(uKey);
            jedis.del(tKey);
            keyManager.deleteKeys(jedis);
        }
    }

    /**
     * Generate 72 hours worth of data.
    */
    @Before
    public void generateData() {
        readings = new ArrayList<>();
        ZonedDateTime time = startingDate;
        for (int i=0; i <  72 * 60; i++) {
            MeterReading reading = new MeterReading();
            reading.setSiteId(siteId);
            reading.setTempC(i * 1.0);
            reading.setWhUsed(i * 1.0);
            reading.setWhGenerated(i * 1.0);
            reading.setDateTime(time);
            readings.add(reading);
            time = time.minusMinutes(1);
        }
        Collections.reverse(readings);
    }

    @Test
    public void testSmall() {
        testInsertAndRetrieve(1);
    }

    @Test
    public void testOneDay() {
        testInsertAndRetrieve(60 * 24);
    }


    @Test
    public void testMultipleDays() {
        testInsertAndRetrieve(60 * 70);
    }

    private void testInsertAndRetrieve(int limit) {
        MetricDao metricDao = new MetricDaoRedisTSImpl(jedisPool);
        for (MeterReading reading : readings) {
            metricDao.insert(reading);
        }

        List<Measurement> measurements = metricDao.getRecent(siteId, MetricUnit.WHGenerated,
         startingDate, limit);
        assertThat(measurements.size(), is(limit));
    }
}