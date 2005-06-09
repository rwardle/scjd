/*
 * ConfigurationPresenter.java
 *
 * Created on 06-Jun-2005
 */


package suncertify.startup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Richard Wardle
 */
public class ConfigurationPresenter {

    public static final int RETURN_CANCEL = 0;
    public static final int RETURN_OK = 1;

    private Configuration configuration;
    private int returnStatus;
    private ConfigurationView view;

    /**
     * 
     */
    public ConfigurationPresenter(Configuration configuration,
            ConfigurationView view) {
        this.configuration = configuration;
        this.view = view;
        this.returnStatus = RETURN_CANCEL;
        addOKListener();
        addCancelListener();
        loadViewFromModel();
    }

    /**
     * Gets the returnStatus.
     * 
     * @return Returns the returnStatus.
     */
    public int getReturnStatus() {
        return this.returnStatus;
    }

    private void addCancelListener() {
        this.view.addCancelButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });
    }

    private void addOKListener() {
        this.view.addOkButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });
    }

    private void cancelButtonActionPerformed() {
        this.view.close();
    }

    private void okButtonActionPerformed() {
        this.returnStatus = RETURN_OK;
        saveViewToModel();
        this.view.close();
    }

    private void loadViewFromModel() {
        this.view.setServerAddress(this.configuration.getServerAddress());
        this.view.setServerPort(this.configuration.getServerPort());
        this.view.setDatabaseFilePath(this.configuration.getDatabaseFilePath());
    }
    
    private void saveViewToModel() {
        this.configuration.setServerAddress(this.view.getServerAddress());
        this.configuration.setServerPort(this.view.getServerPort());
        this.configuration.setDatabaseFilePath(this.view.getDatabaseFilePath());
    }
    
    // TODO
    // addWindowListener(new WindowAdapter() {
    //
    // public void windowClosing(WindowEvent event) {
    // closeDialog();
    // }
    // });

}
