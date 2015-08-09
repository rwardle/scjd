/*
 * AbstractApplicationFactory.java
 *
 * 20 Aug 2007 
 */

package suncertify;

/**
 * Abstract base class for application factories which provides a mechanism for choosing the correct
 * factory to use based on the application mode.
 *
 * @author Richard Wardle
 */
public abstract class AbstractApplicationFactory {

    /**
     * Creates an application using the specified configuration.
     *
     * @param configuration Application configuration.
     * @return The application that is created.
     * @throws IllegalArgumentException If <code>configuration</code> is <code>null</code>.
     */
    public abstract Application createApplication(Configuration configuration);

    /**
     * Returns the application factory that will create an application corresponding to the
     * specified application mode.
     *
     * @param applicationMode Application mode.
     * @return The application factory.
     * @throws IllegalArgumentException If <code>applicationMode</code> is <code>null</code>.
     */
    public static AbstractApplicationFactory getApplicationFactory(ApplicationMode applicationMode) {
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
}
