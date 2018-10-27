import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Seller implements Runnable{

    private String storeName;
    private ArrayList<Integer> availableUPCcodes;
    private HashTable ht;
    private Random r;


    public Seller (String name, ArrayList<Integer> upcCodes, HashTable h) {
        storeName = name;
        availableUPCcodes = upcCodes;
        ht = h;
        r = new Random();
    }

    public void run() {
        while (true) {
            int time = r.nextInt(5000);
            try {
                //basically try to buy things between 0-5 seconds randomly
                Thread.sleep(time);
            } catch (InterruptedException e) {
                System.out.println(storeName + " is now bankrupt! It can no longer change prices");
                break;
            }

            if (time > 2500) {
                changeRandomItemPrice();
            }
            //TODO create a function that adds a new Item to the lists/hashtable





        }
    }


    public void changeRandomItemPrice() {
        int upc = availableUPCcodes.get(r.nextInt(availableUPCcodes.size()));
        float newPrice = r.nextFloat() * 300;
        System.out.println(ht.changeItemPrice(upc, newPrice));;
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
