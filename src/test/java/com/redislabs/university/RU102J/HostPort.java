package com.redislabs.university.RU102J;

public class HostPort {
    final private static String defaultHost = "localhost";
    final private static Integer defaultPort = 6379;
    final private static String defaultPassword = "";

    public static String getRedisHost() {
        return defaultHost;
    }

    public static Integer getRedisPort() {
        return defaultPort;
    }

    public static String getRedisPassword() {
        return defaultPassword;
    }
}
