/*
 * ClientApplicationFactory.java
 *
 * 21 Aug 2007 
 */

package suncertify;

import suncertify.service.RmiServiceImpl;

/**
 * Creates applications that run in {@link ApplicationMode#CLIENT CLIENT} mode.
 * 
 * @author Richard Wardle
 */
public final class ClientApplicationFactory extends AbstractApplicationFactory {

    /**
     * Creates a new instance of <code>ClientApplicationFactory</code>.
     */
    public ClientApplicationFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation creates a client application.
     */
    @Override
    public Application createApplication(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }

        return new ClientApplication(configuration, new RmiServiceImpl());
    }
}
