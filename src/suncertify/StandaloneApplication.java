/*
 * StandaloneApplication.java
 *
 * Created on 05-Jul-2005
 */

package suncertify;

import suncertify.db.Data;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerService;
import suncertify.service.BrokerServiceImpl;

/**
 * The standalone mode application.
 *
 * @author Richard Wardle
 */
public final class StandaloneApplication extends AbstractGuiApplication {

    /**
     * Creates a new instance of <code>StandaloneApplication</code>.
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public StandaloneApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BrokerService getBrokerService() {
        // TODO: If BrokerServiceImpl not singleton should we do something
        // here to prevent multiple instances?
        return new BrokerServiceImpl(
                new Data(getConfiguration().getDatabaseFilePath()));
    }
}
