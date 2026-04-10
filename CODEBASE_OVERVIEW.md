# 🧐 Hotel Management System: Comprehensive Codebase Deep Dive

This document provides an exhaustive walkthrough of the internal logic, architecture, and file responsibilities of the Hotel Management System. It is designed to help anyone understand the "how" and "why" behind every design choice.

---

## 🎨 Design Philosophy & UX
The application is built with a **Premium Dark Glassmorphism** aesthetic. 
- **Visuals**: Uses translucency (`rgba` colors in CSS), backdrop filters, and vibrant accent colors (`#38bdf8`) to create a state-of-the-art feel.
- **Interactivity**: Every button has hover effects, and the system uses **Material SVG Icons** across all tabs for a modern, intuitive layout.
- **Responsiveness**: The UI uses `GridPane` and `VBox/HBox` layouts to ensure elements scale elegantly across different screen sizes.

---

## 🏗 High-Level Architecture
The project follows a robust **Model-View-Controller (MVC)** architectural pattern:
- **Models** (`com.nityant.hotel`): Handle data structures and core business logic (e.g., availability checks).
- **View/Controller** (`MainApp.java`): Manages the JavaFX stage, tab construction, and event listeners.
- **Persistence** (`HotelManagement.java`): A static utility layer facilitating file-based data storage.
- **Service Layer**: Specialized tasks like background room cleaning are handled by dedicated threads (`CleaningThread.java`).

---

## 📄 Detailed File Breakdown

### 1. `MainApp.java` (The Orchestrator)
This is the heart of the application. It initializes the UI and coordinates data flow between models.
- **Icon System**: Uses a `createSVG(String path)` helper to inject Material Design icons into buttons and tabs using `SVGPath`.
- **Reactive State**: Uses `ObservableList` and `HashMap` (e.g., `roomMap`) to ensure that any data change is instantly reflected in the UI without manual refreshes.
- **Tabbed Interface**:
    - **Dashboard**: Aggregates real-time stats (Revenue, Occupancy) using Java Streams.
    - **Booking**: Features a sophisticated logic gate that validates dates, room availability, and unique Customer IDs simultaneously.
    - **Services**: A dedicated bridge to add extra charges to active guest accounts dynamically.
    - **Ledger**: A historical record viewer that allows re-previewing past invoices.
- **Helper Utilities**: Contains condensed methods like `glass()`, `grid()`, and `table()` to keep the UI building code clean and readable.

### 2. `Room.java` & Subclasses (Logic Engine)
The room management system is designed for **Non-Destructive Booking**.
- **`BookingPeriod`**: An inner class that tracks specific check-in/out ranges.
- **Multi-Booking Support**: Unlike simple systems that use a boolean flag, this system stores a `List<BookingPeriod>`. This allows a room to be "Available" today but "Booked" for next week.
- **Polymorphism**: `DeluxeRoom.java` and `SuiteRoom.java` extend the base `Room` class, adding specific perks (Balcony, Jacuzzi) while being handled uniformly by the `MainApp`'s `TableView`.
- **`RoomType.java`**: An Enum that centralizes pricing and type definitions, making it the single source of truth for rates.

### 3. `Customer.java` (Data Container)
Stores guest metadata and ongoing charges.
- **Service Tracking**: Uses a `HashMap<String, Integer>` to track the quantity of various services (Laundry, Massage, etc.) used during the stay.
- **Temporal Data**: Stores `LocalDate` objects for check-in/out, which are used by the billing engine to calculate the stay duration.

### 4. `Bill.java` (The Financial Engine)
Responsible for generating professional, itemized invoices.
- **Auto-Parsing**: Detects prices embedded in service strings (e.g., "Massage - ₹2000") using specialized string parsing logic.
- **Taxation Logic**: Automatically applies **GST (12%)** and **Service Charge (5%)** to the subtotal.
- **Formatting**: The `getFormattedBill()` method produces a pixel-perfect, ASCII-styled invoice ready for display in a `TextArea`.

### 5. `HotelManagement.java` (The Data Vault)
Handles **Object Serialization** for permanent storage.
- **Storage Strategy**: Saves data into `.dat` files (`rooms.dat`, `customers.dat`, `bills.dat`).
- **Binary Format**: By saving objects directly, the system maintains complex internal states (like the list of `BookingPeriod` objects) without needing a SQL database.

### 6. `CleaningThread.java` (Background Task)
Demonstrates multi-threading in a professional context.
- **Simulation**: When a guest checks out, this thread "cleans" the room. It sleeps for 3 seconds to simulate work.
- **UI Safety**: Uses `Platform.runLater()` to ensure that once cleaning is done, the UI update (marking the room available) happens on the JavaFX Application Thread, preventing crashes.

### 7. `styles.css` (The Visual Identity)
A comprehensive stylesheet that defines the glassmorphism design system.
- **Key Classes**:
    - `.glass-panel`: Applies translucency and rounded corners.
    - `.stat-card`: Styles the dashboard cards with distinct backgrounds and typography.
    - `.action-btn` / `.danger-btn`: Standardized button styles with smooth transitions.

---

## 🔄 Core Workflows (Logic Flow)

### 1. Booking a Room
1. User selects dates and room.
2. `MainApp` calls `r.isAvailableFor(in, out)`.
3. `Room` checks its internal `bookings` list for any overlapping periods.
4. If clear, a new `BookingPeriod` is added to the `Room`.
5. A `Customer` object is created and linked to the room number.
6. `HotelManagement.saveXXX()` persists the new state.

### 2. Checking Out
1. `Bill` object is instantiated using the `Customer` and `Room` data.
2. `Bill.calculate()` computes the total stay cost + meals + services + taxes.
3. The invoice is displayed, and the `Bill` is moved to the permanent `Ledger`.
4. `Customer` is removed from memory.
5. **Background Cleaning**: `CleaningThread` is triggered. After 3 seconds, it calls `r.removeBooking(in, out)`, freeing up that specific slot for future guests.

---

## 🛠 Technology Stack & Skills Demonstrated
- **JavaFX**: Advanced UI components, CSS styling, and property binding.
- **Java 8+ Streams**: High-performance data filtering and aggregation in the Dashboard.
- **Multithreading**: Asynchronous background tasks for room maintenance.
- **Serialization**: Efficient file-based persistence for complex objects.
- **OOP Excellence**: Deep use of Inheritance, Encapsulation, and Polymorphism.
