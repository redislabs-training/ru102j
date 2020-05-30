package com.redislabs.university.RU102J.api;

import redis.clients.jedis.GeoUnit;

import java.util.Objects;

/* Value holder class encapsulating
 * the options available for geo queries.
 *
 * onlyExcessCapacity: when set to "true", the query
 * will only return sites that have excess capacity.
 * When set to "false", the query only considers geolocation.
 */
public class GeoQuery {
    private Coordinate coordinate;
    private Double radius;
    private GeoUnit radiusUnit;
    private boolean onlyExcessCapacity;

    public GeoQuery(Coordinate coordinate, Double radius, String radiusUnit) {
        this.coordinate = coordinate;
        this.radius = radius;
        this.radiusUnit = GeoUnit.valueOf(radiusUnit);
        this.onlyExcessCapacity = false;
    }

    public GeoQuery(Coordinate coordinate, Double radius, String radiusUnit,
                    boolean onlyExcessCapacity) {
        this.coordinate = coordinate;
        this.radius = radius;
        this.radiusUnit = GeoUnit.valueOf(radiusUnit);
        this.onlyExcessCapacity = onlyExcessCapacity;
    }


    public GeoQuery(Coordinate coordinate, Double radius, GeoUnit radiusUnit) {
        this.coordinate = coordinate;
        this.radius = radius;
        this.radiusUnit = radiusUnit;
        this.onlyExcessCapacity = false;
    }

    public GeoQuery(Coordinate coordinate, Double radius, GeoUnit radiusUnit,
                    boolean onlyExcessCapacity) {
        this.coordinate = coordinate;
        this.radius = radius;
        this.radiusUnit = radiusUnit;
        this.onlyExcessCapacity = onlyExcessCapacity;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Double getRadius() {
        return radius;
    }

    public GeoUnit getRadiusUnit() {
        return radiusUnit;
    }

    public boolean onlyExcessCapacity() {
        return onlyExcessCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoQuery geoQuery = (GeoQuery) o;
        return onlyExcessCapacity == geoQuery.onlyExcessCapacity &&
                Objects.equals(coordinate, geoQuery.coordinate) &&
                Objects.equals(radius, geoQuery.radius) &&
                radiusUnit == geoQuery.radiusUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, radius, radiusUnit, onlyExcessCapacity);
    }
}
