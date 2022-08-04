package com.project.bagelshop;

import java.io.*;

import javax.print.*;
import javax.print.attribute.*;

public class IOHandler {
    private File receipt; //the current receipt

    public void createReceipt(Order order) throws IOException {
        String folder = "src/main/files/"; //default file path
        receipt = new File(folder + order.getOrderNum() + ".txt");
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
