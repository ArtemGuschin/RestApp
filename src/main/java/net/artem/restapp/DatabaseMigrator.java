package net.artem.restapp;

import org.flywaydb.core.Flyway;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseMigrator {
    public static void main(String[] args) throws IOException {
        final String PROPERTIES_FILE = "src/main/resources/flyway.properties";

        Properties properties = new Properties();
        properties.load(new FileInputStream(PROPERTIES_FILE));

        String url = properties.getProperty("flyway.url");
        String username = properties.getProperty("flyway.username");
        String password = properties.getProperty("flyway.password");
        String locations = properties.getProperty("flyway.locations");

        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .locations(locations)
                .load();

        flyway.migrate();
    }
}
