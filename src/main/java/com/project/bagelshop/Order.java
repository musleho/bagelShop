package com.project.bagelshop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Order {
    private final ArrayList<OrderItem> order = new ArrayList<>();
    private double subtotal;
    private double tax;
    private double total;
    private String orderNum;

    public Order() {
        this.orderNum = orderNumGen();
    }
    protected void addToOrder(OrderItem newItem) {
        this.order.add(newItem);
    }

    public String orderNumGen() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StringBuilder orderNum = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            orderNum.append(rand.nextInt(10));
        }
        return date + "_" + orderNum.toString();
    }
}
