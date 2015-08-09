/*
 * Contractor.java
 *
 * 16 Oct 2007
 */

package suncertify.service;

import java.io.Serializable;

/**
 * A home improvement contractor.
 *
 * @author Richard Wardle
 */
public final class Contractor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Database record number of this contractor.
     *
     * @serial
     */
    private final int recordNumber;

    /**
     * Name of this contractor.
     *
     * @serial
     */
    private final String name;

    /**
     * Locality in which this contractor works.
     *
     * @serial
     */
    private final String location;

    /**
     * Comma separated list of the types of work this contractor can perform.
     *
     * @serial
     */
    private final String specialties;

    /**
     * The number of workers available for this contractor.
     *
     * @serial
     */
    private final String size;

    /**
     * Charge per hour of this contractor.
     *
     * @serial
     */
    private final String rate;

    /**
     * 8-digit ID of the customer who has booked this contractor.
     *
     * @serial
     */
    private final String owner;

    /**
     * Creates a new instance of <code>Contractor</code> with the specfied <code>recordNumber</code>
     * and <code>data</code> array.
     *
     * @param recordNumber Database record number.
     * @param data         Data array. Must be an array of length 6 containing contractor data in the
     *                     following order: name, location, specialties, size, rate, owner.
     * @throws IllegalArgumentException If <code>recordNumber</code> is negative or <code>data</code> is
     *                                  <code>null</code> or the length of <code>data</code> does not match
     *                                  {@link ServiceConstants#FIELD_COUNT}.
     */
    public Contractor(int recordNumber, String[] data) {
        if (recordNumber < 0) {
            throw new IllegalArgumentException("recordNumber must be a positive number");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }

        String[] clonedData = data.clone();
        if (clonedData.length != ServiceConstants.FIELD_COUNT) {
            throw new IllegalArgumentException("data array must be of length "
                    + ServiceConstants.FIELD_COUNT);
        }

        this.recordNumber = recordNumber;
        name = clonedData[ServiceConstants.NAME_FIELD_INDEX];
        location = clonedData[ServiceConstants.LOCATION_FIELD_INDEX];
        specialties = clonedData[ServiceConstants.SPECIALTIES_FIELD_INDEX];
        size = clonedData[ServiceConstants.SIZE_FIELD_INDEX];
        rate = clonedData[ServiceConstants.RATE_FIELD_INDEX];
        owner = clonedData[ServiceConstants.OWNER_FIELD_INDEX];
    }

    /**
     * Returns the database record number of this contractor.
     *
     * @return The record number.
     */
    public int getRecordNumber() {
        return recordNumber;
    }

    /**
     * Returns the name of this contractor.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the locality in which this contractor works.
     *
     * @return The location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the types of work this contractor can perform.
     *
     * @return A comma-separated list of the specialties.
     */
    public String getSpecialties() {
        return specialties;
    }

    /**
     * Returns the number of workers available for this contractor.
     *
     * @return The size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Returns the charge per hour of this contractor.
     *
     * @return The rate.
     */
    public String getRate() {
        return rate;
    }

    /**
     * Returns the ID of the customer who has booked this contractor.
     *
     * @return The 8-digit customer ID.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns a string representation of the <code>Contractor</code>.
     *
     * @return A string representation of the <code>Contractor</code>.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(super.toString()).append(": recordNumber=").append(recordNumber)
                .append(": name=").append(name).append(", location=").append(location)
                .append(", specialties=").append(specialties).append(", size=").append(size)
                .append(", rate=").append(rate).append(", owner=").append(owner).toString();
    }
}
