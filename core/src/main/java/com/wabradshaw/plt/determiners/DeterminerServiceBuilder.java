package com.wabradshaw.plt.determiners;

/**
 * <p>
 * A DeterminerServiceBuilder allows you to create {@link DeterminerService}s. The created instance can then be used
 * to manage determiners, e.g. choosing whether a word should use "a" or "an".
 * </p>
 * <p>
 * The DeterminerServiceBuilder provides all of the options needed to configure the {@link DeterminerService}. Once
 * complete, the service can be built using the {@link #build()} method.
 * </p>
 * <p>
 * All configuration options are optional, sensible defaults will be used if they are not set.
 * </p>
 * <p>
 * Configuration methods are fluent, making it easy to chain configuration together. These chjaings
 * </p>
 */
public class DeterminerServiceBuilder {

    /**
     * Produces a {@link DeterminerService} using all of the configuration options that have been set so far.
     *
     * @return A {@link DeterminerService} using the supplied configuration options.
     */
    public DeterminerService build(){
        return null;
    }
}
