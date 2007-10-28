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
    private static final int CUSTOMER_ID_MAXIMUM_LENGTH = 8;
    private static final int DATABASE_FIELD_COUNT = 6;
    private static final int NAME_INDEX = 0;
    private static final int LOCATION_INDEX = 1;
    private static final int SPECIALTIES_INDEX = 2;
    private static final int SIZE_INDEX = 3;
    private static final int RATE_INDEX = 4;
    private static final int OWNER_INDEX = 5;

    private final Database database;

    public BrokerServiceImpl(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("database cannot be null");
        }
        this.database = database;
    }

    public List<Contractor> search(SearchCriteria searchCriteria)
            throws IOException {
        if (searchCriteria == null) {
            throw new IllegalArgumentException("searchCriteria cannot be null");
        }

        String[] findCriteria = searchCriteria.toArray();
        int[] recNos = this.database.find(findCriteria);

        List<Contractor> contractors = new ArrayList<Contractor>();
        for (int recNo : recNos) {
            try {
                String[] data = this.database.read(recNo);
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
            this.database.lock(recNo);
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
        String[] data = this.database.read(recNo);
        if (isModified(contractor, data)) {
            throw new ContractorModifiedException("Contractor has been modifed");
        }
    }

    private boolean isModified(Contractor contractor, String[] data) {
        boolean modified = !data[NAME_INDEX].equals(contractor.getName())
                || !data[LOCATION_INDEX].equals(contractor.getLocation())
                || !data[SPECIALTIES_INDEX].equals(contractor.getSpecialties())
                || !data[SIZE_INDEX].equals(contractor.getSize())
                || !data[RATE_INDEX].equals(contractor.getRate());

        /*
         * If the only field that does not match is the owner and the owner is
         * an empty string then that implies that the record has been unbooked.
         * Allow the booking to continue in this case.
         */
        return modified || !data[OWNER_INDEX].equals("")
                && !data[OWNER_INDEX].equals(contractor.getOwner());
    }

    private void updateRecord(int recNo, String customerId)
            throws RecordNotFoundException, IOException {
        // Only need to update the owner
        String[] updateData = new String[DATABASE_FIELD_COUNT];
        updateData[OWNER_INDEX] = customerId;
        this.database.update(recNo, updateData);
    }

    private void unlockRecord(int recNo) {
        try {
            this.database.unlock(recNo);
        } catch (RecordNotFoundException e) {
            // TODO This shouldn't be possible since we've got the lock on
            // the record??
            LOGGER.warning(e.getMessage());
        }
    }
}
