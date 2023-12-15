package yar.sam.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Venue {
    private int id;
    private String name;

    @JsonProperty("address_id")
    private int addressId;
    private Address address;
    private List<Account> managers;
    @JsonProperty("manager_ids")
    private List<Integer> managerIds;
    @JsonProperty("venue_sections")
    private List<VenueSection> venueSections;


    public int getAddressId() {
        return addressId;
    }
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public List<VenueSection> getVenueSections() {
        return venueSections;
    }
    public void setVenueSections(List<VenueSection> venueSections) {
        this.venueSections = venueSections;
    }
    public List<Integer> getManagerIds() {
        return managerIds;
    }
    public void setManagerIds(List<Integer> managerIds) {
        this.managerIds = managerIds;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public List<Account> getManagers() {
        return managers;
    }
    public void setManagers(List<Account> managers) {
        this.managers = managers;
    }
}

