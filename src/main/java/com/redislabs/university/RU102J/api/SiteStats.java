package com.redislabs.university.RU102J.api;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

// Site summary stats for a single day.
public class SiteStats {
    private ZonedDateTime lastReportingTime;
    private Long meterReadingCount;
    private Double maxWhGenerated;
    private Double minWhGenerated;
    private Double maxCapacity;

    /* These field names will be used by multiple classes, so we define
       them here to abide by DRY (don't repeat yourself). */
    public final static String reportingTimeField = "lastReportingTime";
    public final static String countField = "meterReadingCount";
    public final static String maxWhField = "maxWhGenerated";
    public final static String minWhField = "minWhGenerated";
    public final static String maxCapacityField = "maxCapacity";

    public SiteStats(Map<String, String> map) {
        this.lastReportingTime = parseTime(map.get(reportingTimeField));
        this.meterReadingCount = parseLong(map.get(countField));
        this.maxWhGenerated = parseDouble(map.get(maxWhField));
        this.minWhGenerated = parseDouble(map.get(minWhField));
        this.maxCapacity = parseDouble(map.get(maxCapacityField));
    }

    private Double parseDouble(String value) {
        if (value == null) {
            return null;
        } else {
            return Double.valueOf(value);
        }
    }

    private Long parseLong(String value) {
        if (value == null) {
            return null;
        } else {
            return Long.valueOf(value);
        }
    }

    private ZonedDateTime parseTime(String time) {
        if (time == null) {
            return null;
        } else {
            return ZonedDateTime.parse(time);
        }
    }

    public ZonedDateTime getLastReportingTime() {
        return lastReportingTime;
    }

    public Long getMeterReadingCount() {
        return meterReadingCount;
    }

    public Double getMaxWhGenerated() {
        return maxWhGenerated;
    }

    public Double getMinWhGenerated() {
        return minWhGenerated;
    }

    public Double getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteStats siteStats = (SiteStats) o;
        return Objects.equals(lastReportingTime, siteStats.lastReportingTime) &&
                Objects.equals(meterReadingCount, siteStats.meterReadingCount) &&
                Objects.equals(maxWhGenerated, siteStats.maxWhGenerated) &&
                Objects.equals(minWhGenerated, siteStats.minWhGenerated) &&
                Objects.equals(maxCapacity, siteStats.maxCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastReportingTime, meterReadingCount, maxWhGenerated, minWhGenerated, maxCapacity);
    }

    @Override
    public String toString() {
        return "SiteStats{" +
                "lastReportingTime=" + lastReportingTime +
                ", meterReadingCount=" + meterReadingCount +
                ", maxWhGenerated=" + maxWhGenerated +
                ", minWhGenerated=" + minWhGenerated +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
