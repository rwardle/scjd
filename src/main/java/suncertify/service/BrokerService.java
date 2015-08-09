/*
 * BrokerService.java
 *
 * 11 Jun 2007
 */

package suncertify.service;

import java.io.IOException;
import java.util.List;

/**
 * Business-level methods for brokering home improvement contractors.
 *
 * @author Richard Wardle
 */
public interface BrokerService {

    /**
     * Searches for contractors that match the specified criteria exactly.
     *
     * @param searchCriteria Search criteria.
     * @return A list of <code>Contractor</code>s that exactly match the search criteria.
     * @throws IOException              If there is an error executing the search.
     * @throws IllegalArgumentException If <code>searchCriteria</code> is <code>null</code>.
     */
    List<Contractor> search(SearchCriteria searchCriteria) throws IOException;

    /**
     * Books the specified contractor for the customer with the specified ID number. Note that it is
     * possible to overwrite an existing booking - no warning will be given if the contractor data
     * supplied to the method matches the data stored in the database.
     *
     * @param customerId 8-digit ID number of the customer making the booking.
     * @param contractor Contractor to be booked.
     * @throws IOException                 If there is an error making the booking.
     * @throws ContractorDeletedException  If the contractor does not exist anymore.
     * @throws ContractorModifiedException If the current data stored on the contractor does not match the data supplied to
     *                                     this method (likely to be because the contractor has been modified since the
     *                                     client last retrieved the data).
     * @throws IllegalArgumentException    If <code>customerId</code> is <code>null</code>, <code>customerId</code> does not
     *                                     consist of 8 digits, <code>contractor</code> is <code>null</code>, or any of the
     *                                     fields of <code>contractor</code> are <code>null</code>.
     */
    void book(String customerId, Contractor contractor) throws IOException,
            ContractorDeletedException, ContractorModifiedException;
}
