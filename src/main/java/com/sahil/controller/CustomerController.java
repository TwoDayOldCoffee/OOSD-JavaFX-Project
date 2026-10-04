package com.sahil.controller;

import com.sahil.model.Customer;
import com.sahil.storage.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * CustomerController handles the Customers tab:
 * - Registering a new customer (name + contact)
 * - Displaying all registered customers in a table
 */
public class CustomerController implements Initializable {

    @FXML private TextField txtCustomerName;
    @FXML private TextField txtContactNumber;
    @FXML private Label lblCustomerMsg;

    @FXML private TableView<Customer>           tblCustomers;
    @FXML private TableColumn<Customer, String> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerName;
    @FXML private TableColumn<Customer, String> colCustomerContact;

    private final DataStore store = DataStore.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCustomerId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCustomerId()));
        colCustomerName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));
        colCustomerContact.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getContactNumber()));

        refreshTable();
    }

    @FXML
    private void addCustomer() {
        String name    = txtCustomerName.getText().trim();
        String contact = txtContactNumber.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            showMsg("Please fill in all fields.", false);
            return;
        }
        if (!contact.matches("\\d{10}")) {
            showMsg("Contact number must be exactly 10 digits.", false);
            return;
        }

        Customer customer = store.addCustomer(name, contact);
        showMsg("Customer registered: " + customer.getCustomerId() + " - " + customer.getName(), true);
        clearCustomerForm();
        refreshTable();
    }

    @FXML
    private void clearCustomerForm() {
        txtCustomerName.clear();
        txtContactNumber.clear();
        lblCustomerMsg.setText("");
    }

    private void refreshTable() {
        tblCustomers.setItems(FXCollections.observableArrayList(store.getCustomers()));
    }

    private void showMsg(String msg, boolean success) {
        lblCustomerMsg.setText(msg);
        lblCustomerMsg.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
