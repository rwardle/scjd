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
        // Use lock/"process"/unlock idiom
        return "Hello world!";
    }

    // TODO Check fields passed in match what is currently in the database
    // before doing the booking
    // public void book()

    // TODO Make search method scalable for future enhancements, i.e. don't
    // restrict to name and location only?
}
