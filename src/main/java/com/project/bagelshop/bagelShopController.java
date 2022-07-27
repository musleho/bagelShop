package com.project.bagelshop;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.print.*;
import javax.print.attribute.*;

public class bagelShopController {

    //Bread selection
    @FXML private ToggleGroup breadGroup;
    @FXML private RadioButton radNoBread;
    @FXML private TextField qtyBread;

    //Coffee selection
    @FXML private ToggleGroup coffeeGroup;
    @FXML private RadioButton radNoCoffee;
    @FXML private TextField qtyCoffee;

    //Toppings
    @FXML private Pane toppingsPane;

    //Price Displays
    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblTotal;

    @FXML private Button btnExit;

    private final Order order = new Order();
    public static final OrderItem s_defaultItem = new OrderItem(); //used to call OrderItem methods before formally constructing an order item to add to the order
    public static final IOHandler ioHandler = new IOHandler();
    private File receipt;

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
        int breadQty = 0;
        String coffeeItem = "none";
        int coffeeQty = 0;
        ArrayList<String> toppingList = new ArrayList<>();
        if (!breadItem.equals("none")) {
            breadQty = getBreadQty();
            coffeeItem = getCoffeeType();
            coffeeQty = getCoffeeQty();
            toppingList = getToppingsList();
            OrderItem newItem = new OrderItem(breadItem, breadQty, coffeeItem, coffeeQty, toppingList);
            order.addToOrder(newItem);
        }

        else {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("You need to select a bagel. That's why it's a BAGEL shop!");
            popup.showAndWait();
            resetForm();
        }
    }

    private String getBreadType() {
        for (Toggle bread : breadGroup.getToggles()) {
            if (bread.isSelected()) {return ((RadioButton) bread).getText().toLowerCase();}
        }
        return "none";
    }

    private int getBreadQty() {
        try { //block executes if the user enters a valid integer
            int breadQty = Integer.parseInt(qtyBread.getText());
            if (breadQty >= 100) { //if they try to order too much bread, set the label text and return 99
                qtyBread.setText("99");
                return 99;
            }
            else if (breadQty < 1) { //if they try to order too little bread, set the label text and return 1
                qtyBread.setText("1");
                return 1;
            }
            else return breadQty;
        }
        catch (NumberFormatException e) { //otherwise...
            try { //if they entered a double, for some reason
                int breadQty = (int) Double.parseDouble(qtyBread.getText()); //truncate a double entry to int
                if (breadQty >= 100) { //if they try to order too much bread, set the label text and return 99
                    qtyBread.setText("99");
                    return 99;
                }
                else if (breadQty < 1) { //if they try to order too little bread, set the label text and return 1
                    qtyBread.setText("1");
                    return 1;
                }
                else { //otherwise correct the label and return the rounded value
                    qtyBread.setText(Integer.toString(breadQty));
                    return breadQty;
                }
            }
            catch (NumberFormatException err) { //if they enter text or nothing, the value should autoset to 1.
                qtyBread.setText("1");
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

    private int getCoffeeQty() {
        String coffeeType = getCoffeeType();
        if (coffeeType.equals("none")) return 0; //should only be zero if they do not order any type of coffee
        else {
            try {
                int coffeeQty = Integer.parseInt(qtyCoffee.getText());
                if (coffeeQty >= 100) { //if they try to order too much coffee, set the label text and return 99
                    qtyCoffee.setText("99");
                    return 99;
                } else if (coffeeQty < 1) { //if they try to order too little coffee, set the label text and return 1
                    qtyCoffee.setText("1");
                    return 1;
                } else return coffeeQty;
            } catch (NumberFormatException e) {
                try {
                    int coffeeQty = (int) Double.parseDouble(qtyCoffee.getText()); //truncate a double entry to int
                    if (coffeeQty >= 100) { //if they try to order too much coffee, set the label text and return 99
                        qtyCoffee.setText("99");
                        return 99;
                    } else if (coffeeQty < 1) { //if they try to order too little coffee, set the label text and return 1
                        qtyCoffee.setText("1");
                        return 1;
                    } else { //otherwise correct the label and return the rounded value
                        qtyCoffee.setText(Integer.toString(coffeeQty));
                        return coffeeQty;
                    }
                } catch (NumberFormatException err) {
                    qtyCoffee.setText("1"); //if they enter text or nothing, the value should autoset to 1.
                    return 1;
                }
            }
        }
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

    private void calculateTotal() {
        if (radNoBread.isSelected()) {
            //set the labels to 0
            lblSubtotal.setText("$0.00");
            lblTax.setText("$0.00");
            lblTotal.setText("$0.00");

        }

        else {calculate();}
    }

    private void calculateOrderSubtotal() {
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
        calculateTotal();
        disableToppings();
    }

    public void saveToFile() throws IOException {
        calculateTotal();
        if (total == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot save receipt for an empty order.");
            popup.showAndWait();
        }

        else {ioHandler.createReceipt(order);}
    }



    public void printToPrinter() throws IOException {
        save();
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        PrintService service = ServiceUI.printDialog(null, 200, 200, ps, defaultService, flavor, pras);
        if (service != null) {
            try {
                DocPrintJob job = service.createPrintJob();
                DocAttributeSet das = new HashDocAttributeSet();
                FileReader fr = new FileReader("src/main/files/" + receipt.getName());
                Doc doc = new SimpleDoc(fr, flavor, das);

                try {
                    job.print(doc, pras);
                    System.out.println("Job sent to printer.");
                }

                catch (PrintException e) {System.out.println("Print error!" + e.getMessage());}
            }

            catch (FileNotFoundException e) {System.out.println("File not found!" + e.getMessage());}
        }
    }

    public void printReceipt() throws IOException {
        calculateTotal();
        if (total == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot save receipt for an empty order.");
            popup.showAndWait();
        }

        else {printToPrinter();}
    }

}
