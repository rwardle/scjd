/*
 * BrokerServiceImpl.java
 *
 * Created on 11 June 2005
 */

package suncertify.service;

import java.util.logging.Logger;

import suncertify.db.DBMain;

/**
 *
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    // TODO: Make this a singleton?

    private static Logger logger = Logger.getLogger(BrokerServiceImpl.class
            .getName());
    
    private DBMain data;

    public BrokerServiceImpl(DBMain data) {
        // TODO: Check for null
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    public String getHelloWorld() {
        return "Hello world!";
    }
}
