package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.CapacityReport;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.SiteCapacityTuple;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CapacityDaoRedisImpl implements CapacityDao {

    private final JedisPool jedisPool;

    public CapacityDaoRedisImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void update(MeterReading reading) {
        String capacityRankingKey = RedisSchema.getCapacityRankingKey();
        Long siteId = reading.getSiteId();

        double currentCapacity = reading.getWhGenerated() - reading.getWhUsed();

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(capacityRankingKey, currentCapacity, String.valueOf(siteId));
        }
    }

    @Override
    public CapacityReport getReport(Integer limit) {
        CapacityReport report;
        String key = RedisSchema.getCapacityRankingKey();

        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline p = jedis.pipelined();
            Response<Set<Tuple>> lowCapacity = p.zrangeWithScores(key, 0, limit-1);
            Response<Set<Tuple>> highCapacity = p.zrevrangeWithScores(key, 0,
                    limit-1);
            p.sync();

            List<SiteCapacityTuple> lowCapacityList = lowCapacity.get().stream()
                    .map(SiteCapacityTuple::new)
                    .collect(Collectors.toList());

            List<SiteCapacityTuple> highCapacityList = highCapacity.get().stream()
                    .map(SiteCapacityTuple::new)
                    .collect(Collectors.toList());

            report = new CapacityReport(highCapacityList, lowCapacityList);
        }

        return report;
    }

    // Challenge #4
    @Override
    public Long getRank(Long siteId) {
        // START Challenge #4
        try(Jedis jedis = jedisPool.getResource()) {
            String key = RedisSchema.getCapacityRankingKey();
            return jedis.zrevrank(key, String.valueOf(siteId));
        }
        // END Challenge #4
    }
}
