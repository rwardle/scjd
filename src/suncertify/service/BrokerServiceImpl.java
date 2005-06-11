/*
 * BrokerServiceImpl.java
 *
 * Created on 11 June 2005
 */


package suncertify.service;

import java.util.logging.Logger;


/**
 *
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    // TODO: Make this a singleton?

    private static Logger logger = Logger.getLogger(BrokerServiceImpl.class
            .getName());

    public BrokerServiceImpl(String databaseFilePath) {
        BrokerServiceImpl.logger.info("Database file path: '"
                + databaseFilePath + "'");
    }

    public String getHelloWorld() {
        return "Hello world!";
    }
}
