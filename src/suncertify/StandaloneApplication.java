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
     */
    public StandaloneApplication() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     */
    protected BrokerService getBrokerService(Configuration configuration) {
        // TODO: If BrokerServiceImpl not singleton should we do something
        // here to prevent multiple instances?
        return new BrokerServiceImpl(
                new Data(configuration.getDatabaseFilePath()));
    }
}
