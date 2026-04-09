# 🏨 Nityant's Hotel Management System

![Project Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)
![Built with JavaFX](https://img.shields.io/badge/Built%20with-JavaFX-blue?style=for-the-badge&logo=java)
![Style](https://img.shields.io/badge/UI-Glassmorphism-purple?style=for-the-badge)

A premium, modern **Hotel Management System** built with **JavaFX** for the OSDL project. This application features a stunning **Dark Glassmorphism** interface and handles everything from room management and booking to service requests and automated billing.

---

## ✨ Key Features

- **🏠 Interactive Dashboard**: real-time overview of total rooms, availability, occupancy, and total revenue.
- **🛏 Room Management**: Dynamic addition and removal of rooms (Standard, Deluxe, Suite).
- **📋 Smart Booking**: Conflict-aware booking system with check-in/check-out date validation and meal plan selection.
- **🛎 Guest Services**: On-demand service booking (Massage, Laundry, Cab, etc.) during guest stay.
- **👤 Guest Management**: Track active guests and handle seamless checkouts.
- **📖 Financial Ledger**: Automated invoice generation and history of all past transactions.
- **🧹 Background Cleaning**: Asynchronous room cleaning simulation using Java Multithreading.
- **💾 Data Persistence**: Full state recovery using Java Object Serialization.

---

## 🎨 Design Aesthetics

The application follows a **Modern Dark Theme** with:
- **Glassmorphism Panels**: Translucent background effects with subtle blurs.
- **Vibrant Accents**: Neon blue and purple highlights for a premium feel.
- **Responsive Layouts**: Uses `GridPane` and `VBox` for clean, professional form alignment.
- **Interactive Tables**: Real-time updates for guest and room statuses.

---

## 🛠 Tech Stack

- **Language**: Java 21
- **Framework**: JavaFX 21
- **Build Tool**: Maven
- **Persistence**: File-based Serialization (.dat files)
- **Concurrency**: Java Threads (for room cleaning simulation)

---

## 📂 Project Structure

```text
Hotel_Management_System/
├── src/main/java/com/nityant/hotel/
│   ├── MainApp.java           # Entry point & JavaFX UI Logic
│   ├── HotelManagement.java   # Data Persistence & File I/O
│   ├── Room.java              # Base Room Model
│   ├── DeluxeRoom.java        # Deluxe Room Specialty
│   ├── SuiteRoom.java         # Suite Room Specialty
│   ├── Customer.java          # Guest Model
│   ├── Bill.java              # Billing & Invoice Logic
│   ├── RoomType.java          # Room Category Enums
│   └── CleaningThread.java    # Background Multithreading
└── src/main/resources/
    └── styles.css             # Glassmorphism Styling
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 or higher
- Maven installed

### Running the Application
1. Clone the repository.
2. Navigate to the project directory.
3. Run the following command:
   ```bash
   mvn javafx:run
   ```

---

## 📝 OSDL Project Details
- **Project Name**: Hotel Management System
- **Developer**: Nityant
- **Course**: OSDL (Open Source Development Lab)
- **Objective**: To demonstrate JavaFX UI design, OOP principles, Multithreading, and File I/O.

---

*Made with ❤️ by Nityant*
