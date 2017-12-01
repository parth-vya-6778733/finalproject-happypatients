package com.sjsu.cs249.Cassandra;

import com.datastax.driver.core.utils.UUIDs;

import java.util.UUID;

public class Patient {

    private UUID id;

    private String firstName;

    private String lastName;

    private String birthDate;

    private String address;

    private String phoneNumber;

    Patient() {
    }

    public Patient(UUID id, String firstName, String lastName, String birthDate, String address, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId() { this.id = UUIDs.timeBased(); }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
