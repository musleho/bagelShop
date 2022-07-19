package com.project.bagelshop;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class bagelShopController {

    //Bread selection
    @FXML private ToggleGroup breadGroup;
    @FXML private RadioButton radNoBread;
    @FXML private RadioButton radWhite;
    @FXML private RadioButton radWheat;
    @FXML private TextField qtyBread;

    //Coffee selection
    @FXML private ToggleGroup coffeeGroup;
    @FXML private RadioButton radNoCoffee;
    @FXML private RadioButton radRegular;
    @FXML private RadioButton radCapp;
    @FXML private RadioButton radCafe;
    @FXML private TextField qtyCoffee;

    //Toppings
    @FXML private Pane toppingsPane;
    @FXML private CheckBox chkCreamCheese;
    @FXML private CheckBox chkButter;
    @FXML private CheckBox chkBlueberry;
    @FXML private CheckBox chkRaspberry;
    @FXML private CheckBox chkPeach;

    //Price Displays
    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblTotal;

    @FXML private Button btnExit;

    private String breadItem;
    private int breadQty;
    private double breadPrice;

    private String coffeeItem;
    private int coffeeQty;
    private double coffeePrice;
    private final ArrayList<String> toppingsList = new ArrayList<>();
    private double subtotal;
    private double tax;
    private double total;

    //Builds the price table and provides easy reference for maintainability.
    //Called in the calculate method
    private HashMap<Node, Double> buildPriceTable(){
        HashMap<Node, Double> priceTable = new HashMap<>();
        priceTable.put(radNoBread, 0.0); //just a placeholder
        priceTable.put(radWhite, 1.25);
        priceTable.put(radWheat, 1.5);
        priceTable.put(radNoCoffee, 0.0);
        priceTable.put(radRegular, 1.25);
        priceTable.put(radCapp, 2.0);
        priceTable.put(radCafe, 1.75);
        priceTable.put(chkCreamCheese, 0.5);
        priceTable.put(chkButter, 0.25);
        priceTable.put(chkBlueberry, 0.75);
        priceTable.put(chkRaspberry, 0.75);
        priceTable.put(chkPeach, 0.75);
        return priceTable;
    }

    public void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void calculateTotal() {
        if (radNoBread.isSelected()) {
            //set the labels to 0
            lblSubtotal.setText("$0.00");
            lblTax.setText("$0.00");
            lblTotal.setText("$0.00");

            //set the instance variables to defaults
            breadItem = "None";
            breadQty = 0;
            breadPrice = 0;
            coffeeItem = "None";
            coffeeQty = 0;
            coffeePrice = 0;
            toppingsList.clear();
            subtotal = 0;
            tax = 0;
            total = 0;
        }

        else {calculate();}
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void calculate() {
        HashMap<Node, Double> priceTable = buildPriceTable();
        subtotal = 0; //value for subtotal

        //light exception handling for the qty fields.
        try {breadQty = Integer.parseInt(qtyBread.getText());}
        catch (NumberFormatException e) {breadQty = 1;}

        try {coffeeQty = Integer.parseInt(qtyCoffee.getText());}
        catch (NumberFormatException e) {coffeeQty = 1;}

        //adds the bread cost and sets variables
        for (Toggle breadChoice : breadGroup.getToggles()) {
            if (breadChoice.isSelected()) {
                subtotal += priceTable.get(breadChoice) * breadQty;
                breadPrice = priceTable.get(breadChoice) * breadQty;
                String rawText = ((RadioButton) breadChoice).getText();
                try {breadItem = rawText.substring(0, rawText.indexOf("(") - 1);}
                catch (StringIndexOutOfBoundsException e) {breadItem = rawText;}
            }
        }

        //adds the coffee cost and sets variables
        for (Toggle coffeeChoice : coffeeGroup.getToggles()) {
            if (coffeeChoice.isSelected()) {
                subtotal += priceTable.get(coffeeChoice) * coffeeQty;
                coffeePrice = priceTable.get(coffeeChoice) * coffeeQty;
                String rawText = ((RadioButton) coffeeChoice).getText();
                try {coffeeItem = rawText.substring(0, rawText.indexOf("(") - 1);}
                catch (StringIndexOutOfBoundsException e) {coffeeItem = rawText;}
            }
        }

        //adds the toppings cost
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof CheckBox) {
                if (((CheckBox) topping).isSelected()) {
                    String price = "$" + String.format("%.2f", (priceTable.get(topping) * breadQty));
                    subtotal += priceTable.get(topping) * breadQty;
                    String rawText = ((CheckBox)topping).getText();
                    String tabs = rawText.equals("Butter ($0.25)") ? "\t\t\t\t" : "\t\t";
                    toppingsList.add("\n\t" + (rawText.substring(0, rawText.indexOf("(") - 1) + tabs + price));
                }
            }
        }

        //calculate the other price elements
        tax = subtotal * 0.13;
        total = subtotal + tax;

        //if the subtotal is too high or user inputs invalid order quantities, reject the order and reset.
        if (subtotal >= 100 || breadQty < 1 || coffeeQty < 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Looks like you're doing something odd!");
            popup.showAndWait();
            resetForm();
        }

        //otherwise, update the labels
        else {
            lblSubtotal.setText("$" + String.format("%.2f", subtotal));
            lblTax.setText("$" + String.format("%.2f", tax));
            lblTotal.setText("$" + String.format("%.2f", total));
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
    }

    public String orderNumGen() {
        StringBuilder orderNum = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            orderNum.append(rand.nextInt(10));
        }
        return orderNum.toString();
    }

    public void saveToFile() throws IOException {
        calculateTotal();
        if (total == 0) {
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Error");
            popup.setContentText("Cannot save receipt for an empty order.");
            popup.showAndWait();
        }

        else {save();}
    }

    private void save() throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String orderNum = orderNumGen();
        String lineBreak = "--------------------------";
        File receipt = new File("src/main/files/" + date + "_" + orderNum + ".txt");
        PrintWriter printer = new PrintWriter(receipt);
        String bread = "";
        for (Toggle breadChoice : breadGroup.getToggles()) {
            if (breadChoice.isSelected()) {
                String price = "$" +  String.format("%.2f", breadPrice);
                bread = breadItem + "\t" + breadQty + "\t\t" + price;
                break;
            }
        }

        String coffee = "\n";
        for (Toggle coffeeChoice : coffeeGroup.getToggles()) {
            if (coffeeChoice.isSelected() && !((RadioButton) coffeeChoice).getText().equals("None")) {
                String price = "$" +  String.format("%.2f", coffeePrice);
                coffee = "\n" + coffeeItem + "\t" + coffeeQty + "\t\t" + price;
                break;
            }
        }
        coffee += "\n"+lineBreak;

        String toppings = "";
        for (String topping : toppingsList) {
            toppings += topping;
        }

        String[] printInfo = {
                "Sheridan Bagel Shop",
                date,
                "Order Number: " + orderNum,
                lineBreak,
                bread,
                toppings,
                coffee,
                "\t\t\tSubtotal: $" + String.format("%.2f", subtotal),
                "\t\t\tTax: $" + String.format("%.2f", tax),
                "\t\t\tTotal: $" + String.format("%.2f", total)
        };

        for (String line : printInfo) {
            printer.write(line + "\n");
            System.out.println(line);
        }
        printer.close();
    }

    public void printReceipt() {
        //send receipt to the printer
    }

}
