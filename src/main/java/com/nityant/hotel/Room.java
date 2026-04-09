package com.nityant.hotel;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private RoomType roomType;
    private double price;
    private boolean isAvailable;

    // Constructor: only room number and type
    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = roomType.getPricePerNight();
        this.isAvailable = true;
    }

    // Constructor: room number, type, and custom price
    public Room(int roomNumber, RoomType roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = true;
    }

    public int getRoomNumber()        { return roomNumber; }
    public RoomType getRoomType()     { return roomType; }
    public double getPrice()          { return price; }
    public boolean isAvailable()      { return isAvailable; }
    public void setAvailable(boolean status) { this.isAvailable = status; }

    public void displayRoom() {
        System.out.println("com.nityant.hotel.Room No: " + roomNumber + " | Type: " + roomType
                + " | Price: ₹" + price + "/night | Available: " + isAvailable);
    }

    @Override
    public String toString() {
        return roomNumber + " | " + roomType + " | ₹" + price + "/night | "
                + (isAvailable ? "Available" : "Booked");
    }
}