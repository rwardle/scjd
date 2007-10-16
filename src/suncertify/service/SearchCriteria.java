/*
 * SearchCriteria.java
 *
 * 16 Oct 2007
 */

package suncertify.service;

public class SearchCriteria {

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
}
