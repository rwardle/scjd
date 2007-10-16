/*
 * Conctractor.java
 *
 * 16 Oct 2007
 */

package suncertify.service;

public class Contractor {

    private final String name;
    private final String location;
    private final String specialties;
    private final String size;
    private final String rate;
    private final String owner;

    public Contractor(String[] data) {
        this.name = data[0];
        this.location = data[1];
        this.specialties = data[2];
        this.size = data[3];
        this.rate = data[4];
        this.owner = data[5];
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
}
