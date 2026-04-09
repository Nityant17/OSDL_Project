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

    // Removed displayDeluxeFeatures() and displayRoom() call since we no longer use console display
    public void displayDeluxeFeatures() {
        System.out.println("  [Deluxe] WiFi: " + wifi + " | Breakfast: " + breakfast);
    }

    @Override
    public String toString() {
        return super.toString() + " | WiFi: " + wifi + " | Breakfast: " + breakfast;
    }
}