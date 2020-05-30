package com.redislabs.university.RU102J.resources;

import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.dao.*;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MeterReadingResourceTest {
    private static final SiteStatsDao siteStatsDao = mock(SiteStatsDao.class);
    private static final MetricDao metricDao = mock(MetricDao.class);
    private static final CapacityDao capacityDao = mock(CapacityDao.class);
    private static final FeedDao feedDao = mock(FeedDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new MeterReadingResource(siteStatsDao, metricDao,
                    capacityDao, feedDao))
            .build();

    @Before
    public void setup() {
    }

    @After
    public void tearDown(){
        reset(siteStatsDao);
        reset(metricDao);
        reset(capacityDao);
        reset(feedDao);
    }

    @Test
    public void testInsertMeterReading() {
        List<MeterReading> readings = new ArrayList<>();
        MeterReading r1 = generateMeterReading(1);
        MeterReading r2 = generateMeterReading(2);
        readings.add(r1);
        readings.add(r2);
        Entity<List<MeterReading>> entity = Entity.json(readings);
        resources.target("/meterReadings").request().post(entity);
        verify(metricDao).insert(r1);
        verify(metricDao).insert(r2);
        verify(capacityDao).update(r1);
        verify(capacityDao).update(r2);
        verify(siteStatsDao).update(r1);
        verify(siteStatsDao).update(r2);
        verify(feedDao).insert(r1);
        verify(feedDao).insert(r2);
    }

    private MeterReading generateMeterReading(long siteId) {
        ZonedDateTime now = ZonedDateTime.now().
                withZoneSameInstant(ZoneId.of("UTC"));
        MeterReading reading = new MeterReading();
        reading.setSiteId(siteId);
        reading.setDateTime(now);
        reading.setTempC(15.0);
        reading.setWhGenerated(0.025);
        reading.setWhUsed(0.015);
        return reading;
    }
}