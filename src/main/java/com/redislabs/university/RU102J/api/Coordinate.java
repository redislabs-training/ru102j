package com.redislabs.university.RU102J.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import redis.clients.jedis.GeoCoordinate;

import java.util.Objects;

/**
 * A simple coordinate class storing latitude and longitude.
 */
public class Coordinate {
    public Double lng;
    public Double lat;

    public Coordinate() {}

    public Coordinate(String lng, String lat) {
        this.lng = Double.valueOf(lng);
        this.lat = Double.valueOf(lat);
    }

    public Coordinate(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    @JsonProperty
    public Double getLat() {
        return lat;
    }

    @JsonProperty
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @JsonProperty
    public Double getLng() {
        return lng;
    }

    @JsonProperty
    public void setLng(Double lng) {
        this.lng = lng;
    }

    @JsonIgnore
    public GeoCoordinate getGeoCoordinate() {
        return new GeoCoordinate(lng, lat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Objects.equals(lat, that.lat) &&
                Objects.equals(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
