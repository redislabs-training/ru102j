package com.redislabs.university.RU102J.examples;

import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

public class UpdateIfLowestScript {
    private final Jedis jedis;
    private final String sha;
    private final static String script =
            "local key = KEYS[1] " +
            "local new = ARGV[1] " +
            "local current = redis.call('GET', key) " +
            "if (current == false) or " +
            "   (tonumber(new) < tonumber(current)) then " +
            "  redis.call('SET', key, new) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";

    // Load the script and cache the sha of the script.
    public UpdateIfLowestScript(Jedis jedis) {
        this.jedis = jedis;
        this.sha = jedis.scriptLoad(script);
    }

    public boolean updateIfLowest(String key, Integer newValue) {
        List<String> keys = Collections.singletonList(key);
        List<String> args = Collections.singletonList(String.valueOf(newValue));
        Object response = jedis.evalsha(sha, keys, args);
        return (Long)response == 1;
    }
}
