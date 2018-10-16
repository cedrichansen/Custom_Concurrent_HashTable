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
        current.setNext(item);
    }


    public int hash(HashTable ht){
        int size = ht.getItems().length;

        char [] upc = (this.upcCode + "").toCharArray();
        int hash = 7;
        for (int i =0; i<upc.length; i++) {
            hash = hash*137 + upc[(i)];
        }

        return Math.abs(hash) & (size-1);
    }





    public static int[] generateUPCCodes(int size) {
        int [] codes = new int[size];
        ArrayList<Integer> codesList = new ArrayList<Integer>();
        Random r = new Random();

        for (int i = 0; i<codes.length; i++) {
            int code = r.nextInt(1000000);
            if (!codesList.contains(code)) {
                codesList.add(code);
                codes[i] = code;
            } else {
                i--;
            }
        }
        return codes;
    }



    public static String[] readJCPData(int numItems) {
        String [] items = new String[numItems];
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(new File("jcpenneyItemsSales.csv")));

        for (int i=0; i<numItems; i++) {
            items[i] = br.readLine();
            if (items[i].startsWith("\"")) {
                //filter out things that start with " because they often have commas in them
                i--;
            }
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



    //TODO: make it so that it only changes the price if the price has not already been changed. Maybe synchronize it?
    public void setPrice(float newPrice) {
        this.price = price;
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
