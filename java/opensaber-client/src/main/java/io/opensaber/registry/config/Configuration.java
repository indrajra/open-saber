package io.opensaber.registry.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Configuration {

    private final static String environment =
            System.getenv("OPENSABER_CLIENT_ENVIRONMENT") == null
                    ? "dev"
                    : System.getenv("OPENSABER_CLIENT_ENVIRONMENT");

    private static Configuration instance = new Configuration();
    private Config config;

    public static Configuration getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    private Configuration() {
        this.config = load();
    }

    private Config load() {
        Config config = ConfigFactory.load().getConfig("opensaber-client");
        if (config.hasPath(environment)) {
            return config.getConfig(environment).withFallback(config);
        }
        return config;
    }

    public static final String MAPPING_FILE = Configuration.instance.config.getString("mapping.file");

    public static final String HOST = Configuration.instance.config.getString("registry.service.host");
    public static final Integer PORT = Configuration.instance.config.getInt("registry.service.port");
    public static final String BASE_URL = Configuration.instance.config.getString("registry.service.baseUrl");
}
