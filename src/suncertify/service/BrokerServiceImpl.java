/*
 * BrokerServiceImpl.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import suncertify.db.Database;
import suncertify.db.RecordNotFoundException;

/**
 * 
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    private static final Logger LOGGER = Logger
            .getLogger(BrokerServiceImpl.class.getName());
    private final Database database;

    public BrokerServiceImpl(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("database cannot be null");
        }
        this.database = database;
    }

    /**
     * {@inheritDoc}
     */
    public String getHelloWorld() {
        return "Hello world!";
    }

    // TODO Have a method to return all records or imply it from null criteria
    // (if so - document it)?

    public List<Contractor> search(SearchCriteria searchCriteria)
            throws IOException {
        if (searchCriteria == null) {
            throw new IllegalArgumentException("searchCriteria cannot be null");
        }

        String[] findCriteria = new String[] { searchCriteria.getName(),
                searchCriteria.getLocation(), searchCriteria.getSpecialties(),
                searchCriteria.getSize(), searchCriteria.getRate(),
                searchCriteria.getOwner() };
        int[] recNos = this.database.find(findCriteria);

        List<Contractor> contractors = new ArrayList<Contractor>();
        for (int recNo : recNos) {
            try {
                String[] data = this.database.read(recNo);
                if (isExactMatch(findCriteria, data)) {
                    contractors.add(new Contractor(data));
                }
            } catch (RecordNotFoundException e) {
                LOGGER.warning("Record " + recNo
                        + " not found, excluding from search results.");
            }
        }

        return contractors;
    }

    private boolean isExactMatch(String[] criteria, String[] data) {
        boolean matching = true;
        for (int i = 0; matching && i < data.length; i++) {
            if (criteria[i] != null) {
                matching = criteria[i].equals(data[i]);
            }
        }
        return matching;
    }

    public void book(Contractor contractor) {
        // TODO Check fields passed in match what is currently in the database
        // before doing the booking
    }
}
