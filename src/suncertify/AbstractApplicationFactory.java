/*
 * AbstractApplicationFactory.java
 *
 * 20 Aug 2007 
 */
package suncertify;

/**
 * 
 * @author Richard Wardle
 */
public abstract class AbstractApplicationFactory {

    /**
     * Gets the application factory for the supplied application mode.
     * 
     * @param applicationMode
     *                The application mode.
     * @throws IllegalArgumentException
     *                 If the application mode is <code>null</code>.
     */
    public static AbstractApplicationFactory getApplicationFactory(
            ApplicationMode applicationMode) {
        if (applicationMode == null) {
            throw new IllegalArgumentException("applicationMode cannot be null");
        }

        AbstractApplicationFactory applicationFactory = null;
        switch (applicationMode) {
        case CLIENT:
            applicationFactory = new ClientApplicationFactory();
            break;
        case SERVER:
            applicationFactory = new ServerApplicationFactory();
            break;
        case STANDALONE:
            applicationFactory = new StandaloneApplicationFactory();
            break;
        default:
            assert false : applicationMode;
            break;
        }
        return applicationFactory;
    }

    /**
     * Creates the application using the supplied configuration.
     * 
     * @param configuration
     *                The application configuration.
     * @return The application.
     */
    public abstract Application createApplication(Configuration configuration);
}
