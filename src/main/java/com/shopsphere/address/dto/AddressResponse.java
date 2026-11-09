package com.shopsphere.address.dto;

import java.time.LocalDateTime;

public class AddressResponse {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String landmark;
    private boolean defaultAddress;
    private LocalDateTime createdAt;

    public AddressResponse(Long id, String fullName, String phoneNumber,
            String addressLine, String city, String state,
            String postalCode, String landmark, boolean defaultAddress,
            LocalDateTime createdAt) {

        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.landmark = landmark;
        this.defaultAddress = defaultAddress;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getLandmark() { return landmark; }
    public boolean isDefaultAddress() { return defaultAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}