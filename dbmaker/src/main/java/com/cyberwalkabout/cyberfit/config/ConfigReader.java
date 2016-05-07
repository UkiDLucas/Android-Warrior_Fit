package com.cyberwalkabout.cyberfit.config;

import java.io.InputStream;

/**
 * Generic interfaces which describes operations to retrieve configuration from the source (source can be file, web resource etc...)
 *
 * @author Andrii Kovalov
 */
public interface ConfigReader {
    Config readConfig(InputStream in) throws ConfigReaderException;
}
