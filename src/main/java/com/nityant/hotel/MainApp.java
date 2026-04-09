package com.nityant.hotel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.beans.value.ChangeListener;

/**
 * MainApp - JavaFX entry point for Nityant's Hotel Management System.
 * Clean UI with proper labels, professional appearance, and historical billing.
 */
public class MainApp extends Application {

    // Shared data
    private ArrayList<Room> rooms = HotelManagement.loadRooms();
    private ArrayList<Customer> customers = HotelManagement.loadCustomers();
    private ArrayList<Bill> pastBills = HotelManagement.loadBills(); // <-- NEW: Permanent ledger
    private HashMap<Integer, Customer> roomMap = new HashMap<>();

    // Observable lists for TableView
    private ObservableList<Room> roomObs = FXCollections.observableArrayList(rooms);
    private ObservableList<Customer> customerObs = FXCollections.observableArrayList(customers);

    // Dashboard stat labels
    private Label lblTotalRooms, lblAvailable, lblOccupied, lblTotalRevenue;

    @Override
    public void start(Stage stage) {
        // Re-build room map on load
        for (Customer c : customers) {
            roomMap.put(c.getRoomNumber(), c);
        }

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabDash = new Tab("Dashboard", buildDashboard());
        Tab tabRooms = new Tab("Rooms", buildRoomsTab());
        Tab tabBook = new Tab("Booking", buildBookingTab());
        Tab tabCust = new Tab("Customers", buildCustomersTab());
        Tab tabBill = new Tab("Billing", buildBillingTab());

        tabPane.getTabs().addAll(tabDash, tabRooms, tabBook, tabCust, tabBill);

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> refreshDashboard());

        Scene scene = new Scene(tabPane, 1100, 700); // Slightly wider for new columns
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Nityant's Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    // ====================== Dashboard ======================
    private VBox buildDashboard() {
        Label title = new Label("Nityant's Grand Hotel");
        title.getStyleClass().add("heading-label");
        title.setStyle("-fx-font-size: 28px;");

        Label sub = new Label("Management Dashboard - Overview");
        sub.getStyleClass().add("sub-label");

        lblTotalRooms = statNumber("0");
        lblAvailable = statNumber("0");
        lblOccupied = statNumber("0");
        lblTotalRevenue = statNumber("₹0");

        HBox cards = new HBox(20,
                statCard(lblTotalRooms, "Total Rooms"),
                statCard(lblAvailable, "Available"),
                statCard(lblOccupied, "Occupied"),
                statCard(lblTotalRevenue, "Total Revenue")
        );
        cards.setAlignment(Pos.CENTER);

        VBox root = new VBox(25, title, sub, cards);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        refreshDashboard();
        return root;
    }

    private void refreshDashboard() {
        long avail = rooms.stream().filter(Room::isAvailable).count();
        // <-- NEW: Revenue calculated from ALL time historical bills, not just current guests
        double rev = pastBills.stream().mapToDouble(Bill::getGrandTotal).sum();

        if (lblTotalRooms != null) {
            lblTotalRooms.setText(String.valueOf(rooms.size()));
            lblAvailable.setText(String.valueOf(avail));
            lblOccupied.setText(String.valueOf(rooms.size() - avail));
            lblTotalRevenue.setText(String.format("₹%.0f", rev));
        }
    }

    private VBox statCard(Label numLabel, String desc) {
        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("stat-desc");
        VBox card = new VBox(5, numLabel, descLabel);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("stat-card");
        card.setMinWidth(170);
        return card;
    }

    private Label statNumber(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("stat-number");
        return l;
    }

    // ====================== Rooms Tab ======================
    private VBox buildRoomsTab() {
        TextField tfRoomNo = new TextField();
        tfRoomNo.setPromptText("Room Number");
        tfRoomNo.setPrefWidth(150);

        ComboBox<RoomType> cbType = new ComboBox<>(
                FXCollections.observableArrayList(RoomType.values()));
        cbType.setPromptText("Select Room Type");
        cbType.setPrefWidth(200);

        Button btnAdd = new Button("Add Room");
        btnAdd.getStyleClass().add("action-btn");

        Button btnRemove = new Button("Remove Room");
        btnRemove.getStyleClass().add("danger-btn");

        HBox form = new HBox(12, tfRoomNo, cbType, btnAdd, btnRemove);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(0, 0, 10, 0));

        // TableView
        TableView<Room> table = new TableView<>(roomObs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, Integer> colNo = col("Room No", "roomNumber");
        TableColumn<Room, String> colType = col("Type", "roomType");
        TableColumn<Room, Double> colPri = col("Price/Night", "price");
        // <-- NEW: Uses getBookingSchedule() instead of boolean isAvailable
        TableColumn<Room, String> colAv = col("Booking Status", "bookingSchedule");

        table.getColumns().addAll(colNo, colType, colPri, colAv);

        // Add Room Action
        btnAdd.setOnAction(e -> {
            if (tfRoomNo.getText().isEmpty() || cbType.getValue() == null) {
                alert(Alert.AlertType.WARNING, "Please fill Room Number and select Room Type.");
                return;
            }
            try {
                int num = Integer.parseInt(tfRoomNo.getText().trim());
                RoomType rt = cbType.getValue();

                if (HotelManagement.findRoom(rooms, num) != null) {
                    alert(Alert.AlertType.ERROR, "Room " + num + " already exists.");
                    return;
                }

                Room room;
                if (rt == RoomType.DELUXE)
                    room = new DeluxeRoom(num, rt, rt.getPricePerNight(), true, true);
                else if (rt == RoomType.SUITE)
                    room = new SuiteRoom(num, rt, rt.getPricePerNight(), true, true, true, true);
                else
                    room = new Room(num, rt);

                rooms.add(room);
                roomObs.add(room);
                HotelManagement.saveRooms(rooms);

                tfRoomNo.clear();
                cbType.setValue(null);

                alert(Alert.AlertType.INFORMATION, "Room " + num + " (" + rt + ") added successfully.");
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Room Number must be a valid integer.");
            }
        });

        // Remove Room Action
        btnRemove.setOnAction(e -> {
            if (tfRoomNo.getText().isEmpty()) {
                alert(Alert.AlertType.WARNING, "Enter room number to remove.");
                return;
            }
            try {
                int num = Integer.parseInt(tfRoomNo.getText().trim());
                Room r = HotelManagement.findRoom(rooms, num);
                if (r == null) {
                    alert(Alert.AlertType.ERROR, "Room not found.");
                    return;
                }
                if (!r.isAvailable()) {
                    alert(Alert.AlertType.ERROR, "Room is currently occupied on some dates.");
                    return;
                }

                rooms.remove(r);
                roomObs.remove(r);
                HotelManagement.saveRooms(rooms);
                tfRoomNo.clear();

                alert(Alert.AlertType.INFORMATION, "Room " + num + " removed successfully.");
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Invalid room number.");
            }
        });

        Label heading = new Label("Room Management");
        heading.getStyleClass().add("heading-label");

        VBox root = new VBox(15, heading, form, table);
        root.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    // ====================== Booking Tab ======================
    private VBox buildBookingTab() {
        TextField tfCid = new TextField();      tfCid.setPromptText("Customer ID");
        TextField tfName = new TextField();     tfName.setPromptText("Customer Name");
        TextField tfContact = new TextField();  tfContact.setPromptText("Contact Number (10 digits)");
        TextField tfRoom = new TextField();     tfRoom.setPromptText("Room Number");

        DatePicker dpCheckIn = new DatePicker();
        dpCheckIn.setPromptText("Check-in Date");
        dpCheckIn.setValue(LocalDate.now().plusDays(1));

        DatePicker dpCheckOut = new DatePicker();
        dpCheckOut.setPromptText("Check-out Date");

        dpCheckIn.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        Button btnBook = new Button("Book Room");
        btnBook.getStyleClass().add("action-btn");

        Label lblPreview = new Label("Select room and dates to see availability.");
        lblPreview.getStyleClass().add("sub-label");
        lblPreview.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(10));

        grid.addRow(0, fieldLabel("Customer ID:"), tfCid);
        grid.addRow(1, fieldLabel("Customer Name:"), tfName);
        grid.addRow(2, fieldLabel("Contact:"), tfContact);
        grid.addRow(3, fieldLabel("Room Number:"), tfRoom);
        grid.addRow(4, fieldLabel("Check-in:"), dpCheckIn);
        grid.addRow(5, fieldLabel("Check-out:"), dpCheckOut);
        grid.add(btnBook, 1, 6);
        grid.add(lblPreview, 0, 7, 2, 1);

        ChangeListener<Object> listener = (obs, old, newVal) -> updateBookingPreview(tfRoom, dpCheckIn, dpCheckOut, lblPreview);
        tfRoom.textProperty().addListener(listener);
        dpCheckIn.valueProperty().addListener(listener);
        dpCheckOut.valueProperty().addListener(listener);

        btnBook.setOnAction(e -> {
            try {
                int cid = Integer.parseInt(tfCid.getText());
                String name = tfName.getText().trim();
                String cont = tfContact.getText().trim();
                int rno = Integer.parseInt(tfRoom.getText());

                LocalDate checkIn = dpCheckIn.getValue();
                LocalDate checkOut = dpCheckOut.getValue();

                if (name.isEmpty() || cont.isEmpty() || checkIn == null || checkOut == null) {
                    alert(Alert.AlertType.WARNING, "Please fill all fields and select dates.");
                    return;
                }
                if (cont.length() != 10 || !cont.matches("\\d+")) {
                    alert(Alert.AlertType.ERROR, "Contact number must be exactly 10 digits.");
                    return;
                }
                if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                    alert(Alert.AlertType.ERROR, "Check-out date must be after Check-in date.");
                    return;
                }

                Room r = HotelManagement.findRoom(rooms, rno);
                if (r == null) {
                    alert(Alert.AlertType.ERROR, "Room not found.");
                    return;
                }
                if (!r.isAvailableFor(checkIn, checkOut)) {
                    alert(Alert.AlertType.ERROR, "Room is not available for the selected dates.");
                    return;
                }
                if (HotelManagement.findCustomer(customers, cid) != null) {
                    alert(Alert.AlertType.ERROR, "Customer ID already exists.");
                    return;
                }

                r.book(checkIn, checkOut);
                roomObs.set(roomObs.indexOf(r), r); // Refresh table

                long days = ChronoUnit.DAYS.between(checkIn, checkOut);
                double totalBeforeTax = r.getPrice() * days;

                // <-- NEW: Passes checkIn and checkOut to the Customer constructor
                Customer c = new Customer(cid, name, cont, rno, (int) days, totalBeforeTax, checkIn, checkOut);
                customers.add(c);
                customerObs.add(c);
                roomMap.put(rno, c);

                HotelManagement.saveRooms(rooms);
                HotelManagement.saveCustomers(customers);

                tfCid.clear(); tfName.clear(); tfContact.clear(); tfRoom.clear();
                dpCheckIn.setValue(LocalDate.now().plusDays(1));
                dpCheckOut.setValue(null);

                lblPreview.setText("Booking successful for " + days + " night(s)!");

                alert(Alert.AlertType.INFORMATION,
                        "Room " + rno + " booked successfully for " + name + "\n" +
                                "From " + checkIn + " to " + checkOut);

                refreshDashboard();
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Invalid numeric input.");
            }
        });

        Label heading = new Label("Room Booking");
        heading.getStyleClass().add("heading-label");

        VBox root = new VBox(15, heading, grid);
        root.setPadding(new Insets(20));
        return root;
    }

    // ====================== Customers Tab ======================
    private VBox buildCustomersTab() {
        TableView<Customer> table = new TableView<>(customerObs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, Integer> cid = col("ID", "customerId");
        TableColumn<Customer, String> cn = col("Name", "name");
        TableColumn<Customer, String> cc = col("Contact", "contact");
        TableColumn<Customer, Integer> cr = col("Room", "roomNumber");
        // <-- NEW: Added Date Columns
        TableColumn<Customer, LocalDate> colIn = col("Check-In", "checkIn");
        TableColumn<Customer, LocalDate> colOut = col("Check-Out", "checkOut");
        TableColumn<Customer, Integer> cd = col("Days", "daysStayed");
        TableColumn<Customer, Double> cb = col("Bill (pre-tax)", "totalBill");

        table.getColumns().addAll(cid, cn, cc, cr, colIn, colOut, cd, cb);

        TextField tfCid = new TextField();
        tfCid.setPromptText("Customer ID to Checkout");

        Button btnCheckout = new Button("Checkout");
        btnCheckout.getStyleClass().add("danger-btn");

        btnCheckout.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfCid.getText());
                Customer c = HotelManagement.findCustomer(customers, id);
                if (c == null) {
                    alert(Alert.AlertType.ERROR, "Customer not found.");
                    return;
                }

                Room r = HotelManagement.findRoom(rooms, c.getRoomNumber());

                // --- NEW: AUTO-BILLING & SAVING LOGIC ---
                Bill generatedBill = new Bill(c, r);
                pastBills.add(generatedBill);
                HotelManagement.saveBills(pastBills); // Save permanently to file

                // Show the bill immediately in a popup window
                Alert billAlert = new Alert(Alert.AlertType.INFORMATION);
                billAlert.setTitle("Checkout Successful - Invoice");
                billAlert.setHeaderText("Final Bill generated for " + c.getName());

                TextArea textArea = new TextArea(generatedBill.getFormattedBill());
                textArea.setEditable(false);
                textArea.setStyle("-fx-font-family: monospace;"); // Makes the ASCII layout align nicely
                textArea.setPrefWidth(400);
                textArea.setPrefHeight(350);
                billAlert.getDialogPane().setContent(textArea);
                billAlert.showAndWait();
                // ---------------------------------------

                customers.remove(c);
                customerObs.remove(c);
                roomMap.remove(c.getRoomNumber());
                HotelManagement.saveCustomers(customers);

                if (r != null) {
                    new CleaningThread(r, () -> Platform.runLater(() -> {
                        r.setAvailable(true);
                        roomObs.set(roomObs.indexOf(r), r);
                        HotelManagement.saveRooms(rooms);
                        refreshDashboard();
                    })).start();
                }

                tfCid.clear();
                refreshDashboard();
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Enter a valid Customer ID.");
            }
        });

        HBox checkoutBar = new HBox(12, tfCid, btnCheckout);
        checkoutBar.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("Customer Records & Checkout");
        heading.getStyleClass().add("heading-label");

        VBox root = new VBox(15, heading, checkoutBar, table);
        root.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    // ====================== Billing Tab ======================
    private VBox buildBillingTab() {
        TextField tfCid = new TextField();
        tfCid.setPromptText("Enter Current Customer ID");

        Button btnGen = new Button("Generate Preview Bill");
        btnGen.getStyleClass().add("success-btn");

        TextArea billArea = new TextArea();
        billArea.setEditable(false);
        billArea.getStyleClass().add("bill-area");
        billArea.setPromptText("Preview of current customer bill will appear here...\n(Final bill is auto-generated on checkout in the Customers Tab)");
        billArea.setPrefHeight(360);

        btnGen.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfCid.getText());
                Customer c = HotelManagement.findCustomer(customers, id);
                if (c == null) {
                    alert(Alert.AlertType.ERROR, "Current Customer not found. (If already checked out, bill is saved in the ledger).");
                    return;
                }
                Room r = HotelManagement.findRoom(rooms, c.getRoomNumber());
                if (r == null) {
                    alert(Alert.AlertType.ERROR, "Room record missing.");
                    return;
                }

                Bill bill = new Bill(c, r);
                billArea.setText(bill.getFormattedBill());
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Enter a valid Customer ID.");
            }
        });

        HBox top = new HBox(12, tfCid, btnGen);
        top.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("Active Guest Billing Preview");
        heading.getStyleClass().add("heading-label");

        VBox root = new VBox(15, heading, top, billArea);
        root.setPadding(new Insets(20));
        VBox.setVgrow(billArea, Priority.ALWAYS);
        return root;
    }

    // ====================== Utilities ======================
    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setPrefWidth(130);
        return l;
    }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void updateBookingPreview(TextField tfRoom, DatePicker dpIn, DatePicker dpOut, Label preview) {
        if (tfRoom.getText().isEmpty() || dpIn.getValue() == null || dpOut.getValue() == null) {
            preview.setText("Select room and dates to check availability.");
            return;
        }

        try {
            int rno = Integer.parseInt(tfRoom.getText());
            Room r = HotelManagement.findRoom(rooms, rno);
            if (r == null) {
                preview.setText("Room " + rno + " not found.");
                return;
            }

            LocalDate in = dpIn.getValue();
            LocalDate out = dpOut.getValue();
            long days = ChronoUnit.DAYS.between(in, out);

            if (days <= 0) {
                preview.setText("Check-out must be after Check-in.");
                return;
            }

            if (r.isAvailableFor(in, out)) {
                preview.setText("Room " + rno + " | " + r.getRoomType() +
                        " | ₹" + r.getPrice() + "/night × " + days + " nights → Available");
            } else {
                preview.setText("Room " + rno + " is NOT available for the selected dates.");
            }
        } catch (Exception ignored) {
            preview.setText("Select room and dates to check availability.");
        }
    }
}