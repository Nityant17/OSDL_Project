package com.nityant.hotel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.*;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Nityant's Hotel Management System - Main Entry Point.
 * Optimized for high performance and clean, dense code structure.
 */
public class MainApp extends Application {
    private ArrayList<Room> rooms = HotelManagement.loadRooms();
    private ArrayList<Customer> customers = HotelManagement.loadCustomers();
    private ArrayList<Bill> pastBills = HotelManagement.loadBills();
    private HashMap<Integer, Customer> roomMap = new HashMap<>();

    private ObservableList<Room> roomObs = FXCollections.observableArrayList(rooms);
    private ObservableList<Customer> customerObs = FXCollections.observableArrayList(customers);
    private ObservableList<Bill> billObs = FXCollections.observableArrayList(pastBills);

    private Label lblTotalRooms, lblAvailable, lblOccupied, lblTotalRevenue;

    @Override
    public void start(Stage stage) {
        customers.forEach(c -> roomMap.put(c.getRoomNumber(), c));
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().addAll(
            tab("Dashboard", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z", buildDashboard()),
            tab("Rooms", "M7 5H3c-1.1 0-2 .9-2 2v10h2v-2h18v2h2V7c0-1.1-.9-2-2-2h-4V4H7v1zm14 10H3V7h18v8z", buildRoomsTab()),
            tab("Booking", "M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V9h10v2z", buildBookingTab()),
            tab("Services", "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", buildServicesTab()),
            tab("Customers", "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z", buildCustomersTab()),
            tab("Ledger", "M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z", buildLedgerTab())
        );

        tabs.getSelectionModel().selectedItemProperty().addListener((o, ol, n) -> refreshDashboard());
        Scene scene = new Scene(tabs, 1150, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Nityant's Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    private Tab tab(String t, String p, javafx.scene.Node c) { 
        Tab tab = new Tab(t, c); tab.setGraphic(createSVG(p)); return tab; 
    }

    // ====================== Dashboard ======================
    private VBox buildDashboard() {
        Label title = new Label("Nityant's Grand Hotel", createSVG("M12 3L2 12h3v8h14v-8h3L12 3zm0 10c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"));
        title.getStyleClass().add("heading-label"); title.setStyle("-fx-font-size: 28px;");

        HBox cards = new HBox(20, 
            statCard(lblTotalRooms = statNum("0"), "Total Rooms"),
            statCard(lblAvailable = statNum("0"), "Available"),
            statCard(lblOccupied = statNum("0"), "Occupied"),
            statCard(lblTotalRevenue = statNum("₹0"), "Total Revenue")
        );
        cards.setAlignment(Pos.CENTER);
        VBox root = glass(title, new Label("Management Dashboard - Overview"), cards);
        root.setAlignment(Pos.CENTER); refreshDashboard();
        return root;
    }

    private void refreshDashboard() {
        long avail = rooms.stream().filter(Room::isAvailable).count();
        double rev = pastBills.stream().mapToDouble(Bill::getGrandTotal).sum();
        if (lblTotalRooms != null) {
            lblTotalRooms.setText(String.valueOf(rooms.size()));
            lblAvailable.setText(String.valueOf(avail));
            lblOccupied.setText(String.valueOf(rooms.size() - avail));
            lblTotalRevenue.setText(String.format("₹%.0f", rev));
        }
    }

    private VBox statCard(Label num, String d) {
        Label dl = new Label(d); dl.getStyleClass().add("stat-desc");
        VBox c = new VBox(5, num, dl); c.setAlignment(Pos.CENTER);
        c.getStyleClass().add("stat-card"); c.setMinWidth(170); return c;
    }
    private Label statNum(String t) { Label l = new Label(t); l.getStyleClass().add("stat-number"); return l; }

    // ====================== Rooms Tab ======================
    private VBox buildRoomsTab() {
        TextField tfNo = new TextField(); tfNo.setPromptText("Room Number");
        ComboBox<RoomType> cbType = new ComboBox<>(FXCollections.observableArrayList(RoomType.values()));
        cbType.setPromptText("Select Room Type");

        Button btnAdd = btn("Add Room", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z", "action-btn");
        Button btnRem = btn("Remove Room", "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z", "danger-btn");

        GridPane form = grid(lbl("Room Number:"), tfNo, lbl("Room Type:"), cbType, btnAdd, btnRem);
        TableView<Room> table = table(roomObs, col("Room No", "roomNumber"), col("Type", "roomType"), col("Price/Night", "price"), col("Status", "bookingSchedule"));

        btnAdd.setOnAction(e -> {
            try {
                int num = Integer.parseInt(tfNo.getText().trim()); RoomType rt = cbType.getValue();
                if (rt == null || HotelManagement.findRoom(rooms, num) != null) { alert("Error", "Check room type or duplicate number."); return; }
                Room room = rt == RoomType.DELUXE ? new DeluxeRoom(num, rt, rt.getPricePerNight(), true, true) :
                            rt == RoomType.SUITE ? new SuiteRoom(num, rt, rt.getPricePerNight(), true, true, true, true) : new Room(num, rt);
                rooms.add(room); roomObs.add(room); HotelManagement.saveRooms(rooms);
                tfNo.clear(); cbType.setValue(null); alert("Success", "Room added.");
            } catch (Exception ex) { alert("Error", "Invalid input."); }
        });

        btnRem.setOnAction(e -> {
            try {
                Room r = HotelManagement.findRoom(rooms, Integer.parseInt(tfNo.getText().trim()));
                if (r == null || !r.isAvailable()) { alert("Error", "Room not found or occupied."); return; }
                rooms.remove(r); roomObs.remove(r); HotelManagement.saveRooms(rooms); tfNo.clear();
            } catch (Exception ex) { alert("Error", "Invalid input."); }
        });

        Label h = new Label("Room Management", createSVG("M7 5H3c-1.1 0-2 .9-2 2v10h2v-2h18v2h2V7c0-1.1-.9-2-2-2h-4V4H7v1zm14 10H3V7h18v8z")); h.getStyleClass().add("heading-label");
        VBox root = glass(h, form, table); VBox.setVgrow(table, Priority.ALWAYS); return root;
    }

    // ====================== Booking Tab ======================
    private VBox buildBookingTab() {
        TextField tfId = new TextField(), tfN = new TextField(), tfC = new TextField(), tfR = new TextField();
        tfId.setPromptText("ID"); tfN.setPromptText("Name"); tfC.setPromptText("Contact"); tfR.setPromptText("Room");
        DatePicker dpIn = new DatePicker(LocalDate.now()), dpOut = new DatePicker();
        dpIn.setDayCellFactory(p -> new DateCell() { @Override public void updateItem(LocalDate d, boolean e) { super.updateItem(d, e); setDisable(e || d.isBefore(LocalDate.now())); }});

        ComboBox<String> cbM = new ComboBox<>(FXCollections.observableArrayList("None - ₹0", "Breakfast - ₹500", "Half Board - ₹1000", "Full Board - ₹1500"));
        cbM.setValue("None - ₹0");
        Button btnB = btn("Book Room", "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z", "action-btn");
        
        GridPane g = new GridPane(); g.setHgap(12); g.setVgap(12); g.setPadding(new Insets(10));
        g.addRow(0, lbl("Customer ID:"), tfId, lbl("Name:"), tfN);
        g.addRow(1, lbl("Contact:"), tfC, lbl("Room:"), tfR);
        g.addRow(2, lbl("Check-in:"), dpIn, lbl("Check-out:"), dpOut);
        g.addRow(3, lbl("Meal Plan:"), cbM);
        g.add(btnB, 1, 4);

        btnB.setOnAction(e -> {
            try {
                int rno = Integer.parseInt(tfR.getText()); String name = tfN.getText().trim(); LocalDate in = dpIn.getValue(), out = dpOut.getValue();
                Room r = HotelManagement.findRoom(rooms, rno);
                if (name.isEmpty() || in == null || out == null || !out.isAfter(in) || r == null || !r.isAvailableFor(in, out) || HotelManagement.findCustomer(customers, Integer.parseInt(tfId.getText())) != null) { alert("Error", "Invalid details or room booked."); return; }
                String mSel = cbM.getValue(); double mp = mSel.contains("₹500") ? 500 : mSel.contains("₹1000") ? 1000 : mSel.contains("₹1500") ? 1500 : 0;
                r.book(in, out); roomObs.set(roomObs.indexOf(r), r);
                Customer customer = new Customer(Integer.parseInt(tfId.getText()), name, tfC.getText(), rno, (int)ChronoUnit.DAYS.between(in, out), 0, in, out, mSel.split(" -")[0], mp);
                customers.add(customer); customerObs.add(customer); roomMap.put(rno, customer);
                HotelManagement.saveRooms(rooms); HotelManagement.saveCustomers(customers);
                alert("Success", "Booked!"); refreshDashboard();
            } catch (Exception ex) { alert("Error", "Invalid input."); }
        });

        Label h = new Label("Room Booking", createSVG("M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V9h10v2z"));
        return glass(h, g);
    }

    // ====================== Services Tab ======================
    private VBox buildServicesTab() {
        TextField tfI = new TextField(); tfI.setPromptText("ID");
        ComboBox<String> cbS = new ComboBox<>(FXCollections.observableArrayList("Massage - ₹2000", "Cleaning - ₹500", "Laundry - ₹300", "Cab - ₹1500"));
        Button btnS = btn("Add Service", "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "action-btn");
        btnS.setOnAction(e -> {
            try {
                Customer c = HotelManagement.findCustomer(customers, Integer.parseInt(tfI.getText()));
                if (c == null || cbS.getValue() == null) { alert("Error", "Customer not found."); return; }
                c.addService(cbS.getValue()); HotelManagement.saveCustomers(customers); customerObs.set(customerObs.indexOf(c), c);
                alert("Added", "Service added for " + c.getName());
            } catch (Exception ex) { alert("Error", "Invalid ID."); }
        });
        Label h = new Label("Additional Services", createSVG("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"));
        return glass(h, grid(lbl("Customer ID:"), tfI, lbl("Service:"), cbS, btnS, new Label()));
    }

    // ====================== Customers Tab ======================
    private VBox buildCustomersTab() {
        TableView<Customer> table = table(customerObs, col("ID", "customerId"), col("Name", "name"), col("Room", "roomNumber"), col("In", "checkIn"), col("Out", "checkOut"), col("Meal", "mealPlan"));
        TextField tfI = new TextField(); tfI.setPromptText("ID to Checkout");
        Button btnC = btn("Checkout", "M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z", "danger-btn");
        btnC.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfI.getText()); Customer c = HotelManagement.findCustomer(customers, id);
                if (c == null) { alert("Error", "Not found."); return; }
                Room r = HotelManagement.findRoom(rooms, c.getRoomNumber()); Bill b = new Bill(c, r);
                pastBills.add(b); billObs.add(b); HotelManagement.saveBills(pastBills);
                Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Invoice");
                TextArea ta = new TextArea(b.getFormattedBill()); ta.setEditable(false); ta.setStyle("-fx-font-family: monospace;"); ta.setPrefSize(450, 450); a.getDialogPane().setContent(ta); a.showAndWait();
                customers.remove(c); customerObs.remove(c); roomMap.remove(c.getRoomNumber()); HotelManagement.saveCustomers(customers);
                if (r != null) {
                    LocalDate in = c.getCheckIn(), out = c.getCheckOut();
                    new CleaningThread(r, () -> Platform.runLater(() -> {
                        r.removeBooking(in, out);
                        roomObs.set(roomObs.indexOf(r), r);
                        HotelManagement.saveRooms(rooms);
                        refreshDashboard();
                    })).start();
                }
                tfI.clear(); refreshDashboard();
            } catch (Exception ex) { alert("Error", "Invalid ID."); }
        });
        Label h = new Label("Active Guests", createSVG("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"));
        VBox root = glass(h, grid(lbl("Customer ID:"), tfI, btnC, new Label()), table); VBox.setVgrow(table, Priority.ALWAYS); return root;
    }

    // ====================== Ledger Tab ======================
    private VBox buildLedgerTab() {
        TableView<Bill> table = table(billObs, col("Date", "generatedAt"), col("Name", "customerName"), col("Total (₹)", "grandTotal"));
        TextArea area = new TextArea(); area.setEditable(false); area.getStyleClass().add("bill-area"); area.setPrefWidth(500);
        table.getSelectionModel().selectedItemProperty().addListener((o, ol, nv) -> { if (nv != null) area.setText(nv.getFormattedBill()); });
        HBox body = new HBox(15, table, area); HBox.setHgrow(table, Priority.ALWAYS);
        Label h = new Label("Ledger", createSVG("M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z"));
        VBox root = glass(h, body); VBox.setVgrow(body, Priority.ALWAYS); return root;
    }

    // ====================== Utilities ======================
    private VBox glass(javafx.scene.Node... n) { VBox v = new VBox(15, n); v.setPadding(new Insets(20)); v.getStyleClass().add("glass-panel"); return v; }
    private GridPane grid(javafx.scene.Node... n) { GridPane g = new GridPane(); g.setHgap(12); g.setVgap(12); g.setPadding(new Insets(10, 0, 15, 0)); for (int i = 0; i < n.length; i++) g.add(n[i], i % 4, i / 4); return g; }
    private <S, T> TableColumn<S, T> col(String t, String p) { TableColumn<S, T> c = new TableColumn<>(t); c.setCellValueFactory(new PropertyValueFactory<>(p)); return c; }
    private <T> TableView<T> table(ObservableList<T> d, TableColumn... c) { TableView<T> t = new TableView<>(d); t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); t.getColumns().addAll(c); return t; }
    private Label lbl(String t) { Label l = new Label(t); l.setPrefWidth(130); return l; }
    private void alert(String h, String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private Button btn(String t, String p, String s) { Button b = new Button(t, createSVG(p)); b.getStyleClass().add(s); return b; }
    private SVGPath createSVG(String p) { SVGPath s = new SVGPath(); s.setContent(p); s.setFill(Color.web("#38bdf8")); s.setScaleX(0.7); s.setScaleY(0.7); return s; }

    public static void main(String[] args) { launch(args); }
}