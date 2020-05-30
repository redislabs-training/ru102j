package com.redislabs.university.RU102J.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import redis.clients.jedis.Tuple;

import java.util.Objects;

public class SiteCapacityTuple {
    private Double capacity;
    private Long siteId;

    public SiteCapacityTuple() {}

    public SiteCapacityTuple(Tuple tuple) {
        this.capacity = tuple.getScore();
        this.siteId = Long.valueOf(tuple.getElement());
    }

    public SiteCapacityTuple(Long siteId, Double capacity) {
        this.capacity = capacity;
        this.siteId = siteId;
    }

    @JsonProperty
    public Double getCapacity() {
        return capacity;
    }

    @JsonProperty
    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    @JsonProperty
    public Long getSiteId() {
        return siteId;
    }

    @JsonProperty
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteCapacityTuple that = (SiteCapacityTuple) o;
        return Objects.equals(capacity, that.capacity) &&
                Objects.equals(siteId, that.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, siteId);
    }
}
