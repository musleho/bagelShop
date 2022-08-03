package com.project.bagelshop;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import org.json.*;
import javax.print.*;
import javax.print.attribute.*;

public class IOHandler {
    private String folder;
    private File receipt; //the current receipt

    public void setFolder(String folder) {
        if (folder.substring(folder.length()-1).equalsIgnoreCase("/")) {
            this.folder = folder;
        }
        else {this.folder = folder + "/";}
    }

    public String getFolder() {return this.folder;}

    public void createReceipt(Order order) throws IOException {
        receipt = new File(folder + order.getOrderNum());
        PrintWriter printer = new PrintWriter(receipt);
        for (String line : order.getReceipt()) {
            printer.write(line + "\n");
        }
        printer.close();
    }

    public void printToPrinter(Order order) throws IOException {
        if (receipt == null) {
            createReceipt(order);
        }
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        PrintService service = ServiceUI.printDialog(null, 200, 200, ps, defaultService, flavor, pras);
        if (service != null) {
            try {
                DocPrintJob job = service.createPrintJob();
                DocAttributeSet das = new HashDocAttributeSet();
                FileReader fr = new FileReader(receipt);
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

}
