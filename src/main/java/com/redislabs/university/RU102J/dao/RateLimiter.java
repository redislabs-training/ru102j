package com.redislabs.university.RU102J.dao;

public interface RateLimiter {
    void hit(String name) throws RateLimitExceededException;
}
