package com.cyberwalkabout.cyberfit.config;

/**
 * Generic configuration reader exception
 *
 * @author Andrii Kovalov
 */
public class ConfigReaderException extends Exception {

    public ConfigReaderException() {
    }

    public ConfigReaderException(String message) {
        super(message);
    }

    public ConfigReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigReaderException(Throwable cause) {
        super(cause);
    }

    public ConfigReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
