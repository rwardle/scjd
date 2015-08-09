/*
 * SearchCriteria.java
 *
 * 16 Oct 2007
 */

package suncertify.service;

import java.io.Serializable;

/**
 * Criteria for searching for contractors. All <code>setXXX</code> methods of this class return
 * <code>this</code> to enable use of the builder pattern when creating <code>SearchCriteria</code>
 * objects.
 *
 * @author Richard Wardle
 */
public final class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Contractor name criteria.
     *
     * @serial
     */
    private String name;

    /**
     * Contractor location criteria.
     *
     * @serial
     */
    private String location;

    /**
     * Contractor specialties criteria.
     *
     * @serial
     */
    private String specialties;

    /**
     * Contractor size criteria.
     *
     * @serial
     */
    private String size;

    /**
     * Contractor rate criteria.
     *
     * @serial
     */
    private String rate;

    /**
     * Contractor owner criteria.
     *
     * @serial
     */
    private String owner;

    /**
     * Creates a new instance of <code>SearchCriteria</code> with all criteria fields initialised to
     * <code>null</code>.
     */
    public SearchCriteria() {
        super();
    }

    /**
     * Returns the contractor name criteria.
     *
     * @return The name criteria.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the contractor name criteria.
     *
     * @param name Name criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the contractor location criteria.
     *
     * @return The location criteria.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the contractor location criteria.
     *
     * @param location Location criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setLocation(String location) {
        this.location = location;
        return this;
    }

    /**
     * Returns the contractor specialties criteria.
     *
     * @return The specialties criteria.
     */
    public String getSpecialties() {
        return specialties;
    }

    /**
     * Sets the contractor specialties criteria.
     *
     * @param specialties Specialties criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setSpecialties(String specialties) {
        this.specialties = specialties;
        return this;
    }

    /**
     * Returns the contractor size criteria.
     *
     * @return The size criteria.
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the contractor size criteria.
     *
     * @param size Size criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setSize(String size) {
        this.size = size;
        return this;
    }

    /**
     * Returns the contractor rate criteria.
     *
     * @return The rate criteria.
     */
    public String getRate() {
        return rate;
    }

    /**
     * Sets the contractor rate criteria.
     *
     * @param rate Rate criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setRate(String rate) {
        this.rate = rate;
        return this;
    }

    /**
     * Returns the contractor owner criteria.
     *
     * @return The owner criteria.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the contractor owner criteria.
     *
     * @param owner Owner criteria.
     * @return This <code>SearchCriteria</code> object.
     */
    public SearchCriteria setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Returns the search criteria as an array of <code>String</code>s.
     *
     * @return An array of length 6 containing the search criteria in the following order: name,
     * location, specialties, size, rate, owner.
     */
    public String[] toArray() {
        return new String[]{name, location, specialties, size, rate, owner};
    }

    /**
     * Returns a string representation of the <code>SearchCriteria</code>.
     *
     * @return A string representation of the <code>SearchCriteria</code>.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(super.toString()).append(": name=").append(name)
                .append(", location=").append(location).append(", specialties=")
                .append(specialties).append(", size=").append(size).append(", rate=").append(rate)
                .append(", owner=").append(owner).toString();
    }
}
