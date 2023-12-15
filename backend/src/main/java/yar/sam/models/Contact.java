package yar.sam.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {
    private int id;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private List<Address> addresses;
    @JsonProperty("address_ids")
    private List<Integer> addressIds;
    public List<Integer> getAddressIds() {
        return addressIds;
    }
    public void setAddressIds(List<Integer> addressIds) {
        this.addressIds = addressIds;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public List<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    
}
