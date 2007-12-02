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
 * Implementation of {@link BrokerService} that uses a {@link Database} object
 * to obtain and store contractor data.
 * 
 * @author Richard Wardle
 */
public final class BrokerServiceImpl implements BrokerService {

    private static final Logger LOGGER = Logger
            .getLogger(BrokerServiceImpl.class.getName());
    private static final int CUSTOMER_ID_MAXIMUM_LENGTH = 8;

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
        if (customerId.length() > CUSTOMER_ID_MAXIMUM_LENGTH) {
            throw new IllegalArgumentException(
                    "customerId cannot be longer than "
                            + CUSTOMER_ID_MAXIMUM_LENGTH + " characters");
        }
        if (contractor == null) {
            throw new IllegalArgumentException("contractor cannot be null");
        }
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
            // TODO This shouldn't be possible since we've got the lock on the
            // record??
            throw new ContractorDeletedException(e);
        } finally {
            unlockRecord(recNo);
        }
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
            // TODO Set cause?
            throw new IOException(
                    "Thread was interrupted while waiting for the lock");
        }
    }

    private void validateRecord(int recNo, Contractor contractor)
            throws ContractorModifiedException, RecordNotFoundException,
            IOException {
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
         * an empty string then that implies that the record has been unbooked.
         * Allow the booking to continue in this case.
         */
        return modified
                || !data[ServiceConstants.OWNER_FIELD_INDEX].equals("")
                && !data[ServiceConstants.OWNER_FIELD_INDEX].equals(contractor
                        .getOwner());
    }

    private void updateRecord(int recNo, String customerId)
            throws RecordNotFoundException, IOException {
        // Only need to update the owner
        String[] updateData = new String[ServiceConstants.FIELD_COUNT];
        updateData[ServiceConstants.OWNER_FIELD_INDEX] = customerId;
        database.update(recNo, updateData);
    }

    private void unlockRecord(int recNo) {
        try {
            database.unlock(recNo);
        } catch (RecordNotFoundException e) {
            // TODO This shouldn't be possible since we've got the lock on
            // the record??
            LOGGER.warning(e.getMessage());
        }
    }
}
