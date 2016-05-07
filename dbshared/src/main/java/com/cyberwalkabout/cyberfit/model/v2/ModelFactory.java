package com.cyberwalkabout.cyberfit.model.v2;

/**
 * @author Andrii Kovalov
 */
public interface ModelFactory<MODEL, IN> {
    MODEL create(IN in);
}
