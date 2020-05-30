package com.redislabs.university.RU102J.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

// Representation of a plot containing a list of data sets.
public class Plot {

    private List<Measurement> measurements;
    private String name;

    public Plot() {
    }

    public Plot(String name, List<Measurement> measurements) {
        this.name = name;
        this.measurements = measurements;
    }

    @JsonProperty
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plot plot = (Plot) o;
        return Objects.equals(measurements, plot.measurements) &&
                Objects.equals(name, plot.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measurements, name);
    }

    @Override
    public String toString() {
        return "Plot{" +
                "measurements=" + measurements +
                ", name='" + name + '\'' +
                '}';
    }
}
