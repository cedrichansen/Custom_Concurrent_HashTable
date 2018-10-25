import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Random;

public class Item {

    private final int upcCode;
    private final String description;
    private float price;
    private Item next;

    public Item(int upcCode, String description, float price) {
        this.upcCode = upcCode;
        this.description = description;
        this.price = price;
    }



    public void addToEnd(Item item){

        Item current = this;
        while(current.next != null) {
            current = current.next;
        }

        current.next = item;
    }


    public int hash(){
        char [] upc = (this.upcCode + "").toCharArray();
        int hash = 7;
        for (int i =0; i<upc.length; i++) {
            hash = hash*137 + upc[(i)];
        }

        return Math.abs(hash) ;
    }





    public static ArrayList<Integer> generateUPCCodes(int size) {
        ArrayList<Integer> codes = new ArrayList<Integer>();
        Random r = new Random();

        for (int i = 0; i<size; i++) {
            int code = r.nextInt(100000000);
            codes.add(code);
        }
        return codes;
    }



    public static ArrayList<String> readJCPData(int numItems) {
        ArrayList<String> items = new ArrayList<String>();
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(new File("jcpenneyItemsSales.csv")));

        for (int i=0; i<numItems; i++) {
            String product = br.readLine();

            //get items that dont start with \
            while (product.startsWith("\"")) {
                product = br.readLine();
            }
            items.add(product);

        }

        br.close();

        } catch (IOException e ) {
            System.out.println("The file does not exist!");
            e.printStackTrace();
        }

        return items;
    }


    public String toString(){
        return "UPC Code: " + this.upcCode + " -- Price: " + this.price + " -- Product: " + this.description;

    }


    public boolean setNewPrice(float newPrice) {
        if (this.price == newPrice) {
            return false;
        }
        this.price = newPrice;
        return true;
    }


    public float getPrice() {
        return price;
    }


    public int getUpcCode() {
        return upcCode;
    }



    public String getDescription() {
        return description;
    }




    public Item getNext() {
        return next;
    }

    public void setNext(Item next) {
        this.next = next;
    }



}
