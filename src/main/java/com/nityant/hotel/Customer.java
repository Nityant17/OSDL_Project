package com.nityant.hotel;

import java.io.Serializable;
import java.time.LocalDate;

public class Customer implements Serializable {
    private static final long serialVersionUID = 5L; // Incremented version

    private int customerId;
    private String name;
    private String contact;
    private int roomNumber;
    private int daysStayed;
    private double totalBill;

    // New fields
    private LocalDate checkIn;
    private LocalDate checkOut;

    public Customer(int customerId, String name, String contact,
                    int roomNumber, int daysStayed, double totalBill,
                    LocalDate checkIn, LocalDate checkOut) {
        this.customerId  = customerId;
        this.name        = name;
        this.contact     = contact;
        this.roomNumber  = roomNumber;
        this.daysStayed  = daysStayed;
        this.totalBill   = totalBill;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
    }

    public int       getCustomerId() { return customerId; }
    public String    getName()       { return name; }
    public String    getContact()    { return contact; }
    public int       getRoomNumber() { return roomNumber; }
    public int       getDaysStayed() { return daysStayed; }
    public double    getTotalBill()  { return totalBill; }
    public LocalDate getCheckIn()    { return checkIn; }
    public LocalDate getCheckOut()   { return checkOut; }
}