package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.CapacityReport;
import com.redislabs.university.RU102J.api.MeterReading;

public interface CapacityDao {
    void update(MeterReading reading);
    CapacityReport getReport(Integer limit);
    Long getRank(Long siteId);
}
