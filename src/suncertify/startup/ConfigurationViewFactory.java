/*
 * ConfigurationViewFactory.java
 *
 * Created on 08 June 2005
 */


package suncertify.startup;


/**
 *
 * @author Richard Wardle
 */
public class ConfigurationViewFactory {
    
    /**
     * Creates a new instance of ConfigurationViewFactory 
     */
    private ConfigurationViewFactory() {
    }
    
    public static ConfigurationView createConfigurationDialog(ApplicationMode mode) {
        ConfigurationView view = null;
        
        if (mode == ApplicationMode.CLIENT) {
            view = new ClientConfigurationDialog();
        } else if (mode == ApplicationMode.SERVER) {
            view = new ServerConfigurationDialog();
        } else if (mode == ApplicationMode.STANDALONE) {
            view = new StandaloneConfigurationDialog();
        } else {
            // TODO check this
            assert false;
        }
        
        return view;
    }
}
