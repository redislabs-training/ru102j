package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.MetricUnit;
import com.redislabs.university.RU102J.core.KeyHelper;

import java.time.ZonedDateTime;

/**
 * Methods to generate key names for Redis
 * data structures. These key names are used
 * by the RedisDaoImpl classes. This class therefore
 * contains a reference to all possible key names
 * used by this application.
 */
public class RedisSchema {
    // sites:info:[siteId]
    // Redis type: hash
    static String getSiteHashKey(long siteId) {
        return KeyHelper.getKey("sites:info:" + siteId);
    }

    // sites:ids
    // Redis type: set
    public static String getSiteIDsKey() {
        return KeyHelper.getKey("sites:ids");
    }

    // sites:stats:[year-month-day]:[siteId]
    // Redis type: sorted set
    public static String getSiteStatsKey(Long siteId, ZonedDateTime dateTime) {
        return KeyHelper.getKey("sites:stats:" +
                getYearMonthDay(dateTime) + ":" +
                String.valueOf(siteId));
    }

    // limiter:[name]:[duration]:[maxHits]
    // Redis type: string of type integer
    static String getRateLimiterKey(String name, int minuteBlock,
                                    long maxHits) {
        return KeyHelper.getKey("limiter:" +
                name + ":" +
                String.valueOf(minuteBlock) + ":" +
                String.valueOf(maxHits));
    }

    // sites:geo
    // Redis type: geo
    static String getSiteGeoKey() {
        return KeyHelper.getKey("sites:geo");
    }

    // sites:capacity:ranking
    // Redis type: sorted set
    static String getCapacityRankingKey() {
        return KeyHelper.getKey("sites:capacity:ranking");
    }

    // metric:[unit-name]:[year-month-day]:[siteId]
    // Redis type: sorted set
    static String getDayMetricKey(Long siteId, MetricUnit unit,
                                  ZonedDateTime dateTime) {
        return KeyHelper.getPrefix() +
                ":metric:" +
                unit.getShortName() +
                ":" +
                getYearMonthDay(dateTime) +
                ":" +
                String.valueOf(siteId);
    }

    // sites:feed
    // Redis type: stream
    static String getGlobalFeedKey() {
        return KeyHelper.getKey("sites:feed");
    }

    // sites:feed:[siteId]
    // Redis type: stream
    static String getFeedKey(long siteId) {
        return KeyHelper.getKey("sites:feed:" + String.valueOf(siteId));
    }

    // Return the year and month in the form YEAR-MONTH-DAY
    private static String getYearMonthDay(ZonedDateTime dateTime) {
        return String.valueOf(dateTime.getYear()) + "-" +
                String.valueOf(dateTime.getMonthValue()) + "-" +
                String.valueOf(dateTime.getDayOfMonth());
    }

    // sites:ts:[siteId]:[unit]
    // Redis type: RedisTimeSeries
    static String getTSKey(Long siteId, MetricUnit unit) {
        return KeyHelper.getKey("sites:ts:" + String.valueOf(siteId) + ":" + unit.toString());
    }
}
