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
    public static HashMap<String, Double> priceTable;
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

    private String breadItem;
    private int breadQty;
    private double breadPrice;

    private String coffeeItem;
    private int coffeeQty;
    private double coffeePrice;
    private final ArrayList<String> toppingsList = new ArrayList<>();
    private double toppingsPrice;
    private double subtotal;
    private double tax;
    private double total;
    private File receipt;

    static { //static initializer for priceTable
        priceTable = new HashMap<>();
        priceTable.put("none", 0.0); //just a placeholder
        priceTable.put("white", 1.25);
        priceTable.put("whole wheat", 1.5);
        priceTable.put("regular", 1.25);
        priceTable.put("cappuccino", 2.0);
        priceTable.put("cafe au lait", 1.75);
        priceTable.put("cream cheese", 0.5);
        priceTable.put("butter", 0.25);
        priceTable.put("blueberry jam", 0.75);
        priceTable.put("raspberry jam", 0.75);
        priceTable.put("peach jelly", 0.75);
    }

    public void closeWindow() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void enableToppings() {
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof Label toppingLabel) {
                if (!toppingLabel.getText().equals("Pick your Toppings")) {
                    toppingLabel.setTextFill(Color.BLACK);
                }
            }
            if (topping instanceof CheckBox) {
                topping.setDisable(false);
            }
        }
    }

    public void disableToppings() {
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof Label toppingLabel) {
                if (!toppingLabel.getText().equals("Pick your Toppings")) {
                    toppingLabel.setTextFill(Color.LIGHTGRAY);
                }
            }
            if (topping instanceof CheckBox) {
                topping.setDisable(true);
            }
        }
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
            toppingsPrice = 0;
            toppingsList.clear();
            subtotal = 0;
            tax = 0;
            total = 0;
        }

        else {calculate();}
    }

    private void getBreadPrice() {
        breadPrice = 0;

        try {breadQty = Integer.parseInt(qtyBread.getText());}
        catch (NumberFormatException e) {breadQty = 1;}

        for (Toggle breadChoice : breadGroup.getToggles()) {
            if (breadChoice.isSelected()) {
                breadItem = ((RadioButton) breadChoice).getText();
                breadPrice = priceTable.get(breadItem.toLowerCase()) * breadQty;
                break;
            }
        }
    }

    private void getCoffeePrice() {
        coffeePrice = 0;

        try {coffeeQty = Integer.parseInt(qtyCoffee.getText());}
        catch (NumberFormatException e) {coffeeQty = 1;}

        for (Toggle coffeeChoice : coffeeGroup.getToggles()) {
            if (coffeeChoice.isSelected()) {
                coffeeItem = ((RadioButton) coffeeChoice).getText();
                coffeePrice = priceTable.get(coffeeItem.toLowerCase()) * coffeeQty;
            }
        }
    }

    private void getToppingsPrice() {
        toppingsPrice = 0;
        toppingsList.clear();
        for (Node topping : toppingsPane.getChildren()) {
            if (topping instanceof CheckBox) {
                if (((CheckBox) topping).isSelected()) {
                    String toppingText = ((CheckBox) topping).getText();
                    toppingsPrice += priceTable.get(toppingText.toLowerCase()) * breadQty;
                    String price = "$" + String.format("%.2f", (priceTable.get(toppingText.toLowerCase()) * breadQty));
                    String tabs = toppingText.equalsIgnoreCase("butter") ? "\t\t\t\t"
                                    : toppingText.equalsIgnoreCase("peach jelly") ? "\t\t\t" : "\t\t";
                    toppingsList.add("\n\t" + (toppingText + tabs + price));
                }
            }
        }
    }

    private void calculate() {
        getBreadPrice(); getCoffeePrice(); getToppingsPrice(); //calculate the prices for each
        subtotal = breadPrice + coffeePrice + toppingsPrice;
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
        disableToppings();
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
        receipt = new File("src/main/files/" + date + "_" + orderNum + ".txt");
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

        StringBuilder toppings = new StringBuilder();
        for (String topping : toppingsList) {
            toppings.append(topping);
        }

        String[] printInfo = {
                "Sheridan Bagel Shop",
                date,
                "Order Number: " + orderNum,
                lineBreak,
                bread,
                toppings.toString(),
                coffee,
                "\t\t\tSubtotal: $" + String.format("%.2f", subtotal),
                "\t\t\tTax: $" + String.format("%.2f", tax),
                "\t\t\tTotal: $" + String.format("%.2f", total)
        };

        for (String line : printInfo) {
            printer.write(line + "\n");
        }

        printer.close();
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
                FileInputStream fis = new FileInputStream("src/main/files/" + receipt.getName());
                Doc doc = new SimpleDoc(fis, flavor, das);
                try {
                    job.print(doc, pras);
                    System.out.println("Job sent to printer.");
                    //fis.close();
                } catch (PrintException e) {
                    System.out.println("Print error!" + e.getMessage());
                }
                //fis.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!" + e.getMessage());
            }
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
