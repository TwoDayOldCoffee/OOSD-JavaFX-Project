package com.sahil.model;

public class Room {

    public enum RoomType { SINGLE, DOUBLE, DELUXE }
    public enum RoomStatus { AVAILABLE, OCCUPIED }

    private int roomNumber;
    private RoomType roomType;
    private double pricePerDay;
    private RoomStatus status;

    public Room(int roomNumber, RoomType roomType, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.status = RoomStatus.AVAILABLE;
    }

    // Getters
    public int getRoomNumber()       { return roomNumber; }
    public RoomType getRoomType()    { return roomType; }
    public double getPricePerDay()   { return pricePerDay; }
    public RoomStatus getStatus()    { return status; }
    public boolean isAvailable()     { return status == RoomStatus.AVAILABLE; }

    // Setters
    public void setStatus(RoomStatus status) { this.status = status; }

    // Convert to CSV line
    public String toCsv() {
        return roomNumber + "," + roomType.name() + "," + pricePerDay + "," + status.name();
    }

    // Create Room from CSV line
    public static Room fromCsv(String line) {
        String[] parts = line.split(",");
        int number = Integer.parseInt(parts[0]);
        RoomType type = RoomType.valueOf(parts[1]);
        double price = Double.parseDouble(parts[2]);
        RoomStatus status = RoomStatus.valueOf(parts[3]);
        Room room = new Room(number, type, price);
        room.setStatus(status);
        return room;
    }

    @Override
    public String toString() {
        return "Room #" + roomNumber + " [" + roomType + "] - Rs." + pricePerDay + "/day - " + status;
    }
}
