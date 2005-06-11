/*
 * BrokerServiceLocator.java
 *
 * Created on 11 June 2005
 */


package suncertify.service;

import java.rmi.Naming;

import suncertify.startup.Application;
import suncertify.startup.ApplicationMode;
import suncertify.startup.Configuration;


/**
 * A service locator for getting instances of {link BrokerService} based on the
 * application mode and configuration.
 *
 * @author Richard Wardle
 */
public final class BrokerServiceLocator {

    // TODO: Change this so that you can load in your own instance of
    // BrokerService and than pass the locator into the main frame instead?

    private BrokerServiceLocator() {
        // No instantiation allowed
    }

    /**
     * Gets a BrokerService based on the application mode and configuration.
     *
     * @param mode The application mode, one of {@link ApplicationMode#CLIENT}
     * or {@link ApplicationMode#STANDALONE}.
     * @param configuration The configuration.
     * @throws UnsupportedOperationException If the mode arguments is
     * {@link ApplicationMode#SERVER}.
     * @return The broker service.
     */
    public static BrokerService getBrokerService(ApplicationMode mode,
            Configuration configuration) {
        if (configuration == null) {
            throw new NullPointerException(
                    "configuration argument must be non-null");
        }

        BrokerService service = null;
        if (mode == ApplicationMode.STANDALONE) {
            service = new BrokerServiceImpl(configuration
                    .getDatabaseFilePath());
        } else if (mode == ApplicationMode.CLIENT) {
            try {
                service = (BrokerService) Naming.lookup("//"
                        + configuration.getServerAddress() + ":"
                        + configuration.getServerPort() + "/"
                        + Application.REMOTE_BROKER_SERVICE_NAME);
            } catch (Exception e) {
                // TODO: Handle these RMI exceptions properly - error dialog?
                throw new RuntimeException(e);
            }
        } else if (mode == ApplicationMode.SERVER) {
            throw new UnsupportedOperationException("Unsupported mode: '" + mode
                    + "'");
        } else {
            assert false : mode;
        }

        return service;
    }
}
