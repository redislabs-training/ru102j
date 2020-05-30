package com.redislabs.university.RU102J.resources;

import com.redislabs.university.RU102J.api.Coordinate;
import com.redislabs.university.RU102J.api.GeoQuery;
import com.redislabs.university.RU102J.api.Site;
import com.redislabs.university.RU102J.dao.SiteGeoDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/sites")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SiteGeoResource {
    private static final Double DEFAULT_RADIUS = 10.0;
    private static final String DEFAULT_GEO_UNIT = "KM";
    private final SiteGeoDao siteDao;

    public SiteGeoResource(SiteGeoDao siteDao) {
        this.siteDao = siteDao;
    }

    @GET
    public Response getSites(@QueryParam("lng") String lng,
                             @QueryParam("lat") String lat,
                             @QueryParam("radius") Double radius,
                             @QueryParam("radiusUnit") String radiusUnit,
                             @QueryParam("onlyExcessCapacity")
                                         Boolean onlyExcessCapacity) {
        if (lng == null && lat == null) {
            return Response.ok(siteDao.findAll())
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } else if (lng != null && lat != null) {
            Set<Site> results = doGeoQuery(lng, lat, radius, radiusUnit,
                    onlyExcessCapacity);
            return Response.ok(results)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        return Response.noContent()
                .status(404)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    private Set<Site> doGeoQuery(String lng, String lat, Double radius,
                                 String radiusUnit, Boolean onlyExcessCapacity) {
        Coordinate coord = new Coordinate(lng, lat);
        if (radius == null) {
            radius = DEFAULT_RADIUS;
        }

        if (radiusUnit == null || !(radiusUnit.equals("M") || radiusUnit.equals("KM")
                || radiusUnit.equals("FT") || radiusUnit.equals("MI"))) {
            radiusUnit = DEFAULT_GEO_UNIT;
        }

        if (onlyExcessCapacity == null) {
            onlyExcessCapacity = false;
        }
        GeoQuery query = new GeoQuery(coord, radius, radiusUnit, onlyExcessCapacity);

        return siteDao.findByGeo(query);
    }

    @GET
    @Path("/{id}")
    public Response getSite(@PathParam("id") Long id) {
        Site site = siteDao.findById(id);
        if (site == null) {
            return Response.noContent().status(404).build();
        }
        return Response.ok(site).header("Access-Control-Allow-Origin", "*").build();
    }
}
