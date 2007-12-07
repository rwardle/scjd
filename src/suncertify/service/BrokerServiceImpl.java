/*
 * BrokerServiceImpl.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.db.Database;
import suncertify.db.RecordNotFoundException;

/**
 * Implementation of {@link BrokerService} that uses a {@link Database} object
 * to obtain and store contractor data.
 * 
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    private static final Logger LOGGER = Logger
            .getLogger(BrokerServiceImpl.class.getName());
    private static final int CUSTOMER_ID_LENGTH = 8;

    private final Database database;

    /**
     * Creates a new instance of <code>BrokerServiceImpl</code> using the
     * specified database.
     * 
     * @param database
     *                Database of contractors.
     * @throws IllegalArgumentException
     *                 If <code>database</code> is <code>null</code>.
     */
    public BrokerServiceImpl(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("database cannot be null");
        }
        this.database = database;
    }

    /** {@inheritDoc} */
    public List<Contractor> search(SearchCriteria searchCriteria)
            throws IOException {
        if (searchCriteria == null) {
            throw new IllegalArgumentException("searchCriteria cannot be null");
        }

        String[] findCriteria = searchCriteria.toArray();
        int[] recNos = database.find(findCriteria);

        /*
         * Database find does "starts-with" matches, so read each record from
         * the database and only include the ones that still exist and are exact
         * matches.
         */
        List<Contractor> contractors = new ArrayList<Contractor>();
        for (int recNo : recNos) {
            try {
                String[] data = database.read(recNo);
                if (isExactMatch(findCriteria, data)) {
                    contractors.add(new Contractor(recNo, data));
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

    /** {@inheritDoc} */
    public void book(String customerId, Contractor contractor)
            throws IOException, ContractorDeletedException,
            ContractorModifiedException {
        if (customerId == null) {
            throw new IllegalArgumentException("customeId cannot be null");
        }
        if (customerId.length() != CUSTOMER_ID_LENGTH) {
            throw new IllegalArgumentException("customerId must be of length "
                    + CUSTOMER_ID_LENGTH);
        }
        if (!containsOnlyDigits(customerId)) {
            throw new IllegalArgumentException(
                    "customerId must only contain digits");
        }

        if (contractor == null) {
            throw new IllegalArgumentException("contractor cannot be null");
        }

        /*
         * Contractor should not have any null fields - empty record values are
         * represented by an empty string.
         */
        if (hasNullField(contractor)) {
            throw new IllegalArgumentException(
                    "contractor cannot have a null field");
        }

        int recNo = contractor.getRecordNumber();
        lockRecord(recNo);
        try {
            validateRecord(recNo, contractor);
            updateRecord(recNo, customerId);
        } catch (RecordNotFoundException e) {
            throw new ContractorDeletedException(e);
        } finally {
            unlockRecord(recNo);
        }
    }

    private boolean containsOnlyDigits(String customerId) {
        boolean digitsOnly = true;
        for (int i = 0; digitsOnly && i < CUSTOMER_ID_LENGTH; i++) {
            digitsOnly = Character.isDigit(customerId.charAt(i));
        }
        return digitsOnly;
    }

    private boolean hasNullField(Contractor contractor) {
        return contractor.getName() == null || contractor.getLocation() == null
                || contractor.getSpecialties() == null
                || contractor.getSize() == null || contractor.getRate() == null
                || contractor.getOwner() == null;
    }

    private void lockRecord(int recNo) throws ContractorDeletedException,
            IOException {
        try {
            database.lock(recNo);
        } catch (RecordNotFoundException e) {
            throw new ContractorDeletedException(e);
        } catch (InterruptedException e) {
            IOException ioException = new IOException(
                    "Thread was interrupted while waiting for the lock on recNo: "
                            + recNo);
            ioException.initCause(e);
            throw ioException;
        }
    }

    private void validateRecord(int recNo, Contractor contractor)
            throws ContractorModifiedException, RecordNotFoundException,
            IOException {
        // Read the record from the database and check it hasn't been modified
        String[] data = database.read(recNo);
        if (isModified(contractor, data)) {
            throw new ContractorModifiedException("Contractor has been modifed");
        }
    }

    private boolean isModified(Contractor contractor, String[] data) {
        boolean modified = !data[ServiceConstants.NAME_FIELD_INDEX]
                .equals(contractor.getName())
                || !data[ServiceConstants.LOCATION_FIELD_INDEX]
                        .equals(contractor.getLocation())
                || !data[ServiceConstants.SPECIALTIES_FIELD_INDEX]
                        .equals(contractor.getSpecialties())
                || !data[ServiceConstants.SIZE_FIELD_INDEX].equals(contractor
                        .getSize())
                || !data[ServiceConstants.RATE_FIELD_INDEX].equals(contractor
                        .getRate());

        /*
         * If the only field that does not match is the owner and the owner is
         * an empty string then that implies that the record has been unbooked
         * by another user. Allow the booking to continue in this case (i.e.
         * return false from this method).
         */
        boolean ownerFieldEmpty = data[ServiceConstants.OWNER_FIELD_INDEX]
                .equals("");
        boolean ownerFieldModified = !data[ServiceConstants.OWNER_FIELD_INDEX]
                .equals(contractor.getOwner());
        return modified || !ownerFieldEmpty && ownerFieldModified;
    }

    private void updateRecord(int recNo, String customerId)
            throws RecordNotFoundException, IOException {
        /*
         * Only need to update the owner field, all other fields are null to
         * signify that they should not be updated.
         */
        String[] updateData = new String[ServiceConstants.FIELD_COUNT];
        updateData[ServiceConstants.OWNER_FIELD_INDEX] = customerId;
        database.update(recNo, updateData);
    }

    private void unlockRecord(int recNo) {
        try {
            database.unlock(recNo);
        } catch (RecordNotFoundException e) {
            LOGGER.log(Level.WARNING, "Error unlocking recNo: " + recNo, e);
        }
    }
}
