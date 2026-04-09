package com.nityant.hotel;

public enum RoomType {
    STANDARD(2000),
    DELUXE(4500),
    SUITE(9000);

    private final double pricePerNight;

    RoomType(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public double calculateCost(int nights) {
        return pricePerNight * nights;
    }
}