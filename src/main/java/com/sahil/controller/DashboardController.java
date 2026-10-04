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

public class DashboardController implements Initializable {

    @FXML private Label lblTotalRooms;
    @FXML private Label lblAvailableRooms;
    @FXML private Label lblOccupiedRooms;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblActiveBookings;

    @FXML private TableView<Booking>           tblRecentBookings;
    @FXML private TableColumn<Booking, String> colDashBkId;
    @FXML private TableColumn<Booking, String> colDashCustomer;
    @FXML private TableColumn<Booking, String> colDashRoom;
    @FXML private TableColumn<Booking, String> colDashDays;
    @FXML private TableColumn<Booking, String> colDashTotal;
    @FXML private TableColumn<Booking, String> colDashStatus;

    public static DashboardController instance;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        colDashBkId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getBookingId()));
        colDashCustomer.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomer().getName()));
        colDashRoom.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getRoom().getRoomNumber())));
        colDashDays.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getNumberOfDays())));
        colDashTotal.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getTotalAmount())));
        colDashStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));

        refreshDashboard();
    }

    public void refreshDashboard() {
        lblTotalRooms.setText(String.valueOf(store.getRooms().size()));
        lblAvailableRooms.setText(String.valueOf(store.getAvailableRooms().size()));
        lblOccupiedRooms.setText(String.valueOf(
                store.getRooms().size() - store.getAvailableRooms().size()));
        lblTotalCustomers.setText(String.valueOf(store.getCustomers().size()));
        lblActiveBookings.setText(String.valueOf(store.getActiveBookings().size()));

        tblRecentBookings.setItems(FXCollections.observableArrayList(store.getActiveBookings()));
    }
}
