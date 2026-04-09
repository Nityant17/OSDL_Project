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
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        for (Customer c : customers) {
            roomMap.put(c.getRoomNumber(), c);
        }

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabDash = new Tab("Dashboard", buildDashboard());
        tabDash.setGraphic(createSVGIcon("M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"));
        
        Tab tabRooms = new Tab("Rooms", buildRoomsTab());
        tabRooms.setGraphic(createSVGIcon("M7 5H3c-1.1 0-2 .9-2 2v10h2v-2h18v2h2V7c0-1.1-.9-2-2-2h-4V4H7v1zm14 10H3V7h18v8z"));
        
        Tab tabBook = new Tab("Booking", buildBookingTab());
        tabBook.setGraphic(createSVGIcon("M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V9h10v2z"));
        
        Tab tabServ = new Tab("Services", buildServicesTab());
        tabServ.setGraphic(createSVGIcon("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"));
        
        Tab tabCust = new Tab("Customers", buildCustomersTab());
        tabCust.setGraphic(createSVGIcon("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"));
        
        Tab tabLedger = new Tab("Ledger", buildLedgerTab());
        tabLedger.setGraphic(createSVGIcon("M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z"));

        tabPane.getTabs().addAll(tabDash, tabRooms, tabBook, tabServ, tabCust, tabLedger);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> refreshDashboard());

        Scene scene = new Scene(tabPane, 1150, 750); 
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Nityant's Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    // ====================== Dashboard ======================
    private VBox buildDashboard() {
        Label title = new Label("Nityant's Grand Hotel");
        title.setGraphic(createSVGIcon("M12 3L2 12h3v8h14v-8h3L12 3zm0 10c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"));
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

        VBox root = createGlassRoot(title, sub, cards);
        root.setAlignment(Pos.CENTER);
        refreshDashboard();
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
        TextField tfRoomNo = new TextField(); tfRoomNo.setPromptText("Room Number");
        ComboBox<RoomType> cbType = new ComboBox<>(FXCollections.observableArrayList(RoomType.values()));
        cbType.setPromptText("Select Room Type");

        Button btnAdd = new Button("Add Room"); btnAdd.getStyleClass().add("action-btn");
        btnAdd.setGraphic(createSVGIcon("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"));

        Button btnRemove = new Button("Remove Room"); btnRemove.getStyleClass().add("danger-btn");
        btnRemove.setGraphic(createSVGIcon("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"));

        GridPane form = createGrid(
                fieldLabel("Room Number:"), tfRoomNo,
                fieldLabel("Room Type:"), cbType,
                btnAdd, btnRemove
        );

        TableView<Room> table = new TableView<>(roomObs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                col("Room No", "roomNumber"), col("Type", "roomType"),
                col("Price/Night", "price"), col("Booking Status", "bookingSchedule")
        );

        btnAdd.setOnAction(e -> {
            try {
                int num = Integer.parseInt(tfRoomNo.getText().trim());
                RoomType rt = cbType.getValue();
                if (rt == null) { alert(Alert.AlertType.WARNING, "Select Room Type."); return; }
                if (HotelManagement.findRoom(rooms, num) != null) { alert(Alert.AlertType.ERROR, "Room exists."); return; }

                Room room = (rt == RoomType.DELUXE) ? new DeluxeRoom(num, rt, rt.getPricePerNight(), true, true) :
                            (rt == RoomType.SUITE) ? new SuiteRoom(num, rt, rt.getPricePerNight(), true, true, true, true) :
                            new Room(num, rt);

                rooms.add(room); roomObs.add(room);
                HotelManagement.saveRooms(rooms);
                tfRoomNo.clear(); cbType.setValue(null);
                alert(Alert.AlertType.INFORMATION, "Room added.");
            } catch (Exception ex) { alert(Alert.AlertType.ERROR, "Invalid Number."); }
        });

        btnRemove.setOnAction(e -> {
            try {
                int num = Integer.parseInt(tfRoomNo.getText().trim());
                Room r = HotelManagement.findRoom(rooms, num);
                if (r == null) { alert(Alert.AlertType.ERROR, "Not found."); return; }
                if (!r.isAvailable()) { alert(Alert.AlertType.ERROR, "Occupied."); return; }

                rooms.remove(r); roomObs.remove(r);
                HotelManagement.saveRooms(rooms);
                tfRoomNo.clear();
            } catch (Exception ex) { alert(Alert.AlertType.ERROR, "Invalid."); }
        });

        Label heading = new Label("Room Management"); heading.getStyleClass().add("heading-label");
        heading.setGraphic(createSVGIcon("M7 5H3c-1.1 0-2 .9-2 2v10h2v-2h18v2h2V7c0-1.1-.9-2-2-2h-4V4H7v1zm14 10H3V7h18v8z"));
        VBox root = createGlassRoot(heading, form, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    // ====================== Booking Tab ======================
    private VBox buildBookingTab() {
        TextField tfCid = new TextField();      tfCid.setPromptText("Customer ID");
        TextField tfName = new TextField();     tfName.setPromptText("Customer Name");
        TextField tfContact = new TextField();  tfContact.setPromptText("Contact Number (10 digits)");
        TextField tfRoom = new TextField();     tfRoom.setPromptText("Room Number");

        DatePicker dpCheckIn = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker dpCheckOut = new DatePicker();
        dpCheckIn.setDayCellFactory(picker -> new DateCell() {
            @Override public void updateItem(LocalDate d, boolean e) {
                super.updateItem(d, e); setDisable(e || d.isBefore(LocalDate.now()));
            }
        });

        ComboBox<String> cbMeal = new ComboBox<>(FXCollections.observableArrayList(
                "None - ₹0", "Breakfast - ₹500", "Half Board - ₹1000", "Full Board - ₹1500"
        ));
        cbMeal.setValue("None - ₹0");

        Button btnBook = new Button("Book Room"); btnBook.getStyleClass().add("action-btn");
        btnBook.setGraphic(createSVGIcon("M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"));
        Label lblPreview = new Label("Select room and dates..."); lblPreview.getStyleClass().add("sub-label");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(10));
        grid.addRow(0, fieldLabel("Customer ID:"), tfCid, fieldLabel("Customer Name:"), tfName);
        grid.addRow(1, fieldLabel("Contact:"), tfContact, fieldLabel("Room Number:"), tfRoom);
        grid.addRow(2, fieldLabel("Check-in:"), dpCheckIn, fieldLabel("Check-out:"), dpCheckOut);
        grid.addRow(3, fieldLabel("Meal Plan:"), cbMeal);
        grid.add(btnBook, 1, 4);
        grid.add(lblPreview, 0, 5, 4, 1);

        btnBook.setOnAction(e -> {
            try {
                int cid = Integer.parseInt(tfCid.getText());
                int rno = Integer.parseInt(tfRoom.getText());
                String name = tfName.getText().trim();
                String cont = tfContact.getText().trim();
                LocalDate in = dpCheckIn.getValue(), out = dpCheckOut.getValue();

                if (name.isEmpty() || cont.isEmpty() || in == null || out == null || out.isBefore(in) || out.isEqual(in)) {
                    alert(Alert.AlertType.ERROR, "Invalid details or dates."); return;
                }

                Room r = HotelManagement.findRoom(rooms, rno);
                if (r == null || !r.isAvailableFor(in, out)) { alert(Alert.AlertType.ERROR, "Room unavailable."); return; }
                if (HotelManagement.findCustomer(customers, cid) != null) { alert(Alert.AlertType.ERROR, "ID exists."); return; }

                String mealSel = cbMeal.getValue();
                double mPrice = mealSel.contains("₹500") ? 500 : mealSel.contains("₹1000") ? 1000 : mealSel.contains("₹1500") ? 1500 : 0;
                String mealName = mealSel.split(" -")[0];

                long days = ChronoUnit.DAYS.between(in, out);
                
                r.book(in, out);
                roomObs.set(roomObs.indexOf(r), r);

                Customer c = new Customer(cid, name, cont, rno, (int) days, 0, in, out, mealName, mPrice);
                customers.add(c); customerObs.add(c); roomMap.put(rno, c);
                HotelManagement.saveRooms(rooms); HotelManagement.saveCustomers(customers);

                alert(Alert.AlertType.INFORMATION, "Booked!");
                tfCid.clear(); tfName.clear(); tfContact.clear(); tfRoom.clear(); dpCheckOut.setValue(null);
                refreshDashboard();
            } catch (Exception ex) { alert(Alert.AlertType.ERROR, "Invalid input."); }
        });

        Label heading = new Label("Room Booking"); heading.getStyleClass().add("heading-label");
        heading.setGraphic(createSVGIcon("M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V9h10v2z"));
        return createGlassRoot(heading, grid);
    }

    // ====================== Services Tab ======================
    private VBox buildServicesTab() {
        TextField tfCid = new TextField(); tfCid.setPromptText("Customer ID");
        ComboBox<String> cbServ = new ComboBox<>(FXCollections.observableArrayList(
                "Massage Session - ₹2000", "Room Cleaning - ₹500", "Laundry - ₹300", "Cab Service - ₹1500"
        ));
        cbServ.setPromptText("Select Service");
        Button btnAddServ = new Button("Add Service to Room");
        btnAddServ.getStyleClass().add("action-btn");
        btnAddServ.setGraphic(createSVGIcon("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"));

        GridPane form = createGrid(
                fieldLabel("Customer ID:"), tfCid,
                fieldLabel("Service:"), cbServ,
                btnAddServ, new Label()
        );

        Label heading = new Label("Add Services for Active Guests");
        heading.getStyleClass().add("heading-label");
        heading.setGraphic(createSVGIcon("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"));

        btnAddServ.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfCid.getText());
                Customer c = HotelManagement.findCustomer(customers, id);
                if (c == null) { alert(Alert.AlertType.ERROR, "Active Customer not found."); return; }
                if (cbServ.getValue() == null) { alert(Alert.AlertType.WARNING, "Select a service."); return; }

                c.addService(cbServ.getValue());
                HotelManagement.saveCustomers(customers);
                customerObs.set(customerObs.indexOf(c), c); // Trigger update
                
                alert(Alert.AlertType.INFORMATION, "Service added for " + c.getName());
                tfCid.clear(); cbServ.setValue(null);
            } catch (Exception ex) { alert(Alert.AlertType.ERROR, "Invalid ID."); }
        });

        return createGlassRoot(heading, form);
    }

    // ====================== Customers Tab ======================
    private VBox buildCustomersTab() {
        TableView<Customer> table = new TableView<>(customerObs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                col("ID", "customerId"), col("Name", "name"), col("Room", "roomNumber"),
                col("In", "checkIn"), col("Out", "checkOut"), col("Meal", "mealPlan")
        );

        TextField tfCid = new TextField(); tfCid.setPromptText("Customer ID to Checkout");
        Button btnCheckout = new Button("Checkout & Auto-Bill"); btnCheckout.getStyleClass().add("danger-btn");
        btnCheckout.setGraphic(createSVGIcon("M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z"));

        GridPane checkoutBar = createGrid(fieldLabel("Customer ID:"), tfCid, btnCheckout, new Label());

        btnCheckout.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfCid.getText());
                Customer c = HotelManagement.findCustomer(customers, id);
                if (c == null) { alert(Alert.AlertType.ERROR, "Not found."); return; }

                Room r = HotelManagement.findRoom(rooms, c.getRoomNumber());
                Bill bill = new Bill(c, r);
                pastBills.add(bill); billObs.add(bill);
                HotelManagement.saveBills(pastBills);

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Invoice"); a.setHeaderText("Checkout Complete");
                TextArea ta = new TextArea(bill.getFormattedBill());
                ta.setEditable(false); ta.setStyle("-fx-font-family: monospace;"); ta.setPrefSize(450, 450);
                a.getDialogPane().setContent(ta); a.showAndWait();

                customers.remove(c); customerObs.remove(c); roomMap.remove(c.getRoomNumber());
                HotelManagement.saveCustomers(customers);

                if (r != null) {
                    new CleaningThread(r, () -> Platform.runLater(() -> {
                        r.setAvailable(true); roomObs.set(roomObs.indexOf(r), r);
                        HotelManagement.saveRooms(rooms); refreshDashboard();
                    })).start();
                }
                tfCid.clear(); refreshDashboard();
            } catch (Exception ex) { alert(Alert.AlertType.ERROR, "Enter valid ID."); }
        });

        Label heading = new Label("Active Guests & Checkout"); heading.getStyleClass().add("heading-label");
        heading.setGraphic(createSVGIcon("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"));
        VBox root = createGlassRoot(heading, checkoutBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    // ====================== Ledger Tab ======================
    private VBox buildLedgerTab() {
        TableView<Bill> table = new TableView<>(billObs);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(
                col("Generated At", "generatedAt"),
                col("Customer Name", "customerName"),
                col("Grand Total (₹)", "grandTotal")
        );

        TextArea billArea = new TextArea();
        billArea.setEditable(false);
        billArea.getStyleClass().add("bill-area");
        billArea.setPrefWidth(500);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) billArea.setText(newVal.getFormattedBill());
        });

        HBox split = new HBox(15, table, billArea);
        HBox.setHgrow(table, Priority.ALWAYS);

        Label heading = new Label("Past Checkouts & Invoices"); heading.getStyleClass().add("heading-label");
        heading.setGraphic(createSVGIcon("M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z"));
        VBox root = createGlassRoot(heading, split);
        VBox.setVgrow(split, Priority.ALWAYS);
        return root;
    }

    // ====================== Utilities ======================
    private VBox createGlassRoot(javafx.scene.Node... children) {
        VBox v = new VBox(15, children);
        v.setPadding(new Insets(20));
        v.getStyleClass().add("glass-panel");
        return v;
    }

    private GridPane createGrid(javafx.scene.Node... nodes) {
        GridPane g = new GridPane(); g.setHgap(12); g.setVgap(12); g.setPadding(new Insets(10, 0, 15, 0));
        for(int i=0; i<nodes.length; i+=2) { g.add(nodes[i], i%4, i/4); if(i+1<nodes.length) g.add(nodes[i+1], (i%4)+1, i/4); }
        return g;
    }

    private <S, T> TableColumn<S, T> col(String title, String prop) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private Label fieldLabel(String t) { Label l = new Label(t); l.setPrefWidth(130); return l; }
    private void alert(Alert.AlertType t, String m) { new Alert(t, m).showAndWait(); }

    private SVGPath createSVGIcon(String pathContent) {
        SVGPath svg = new SVGPath();
        svg.setContent(pathContent);
        svg.setFill(Color.web("#38bdf8")); // Premium cyan accent
        svg.setScaleX(0.7);
        svg.setScaleY(0.7);
        return svg;
    }

    public static void main(String[] args) { launch(args); }
}