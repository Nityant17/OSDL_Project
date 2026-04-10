package com.nityant.hotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

public class Customer implements Serializable {
    private static final long serialVersionUID = 6L; // Incremented version

    private int customerId;
    private String name;
    private String contact;
    private int roomNumber;
    private int daysStayed;
    private double totalBill; 
    private double roomRateAtBooking; 

    private LocalDate checkIn;
    private LocalDate checkOut;

    // New fields for Meals and Services
    private String mealPlan;
    private double mealPrice; // Daily price
    private HashMap<String, Integer> servicesUsed;

    public Customer(int customerId, String name, String contact,
                    int roomNumber, int daysStayed, double totalBill,
                    LocalDate checkIn, LocalDate checkOut,
                    String mealPlan, double mealPrice) {
        this.customerId  = customerId;
        this.name        = name;
        this.contact     = contact;
        this.roomNumber  = roomNumber;
        this.daysStayed  = daysStayed;
        this.totalBill   = totalBill;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
        this.mealPlan    = mealPlan;
        this.mealPrice   = mealPrice;
        this.roomRateAtBooking = 0; // Will be set explicitly
        this.servicesUsed = new HashMap<>();
    }

    public void setRoomRateAtBooking(double rate) { this.roomRateAtBooking = rate; }
    public double getRoomRateAtBooking() { return roomRateAtBooking; }

    public void addService(String serviceName) {
        servicesUsed.put(serviceName, servicesUsed.getOrDefault(serviceName, 0) + 1);
    }

    public int       getCustomerId() { return customerId; }
    public String    getName()       { return name; }
    public String    getContact()    { return contact; }
    public int       getRoomNumber() { return roomNumber; }
    public int       getDaysStayed() { return daysStayed; }
    public double    getTotalBill()  { return totalBill; }
    public LocalDate getCheckIn()    { return checkIn; }
    public LocalDate getCheckOut()   { return checkOut; }
    
    public String    getMealPlan()   { return mealPlan; }
    public double    getMealPrice()  { return mealPrice; }
    public HashMap<String, Integer> getServicesUsed() { return servicesUsed; }
}