package com.redislabs.university.RU102J.resources;

import com.redislabs.university.RU102J.dao.CapacityDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/capacity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CapacityResource {

    private final CapacityDao capacityDao;
    private static final Integer defaultLimit = 10;

    public CapacityResource(CapacityDao capacityDao) {
        this.capacityDao = capacityDao;
    }

    @GET
    @Path("/")
    public Response getCapacity(@PathParam("limit") Integer limit) {
        return Response.ok(capacityDao.getReport(defaultLimit))
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }
}
