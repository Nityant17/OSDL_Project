package com.nityant.hotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private RoomType roomType;
    private double price;
    private List<BookingPeriod> bookings = new ArrayList<>(); // ← Fixed: initialized here

    // Constructors
    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = roomType.getPricePerNight();
    }

    public Room(int roomNumber, RoomType roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    // Check availability for a date range (new logic)
    public boolean isAvailableFor(LocalDate checkIn, LocalDate checkOut) {
        for (BookingPeriod bp : bookings) {
            if (checkIn.isBefore(bp.checkOut) && checkOut.isAfter(bp.checkIn)) {
                return false; // overlap
            }
        }
        return true;
    }

    // Book the room
    public void book(LocalDate checkIn, LocalDate checkOut) {
        bookings.add(new BookingPeriod(checkIn, checkOut));
    }

    // Check if the room is available TODAY
    public boolean isAvailable() {
        return isAvailableFor(LocalDate.now(), LocalDate.now().plusDays(1));
    }

    public void removeBooking(LocalDate in, LocalDate out) {
        bookings.removeIf(p -> p.checkIn.equals(in) && p.checkOut.equals(out));
    }

    @Override
    public String toString() {
        return roomNumber + " | " + roomType + " | ₹" + price + "/night";
    }

    // Inner class for booking periods
    private static class BookingPeriod implements Serializable {
        private final LocalDate checkIn;
        private final LocalDate checkOut;

        public BookingPeriod(LocalDate checkIn, LocalDate checkOut) {
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }
    }

    // Add this to Room.java
    public String getBookingSchedule() {
        if (bookings.isEmpty())
            return "Available";

        StringBuilder sb = new StringBuilder();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMM dd");

        for (int i = 0; i < bookings.size(); i++) {
            sb.append(bookings.get(i).checkIn.format(fmt))
                    .append(" to ")
                    .append(bookings.get(i).checkOut.format(fmt));
            if (i < bookings.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }
}