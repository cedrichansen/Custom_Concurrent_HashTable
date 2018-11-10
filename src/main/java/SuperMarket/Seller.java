package SuperMarket;

import CustomHashTable.HashTable;
import CustomHashTable.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Seller implements Runnable{

    private String storeName;
    private ArrayList<Integer> availableUPCcodes;
    private ArrayList<String> availableItems;
    private HashTable ht;
    private Random r;

    static ReentrantLock counterLock = new ReentrantLock(true);


    public Seller (String name, ArrayList<Integer> upcCodes,ArrayList<String> items,  HashTable h) {
        storeName = name;
        availableUPCcodes = upcCodes;
        availableItems = items;
        ht = h;
        r = new Random();
    }

    public void run() {
//        while (true) {
//            int time = r.nextInt(50);
//            try {
//                Thread.sleep(time);
//                changeRandomItemPrice();
//                if (ht.getItemCount().get() < 7999 && time < 5) {
//                    addItem(availableUPCcodes, availableItems);
//                }
//
//            } catch (InterruptedException e) {
//                System.out.println(storeName + " is now closed! It can no longer change prices");
//                break;
//            }
//
//        }

        changeRandomItemPrice();


        if (ht.getItemCount().get() < 7999) {
            addItem(availableUPCcodes, availableItems);
        }


    }



    public void changeRandomItemPrice() {
        int index = r.nextInt(ht.getItemCount().get());
        int upc = availableUPCcodes.get(index);
        float newPrice = r.nextFloat() * 300;
        System.out.println(this.storeName + " " + ht.changeItemPrice(upc, newPrice));
    }


    public void addItem(ArrayList<Integer> currentUpcCodes, ArrayList<String> availableItems) {

        String [] itemStuff = availableItems.get(ht.getItemCount().get()).split(",");
        int upc = 0;
        try {
            int index = ht.getItemCount().get()-1;
            upc = availableUPCcodes.get(index);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("something went wrong");
            e.printStackTrace();
        }
        Item i = new Item(upc, itemStuff[0], Float.parseFloat(itemStuff[1]));
        ht.put(i);
    }


    public static String getSellerName(int storeNum) {
        try{
            BufferedReader br = new BufferedReader(new FileReader("stores.csv"));
            int i = 0;
            String name;
            for (name = br.readLine(); name != null; name = br.readLine()) {
                if (i == storeNum) {
                    return name;
                }
                i++;
            }
        } catch (IOException e) {
            System.out.println("File not found....");
            e.printStackTrace();
            return null;
        }
        return null;
    }





}
