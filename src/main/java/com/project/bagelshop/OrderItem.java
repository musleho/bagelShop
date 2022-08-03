package com.project.bagelshop;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A concrete class used to represent a single order item inclusive of the bagel choice, coffee, and toppings.
 * Unlike in version one of this application, all details related to an orderItem are built into the orderItem
 * class, which greatly simplifies/reduces the methods needed in the bagelShopController class
 *
 * @author Omar Musleh
 */

public class OrderItem {

    //DONE: Add method for editing OrderItems
    private static HashMap<String, Double> priceTable; //IDEA thinks this should be final except I initialize it later
    public static final String[] validToppings = {"cream cheese", "butter", "blueberry jam",
            "raspberry jam", "peach jelly"};
    private int itemID; //corresponds to item's index in the order list (see Order class)

    //Fields for the bread
    private String breadItem; //name of the bread item (none, white, or wheat)
    private int breadQty; //duh
    private double breadPrice; //duh

    //Fields for the coffee
    private String coffeeItem; //duh
    private int coffeeQty; //duh
    private double coffeePrice; //duh

    //Fields for the toppings
    private final ArrayList<String> toppingsList = new ArrayList<>();
    private double toppingsPrice;
    private double subtotal;
    private String receiptEntry;

    //static initializer for priceTable
    static {
        priceTable = new HashMap<>();
        priceTable.put("none", 0.0); //just a placeholder since this has no value
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

    /**
     * @Constructor with params to build new line items
     * @param breadItem String - type of bread in this line item as String
     * @param breadQty int - number of bagels in this line item as int
     * @param coffeeItem String - type of coffee in this line item as String
     * @param coffeeQty int - number of coffees in this line item
     * @param toppingsList ArrayList - Strings of toppings for the bagels in this line item
     */
    protected OrderItem(String breadItem, int breadQty, String coffeeItem, int coffeeQty,
                        ArrayList<String> toppingsList) {
        updateItem(breadItem, breadQty, coffeeItem, coffeeQty, toppingsList);
    }

    /**
     * @Constructor default constructor for "temp" order item used in controller to call OrderItem methods
     */
    protected OrderItem() {
        reset();
    }

    protected void updateItem(String breadItem, int breadQty, String coffeeItem, int coffeeQty,
                              ArrayList<String> toppingsList) {
        if (!breadItem.equalsIgnoreCase(this.breadItem)) setBreadItem(breadItem);
        if (breadQty != this.breadQty) setBreadQty(breadQty);
        if (!coffeeItem.equalsIgnoreCase(this.coffeeItem)) setCoffeeItem(coffeeItem);
        if (coffeeQty != this.coffeeQty) setCoffeeQty(coffeeQty);
        for (String topping : toppingsList) {
            addTopping(topping);
        }
        calculatePrices();
    }

    protected void reset() {
        this.breadItem = "none";
        this.breadQty = 0;
        this.breadPrice = 0;
        this.coffeeItem = "none";
        this.coffeeQty = 0;
        this.coffeePrice = 0;
        this.toppingsList.clear();
        this.toppingsPrice = 0;
        this.subtotal = 0;
    }

    //Getters and setters
    protected int getItemID() {
        return itemID;
    } //likely useful for associating remove button with order item

    protected void setItemID(int id) {
        itemID = id;
    }

    protected String getBreadItem() {
        return breadItem;
    }

    protected void setBreadItem(String breadItem) {
        if (breadItem.equalsIgnoreCase("white") || breadItem.equalsIgnoreCase("wheat")) {
            this.breadItem = breadItem;
        } else {
            this.breadItem = "none";
        }
    }

    protected int getBreadQty() {
        return breadQty;
    }

    protected void setBreadQty(int breadQty) {
        if (breadQty > 0 && breadQty < 100) this.breadQty = breadQty;
        else this.breadQty = 0;
    }

    protected double getBreadPrice() {
        return breadPrice;
    }

    protected void setBreadPrice() {
        breadPrice = priceTable.get(breadItem) * breadQty;
    }

    protected String getCoffeeItem() {
        return coffeeItem;
    }

    protected void setCoffeeItem(String coffeeItem) {
        this.coffeeItem = "none";
        for (String coffeeName : priceTable.keySet()) {
            if (coffeeItem.equalsIgnoreCase(coffeeName)) {
                this.coffeeItem = coffeeItem;
                break;
            }
        }
    }

    protected int getCoffeeQty() {
        return coffeeQty;
    }

    protected double getCoffeePrice() {
        return this.coffeePrice;
    }

    protected void setCoffeePrice() {
        this.coffeePrice = priceTable.get(coffeeItem.toLowerCase()) * coffeeQty;
    }

    protected void setCoffeeQty(int coffeeQty) {
        if (coffeeQty > 0 && coffeeQty < 100) this.coffeeQty = coffeeQty;
        else this.coffeeQty = 0;
    }

    protected ArrayList<String> getToppingsList() {
        return toppingsList;
    }

    protected void addTopping(String topping) {
        boolean isValid = false;
        for (String validTopping : validToppings) {
            if (topping.equalsIgnoreCase(validTopping)) {
                isValid = true;
                break;
            }
        }
        if (isValid) toppingsList.add(topping);
    }

    protected void setToppingsPrice() {
        for (String topping : toppingsList) {
            toppingsPrice += priceTable.get(topping) * breadQty;
        }
    }

    protected double getToppingsPrice() {
        return this.toppingsPrice;
    }

    protected double getSubtotalAsDouble() {
        return this.subtotal;
    } //for calculation

    protected String getSubtotalAsString() {return String.format("$%.2f", this.subtotal);} //for printing

    protected void calculatePrices() {
        setBreadPrice();
        setCoffeePrice();
        setToppingsPrice();
        subtotal = breadPrice + coffeePrice + toppingsPrice;
        setReceiptEntry();
    }

    private void setReceiptEntry() {
        if (breadItem.equalsIgnoreCase("none")) {
            receiptEntry = "";
        }
        else {
            String lineBreak = "--------------------------";
            String bread = breadItem + "\t" + breadQty + "\t\t" + breadPrice;

            String coffee = "\n";
            if (!coffeeItem.equalsIgnoreCase("none")) {
                coffee = "\n" + coffeeItem + "\t" + coffeeQty + "\t\t" + coffeePrice;
            }
            coffee += "\n" + lineBreak;

            StringBuilder toppings = new StringBuilder();
            for (String topping : toppingsList) {
                toppings.append(topping);
            }
        }
    }

    protected String getReceiptEntry() {return this.receiptEntry;}

}
