import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    static int NUMPRODUCTS = 16; //do not exceed 8000 (only 8000 items in total)
    static int  NUMSELLERS = 1; //do not exceed 12 (only 12 stores listed in text file)

    public static void main(String[] args) {

        HashTable ht = new HashTable(4);

        ArrayList<Integer> UPCcodes = Item.generateUPCCodes(NUMPRODUCTS);
        ArrayList<String> JCPItems = Item.readJCPData(NUMPRODUCTS);

        for (int i = 0; i< JCPItems.size(); i++) {
            Item temp = new Item(UPCcodes.get(i), JCPItems.get(i).split(",")[0], Float.parseFloat(JCPItems.get(i).split(",")[1]));
            ht.put(temp);
            System.out.println(temp.toString());
        }

        letShoppersIn(UPCcodes, ht);


    }

    public static void letShoppersIn(ArrayList<Integer> UPCcodes, HashTable ht){
        try {
            int numThreads = Runtime.getRuntime().availableProcessors();
            numThreads = (numThreads > 32) ? 32 : numThreads;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i<numThreads- NUMSELLERS; i++) {
                Shopper temp = new Shopper(Shopper.getShopperName(i), UPCcodes, ht);
                executor.execute(temp);
            }

            for (int i = 0; i < NUMSELLERS; i++) {
                Seller temp = new Seller(Seller.getSellerName(i), UPCcodes, ht);
                executor.execute(temp);
            }

            //let shoppers shop for 15 seconds
            executor.awaitTermination(15, TimeUnit.SECONDS);
            executor.shutdownNow();
            System.out.println("\nStore is now closed!");

        } catch (InterruptedException e) {
            System.out.println("process was interrupted...");
            e.printStackTrace();
        }

    }






}
