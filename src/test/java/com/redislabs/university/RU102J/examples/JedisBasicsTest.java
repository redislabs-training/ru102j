package com.redislabs.university.RU102J.examples;

import com.redislabs.university.RU102J.HostPort;
import org.junit.*;
import redis.clients.jedis.Jedis;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JedisBasicsTest {

    public static String[] testPlanets = { "Mercury", "Mercury", "Venus",
            "Earth", "Earth", "Mars",
            "Jupiter", "Saturn", "Uranus",
            "Neptune", "Pluto" };

    private Jedis jedis;

    @Before
    public void setUp() {
        this.jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (HostPort.getRedisPassword().length() > 0) {
            jedis.auth(HostPort.getRedisPassword());
        }
        
        jedis.del("planets");
        jedis.del("earth");
    }

    @After
    public void tearDown() {
        jedis.del("planets");
        jedis.del("earth");
        jedis.close();
    }

    @Test
    public void testRedisList() {
        assertThat(testPlanets.length, is(11));

        /* Add all test planets to the Redis set */
        Long result = jedis.rpush("planets", testPlanets);
        assertThat(result, is(11L));

        // Check the length of the list
        Long length = jedis.llen("planets");
        assertThat(length, is(11L));

        // Get the planets from the list
        // Note: LRANGE is an O(n) command. Be careful running this command
        // with high-cardinality sets.
        List<String> planets = jedis.lrange("planets", 0, -1);
        assertThat(planets, is(Arrays.asList(testPlanets)));

        // Remove the elements that we know are duplicates
        // Note: O(n) operation.
        jedis.lrem("planets", 1, "Mercury");
        jedis.lrem("planets", 1, "Earth");

        // Drop a planet from the end of the list
        String planet = jedis.rpop("planets");
        assertThat(planet, is("Pluto"));

        assertThat(jedis.llen("planets"), is(8L));
    }

    @Test
    public void testRedisSet() {
        assertThat(testPlanets.length, is(11));

        // Add all test planets to the Redis set
        jedis.sadd("planets", testPlanets);

        // Return the cardinality of the set
        Long length = jedis.scard("planets");
        assertThat(length, is(9L));

        // Fetch all values from the set
        // Note: SMEMBERS is an O(n) command. Be careful running this command
        // with high-cardinality sets. Consider SSCAN as an alternative.
        Set<String> planets = jedis.smembers("planets");

        // Ensure that a HashSet created and stored in Java memory and the set stored
        // in Redis have the same values.
        Set<String> planetSet = new HashSet<>(Arrays.asList(testPlanets));
        assertThat(planets, is(planetSet));

        // Pluto is, of course, no longer a first-class planet. Remove it.
        Long response = jedis.srem("planets", "Pluto");
        assertThat(response, is(1L));

        // Now we have 8 planets, as expected.
        Long newLength = jedis.scard("planets");
        assertThat(newLength, is(8L));
    }

    @Test
    public void testRedisHash() {
        Map<String, String> earthProperties = new HashMap<>();
        earthProperties.put("diameterKM", "12756");
        earthProperties.put("dayLengthHrs", "24");
        earthProperties.put("meanTempC", "15");
        earthProperties.put("moonCount", "1");

        // Set the fields of the hash one by one.
        for (Map.Entry<String, String> property : earthProperties.entrySet()) {
            jedis.hset("earth", property.getKey(), property.getValue());
        }

        // Get the hash we just created back from Redis.
        Map<String, String> storedProperties = jedis.hgetAll("earth");
        assertThat(storedProperties, is(earthProperties));

        // Setting fields all at once is more efficient.
        jedis.hmset("earth", earthProperties);
        storedProperties = jedis.hgetAll("earth");
        assertThat(storedProperties, is(earthProperties));

        // Test that we can get a single property.
        String diameter = jedis.hget("earth", "diameterKM");
        assertThat(diameter, is(earthProperties.get("diameterKM")));
    }
}
