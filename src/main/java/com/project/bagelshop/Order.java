package com.project.bagelshop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Order {
    private final ArrayList<OrderItem> order = new ArrayList<>();
    private String orderNum;
    private double subtotal;
    private double tax;
    private double total;
    private String[] receipt;

    public Order() {
        setOrderNum();
    }
    protected boolean addToOrder(OrderItem newItem) {
        double postSubtotal = this.subtotal + newItem.getSubtotalAsDouble(); //determine what the subtotal would be after adding the new item
        if (order.size() < 6 && postSubtotal <= 100) { //if you have less than ten items and the order won't go over $100.00
            newItem.setItemID(order.size()); //set the new item's ID - could call updateItemIDs to be safer but this should always work
            this.order.add(newItem); //add the item to the order
            setPrices(); //ensures that the prices are recalculated when a new item is added
            return true; //shows that adding to order was done successfully, to be passed to controller for GUI output
        }
        return false; //shows that we could not add to order, to be passed to controller for GUI output
    }

    private void setOrderNum() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StringBuilder orderNum = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            orderNum.append(rand.nextInt(6));
        }
        this.orderNum = date + "_" + orderNum; //just makes the order number the date and a random 5-digit sequence
    }

    public String getOrderNum() {return orderNum;}

    public ArrayList<OrderItem> getOrder() {return this.order;}

    private void updateItemIDs() {
        /**
         * Ensures that item IDs match their index in the order list
         * whenever there is a change to the list
         * @return void
         *
         */
        for (int i = 0; i < order.size(); i++) {
            OrderItem item = order.get(i);
            item.setItemID(i);
        }
    }

    private void updateOrder(int itemID, String breadItem, int breadQty, String coffeeItem, int coffeeQty,
                             ArrayList<String> toppingsList) {
        order.get(itemID).updateItem(breadItem, breadQty, coffeeItem, coffeeQty,
                                     toppingsList);
        setPrices(); //ensures prices are updated when an order item is changed.
    }

    protected void removeItem(int itemID) {
        order.remove(itemID);
        updateItemIDs();
        setPrices();
    }

    public void setPrices() {
        subtotal = 0;
        if (order.size() > 0) {
            for (OrderItem item : order) {
                subtotal += item.getSubtotalAsDouble();
            }

            tax = subtotal * 0.13;
            total = subtotal + tax;
        }
        else {
            subtotal = 0;
            tax = 0;
            total = 0;
        }
        createReceipt();
    }

    public double getSubtotalAsDouble() {
        return this.subtotal;
    }

    public String getSubtotalAsString() {
        return String.format("$%.2f", this.subtotal);
    }

    public String getTaxAsString() {return String.format("$%.2f",this.tax);}

    public double getTotalAsDouble() {return this.total;}

    public String getTotalAsString() {return String.format("$%.2f",this.total);}

    public void createReceipt() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String lineBreak = "\n------------------------------------";
        String doubleBreak = lineBreak + lineBreak;
        StringBuilder itemEntries = new StringBuilder();
        for (OrderItem item : order) {
            itemEntries.append(item.getReceiptEntry()).append(lineBreak).append("\n");
        }
        receipt = new String[]{
                "Sheridan Bagel Shop",
                    date,
                    "Order Number: " + orderNum,
                    doubleBreak,
                    itemEntries.toString(),
                    doubleBreak,
                    "Subtotal: " + getSubtotalAsString(),
                    "Tax: " + getTaxAsString(),
                    "Total: " + getTotalAsString()
        };
    }

    public String[] getReceipt() {return this.receipt;}

}
