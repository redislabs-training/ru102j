package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.HostPort;
import com.redislabs.university.RU102J.TestKeyManager;
import com.redislabs.university.RU102J.api.Coordinate;
import com.redislabs.university.RU102J.api.GeoQuery;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.api.Site;
import org.junit.*;
import org.junit.rules.ExpectedException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

public class SiteGeoDaoRedisImplTest {

    private static JedisPool jedisPool;
    private static Jedis jedis;
    private static TestKeyManager keyManager;
    private Set<Site> sites;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws Exception {
        String password = HostPort.getRedisPassword();

        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(), HostPort.getRedisHost(), HostPort.getRedisPort(), 2000, password);
        } else {
            jedisPool = new JedisPool(HostPort.getRedisHost(), HostPort.getRedisPort());
        }

        jedis = new Jedis(HostPort.getRedisHost(), HostPort.getRedisPort());

        if (password.length() > 0) {
            jedis.auth(password);
        }

        keyManager = new TestKeyManager("test");
    }

    @AfterClass
    public static void tearDown() {
        jedisPool.destroy();
        jedis.close();
    }

    @After
    public void flush() {
        keyManager.deleteKeys(jedis);
    }

    @Before
    public void generateData() {
        sites = new HashSet<>();

        Site site1 = new Site(1, 4.5, 3, "637 Britannia Drive",
                "Vallejo", "CA", "94591" );
        site1.setCoordinate(new Coordinate("-122.193849", "38.10476999999999"));
        sites.add(site1);

        Site site2 = new Site(2, 4.5, 3, "31353 Santa Elena Way",
                "Union City", "CA", "94587" );
        site2.setCoordinate(new Coordinate("-122.059762", "37.593981"));
        sites.add(site2);

        Site site3 = new Site(3, 4.5, 3, "1732 27th Avenue",
                "Oakland", "CA", "94601" );
        site3.setCoordinate(new Coordinate("-122.228238", "37.783431"));
        sites.add(site3);
    }

    @Test
    public void findAllWithMultipleSites() {
        SiteGeoDao dao = new SiteGeoDaoRedisImpl(jedisPool);
        // Insert all sites
        for (Site site : sites) {
            dao.insert(site);
        }

        assertThat(dao.findAll(), is(sites));
    }

    @Test
    public void findAllWithEmptySites() {
        SiteDaoRedisImpl dao = new SiteDaoRedisImpl(jedisPool);
        assertThat(dao.findAll(), is(empty()));
    }

    @Test
    public void findByGeo() {
        // Insert sites
        SiteGeoDao dao = new SiteGeoDaoRedisImpl(jedisPool);
        for (Site site : sites) {
            dao.insert(site);
        }

        Coordinate oakland = new Coordinate("-122.272476", "37.804829");
        Set<Site> oaklandSites = dao.findByGeo(new GeoQuery(oakland, 10.0, "KM"));
        assertThat(oaklandSites.size(), is(1));


        Coordinate vallejo = new Coordinate("-122.256637", "38.104086");
        Set<Site> vallejoSites = dao.findByGeo(new GeoQuery(vallejo, 10.0, "KM"));
        assertThat(vallejoSites.size(), is(1));


        Coordinate unionCity = new Coordinate("-122.081630", "37.596323");
        Set<Site> unionCitySites = dao.findByGeo(new GeoQuery(unionCity, 10.0, "KM"));
        assertThat(unionCitySites.size(), is(1));

        // Expand the radius to return all 3 sites
        Set<Site> californiaSites = dao.findByGeo(new GeoQuery(unionCity, 60.0, "KM"));
        assertThat(californiaSites.size(), is(3));
    }

    // Challenge #5
    @Test
    public void findByGeoWithExcessCapacity() {
        SiteGeoDao siteDao = new SiteGeoDaoRedisImpl(jedisPool);
        CapacityDao capacityDao = new CapacityDaoRedisImpl(jedisPool);
        Site vallejo = new Site(1, 4.5, 3, "637 Britannia Drive",
                "Vallejo", "CA", "94591" );
        Coordinate vallejoCoord = new Coordinate("-122.256637", "38.104086");
        vallejo.setCoordinate(vallejoCoord);
        siteDao.insert(vallejo);

        // This site is returned when we're not looking for excess capacity.
        Set<Site> sites = siteDao.findByGeo(new GeoQuery(vallejoCoord, 10.0, "KM"));
        assertThat(sites.size(), is(1));
        assertThat(sites.contains(vallejo), is(true));

        // Simulate changing a meter reading with no excess capacity
        MeterReading reading = new MeterReading();
        reading.setSiteId(vallejo.getId());
        reading.setWhUsed(1.0);
        reading.setWhGenerated(0.0);
        capacityDao.update(reading);

        // In this case, no sites are returned on the excess capacity query
        sites = siteDao.findByGeo(new GeoQuery(vallejoCoord, 10.0, "KM", true));
        assertThat(sites.size(), is(0));

        // Simulate changing a meter reading indicating excess capacity
        reading.setWhGenerated(2.0);
        capacityDao.update(reading);

        // In this case, one site is returned on the excess capacity query
        sites = siteDao.findByGeo(new GeoQuery(vallejoCoord, 10.0, "KM", true));
        assertThat(sites.size(), is(1));
        assertThat(sites.contains(vallejo), is(true));
    }

    @Test
    public void insert() {
         SiteGeoDao dao = new SiteGeoDaoRedisImpl(jedisPool);
         Site vallejo = new Site(7, 4.5, 3, "637 Britannia Drive",
                "Vallejo", "CA", "94591" );
         vallejo.setCoordinate(new Coordinate("-122.193849", "38.10476999999999"));
         dao.insert(vallejo);
         String key = RedisSchema.getSiteHashKey(vallejo.getId());
         Map<String, String> response = jedis.hgetAll(key);
         assertThat(response.get("panels"), is(vallejo.getPanels().toString()));
         assertThat(response.get("capacity"), is(vallejo.getCapacity().toString()));
         assertThat(response.get("address"), is(vallejo.getAddress()));
         assertThat(response.get("city"), is(vallejo.getCity()));
         assertThat(response.get("state"), is(vallejo.getState()));
         assertThat(response.get("postalCode"), is(vallejo.getPostalCode()));
         assertThat(response.get("lat"), is(vallejo.getCoordinate().getLat().toString()));
         assertThat(response.get("lng"), is(vallejo.getCoordinate().getLng().toString()));
    }

    @Test
    public void insertFailsWithoutCoordinate() {
        SiteGeoDao dao = new SiteGeoDaoRedisImpl(jedisPool);
        Site vallejo = new Site(7, 4.5, 3, "637 Britannia Drive",
                "Vallejo", "CA", "94591" );
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Coordinate required for Geo insert.");
        dao.insert(vallejo);
    }
}