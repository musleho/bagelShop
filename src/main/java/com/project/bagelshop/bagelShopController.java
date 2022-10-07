/**
 * Submission for PROG24178 Final Project - Sheridan Bagel Shop
 * @author Omar Musleh
 * @author Pizaan Tadiwala
 * date submitted: August 8th, 2022
 */

package com.project.bagelshop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class bagelShopController {
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
    @FXML private Label itemSubtotal; //label for subtotal
    @FXML private Label itemTax; //label for tax
    @FXML private Label itemTotal; //label for total

    //Order Elements
    @FXML private Pane itemPanes; //contains the list of items in the order

    //Buttons for removing particular elements
    @FXML private Button remove0;
    @FXML private Button remove1;
    @FXML private Button remove2;
    @FXML private Button remove3;
    @FXML private Button remove4;
    @FXML private Button remove5;

    @FXML private Label orderSubtotal; //label for subtotal
    @FXML private Label orderTax; //label for tax
    @FXML private Label orderTotal; //label for total

    @FXML private Button btnExit; //exit button

    private Order order = new Order(); //contains the order information
    public static final OrderItem s_defaultItem = new OrderItem(); //used to call OrderItem methods before formally constructing an order item to add to the order
    public static final IOHandler ioHandler = new IOHandler();

    public void closeWindow() {
        /**
         * Closes the active window
         */
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void enableToppings() {
        /**
         * Sets all toppings to enabled when an actual bread item is selected
         */
        for (Node topping : toppingsPane.getChildren()) {
            topping.setDisable(false);
        }
    }

    public void disableToppings() {
        /**
         * Sets all toppings to disabled when bread item is None
         */
        for (Node topping : toppingsPane.getChildren()) {
            if (!(topping instanceof Label label && label.getText().contains("Pick")))
                topping.setDisable(true);
        }
    }
//-------------------------------------------FOR MODIFYING THE ORDER---------------------------------------------------
    @FXML
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
        String coffeeItem;
        int coffeeQty;
        ArrayList<String> toppingList;
        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle("Error");
        if (!breadItem.equalsIgnoreCase("none")) {
            breadQty = getQty(qtyBread);
            coffeeItem = getCoffeeType();
            coffeeQty = getQty(qtyCoffee);
            toppingList = getToppingsList();
            OrderItem newItem = new OrderItem(breadItem, breadQty, coffeeItem, coffeeQty, toppingList);
            newItem.calculatePrices();
            boolean successful = order.addToOrder(newItem);
            if (successful) {
                popup.setTitle("Confirmed!");
                popup.setContentText("Your item has been added to your order!");
                setItemDisables();
            }
            else {
                popup.setContentText("Looks like your order is too big! Try removing or modifying an item.");
            }
        }

        else {
            popup.setTitle("Error");
            popup.setContentText("You need to select a bagel. That's why it's a BAGEL shop!");
            //resetForm(); //maybe not needed
        }
        popup.showAndWait();
        updateOrderPrices();
    }

    public void hideElements(Pane pane) {
        /**
         * Hides elements in a given pane to make them invisible and non-interactable
         * @param Pane pane - the pane and children to be made visible
         */
        if (pane.getChildren().size() > 0) {
            for (Node child : pane.getChildren()) {
                child.setDisable(true);
                child.setStyle("-fx-text-fill: white; -fx-background-color: white;");
            }
        }
    }

    public void showElements(Pane pane) {
        /**
         * Shoes elements in a given pane to make them visible and/or interactable
         * @param Pane pane - the pane and children to be made visible
         */
        if (pane.getChildren().size() > 0) {
            for (Node child : pane.getChildren()) {
                child.setDisable(false);
                if (child instanceof Button removeButton) removeButton.setStyle("-fx-text-fill: red; -fx-background-color: white;");
                else child.setStyle("-fx-text-fill: black;");
            }
        }
    }

    private void setItemDisables() {
        /**
         * Uses hideElements() and showElements() to hide or show order items in the Your Order pane
         */
        for (int i = 0; i < 6; i++){
            try {
                OrderItem currentItem = order.getOrder().get(i);
                Pane currentPane = (Pane) itemPanes.getChildren().get(i); //gets whichever entry in the list we need to access
                showElements(currentPane);
                for (Node child : currentPane.getChildren()) {
                    if (child instanceof Label item) {
                        if (!item.getText().contains("$"))
                            item.setText(currentItem.getBreadItem() + " x " +currentItem.getBreadQty());
                        else item.setText(currentItem.getSubtotalAsString());
                    }
                }
            }
            catch (IndexOutOfBoundsException e) { //for items in orders of less than six.
                Pane currentPane = (Pane) itemPanes.getChildren().get(i); //gets whichever entry in the list we need to access
                hideElements(currentPane);
                for (Node child : currentPane.getChildren()) {
                    if (child instanceof Label item && !item.getText().contains("$")) {
                        item.setText("None.");
                    }
                }
            }
        }
    }

    @FXML
    public void removeItem(ActionEvent e) {
        /**
         * Removes an item that has already been added to the order
         * @param ActionEvent e - gets the source of the button click to determine which order item to remove
         */
        Button[] removeButtons = {remove0, remove1, remove2, remove3, remove4, remove5};
        Button source = (Button) e.getSource();
        for (int i = 0; i < removeButtons.length; i++) {
            if (removeButtons[i].equals(source)) {
                order.removeItem(i);
            }
        }
        setItemDisables();
        updateOrderPrices();
    }

    private void updateOrderPrices() {
        /**
         * Updates the labels for the full order value
         */
        orderSubtotal.setText(order.getSubtotalAsString());
        orderTax.setText(order.getTaxAsString());
        orderTotal.setText(order.getTotalAsString());
    }

    @FXML
    public void confirmOrder() throws IOException {
        /**
         * Confirms the order, saves the receipt to a file and sends to printer
         */
        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        if (order.getTotalAsDouble() > 0) {
            popup.setTitle("Confirmed!");
            popup.setContentText("Your order has been confirmed! Thanks!");
            popup.showAndWait();
            saveToFile();
            printReceipt();
            clearOrder(); //resets everything after saving and printing
        }
        else {
            popup.setTitle("Error!");
            popup.setContentText("Cannot place an empty order");
            popup.showAndWait();
        }
    }
    @FXML
    public void cancelOrder() {
        /**
         * Cancels an order and resets all labels
         */
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Are you sure?");
        confirmation.setContentText("Are you sure you want to cancel and reset your entire order?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                clearOrder();
            }
        }
    }

    private void clearOrder() {
        /**
         * Does the actual clearing of things for cancel order
         */
        resetForm();
        order = new Order();
        updateOrderPrices();
        setItemDisables();
    }

//-------------------------------------------FOR DEALING WITH THE ITEM--------------------------------------------------
    private String getBreadType() {
        /**
         * Pulls the bread type from the UI for passing to OrderItems
         */
        for (Toggle bread : breadGroup.getToggles()) {
            if (bread.isSelected()) {return ((RadioButton) bread).getText();}
        }
        return "none";
    }

    private int getQty(TextField qty) {
        /**
         * Gets the quantity of a given textfield and validates it
         * @param TextField qty - the text field containing the presumptive qty value
         */
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
        /**
         * Pulls the coffee type information from the UI to pass to the OrderItem
         */
        for (Toggle coffee : coffeeGroup.getToggles()) {
            if (coffee.isSelected()) {return ((RadioButton) coffee).getText();}
        }
        return "none";
    }

    private ArrayList<String> getToppingsList() {
        /**
         * Determines which topics were requested for passing to OrderItem
         */
        ArrayList<String> toppingsList = new ArrayList<>();
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof CheckBox) { //if the node is a checkbox
                if (((CheckBox) topping).isSelected()) //and that checkbox is selected
                    toppingsList.add(((CheckBox) topping).getText()); //add it to the toppings list
            }
        }
        return toppingsList;
    }

    @FXML
    private void updateFields() {
        /**
         * Uses s_defaultItem as placeholder to calculate the information and sets the appropriate label text
         */
        s_defaultItem.updateItem(getBreadType(), getQty(qtyBread), getCoffeeType(), getQty(qtyCoffee), getToppingsList());
        itemSubtotal.setText(s_defaultItem.getSubtotalAsString());
        itemTax.setText(s_defaultItem.getTaxAsString());
        itemTotal.setText(s_defaultItem.getTotalAsString());
    }

    private void editItem() {
        /**
         * Just a dream :')
         */

    }

    @FXML
    private void calculateOrder() {
        /**
         * Doesn't actually calculate the order total, just gets the info from the order object and does some checking
         * Also updates labels
         */
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
            orderSubtotal.setText(order.getSubtotalAsString());
            orderTax.setText(order.getTaxAsString());
            orderTotal.setText(order.getTotalAsString());
        }
    }

    public void resetForm() {
        /**
         * Resets all the info on the UI and resets s_defaultItem
         */
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
        updateFields();
        disableToppings();
    }

    public void saveToFile() throws IOException {
        /**
         * Saves the current order receipt to a file using ioHandler class method if the order is not empty
         */
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
        /**
         * Sends the current order receipt to the printer if the order is not empty
         */
        calculateOrder();
        if (order.getTotalAsDouble() == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot print receipt for an empty order.");
            popup.showAndWait();
        }

        else {ioHandler.printToPrinter(order);}
    }

}
