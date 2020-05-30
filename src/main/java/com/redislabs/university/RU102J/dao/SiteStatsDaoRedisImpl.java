package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.SiteStats;
import com.redislabs.university.RU102J.script.CompareAndUpdateScript;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

public class SiteStatsDaoRedisImpl implements SiteStatsDao {

    private final int weekSeconds = 60 * 60 * 24 * 7;
    private final JedisPool jedisPool;
    private final CompareAndUpdateScript compareAndUpdateScript;

    public SiteStatsDaoRedisImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.compareAndUpdateScript = new CompareAndUpdateScript(jedisPool);
    }

    // Returns the site stats for the current day
    @Override
    public SiteStats findById(long siteId) {
        return findById(siteId, ZonedDateTime.now());
    }

    @Override
    public SiteStats findById(long siteId, ZonedDateTime day) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = RedisSchema.getSiteStatsKey(siteId, day);
            Map<String, String> fields = jedis.hgetAll(key);
            if (fields == null || fields.isEmpty()) {
                return null;
            }
            return new SiteStats(fields);
        }
    }

    @Override
    public void update(MeterReading reading) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long siteId = reading.getSiteId();
            ZonedDateTime day = reading.getDateTime();
            String key = RedisSchema.getSiteStatsKey(siteId, day);

            updateBasic(jedis, key, reading);
        }
    }

    // A naive implementation of update. This implementation has
    // potential race conditions and makes several round trips to Redis.
    private void updateBasic(Jedis jedis, String key, MeterReading reading) {
        String reportingTime = ZonedDateTime.now(ZoneOffset.UTC).toString();
        jedis.hset(key, SiteStats.reportingTimeField, reportingTime);
        jedis.hincrBy(key, SiteStats.countField, 1);
        jedis.expire(key, weekSeconds);

        String maxWh = jedis.hget(key, SiteStats.maxWhField);
        if (maxWh == null || reading.getWhGenerated() > Double.valueOf(maxWh)) {
            jedis.hset(key, SiteStats.maxWhField,
                    String.valueOf(reading.getWhGenerated()));
        }

        String minWh = jedis.hget(key, SiteStats.minWhField);
        if (minWh == null || reading.getWhGenerated() < Double.valueOf(minWh)) {
            jedis.hset(key, SiteStats.minWhField,
                    String.valueOf(reading.getWhGenerated()));
        }

        String maxCapacity = jedis.hget(key, SiteStats.maxCapacityField);
        if (maxCapacity == null || getCurrentCapacity(reading) > Double.valueOf(maxCapacity)) {
            jedis.hset(key, SiteStats.maxCapacityField,
                    String.valueOf(getCurrentCapacity(reading)));
        }
    }

    // Challenge #3
    private void updateOptimized(Jedis jedis, String key, MeterReading reading) {
        // START Challenge #3
        // END Challenge #3
    }

    private Double getCurrentCapacity(MeterReading reading) {
        return reading.getWhGenerated() - reading.getWhUsed();
    }
}
