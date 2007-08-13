/*
 * BrokerServiceImpl.java
 *
 * 11 June 2007
 */

package suncertify.service;

import suncertify.db.DBMain;

/**
 * 
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    public BrokerServiceImpl(DBMain data) {
    }

    /**
     * {@inheritDoc}
     */
    public String getHelloWorld() {
        return "Hello world!";
    }
}
