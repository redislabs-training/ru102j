package com.redislabs.university.RU102J.resources;

import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.dao.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/meterReadings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MeterReadingResource {
    final static private Integer MAX_RECENT_FEEDS = 1000;
    final static private Integer DEFAULT_RECENT_FEEDS = 100;
    private final SiteStatsDao siteStatsDao;
    private final MetricDao metricDao;
    private final CapacityDao capacityDao;
    private final FeedDao feedDao;

    public MeterReadingResource(SiteStatsDao siteStatsDao, MetricDao metricDao,
                                CapacityDao capacityDao, FeedDao feedDao) {
        this.siteStatsDao = siteStatsDao;
        this.metricDao = metricDao;
        this.capacityDao = capacityDao;
        this.feedDao = feedDao;
    }

    @POST
    public Response addAll(List<MeterReading> readings) {
        for (MeterReading reading : readings) {
            add(reading);
        }

        return Response.accepted().build();
    }

    public Response add(MeterReading reading) {
        metricDao.insert(reading);
        siteStatsDao.update(reading);
        capacityDao.update(reading);
        feedDao.insert(reading);

        return Response.accepted().build();
    }

    @GET
    public Response getGlobal(@PathParam("n") Integer count) {
        List<MeterReading> readings = feedDao.getRecentGlobal(getFeedCount(count));
        return Response.ok(readings)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getForSite(@PathParam("id") Long id,
                               @PathParam("n") Integer count) {
        List<MeterReading> readings =
                feedDao.getRecentForSite(id, getFeedCount(count));
        return Response.ok(readings)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    private Integer getFeedCount(Integer count) {
        if (count == null || count < 0) {
            return DEFAULT_RECENT_FEEDS;
        } else if (count > MAX_RECENT_FEEDS) {
            return MAX_RECENT_FEEDS;
        } else {
            return count;
        }
    }
}
