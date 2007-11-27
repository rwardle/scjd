/*
 * ClientApplicationFactory.java
 *
 * 21 Aug 2007 
 */

package suncertify;

import suncertify.service.RmiServiceImpl;

/**
 * Creates client applications.
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
     * This implementation creates a client application.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Application createApplication(Configuration configuration) {
        return new ClientApplication(configuration, new RmiServiceImpl());
    }
}
