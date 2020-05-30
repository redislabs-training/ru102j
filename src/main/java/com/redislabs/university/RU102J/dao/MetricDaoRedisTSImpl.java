package com.redislabs.university.RU102J.dao;

import com.redislabs.redistimeseries.RedisTimeSeries;
import com.redislabs.redistimeseries.Value;
import com.redislabs.university.RU102J.api.Measurement;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.MetricUnit;
import redis.clients.jedis.JedisPool;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Retain metrics using the Redis Time Series module
 * (see https://github.com/RedisLabsModules/RedisTimeSeries)
 *
 */
public class MetricDaoRedisTSImpl implements MetricDao {
    static private final Integer RETENTION_MS =
            60 * 60 * 24 * 14 * 1000;
    private final RedisTimeSeries rts;

    public MetricDaoRedisTSImpl(JedisPool pool) {
        this.rts = new RedisTimeSeries(pool);
    }

    @Override
    public void insert(MeterReading reading) {
        insertMetric(reading.getSiteId(), reading.getWhGenerated(),
                MetricUnit.WHGenerated, reading.getDateTime());
        insertMetric(reading.getSiteId(), reading.getWhUsed(),
                MetricUnit.WHUsed, reading.getDateTime());
        insertMetric(reading.getSiteId(), reading.getTempC(),
                MetricUnit.TemperatureCelsius, reading.getDateTime());
    }

    private void insertMetric(Long siteId, Double value, MetricUnit unit,
                              ZonedDateTime dateTime) {
        String metricKey = RedisSchema.getTSKey(siteId, unit);
        rts.add(metricKey, dateTime.toEpochSecond() * 1000, value, RETENTION_MS);
    }


    // Return the `limit` most-recent minute-level measurements starting at the
    // provided timestamp.
    @Override
    public List<Measurement> getRecent(Long siteId, MetricUnit unit, ZonedDateTime time, Integer limit) {
        List<Measurement> measurements = new ArrayList<>();
        String metricKey = RedisSchema.getTSKey(siteId, unit);

        Long nowMs = time.toEpochSecond() * 1000;
        Long initialTimestamp = nowMs - (limit * 60) * 1000;
        Value[] values = rts.range(metricKey, initialTimestamp, nowMs);

        for (int j=0; j<limit && j<values.length; j++) {
            Measurement m = new Measurement();
            m.setSiteId(siteId);
            m.setMetricUnit(unit);
            Instant i = Instant.ofEpochSecond(values[j].getTime() / 1000);
            m.setDateTime(ZonedDateTime.ofInstant(i, ZoneId.of("UTC")));
            m.setValue(values[j].getValue());
            measurements.add(m);
        }

        return measurements;
    }
}
