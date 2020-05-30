package com.redislabs.university.RU102J.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Objects;

/** Model class used to represent a single measurement
 * at a particular time. These objects are returned in API
 * calls that may request a series of points for a chart.
 */
public class Measurement {
    public Long siteId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public ZonedDateTime dateTime;
    public Double value;
    public MetricUnit metricUnit;

    public Measurement() {}

    public Measurement(Long siteId, MetricUnit metricUnit, ZonedDateTime date, Double value) {
        this.siteId = siteId;
        this.metricUnit = metricUnit;
        this.dateTime = date;
        this.value = value;
    }

    @JsonProperty
    public Long getSiteId() {
        return siteId;
    }

    @JsonProperty
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    @JsonProperty
    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @JsonProperty
    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @JsonProperty
    public Double getValue() {
        return value;
    }

    @JsonProperty
    public void setValue(Double value) {
        this.value = value;
    }

    @JsonProperty
    public MetricUnit getMetricUnit() {
        return metricUnit;
    }

    @JsonProperty
    public void setMetricUnit(MetricUnit metricUnit) {
        this.metricUnit = metricUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(siteId, that.siteId) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(value, that.value) &&
                metricUnit == that.metricUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, dateTime, value, metricUnit);
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "siteId=" + siteId +
                ", dateTime=" + dateTime +
                ", value=" + value +
                ", metricUnit=" + metricUnit +
                '}';
    }
}
