package com.nityant.hotel;

import java.io.Serializable;

public class SuiteRoom extends DeluxeRoom implements Serializable {
    private static final long serialVersionUID = 3L;

    private boolean privatePool;
    private boolean personalButler;

    public SuiteRoom(int roomNumber, RoomType roomType, double price,
                     boolean wifi, boolean breakfast,
                     boolean privatePool, boolean personalButler) {
        super(roomNumber, roomType, price, wifi, breakfast);
        this.privatePool   = privatePool;
        this.personalButler = personalButler;
    }

    public boolean hasPrivatePool()    { return privatePool; }
    public boolean hasPersonalButler() { return personalButler; }

    public void displaySuiteFeatures() {
        displayDeluxeFeatures(); // inherited deluxe features
        System.out.println("  [Suite] Private Pool: " + privatePool
                + " | Personal Butler: " + personalButler);
    }

    @Override
    public String toString() {
        return super.toString() + " | Pool: " + privatePool + " | Butler: " + personalButler;
    }
}