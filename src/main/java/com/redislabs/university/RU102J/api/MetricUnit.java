package com.redislabs.university.RU102J.api;

public enum MetricUnit {
    WHGenerated("whG"),
    WHUsed("whU"),
    TemperatureCelsius("tempC");

    private final String shortName;

    MetricUnit(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
