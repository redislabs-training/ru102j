package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.SiteStats;

import java.time.ZonedDateTime;

public interface SiteStatsDao {
    SiteStats findById(long siteId);
    SiteStats findById(long siteId, ZonedDateTime day);
    void update(MeterReading reading);
}
