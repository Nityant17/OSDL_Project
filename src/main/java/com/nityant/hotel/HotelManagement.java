package com.nityant.hotel;

import java.io.*;
import java.util.ArrayList;

/**
 * com.nityant.hotel.HotelManagement — handles all file I/O (serialization / deserialization).
 * Uses ObjectOutputStream / ObjectInputStream for data persistence.
 */
public class HotelManagement {

    private static final String ROOMS_FILE     = "rooms.dat";
    private static final String CUSTOMERS_FILE = "customers.dat";
    private static final String BILLS_FILE = "bills.dat";
    // ── Rooms ────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public static ArrayList<Room> loadRooms() {
        try (ObjectInputStream ois =
                 new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            return (ArrayList<Room>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveRooms(ArrayList<Room> rooms) {
        try (ObjectOutputStream oos =
                 new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    // ── Customers ─────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public static ArrayList<Customer> loadCustomers() {
        try (ObjectInputStream ois =
                 new ObjectInputStream(new FileInputStream(CUSTOMERS_FILE))) {
            return (ArrayList<Customer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveCustomers(ArrayList<Customer> customers) {
        try (ObjectOutputStream oos =
                 new ObjectOutputStream(new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Bill> loadBills() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            return (ArrayList<Bill>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveBills(ArrayList<Bill> bills) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILLS_FILE))) {
            oos.writeObject(bills);
        } catch (IOException e) {
            System.err.println("Error saving bills: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public static Room findRoom(ArrayList<Room> rooms, int roomNumber) {
        for (Room r : rooms)
            if (r.getRoomNumber() == roomNumber) return r;
        return null;
    }

    public static Customer findCustomer(ArrayList<Customer> customers, int customerId) {
        for (Customer c : customers)
            if (c.getCustomerId() == customerId) return c;
        return null;
    }
}