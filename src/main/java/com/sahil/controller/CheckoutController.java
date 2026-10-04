package com.sahil.controller;

import com.sahil.model.Booking;
import com.sahil.storage.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * CheckoutController handles the Checkout tab:
 * - Shows all active bookings in a ComboBox and a table
 * - Allows the user to select a booking and check out
 * - On checkout, the room is marked AVAILABLE again
 */
public class CheckoutController implements Initializable {

    @FXML private ComboBox<Booking> cmbActiveBooking;
    @FXML private Label             lblCheckoutMsg;

    @FXML private TableView<Booking>           tblActiveBookings;
    @FXML private TableColumn<Booking, String> colChkBkId;
    @FXML private TableColumn<Booking, String> colChkCustomer;
    @FXML private TableColumn<Booking, String> colChkRoom;
    @FXML private TableColumn<Booking, String> colChkDays;
    @FXML private TableColumn<Booking, String> colChkTotal;

    public static CheckoutController instance;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        // Set up table columns
        colChkBkId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBookingId()));
        colChkCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomer().getName()));
        colChkRoom.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getRoom().getRoomNumber())));
        colChkDays.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getNumberOfDays())));
        colChkTotal.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getTotalAmount())));

        refreshCheckout();
    }

    @FXML
    private void checkout() {
        Booking selected = cmbActiveBooking.getValue();

        if (selected == null) {
            showMsg("Please select an active booking to checkout.", false);
            return;
        }

        boolean success = store.checkout(selected.getBookingId());

        if (success) {
            showMsg("Checkout successful. Room #"
                    + selected.getRoom().getRoomNumber() + " is now available.", true);
        } else {
            showMsg("Checkout failed. Booking may already be checked out.", false);
        }

        refreshCheckout();
    }

    public void refreshCheckout() {
        // Populate the ComboBox with active bookings
        cmbActiveBooking.setItems(FXCollections.observableArrayList(store.getActiveBookings()));

        // Show booking details in the ComboBox dropdown
        cmbActiveBooking.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Booking b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null :
                        b.getBookingId() + " | " + b.getCustomer().getName()
                        + " | Room #" + b.getRoom().getRoomNumber());
            }
        });
        cmbActiveBooking.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Booking b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null :
                        b.getBookingId() + " | " + b.getCustomer().getName()
                        + " | Room #" + b.getRoom().getRoomNumber());
            }
        });

        // Refresh the table
        tblActiveBookings.setItems(FXCollections.observableArrayList(store.getActiveBookings()));
    }

    private void showMsg(String msg, boolean success) {
        lblCheckoutMsg.setText(msg);
        lblCheckoutMsg.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
