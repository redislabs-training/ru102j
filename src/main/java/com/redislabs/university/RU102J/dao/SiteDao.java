package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.Site;

import java.util.Set;

public interface SiteDao {
    void insert(Site site);
    Site findById(long id);
    Set<Site> findAll();
}
