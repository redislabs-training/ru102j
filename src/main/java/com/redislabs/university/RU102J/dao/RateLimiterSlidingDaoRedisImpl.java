package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.core.KeyHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.time.ZonedDateTime;

public class RateLimiterSlidingDaoRedisImpl implements RateLimiter {

    private final JedisPool jedisPool;
    private final long windowSizeMS;
    private final long maxHits;

    public RateLimiterSlidingDaoRedisImpl(JedisPool pool, long windowSizeMS,
                                          long maxHits) {
        this.jedisPool = pool;
        this.windowSizeMS = windowSizeMS;
        this.maxHits = maxHits;
    }

    // Challenge #7
    @Override
    public void hit(String name) throws RateLimitExceededException {
        // START CHALLENGE #7
        try (Jedis jedis = jedisPool.getResource()) {
            String key = KeyHelper.getKey("limiter:" + windowSizeMS + ":" + name + ":" + maxHits);
            long now = ZonedDateTime.now().toInstant().toEpochMilli();

            Transaction t = jedis.multi();
            String member = now + "-" + Math.random();
            t.zadd(key, now, member);
            t.zremrangeByScore(key, 0, now - windowSizeMS);
            Response<Long> hits = t.zcard(key);
            t.exec();

            if (hits.get() > maxHits) {
                throw new RateLimitExceededException();
            }
        }
        // END CHALLENGE #7
    }
}
