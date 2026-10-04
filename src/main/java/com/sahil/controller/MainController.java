package com.sahil.controller;

import com.sahil.storage.DataStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblDate;
    @FXML private Label lblFooterInfo;
    @FXML private TabPane mainTabPane;
    @FXML private Tab tabBooking;
    @FXML private Tab tabCheckout;
    @FXML private Tab tabDashboard;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        lblDate.setText(date);
        updateFooter();

        // When the user clicks on the Booking, Checkout, or Dashboard tab,
        // tell that tab's controller to reload its data from DataStore
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldTab, newTab) -> {
                if (newTab == tabBooking) {
                    BookingController.instance.refreshForm();
                }
                if (newTab == tabCheckout) {
                    CheckoutController.instance.refreshCheckout();
                }
                if (newTab == tabDashboard) {
                    DashboardController.instance.refreshDashboard();
                }
                updateFooter();
            }
        );
    }

    public void updateFooter() {
        lblFooterInfo.setText(
            "Total Rooms: " + store.getRooms().size()
            + "   |   Available: " + store.getAvailableRooms().size()
            + "   |   Active Bookings: " + store.getActiveBookings().size()
        );
    }
}