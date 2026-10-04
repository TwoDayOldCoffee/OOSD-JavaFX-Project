package com.sahil.model;

public class Booking {

    public enum BookingStatus { ACTIVE, CHECKED_OUT }

    private String bookingId;
    private Customer customer;
    private Room room;
    private int numberOfDays;
    private BookingStatus status;

    public Booking(String bookingId, Customer customer, Room room, int numberOfDays) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.numberOfDays = numberOfDays;
        this.status = BookingStatus.ACTIVE;
    }

    // Billing calculation
    public double getRoomCharges() {
        return numberOfDays * room.getPricePerDay();
    }

    public double getTax() {
        return getRoomCharges() * 0.18;
    }

    public double getTotalAmount() {
        return getRoomCharges() + getTax();
    }

    // Getters
    public String getBookingId()        { return bookingId; }
    public Customer getCustomer()       { return customer; }
    public Room getRoom()               { return room; }
    public int getNumberOfDays()        { return numberOfDays; }
    public BookingStatus getStatus()    { return status; }

    // Setter
    public void setStatus(BookingStatus status) { this.status = status; }

    // Convert to CSV line — stores IDs only, not full objects
    public String toCsv() {
        return bookingId + "," + customer.getCustomerId() + "," + room.getRoomNumber()
                + "," + numberOfDays + "," + status.name();
    }

    @Override
    public String toString() {
        return bookingId + " | Room #" + room.getRoomNumber()
                + " | " + customer.getName()
                + " | " + numberOfDays + " days"
                + " | Rs." + String.format("%.2f", getTotalAmount());
    }
}
