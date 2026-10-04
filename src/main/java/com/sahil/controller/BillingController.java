package com.sahil.controller;

import com.sahil.model.Booking;
import com.sahil.storage.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class BillingController implements Initializable {

    @FXML private TextField txtBillingBookingId;
    @FXML private Label     lblBillingMsg;

    @FXML private Label lblBillCustomerName;
    @FXML private Label lblBillContact;
    @FXML private Label lblBillBookingId;
    @FXML private Label lblBillRoomNumber;
    @FXML private Label lblBillRoomType;
    @FXML private Label lblBillDays;

    @FXML private TableView<ChargeRow>           tblCharges;
    @FXML private TableColumn<ChargeRow, String> colChargeDesc;
    @FXML private TableColumn<ChargeRow, String> colChargeQty;
    @FXML private TableColumn<ChargeRow, String> colChargeRate;
    @FXML private TableColumn<ChargeRow, String> colChargeAmount;

    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblGrandTotal;

    private final DataStore store = DataStore.getInstance();
   
    public static class ChargeRow {
        final String description, qty, rate, amount;
        ChargeRow(String description, String qty, String rate, String amount) {
            this.description = description;
            this.qty = qty;
            this.rate = rate;
            this.amount = amount;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colChargeDesc.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().description));
        colChargeQty.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().qty));
        colChargeRate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().rate));
        colChargeAmount.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().amount));

        clearBill();
    }

    @FXML
    private void generateBill() {
        String id = txtBillingBookingId.getText().trim().toUpperCase();

        if (id.isEmpty()) {
            showMsg("Please enter a Booking ID.", false);
            return;
        }

        Booking found = null;
        for (Booking b : store.getBookings()) {
            if (b.getBookingId().equals(id)) {
                found = b;
                break;
            }
        }

        if (found == null) {
            showMsg("No booking found with ID: " + id, false);
            clearBill();
            return;
        }

        populateBill(found);
        showMsg("Bill generated for " + found.getBookingId(), true);
    }

    private void populateBill(Booking b) {
        lblBillCustomerName.setText(b.getCustomer().getName());
        lblBillContact.setText("Contact: " + b.getCustomer().getContactNumber());
        lblBillBookingId.setText(b.getBookingId());
        lblBillRoomNumber.setText(String.valueOf(b.getRoom().getRoomNumber()));
        lblBillRoomType.setText(b.getRoom().getRoomType().name());
        lblBillDays.setText(b.getNumberOfDays() + " day(s)");

        double roomCharges = b.getRoomCharges();
        double tax         = b.getTax();
        double total       = b.getTotalAmount();

        List<ChargeRow> rows = List.of(
            new ChargeRow(
                "Room Charges - Room #" + b.getRoom().getRoomNumber()
                        + " [" + b.getRoom().getRoomType() + "]",
                String.valueOf(b.getNumberOfDays()),
                "Rs. " + b.getRoom().getPricePerDay(),
                String.format("Rs. %.2f", roomCharges)
            ),
            new ChargeRow(
                "Tax @ 18%", "—", "18%",
                String.format("Rs. %.2f", tax)
            )
        );

        tblCharges.setItems(FXCollections.observableArrayList(rows));

        lblSubtotal.setText(String.format("Rs. %.2f", roomCharges));
        lblTax.setText(String.format("Rs. %.2f", tax));
        lblGrandTotal.setText(String.format("Rs. %.2f", total));
    }

    @FXML
    private void resetBilling() {
        txtBillingBookingId.clear();
        lblBillingMsg.setText("");
        clearBill();
    }

    private void clearBill() {
        lblBillCustomerName.setText("—");
        lblBillContact.setText("—");
        lblBillBookingId.setText("—");
        lblBillRoomNumber.setText("—");
        lblBillRoomType.setText("—");
        lblBillDays.setText("—");
        tblCharges.setItems(FXCollections.observableArrayList());
        lblSubtotal.setText("Rs. 0.00");
        lblTax.setText("Rs. 0.00");
        lblGrandTotal.setText("Rs. 0.00");
    }

    private void showMsg(String msg, boolean success) {
        lblBillingMsg.setText(msg);
        lblBillingMsg.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
