package com.airbus.archivemanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Archivemanager.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private static String rootPathInST;
    private static String rootPathInLT;


    public ApplicationProperties() {
        //empty constructor
    }

    /**
     * <p>Getter for the field <code>root-path-in-st</code>.</p>
     *
     * @return root path in STS.
     */
    public static synchronized String getRootPathInST() {
        return rootPathInST;
    }

    /**
     * <p>Setter for the field <code>root-path-in-st</code>.</p>
     */
    public synchronized void setRootPathInST(String rootPathInST) {
        ApplicationProperties.rootPathInST = rootPathInST;
    }

    /**
     * <p>Getter for the field <code>root-path-in-lt</code>.</p>
     *
     * @return root path in LTS.
     */
    public static synchronized String getRootPathInLT() {
        return rootPathInLT;
    }

    /**
     * <p>Setter for the field <code>root-path-in-lt</code>.</p>
     */
    public synchronized void setRootPathInLT(String rootPathInLT) {
        ApplicationProperties.rootPathInLT = rootPathInLT;
    }
}
