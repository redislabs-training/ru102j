package com.redislabs.university.RU102J.api;

import static io.dropwizard.testing.FixtureHelpers.*;

import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MeterReadingTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private MeterReading reading;

    @Before
    public void setUp() {
        this.reading = new MeterReading(1L,
                ZonedDateTime.parse("2020-01-01T00:00:00+00:00",
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                2.5,
                3.0,
                22.0);
    }

    @Test
    public void testSerializeMap() {
        Map<String, String> map = reading.toMap();
        assertThat(new MeterReading(map), is(reading));
    }

    @Test
    public void serializesToJSON() throws Exception {
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/meterReading.json"), MeterReading.class));

        assertThat(MAPPER.writeValueAsString(reading), is(expected));
    }

    @Test
    @Ignore
    public void deserializesFromJSON() throws Exception {
        assertThat(MAPPER.readValue(fixture("fixtures/meterReading.json"), MeterReading.class),
                is(reading));
    }
}