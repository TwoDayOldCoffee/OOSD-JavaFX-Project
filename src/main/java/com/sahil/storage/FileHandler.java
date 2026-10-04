package com.sahil.storage;

import com.sahil.model.Booking;
import com.sahil.model.Customer;
import com.sahil.model.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * FileHandler handles all reading and writing of data to CSV files.
 * Each model (Room, Customer, Booking) has its own CSV file.
 * Files are stored in a "data/" folder created automatically.
 */
public class FileHandler {

    private static final String ROOMS_FILE    = "data/rooms.csv";
    private static final String CUSTOMERS_FILE = "data/customers.csv";
    private static final String BOOKINGS_FILE  = "data/bookings.csv";

    // ─── Ensure data folder exists ───────────────────────────────────────────────

    private static void ensureDataFolder() {
        new File("data").mkdirs();
    }

    // ─── ROOMS ───────────────────────────────────────────────────────────────────

    public static void saveRooms(List<Room> rooms) {
        ensureDataFolder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) {
                writer.write(room.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return rooms;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    rooms.add(Room.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // ─── CUSTOMERS ───────────────────────────────────────────────────────────────

    public static void saveCustomers(List<Customer> customers) {
        ensureDataFolder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.write(customer.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return customers;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    customers.add(Customer.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return customers;
    }

    // ─── BOOKINGS ────────────────────────────────────────────────────────────────
    // Bookings CSV stores IDs only. When loading, we look up the actual objects
    // from the rooms and customers lists that are passed in.

    public static void saveBookings(List<Booking> bookings) {
        ensureDataFolder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.write(booking.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookings(List<Customer> customers, List<Room> rooms) {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return bookings;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String bookingId   = parts[0];
                String customerId  = parts[1];
                int    roomNumber  = Integer.parseInt(parts[2]);
                int    days        = Integer.parseInt(parts[3]);
                Booking.BookingStatus status = Booking.BookingStatus.valueOf(parts[4]);

                // Find the matching customer and room by ID
                Customer customer = customers.stream()
                        .filter(c -> c.getCustomerId().equals(customerId))
                        .findFirst().orElse(null);
                Room room = rooms.stream()
                        .filter(r -> r.getRoomNumber() == roomNumber)
                        .findFirst().orElse(null);

                if (customer != null && room != null) {
                    Booking booking = new Booking(bookingId, customer, room, days);
                    booking.setStatus(status);
                    bookings.add(booking);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }
}
