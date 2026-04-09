package com.nityant.hotel;

import java.io.Serializable;

public class DeluxeRoom extends Room implements Serializable {
    private static final long serialVersionUID = 2L;

    private boolean wifi;
    private boolean breakfast;

    public DeluxeRoom(int roomNumber, RoomType roomType, double price,
                      boolean wifi, boolean breakfast) {
        super(roomNumber, roomType, price);
        this.wifi = wifi;
        this.breakfast = breakfast;
    }

    public boolean hasWifi()      { return wifi; }
    public boolean hasBreakfast() { return breakfast; }

    public void displayDeluxeFeatures() {
        super.displayRoom();
        System.out.println("  [Deluxe] WiFi: " + wifi + " | Breakfast: " + breakfast);
    }

    @Override
    public String toString() {
        return super.toString() + " | WiFi: " + wifi + " | Breakfast: " + breakfast;
    }
}