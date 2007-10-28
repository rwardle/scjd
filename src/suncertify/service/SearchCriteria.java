/*
 * java
 *
 * 16 Oct 2007
 */

package suncertify.service;

import java.io.Serializable;

public class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String location;
    private String specialties;
    private String size;
    private String rate;
    private String owner;

    public String getName() {
        return this.name;
    }

    public SearchCriteria setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return this.location;
    }

    public SearchCriteria setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getSpecialties() {
        return this.specialties;
    }

    public SearchCriteria setSpecialties(String specialties) {
        this.specialties = specialties;
        return this;
    }

    public String getSize() {
        return this.size;
    }

    public SearchCriteria setSize(String size) {
        this.size = size;
        return this;
    }

    public String getRate() {
        return this.rate;
    }

    public SearchCriteria setRate(String rate) {
        this.rate = rate;
        return this;
    }

    public String getOwner() {
        return this.owner;
    }

    public SearchCriteria setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String[] toArray() {
        return new String[] { this.name, this.location, this.specialties,
                this.size, this.rate, this.owner };
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SearchCriteria)) {
            return false;
        }
        SearchCriteria criteria = (SearchCriteria) obj;
        return this.name == null ? criteria.name == null
                : this.name.equals(criteria.name) && this.location == null ? criteria.location == null
                        : this.location.equals(criteria.location)
                                && this.specialties == null ? criteria.specialties == null
                                : this.specialties.equals(criteria.specialties)
                                        && this.size == null ? criteria.size == null
                                        : this.size.equals(criteria.size)
                                                && this.rate == null ? criteria.rate == null
                                                : this.rate
                                                        .equals(criteria.rate)
                                                        && this.owner == null ? criteria.owner == null
                                                        : this.owner
                                                                .equals(criteria.owner);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 37 * result
                + (this.location == null ? 0 : this.location.hashCode());
        result = 37 * result
                + (this.specialties == null ? 0 : this.specialties.hashCode());
        result = 37 * result + (this.size == null ? 0 : this.size.hashCode());
        result = 37 * result + (this.rate == null ? 0 : this.rate.hashCode());
        result = 37 * result + (this.owner == null ? 0 : this.owner.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(super.toString()).append(": name=").append(
                this.name).append(", location=").append(this.location).append(
                ", specialties=").append(this.specialties).append(", size=")
                .append(this.size).append(", rate=").append(this.rate).append(
                        ", owner=").append(this.owner).toString();
    }
}
