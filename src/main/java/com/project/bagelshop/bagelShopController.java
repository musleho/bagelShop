package com.project.bagelshop;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class bagelShopController {
    //TODO revise GUI for multiple order items
    //TODO add edit and remove order item buttons
    //TODO add checkout page

    //Bread selection
    @FXML private ToggleGroup breadGroup; //contains bread choice radio buttons
    @FXML private RadioButton radNoBread; //the "none" radio button for bread
    @FXML private TextField qtyBread; //the textfield containing the user's desired quantity

    //Coffee selection
    @FXML private ToggleGroup coffeeGroup; //contains coffee choice radio buttons
    @FXML private RadioButton radNoCoffee; //the "none radio button for coffee
    @FXML private TextField qtyCoffee; //the textfield containing

    //Toppings
    @FXML private Pane toppingsPane; //contains labels and checkboxes for toppings

    //Price Displays
    @FXML private Label lblSubtotal; //label for subtotal
    @FXML private Label lblTax; //label for tax
    @FXML private Label lblTotal; //label for total

    @FXML private Button btnExit; //exit button

    private final Order order = new Order(); //contains the order information
    public static final OrderItem s_defaultItem = new OrderItem(); //used to call OrderItem methods before formally constructing an order item to add to the order
    public static final IOHandler ioHandler = new IOHandler();
    private File receipt; //probably will be removed and delegated to IOHandler

    public void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void enableToppings() {
        for (Node topping : toppingsPane.getChildren()) {
            topping.setDisable(false);
        }
    }

    public void disableToppings() {
        for (Node topping : toppingsPane.getChildren()) {
            topping.setDisable(true);
        }
    }

    private void addToOrder() {
        /**
         * Pulls info from the GUI and passes it into the OrderItem constructor
         * It then adds the new OrderItem object to the Order object order
         * If the bread selection is none, it just throws a popup error box
         */
        //initializes the variables we will eventually pass to the OrderItem constructor
        //could just declare them, but initializing adds extra precaution to avoid Null Pointer Exceptions downstream
        String breadItem = getBreadType();
        int breadQty;
        String coffeeItem = "none";
        int coffeeQty;
        ArrayList<String> toppingList = new ArrayList<>();
        if (!breadItem.equals("none")) {
            breadQty = getQty(qtyBread);
            coffeeItem = getCoffeeType();
            coffeeQty = getQty(qtyCoffee);
            toppingList = getToppingsList();
            OrderItem newItem = new OrderItem(breadItem, breadQty, coffeeItem, coffeeQty, toppingList);
            newItem.calculatePrices();
            order.addToOrder(newItem);
        }

        else {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("You need to select a bagel. That's why it's a BAGEL shop!");
            popup.showAndWait();
            //resetForm(); //maybe not needed
        }
    }

    private String getBreadType() {
        for (Toggle bread : breadGroup.getToggles()) {
            if (bread.isSelected()) {return ((RadioButton) bread).getText().toLowerCase();}
        }
        return "none";
    }

    private int getQty(TextField qty) {
        try { //block executes if the user enters a valid integer
            int breadQty = Integer.parseInt(qty.getText());
            if (breadQty >= 100) { //if they try to order too much bread, set the label text and return 99
                qty.setText("99");
                return 99;
            }
            else if (breadQty < 1) { //if they try to order too little bread, set the label text and return 1
                qty.setText("1");
                return 1;
            }
            else return breadQty;
        }
        catch (NumberFormatException e) { //otherwise...
            try { //if they entered a double, for some reason
                int breadQty = (int) Double.parseDouble(qty.getText()); //truncate a double entry to int
                if (breadQty >= 100) { //if they try to order too much bread, set the label text and return 99
                    qty.setText("99");
                    return 99;
                }
                else if (breadQty < 1) { //if they try to order too little bread, set the label text and return 1
                    qty.setText("1");
                    return 1;
                }
                else { //otherwise correct the label and return the rounded value
                    qty.setText(Integer.toString(breadQty));
                    return breadQty;
                }
            }
            catch (NumberFormatException err) { //if they enter text or nothing, the value should autoset to 1.
                qty.setText("1");
                return 1;
            }
        }
    }

    private String getCoffeeType() {
        for (Toggle coffee : coffeeGroup.getToggles()) {
            if (coffee.isSelected()) {return ((RadioButton) coffee).getText().toLowerCase();}
        }
        return "none";
    }

    private ArrayList<String> getToppingsList() {
        ArrayList<String> toppingsList = new ArrayList<>();
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof CheckBox) { //if the node is a checkbox
                if (((CheckBox) topping).isSelected()) //and that checkbox is selected
                    toppingsList.add(((CheckBox) topping).getText().toLowerCase()); //add it to the toppings list
            }
        }
        return toppingsList;
    }

    @FXML
    private void calculateTotal() {
        if (radNoBread.isSelected()) {
            //set the labels to 0
            lblSubtotal.setText("$0.00");
            lblTax.setText("$0.00");
            lblTotal.setText("$0.00");

        }

        else {s_defaultItem.calculatePrices();}
    }

    @FXML
    private void calculateOrder() {
        //if the subtotal is too high or user inputs invalid order quantities, reject the order and reset.
        if (order.getSubtotalAsDouble() >= 100) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Looks like you're doing something odd!");
            popup.showAndWait();
            resetForm();
        }

        //otherwise, update the labels
        else {
            lblSubtotal.setText(order.getSubtotalAsString());
            lblTax.setText(order.getTaxAsString());
            lblTotal.setText(order.getTotalAsString());
        }
    }

    public void resetForm() {
        radNoBread.setSelected(true);
        radNoCoffee.setSelected(true);
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof CheckBox) {
                ((CheckBox) topping).setSelected(false);
            }
        }
        qtyBread.setText("");
        qtyCoffee.setText("");
        s_defaultItem.reset();
        //should call update() method
        disableToppings();
    }

    //TODO create update method which makes all appropriate changes to s_defaultItem and GUI
    //TODO set update() method as onActionEvent and onKeyPress for the AnchorPane parent node
    //TODO create methods to pass info from GUI to s_defaultItem for managing
    //TODO make a method updateLabels to update labels based on s_defaultItem prices

    public void saveToFile() throws IOException {
        order.setPrices();
        if (order.getTotalAsDouble() == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot save receipt for an empty order.");
            popup.showAndWait();
        }

        else {ioHandler.createReceipt(order);}
    }

    @FXML
    public void printReceipt() throws IOException {
        calculateTotal();
        if (order.getTotalAsDouble() == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot print receipt for an empty order.");
            popup.showAndWait();
        }

        else {ioHandler.printToPrinter(order);}
    }

}
