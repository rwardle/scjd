/*
 * StandaloneApplicationFactory.java
 *
 * 21 Aug 2007 
 */

package suncertify;

import suncertify.db.DatabaseFactoryImpl;

/**
 * Creates standalone applications.
 * 
 * @author Richard Wardle
 */
public final class StandaloneApplicationFactory extends
        AbstractApplicationFactory {

    /**
     * Creates a new instance of <code>StandaloneApplicationFactory</code>.
     */
    public StandaloneApplicationFactory() {
        super();
    }

    /**
     * This implementation creates a standalone application.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new StandaloneApplication(configuration,
                new DatabaseFactoryImpl());
    }
}
