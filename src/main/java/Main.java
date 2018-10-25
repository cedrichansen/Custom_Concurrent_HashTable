import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    static int NUMPRODUCTS = 50;

    public static void main(String[] args) {

        HashTable ht = new HashTable(4);

        int [] UPCcodes = Item.generateUPCCodes(NUMPRODUCTS);
        String [] JCPItems = Item.readJCPData(NUMPRODUCTS);

        for (int i = 0; i< JCPItems.length; i++) {
            Item temp = new Item(UPCcodes[i], JCPItems[i].split(",")[0], Float.parseFloat(JCPItems[i].split(",")[1]));
            ht.put(temp);
            System.out.println(temp.toString());
        }

        letShoppersIn(UPCcodes, ht);


    }

    public static void letShoppersIn(int [] UPCcodes, HashTable ht){
        try {
            int numThreads = Runtime.getRuntime().availableProcessors();
            numThreads = (numThreads > 32) ? 32 : numThreads;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i<numThreads; i++) {
                Shopper temp = new Shopper(Shopper.getShopperName(i), UPCcodes, ht);
                executor.execute(temp);
            }


            //let shoppers shop for 30 seconds
            executor.shutdown();
            executor.awaitTermination(30 * 1000, TimeUnit.SECONDS);


        } catch (InterruptedException e) {
            System.out.println("process was interrupted...");
            e.printStackTrace();
        }

    }






}
