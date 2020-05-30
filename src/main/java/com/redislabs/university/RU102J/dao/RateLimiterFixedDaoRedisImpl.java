package com.redislabs.university.RU102J.dao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.time.ZonedDateTime;

/* A fixed-window rate-limiter.
 *
 * Must be configured with a name, a minuteInterval, and
 * the max hits allowed in that interval.
 *
 * For example, a minuteInterval of 1 and a maxHits of 10
 * means that...
 *
 * Interval    Hits   Result
 * 12:00          9       OK
 * 12:01         10       OK
 * 12:02         11       RateLimitExceededException
 *
 * For a minuteInterval of 5 and maxHits of 50...
 *
 * Interval    Hits    Result
 * 12:00         99        OK
 * 12:05          2        OK
 * 12:10        101        RateLimitExceededException
 */
public class RateLimiterFixedDaoRedisImpl implements RateLimiter {

    private final MinuteInterval interval;
    private final int expiration;
    private final long maxHits;
    private final JedisPool jedisPool;

    public RateLimiterFixedDaoRedisImpl(JedisPool jedisPool,
                                        MinuteInterval interval, long maxHits) {
        this.jedisPool = jedisPool;
        this.interval = interval;
        this.expiration = interval.getValue() * 60;
        this.maxHits = maxHits;
    }


    @Override
    public void hit(String name) throws RateLimitExceededException {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = getKey(name);
            Pipeline pipeline = jedis.pipelined();
            Response<Long> hits = pipeline.incr(key);
            pipeline.expire(key, expiration);
            pipeline.sync();
            if (hits.get() > maxHits) {
                throw new RateLimitExceededException();
            }
        }
    }

    private String getKey(String name) {
        int dayMinuteBlock = getMinuteOfDayBlock(ZonedDateTime.now());
        return RedisSchema.getRateLimiterKey(name, dayMinuteBlock, maxHits);
    }

    private int getMinuteOfDayBlock(ZonedDateTime dateTime) {
        int minuteOfDay = dateTime.getHour() * 60 + dateTime.getMinute();
        return minuteOfDay / interval.getValue();
    }
}
