/*
 * ConfigurationViewFactory.java
 *
 * Created on 08 June 2005
 */


package suncertify.startup;


/**
 * A simple parameterised factory for creating instances of
 * {@link ConfigurationView} based on the application mode.
 *
 * @author Richard Wardle
 */
public final class ConfigurationViewFactory {

    private ConfigurationViewFactory() {
    }

    /**
     * Creates a new ConfigurationView based on the application mode.
     *
     * @param mode The application mode.
     * @return The configuration view.
     */
    public static ConfigurationView createConfigurationView(
            ApplicationMode mode) {
        ConfigurationView view = null;

        if (mode == ApplicationMode.CLIENT) {
            view = new ClientConfigurationDialog();
        } else if (mode == ApplicationMode.SERVER) {
            view = new ServerConfigurationDialog();
        } else if (mode == ApplicationMode.STANDALONE) {
            view = new StandaloneConfigurationDialog();
        } else {
            assert false : mode;
        }

        return view;
    }
}
