package com.sahil.model;

public class Customer {

    private String customerId;
    private String name;
    private String contactNumber;

    public Customer(String customerId, String name, String contactNumber) {
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    // Getters
    public String getCustomerId()     { return customerId; }
    public String getName()           { return name; }
    public String getContactNumber()  { return contactNumber; }

    // Convert to CSV line
    public String toCsv() {
        return customerId + "," + name + "," + contactNumber;
    }

    // Create Customer from CSV line
    public static Customer fromCsv(String line) {
        String[] parts = line.split(",");
        return new Customer(parts[0], parts[1], parts[2]);
    }

    @Override
    public String toString() {
        return customerId + " - " + name;
    }
}
