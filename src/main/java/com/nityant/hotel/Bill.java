package com.nityant.hotel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Bill — generates clean, professional invoice with meals and services included.
 */
public class Bill implements Serializable {
    private static final long serialVersionUID = 6L;

    private static final double GST_RATE = 0.12;
    private static final double SERVICE_CHARGE = 0.05;

    private int customerId;
    private String customerName;
    private int roomNumber;
    private RoomType roomType;
    private int daysStayed;
    private double roomRatePerNight;
    
    private String mealPlan;
    private double mealPrice;
    private HashMap<String, Integer> servicesUsed;
    private double servicesCost;
    
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
        this.mealPlan = customer.getMealPlan();
        this.mealPrice = customer.getMealPrice();
        this.servicesUsed = new HashMap<>(customer.getServicesUsed()); // Copy

        calculate();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.generatedAt = LocalDateTime.now().format(fmt);
    }

    private void calculate() {
        double roomCost = roomRatePerNight * daysStayed;
        double mealsCost = mealPrice * daysStayed;
        
        servicesCost = 0;
        for (Map.Entry<String, Integer> entry : servicesUsed.entrySet()) {
            servicesCost += parseServicePrice(entry.getKey()) * entry.getValue();
        }

        this.subtotal = roomCost + mealsCost + servicesCost;
        this.gstAmount = subtotal * GST_RATE;
        this.serviceCharge = subtotal * SERVICE_CHARGE;
        this.grandTotal = subtotal + gstAmount + serviceCharge;
    }

    private double parseServicePrice(String serviceName) {
        if (serviceName.contains("₹")) {
            try {
                String priceStr = serviceName.substring(serviceName.indexOf("₹") + 1).trim();
                return Double.parseDouble(priceStr);
            } catch (Exception e) { return 0; }
        }
        return 0;
    }

    public String getFormattedBill() {
        String line = "─".repeat(50);
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(line).append("\n")
          .append("          NITYANT'S GRAND HOTEL\n")
          .append("               INVOICE / BILL\n")
          .append(line).append("\n")
          .append(String.format("Bill Date      : %s%n", generatedAt))
          .append(String.format("Customer ID    : %d%n", customerId))
          .append(String.format("Customer Name  : %s%n", customerName))
          .append(String.format("Room Number    : %d%n", roomNumber))
          .append(String.format("Room Type      : %s%n", roomType))
          .append(String.format("Days Stayed    : %d night(s)%n", daysStayed))
          .append(line).append("\n")
          .append(String.format("Room Rate      : ₹%.2f /night%n", roomRatePerNight))
          .append(String.format("Room Subtotal  : ₹%.2f%n", (roomRatePerNight * daysStayed)));
          
        if (mealPrice > 0) {
            sb.append(String.format("Meal Plan      : %s%n", mealPlan))
              .append(String.format("Meals Subtotal : ₹%.2f%n", (mealPrice * daysStayed)));
        }

        if (!servicesUsed.isEmpty()) {
            sb.append("\nServices:\n");
            for (Map.Entry<String, Integer> entry : servicesUsed.entrySet()) {
                sb.append(String.format("  - %-20s x %d%n", entry.getKey(), entry.getValue()));
            }
            sb.append(String.format("Services Total : ₹%.2f%n", servicesCost));
        }

        sb.append(line).append("\n")
          .append(String.format("Subtotal       : ₹%.2f%n", subtotal))
          .append(String.format("GST (12%%)      : ₹%.2f%n", gstAmount))
          .append(String.format("Service (5%%)   : ₹%.2f%n", serviceCharge))
          .append(line).append("\n")
          .append(String.format("GRAND TOTAL    : ₹%.2f%n", grandTotal))
          .append(line).append("\n")
          .append("   Thank you for staying with us!\n")
          .append(line).append("\n");

        return sb.toString();
    }

    public double getGrandTotal() { return grandTotal; }
    public String getCustomerName() { return customerName; }
    public String getGeneratedAt() { return generatedAt; }
}