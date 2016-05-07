package com.cyberwalkabout.cyberfit;


import com.cyberwalkabout.cyberfit.config.Config;
import com.cyberwalkabout.cyberfit.config.ConfigReaderException;
import com.cyberwalkabout.cyberfit.config.xml.XMLConfigReader;
import com.cyberwalkabout.cyberfit.db.DBCreator;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.DBSchema;
import com.google.common.io.Closer;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Resource;

@SpringBootApplication
public class DBMaker implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(DBMaker.class);

    private static final String ARG_USAGE = "usage";
    private static final String ARG_DB_PATH = "dbpath";
    private static final String ARG_CONFIG_PATH = "configpath";
    private static final String DB_EXT = ".db";
    private static final String DEFAULT_XML_CONFIG_RESOURCE = "default_config.xml";

    @Resource
    private DBCreator dbCreator;

    public static void main(String[] args) {
        SpringApplication.run(DBMaker.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        PropertySource argsSource = new SimpleCommandLinePropertySource(args);

        if (argsSource.containsProperty(ARG_USAGE)) {
            printUsage();
        } else {
            try {
                Path dbPath = getDBPath(argsSource);

                Config config = loadConfig(argsSource);

                LOG.info("Configuration loaded successfully");

                if (!Files.exists(dbPath.getParent())) {
                    Files.createDirectory(dbPath.getParent());
                }

                dbCreator.createDatabase(config, DBSchema.defaultSchema(), dbPath);

                // TODO: validate DB
                // 1) make sure that every exercise has at least one program assigned

                LOG.info("DB successfully created at '" + dbPath.toAbsolutePath() + "'");
            } catch (Exception e) {
                LOG.error("Unexpected error", e);
            }
        }
    }

    private Path getDBPath(PropertySource argsSource) {
        Path dbPath;
        if (argsSource.containsProperty(ARG_DB_PATH)) {
            dbPath = Paths.get(argsSource.getProperty(ARG_DB_PATH).toString());
        } else {
            dbPath = Paths.get("build/db/" + DBSchema.DEFAULT_NAME + DB_EXT);
        }
        return dbPath;
    }

    private Config loadConfig(PropertySource argsSource) throws IOException, ConfigReaderException {
        InputStream in;

        try (Closer closer = Closer.create()) {
            if (argsSource.containsProperty(ARG_CONFIG_PATH)) {
                String path = argsSource.getProperty(ARG_CONFIG_PATH).toString();
                in = new FileInputStream(path);
                closer.register(in);
            } else {
                URL resource = Resources.getResource(DEFAULT_XML_CONFIG_RESOURCE);
                in = resource.openStream();
                closer.register(in);
            }

            XMLConfigReader configReader = new XMLConfigReader();
            return configReader.readConfig(in);
        }
    }


    // TODO: implemented in app usage instructions
    private void printUsage() {
        LOG.info("Usage: ");
    }
}
