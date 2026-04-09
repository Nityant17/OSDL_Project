package com.nityant.hotel;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 4L;

    private int customerId;
    private String name;
    private String contact;
    private int roomNumber;
    private int daysStayed;
    private double totalBill;

    public Customer(int customerId, String name, String contact,
                    int roomNumber, int daysStayed, double totalBill) {
        this.customerId  = customerId;
        this.name        = name;
        this.contact     = contact;
        this.roomNumber  = roomNumber;
        this.daysStayed  = daysStayed;
        this.totalBill   = totalBill;
    }

    public int    getCustomerId() { return customerId; }
    public String getName()       { return name; }
    public String getContact()    { return contact; }
    public int    getRoomNumber() { return roomNumber; }
    public int    getDaysStayed() { return daysStayed; }
    public double getTotalBill()  { return totalBill; }

    @Override
    public String toString() {
        return customerId + " | " + name + " | " + contact
                + " | com.nityant.hotel.Room: " + roomNumber + " | Days: " + daysStayed
                + " | com.nityant.hotel.Bill: ₹" + totalBill;
    }
}