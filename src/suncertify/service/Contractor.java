/*
 * Conctractor.java
 *
 * 16 Oct 2007
 */

package suncertify.service;

import java.io.Serializable;

public class Contractor implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int recordNumber;
    private final String name;
    private final String location;
    private final String specialties;
    private final String size;
    private final String rate;
    private final String owner;

    public Contractor(int recordNumber, String[] data) {
        if (recordNumber < 0) {
            throw new IllegalArgumentException(
                    "recordNumber must be a positive number");
        }
        if (data == null || data.length != 6) {
            throw new IllegalArgumentException(
                    "data array must be non-null and of length 6");
        }
        this.recordNumber = recordNumber;
        this.name = data[0];
        this.location = data[1];
        this.specialties = data[2];
        this.size = data[3];
        this.rate = data[4];
        this.owner = data[5];
    }

    public int getRecordNumber() {
        return this.recordNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getSpecialties() {
        return this.specialties;
    }

    public String getSize() {
        return this.size;
    }

    public String getRate() {
        return this.rate;
    }

    public String getOwner() {
        return this.owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contractor)) {
            return false;
        }
        Contractor contractor = (Contractor) obj;
        return this.recordNumber == contractor.recordNumber
                && this.name == null ? contractor.name == null
                : this.name.equals(contractor.name) && this.location == null ? contractor.location == null
                        : this.location.equals(contractor.location)
                                && this.specialties == null ? contractor.specialties == null
                                : this.specialties
                                        .equals(contractor.specialties)
                                        && this.size == null ? contractor.size == null
                                        : this.size.equals(contractor.size)
                                                && this.rate == null ? contractor.rate == null
                                                : this.rate
                                                        .equals(contractor.rate)
                                                        && this.owner == null ? contractor.owner == null
                                                        : this.owner
                                                                .equals(contractor.owner);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.recordNumber;
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
        return builder.append(super.toString()).append(": recordNumber=")
                .append(this.recordNumber).append(": name=").append(this.name)
                .append(", location=").append(this.location).append(
                        ", specialties=").append(this.specialties).append(
                        ", size=").append(this.size).append(", rate=").append(
                        this.rate).append(", owner=").append(this.owner)
                .toString();
    }
}
