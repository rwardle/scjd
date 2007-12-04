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
     * @param searchCriteria
     *                Search criteria.
     * @return A list of <code>Contractor</code>s that exactly match the
     *         search criteria.
     * @throws IOException
     *                 If there is an error executing the search.
     */
    List<Contractor> search(SearchCriteria searchCriteria) throws IOException;

    /**
     * Books the specified contractor for the customer with the specified ID
     * number.
     * 
     * @param customerId
     *                ID number of the customer making the booking.
     * @param contractor
     *                Contractor to be booked.
     * @throws IOException
     *                 If there is an error making the booking.
     * @throws ContractorDeletedException
     *                 If the contractor does not exist anymore.
     * @throws ContractorModifiedException
     *                 If the current data stored on the contractor does not
     *                 match the data supplied to this method (likely to be
     *                 because the contractor has been modified since the client
     *                 last retrieved the data).
     */
    void book(String customerId, Contractor contractor) throws IOException,
            ContractorDeletedException, ContractorModifiedException;
}
