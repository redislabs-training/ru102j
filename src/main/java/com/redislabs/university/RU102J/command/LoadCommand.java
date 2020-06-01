package com.redislabs.university.RU102J.command;

import com.redislabs.university.RU102J.core.DataLoader;
import com.redislabs.university.RU102J.core.SampleDataGenerator;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LoadCommand extends Command {
    public LoadCommand() {
        super("load", "Load the specified JSON file into Redis.");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("--flush")
                .dest("flush")
                .type(Boolean.class)
                .required(false)
                .setDefault(false)
                .help("Run the 'flushdb' command on the Redis database to " +
                        "clear all data before loading.");

        subparser.addArgument("--filename")
                .dest("filename")
                .type(String.class)
                .required(false)
                .help("The filename containing the JSON to load. If not specified, " +
                        "will load the sites.json file bundled with this JAR.");

        subparser.addArgument("--host")
                .dest("host")
                .type(String.class)
                .required(false)
                .setDefault("localhost")
                .help("The host of the Redis server to connect to");

        subparser.addArgument("--port")
                .dest("port")
                .type(Integer.class)
                .required(false)
                .setDefault(6379)
                .help("The port of the Redis server to connect to");

        subparser.addArgument("--password")
                .dest("password")
                .type(String.class)
                .required(false)
                .setDefault("")
                .help("The password of the Redis server to connect to");
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        JedisPool jedisPool;
        String password = (String)namespace.get("password");

        System.out.println("Using Redis at " + (String)namespace.get("host") + ":" + namespace.get("port"));
        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(),
                    (String)namespace.get("host"), namespace.get("port"), 2000, (String)namespace.get("password"));
        } else {
            jedisPool = new JedisPool((String)namespace.get("host"), namespace.get("port"));
        }

        DataLoader loader = new DataLoader(jedisPool);
        Boolean flush = namespace.get("flush");
        if (flush) {
            loader.flush();
        }
        loader.load();
        SampleDataGenerator generator = new SampleDataGenerator(jedisPool);
        generator.generateHistorical(1);
        System.out.println("Data load complete!");
        System.exit(0);
    }
}
