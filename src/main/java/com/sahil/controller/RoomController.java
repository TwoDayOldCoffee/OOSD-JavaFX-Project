package com.sahil.controller;

import com.sahil.model.Room;
import com.sahil.storage.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*
 * RoomController handles everything on the Rooms tab:
 * - Adding new rooms
 * - Viewing all rooms in the table
 * - Filtering by availability
 */
public class RoomController implements Initializable {

    @FXML private TextField txtRoomNumber;
    @FXML private ComboBox<String> cmbRoomType;
    @FXML private TextField txtRoomPrice;
    @FXML private Label lblRoomMsg;

    @FXML private ComboBox<String> cmbFilterStatus;

    @FXML private TableView<Room>           tblRooms;
    @FXML private TableColumn<Room, String> colRoomNumber;
    @FXML private TableColumn<Room, String> colRoomType;
    @FXML private TableColumn<Room, String> colRoomPrice;
    @FXML private TableColumn<Room, String> colRoomStatus;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate ComboBoxes
        cmbRoomType.setItems(FXCollections.observableArrayList("SINGLE", "DOUBLE", "DELUXE"));
        cmbFilterStatus.setItems(FXCollections.observableArrayList("All Rooms", "AVAILABLE", "OCCUPIED"));
        cmbFilterStatus.getSelectionModel().selectFirst();

        // Set up table columns using lambda (connects each column to a field in Room)
        colRoomNumber.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getRoomNumber())));
        colRoomType.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getRoomType().name()));
        colRoomPrice.setCellValueFactory(c ->
                new SimpleStringProperty("Rs. " + c.getValue().getPricePerDay()));
        colRoomStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));

        refreshTable();
    }

    @FXML
    private void addRoom() {
        lblRoomMsg.setText("");
        String numStr   = txtRoomNumber.getText().trim();
        String typeStr  = cmbRoomType.getValue();
        String priceStr = txtRoomPrice.getText().trim();

        // Validation
        if (numStr.isEmpty() || typeStr == null || priceStr.isEmpty()) {
            showMsg("Please fill in all fields.", false);
            return;
        }

        int roomNum;
        double price;
        try {
            roomNum = Integer.parseInt(numStr);
            if (roomNum <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showMsg("Room number must be a positive whole number.", false);
            return;
        }

        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showMsg("Price must be a positive number.", false);
            return;
        }

        Room.RoomType type = Room.RoomType.valueOf(typeStr);
        Room room = new Room(roomNum, type, price);

        if (store.addRoom(room)) {
            showMsg("Room " + roomNum + " added successfully.", true);
            clearRoomForm();
            refreshTable();
        } else {
            showMsg("Room number " + roomNum + " already exists.", false);
        }
    }

    @FXML
    private void clearRoomForm() {
        txtRoomNumber.clear();
        txtRoomPrice.clear();
        cmbRoomType.getSelectionModel().clearSelection();
        lblRoomMsg.setText("");
    }

    @FXML
    private void applyFilter() {
        String filter = cmbFilterStatus.getValue();
        if (filter == null || filter.equals("All Rooms")) {
            refreshTable();
            return;
        }
        List<Room> filtered = new ArrayList<>();
        for (Room r : store.getRooms()) {
            if (r.getStatus().name().equals(filter)) {
                filtered.add(r);
            }
        }
        tblRooms.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void showAll() {
        cmbFilterStatus.getSelectionModel().selectFirst();
        refreshTable();
    }

    private void refreshTable() {
        tblRooms.setItems(FXCollections.observableArrayList(store.getRooms()));
    }

    private void showMsg(String msg, boolean success) {
        lblRoomMsg.setText(msg);
        lblRoomMsg.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
