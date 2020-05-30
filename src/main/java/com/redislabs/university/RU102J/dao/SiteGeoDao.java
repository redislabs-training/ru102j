package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.GeoQuery;
import com.redislabs.university.RU102J.api.Site;

import java.util.Set;

public interface SiteGeoDao extends SiteDao {
    Set<Site> findByGeo(GeoQuery query);
}
