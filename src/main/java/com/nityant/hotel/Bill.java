package com.nityant.hotel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bill — generates clean, professional invoice.
 */
public class Bill implements Serializable {
    private static final long serialVersionUID = 5L;

    private static final double GST_RATE = 0.12;
    private static final double SERVICE_CHARGE = 0.05;

    private int customerId;
    private String customerName;
    private int roomNumber;
    private RoomType roomType;
    private int daysStayed;
    private double roomRatePerNight;
    private double subtotal;
    private double gstAmount;
    private double serviceCharge;
    private double grandTotal;
    private String generatedAt;

    public Bill(Customer customer, Room room) {
        this.customerId = customer.getCustomerId();
        this.customerName = customer.getName();
        this.roomNumber = room.getRoomNumber();
        this.roomType = room.getRoomType();
        this.daysStayed = customer.getDaysStayed();
        this.roomRatePerNight = room.getPrice();

        calculate();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.generatedAt = LocalDateTime.now().format(fmt);
    }

    private void calculate() {
        this.subtotal = roomRatePerNight * daysStayed;
        this.gstAmount = subtotal * GST_RATE;
        this.serviceCharge = subtotal * SERVICE_CHARGE;
        this.grandTotal = subtotal + gstAmount + serviceCharge;
    }

    public String getFormattedBill() {
        String line = "─".repeat(50);
        return "\n" + line + "\n" +
                "          NITYANT'S GRAND HOTEL\n" +
                "               INVOICE / BILL\n" +
                line + "\n" +
                String.format("Bill Date      : %s%n", generatedAt) +
                String.format("Customer ID    : %d%n", customerId) +
                String.format("Customer Name  : %s%n", customerName) +
                String.format("Room Number    : %d%n", roomNumber) +
                String.format("Room Type      : %s%n", roomType) +
                String.format("Days Stayed    : %d night(s)%n", daysStayed) +
                line + "\n" +
                String.format("Room Rate      : ₹%.2f /night%n", roomRatePerNight) +
                String.format("Subtotal       : ₹%.2f%n", subtotal) +
                String.format("GST (12%%)      : ₹%.2f%n", gstAmount) +
                String.format("Service (5%%)   : ₹%.2f%n", serviceCharge) +
                line + "\n" +
                String.format("GRAND TOTAL    : ₹%.2f%n", grandTotal) +
                line + "\n" +
                "   Thank you for staying with us!\n" +
                line + "\n";
    }

    // Getters (if needed elsewhere)
    public double getGrandTotal() { return grandTotal; }
}