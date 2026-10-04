package com.sahil.storage;

import com.sahil.model.Booking;
import com.sahil.model.Customer;
import com.sahil.model.Room;

import java.util.ArrayList;
import java.util.List;

/*
 * DataStore is a Singleton — there is only one instance of it in the whole app.
 * All controllers talk to this class to get or change data.
 * It keeps everything in memory (ArrayLists) and saves to CSV files whenever data changes.
 */
public class DataStore {

    // Single shared instance
    private static DataStore instance;

    // In-memory data lists
    private List<Room>     rooms     = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Booking>  bookings  = new ArrayList<>();

    // Counters for generating IDs
    private int customerCounter = 1;
    private int bookingCounter  = 1;

    // Private constructor — called only once
    private DataStore() {
        loadAll();
        syncCounters();
        // Add some sample rooms if none exist
        if (rooms.isEmpty()) {
            seedRooms();
        }
    }

    // Get the single instance
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ─── ROOM OPERATIONS ─────────────────────────────────────────────────────────

    public List<Room> getRooms() { return rooms; }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    public boolean addRoom(Room room) {
        // Prevent duplicate room numbers
        for (Room r : rooms) {
            if (r.getRoomNumber() == room.getRoomNumber()) return false;
        }
        rooms.add(room);
        FileHandler.saveRooms(rooms);
        return true;
    }

    // ─── CUSTOMER OPERATIONS ─────────────────────────────────────────────────────

    public List<Customer> getCustomers() { return customers; }

    public Customer addCustomer(String name, String contactNumber) {
        String id = "C" + String.format("%03d", customerCounter++);
        Customer customer = new Customer(id, name, contactNumber);
        customers.add(customer);
        FileHandler.saveCustomers(customers);
        return customer;
    }

    // ─── BOOKING OPERATIONS ──────────────────────────────────────────────────────

    public List<Booking> getBookings() { return bookings; }

    public List<Booking> getActiveBookings() {
        List<Booking> active = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.BookingStatus.ACTIVE) active.add(b);
        }
        return active;
    }

    public Booking createBooking(Customer customer, Room room, int numberOfDays) {
        String id = "B" + String.format("%03d", bookingCounter++);
        Booking booking = new Booking(id, customer, room, numberOfDays);
        room.setStatus(Room.RoomStatus.OCCUPIED);
        bookings.add(booking);
        saveAll();
        return booking;
    }

    public boolean checkout(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)
                    && b.getStatus() == Booking.BookingStatus.ACTIVE) {
                b.setStatus(Booking.BookingStatus.CHECKED_OUT);
                b.getRoom().setStatus(Room.RoomStatus.AVAILABLE);
                saveAll();
                return true;
            }
        }
        return false;
    }

    // ─── PERSISTENCE ─────────────────────────────────────────────────────────────

    public void saveAll() {
        FileHandler.saveRooms(rooms);
        FileHandler.saveCustomers(customers);
        FileHandler.saveBookings(bookings);
    }

    private void loadAll() {
        rooms     = FileHandler.loadRooms();
        customers = FileHandler.loadCustomers();
        bookings  = FileHandler.loadBookings(customers, rooms);
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────────

    private void seedRooms() {
        rooms.add(new Room(101, Room.RoomType.SINGLE, 1000));
        rooms.add(new Room(102, Room.RoomType.SINGLE, 1000));
        rooms.add(new Room(201, Room.RoomType.DOUBLE, 1800));
        rooms.add(new Room(202, Room.RoomType.DOUBLE, 1800));
        rooms.add(new Room(301, Room.RoomType.DELUXE, 3000));
        rooms.add(new Room(302, Room.RoomType.DELUXE, 3000));
        FileHandler.saveRooms(rooms);
    }

    private void syncCounters() {
        for (Customer c : customers) {
            try {
                int num = Integer.parseInt(c.getCustomerId().substring(1));
                if (num >= customerCounter) customerCounter = num + 1;
            } catch (NumberFormatException ignored) {}
        }
        for (Booking b : bookings) {
            try {
                int num = Integer.parseInt(b.getBookingId().substring(1));
                if (num >= bookingCounter) bookingCounter = num + 1;
            } catch (NumberFormatException ignored) {}
        }
    }
}
