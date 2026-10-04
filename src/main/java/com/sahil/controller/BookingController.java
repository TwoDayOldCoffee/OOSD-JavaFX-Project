package com.sahil.controller;

import com.sahil.model.Booking;
import com.sahil.model.Customer;
import com.sahil.model.Room;
import com.sahil.storage.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * BookingController handles the Booking tab:
 * - Selecting a customer and available room
 * - Entering number of days and seeing a cost preview
 * - Creating a booking (prevents booking occupied rooms)
 * - Showing all bookings in a table
 */
public class BookingController implements Initializable {

    @FXML private ComboBox<Customer> cmbCustomer;
    @FXML private ComboBox<Room>     cmbRoom;
    @FXML private TextField          txtDays;
    @FXML private Label              lblCostPreview;
    @FXML private Label              lblBookingMsg;

    @FXML private TableView<Booking>           tblBookings;
    @FXML private TableColumn<Booking, String> colBkId;
    @FXML private TableColumn<Booking, String> colBkCustomer;
    @FXML private TableColumn<Booking, String> colBkRoom;
    @FXML private TableColumn<Booking, String> colBkDays;
    @FXML private TableColumn<Booking, String> colBkTotal;
    @FXML private TableColumn<Booking, String> colBkStatus;

    public static BookingController instance;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        // Set up table columns
        colBkId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBookingId()));
        colBkCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomer().getName()));
        colBkRoom.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getRoom().getRoomNumber())));
        colBkDays.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getNumberOfDays())));
        colBkTotal.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getTotalAmount())));
        colBkStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));

        // Update cost preview whenever room or days changes
        cmbRoom.valueProperty().addListener((obs, oldVal, newVal) -> updateCostPreview());
        txtDays.textProperty().addListener((obs, oldVal, newVal) -> updateCostPreview());

        refreshForm();
    }

    @FXML
    private void bookRoom() {
        Customer customer = cmbCustomer.getValue();
        Room room         = cmbRoom.getValue();
        String daysStr    = txtDays.getText().trim();

        // Validation
        if (customer == null || room == null || daysStr.isEmpty()) {
            showMsg("Please select a customer, room, and enter number of days.", false);
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showMsg("Number of days must be a positive whole number.", false);
            return;
        }

        // Check if room is still available (it should be, since we only show available rooms in combo)
        if (!room.isAvailable()) {
            showMsg("This room is already occupied. Please select another room.", false);
            refreshForm();
            return;
        }

        Booking booking = store.createBooking(customer, room, days);
        showMsg("Booking created: " + booking.getBookingId()
                + "  |  Total: Rs. " + String.format("%.2f", booking.getTotalAmount()), true);

        // Clear form
        cmbCustomer.getSelectionModel().clearSelection();
        cmbRoom.getSelectionModel().clearSelection();
        txtDays.clear();
        lblCostPreview.setText("Rs. —");

        refreshForm();
    }

    public void refreshForm() {
        // Refresh customer and room lists from DataStore
        cmbCustomer.setItems(FXCollections.observableArrayList(store.getCustomers()));
        cmbRoom.setItems(FXCollections.observableArrayList(store.getAvailableRooms()));

        // Show room details in the ComboBox dropdown
        cmbRoom.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Room r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null :
                        "Room #" + r.getRoomNumber() + " [" + r.getRoomType() + "] Rs." + r.getPricePerDay());
            }
        });
        cmbRoom.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Room r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null :
                        "Room #" + r.getRoomNumber() + " [" + r.getRoomType() + "] Rs." + r.getPricePerDay());
            }
        });

        // Show customer ID + name in the dropdown
        cmbCustomer.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCustomerId() + " - " + c.getName());
            }
        });
        cmbCustomer.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCustomerId() + " - " + c.getName());
            }
        });

        tblBookings.setItems(FXCollections.observableArrayList(store.getBookings()));
    }

    private void updateCostPreview() {
        Room room = cmbRoom.getValue();
        String daysStr = txtDays.getText().trim();
        if (room != null && !daysStr.isEmpty()) {
            try {
                int days = Integer.parseInt(daysStr);
                if (days > 0) {
                    double total = days * room.getPricePerDay() * 1.18;
                    lblCostPreview.setText(String.format("Rs. %.2f (incl. 18%% tax)", total));
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        lblCostPreview.setText("Rs. —");
    }

    private void showMsg(String msg, boolean success) {
        lblBookingMsg.setText(msg);
        lblBookingMsg.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
