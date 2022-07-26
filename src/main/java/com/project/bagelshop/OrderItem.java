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
    private static HashMap<String, Double> priceTable;
    public static final String[] validToppings = {"cream cheese", "butter", "blueberry jam",
            "raspberry jam", "peach jelly"};
    private int itemID;

    //Fields for the bread
    private String breadItem;
    private int breadQty;
    private double breadPrice;

    //Fields for the coffee
    private String coffeeItem;
    private int coffeeQty;
    private double coffeePrice;

    //Fields for the toppings
    private final ArrayList<String> toppingsList = new ArrayList<>();
    private double toppingsPrice;
    private double subtotal;

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
     * @param breadItem type of bread in this line item as String
     * @param breadQty number of bagels in this line item as int
     * @param coffeeItem type of coffee in this line item as String
     * @param coffeeQty number of coffees in this line item
     * @param toppingsList ArrayList of toppings for the bagels in this line item
     */
    protected OrderItem(String breadItem, int breadQty, String coffeeItem, int coffeeQty,
                        @NotNull ArrayList<String> toppingsList) {
        setBreadItem(breadItem);
        setBreadQty(breadQty);
        setCoffeeItem(coffeeItem);
        setCoffeeQty(coffeeQty);
        for (String topping : toppingsList) {
            addTopping(topping);
        }
        calculatePrices();
    }

    /**
     * @Constructor default constructor for "temp" order item used in controller to call OrderItem methods
     */
    protected OrderItem() {
        reset();
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
    }

    protected void setItemID(int id) {
        itemID = id;
    }

    protected String getBreadItem() {
        return breadItem;
    }

    protected void setBreadItem(@NotNull String breadItem) {
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

    protected double getSubtotal() {
        return this.subtotal;
    }

    private void calculatePrices() {
        setBreadPrice();
        setCoffeePrice();
        setToppingsPrice();
        subtotal = breadPrice + coffeePrice + toppingsPrice;
    }

}
