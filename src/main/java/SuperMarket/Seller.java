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


    static int numItems;
    static ReentrantLock counterLock = new ReentrantLock(true);


    public Seller (String name, ArrayList<Integer> upcCodes,ArrayList<String> items,  HashTable h, int nItems) {
        storeName = name;
        availableUPCcodes = upcCodes;
        availableItems = items;
        ht = h;
        r = new Random();
        numItems = nItems;
    }

    public void run() {
//        while (true) {
//            int time = r.nextInt(5000);
//            try {
//                //basically try to buy things between 0-5 seconds randomly
//                Thread.sleep(time);
//
//                if (time > 2500) {
//                    addItem(availableUPCcodes, availableItems);
//                } else {
//                    changeRandomItemPrice();
//                    addItem(availableUPCcodes, availableItems);
//                }
//            } catch (InterruptedException e) {
//                System.out.println(storeName + " is now closed! It can no longer change prices");
//                break;
//            }
//
//
//
//        }

        changeRandomItemPrice();


        addItem(availableUPCcodes, availableItems);

    }
    static void incrementCounter(){
        counterLock.lock();

        // Always good practice to enclose locks in a try-finally block
        try{
            numItems++;
        }finally{
            counterLock.unlock();
        }
    }


    public void changeRandomItemPrice() {
        int upc = availableUPCcodes.get(r.nextInt(availableUPCcodes.size()));
        float newPrice = r.nextFloat() * 300;
        System.out.println(this.storeName + " " + ht.changeItemPrice(upc, newPrice));
    }


    public synchronized void addItem(ArrayList<Integer> currentUpcCodes, ArrayList<String> availableItems) {

        String [] itemStuff = availableItems.get(numItems).split(",");
        int upc = Item.generateUPCCodes(1).get(0);
        Item i = new Item(upc, itemStuff[0], Float.parseFloat(itemStuff[1]));
        ht.put(i);
        currentUpcCodes.add(upc);
        incrementCounter();
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
