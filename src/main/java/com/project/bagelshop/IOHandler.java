package com.project.bagelshop;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import org.json.*;
import javax.print.*;
import javax.print.attribute.*;

public class IOHandler {
    private String folder;

    public void setFolder(String folder) {
        if (folder.substring(folder.length()-1).equalsIgnoreCase("/")) {
            this.folder = folder;
        }
        else {this.folder = folder + "/";}
    }

    public String getFolder() {return this.folder;}

    public void createReceipt(Order order) throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String lineBreak = "\n------------------------------------";
        String doubleBreak = lineBreak + lineBreak;
        File receipt = new File(folder + order.getOrderNum());
        PrintWriter printer = new PrintWriter(receipt);
        StringBuilder itemEntries = new StringBuilder();
        for (OrderItem item : order.getOrder()) {
            itemEntries.append(item.getReceiptEntry()).append(lineBreak).append("\n");
        }
        String[] printInfo = {
                "Sheridan Bagel Shop",
                date,
                "Order Number: " + order.getOrderNum(),
                doubleBreak,
                itemEntries.toString(),
                doubleBreak,
                "Subtotal: " + order.getSubtotalAsString(),
                "Tax: " + order.getTaxAsString(),
                "Total: " + order.getTotalAsString()
        };

        for (String line : printInfo) {
            printer.write(line + "\n");
        }

        printer.close();
    }
}
