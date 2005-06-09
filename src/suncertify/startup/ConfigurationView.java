/*
 * ConfigurationView.java
 *
 * Created on 06-Jun-2005
 */


package suncertify.startup;

import java.awt.event.ActionListener;

/**
 * @author Richard Wardle
 */
public interface ConfigurationView {

    void initialiseComponents();
    
    /**
     * 
     * 
     * @param listener
     */
    void addCancelButtonListener(ActionListener listener);
    
    /**
     * @param listener
     */
    void addOkButtonListener(ActionListener listener);
    
    /**
     * 
     */
    void close();
    
    /**
     * @return
     */
    String getDatabaseFilePath();

    /**
     * @return
     */
    String getServerAddress();
    
    /**
     * @return
     */
    String getServerPort();

    /**
     * 
     */
    void packAndShow();
    
    /**
     * @param databaseFilePath
     */
    void setDatabaseFilePath(String databaseFilePath);

    /**
     * @param serverAddress
     */
    void setServerAddress(String serverAddress);
    
    /**
     * @param serverPort
     */
    void setServerPort(String serverPort);
}
