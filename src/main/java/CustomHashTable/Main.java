package CustomHashTable;

import SuperMarket.Item;
import SuperMarket.Seller;
import SuperMarket.Shopper;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Hashtable;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    public static int NUMPRODUCTS = 16; //do not exceed 8000 (only 8000 items in total)
    static int  NUMSELLERS = 2; //do not exceed 12 (only 12 stores listed in text file

    public static void main(String[] args) {

        HashTable ht = new HashTable(4);
        //java.util.Hashtable table = new Hashtable();

        ArrayList<String> JCPItems = Item.readJCPData();
        ArrayList<Integer> UPCcodes = Item.generateUPCCodes(1000000);

        for (int i = 0; i< NUMPRODUCTS; i++) {
            Item temp = new Item(UPCcodes.get(i), JCPItems.get(i).split(",")[0], Float.parseFloat(JCPItems.get(i).split(",")[1]));
            ht.put(temp);
            //table.put(temp.getUpcCode(), temp);
        }

        //letShoppersIn(UPCcodes, JCPItems, ht);

        //System.out.println("Done the custom implentation\nNow processing the JDK concurrentHashtable ");

        //letShoppersIn(UPCcodes, JCPItems, table);


        final Options options = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .forks(1)
                .build();


        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }



    }

    public static void letShoppersIn(ArrayList<Integer> UPCcodes, ArrayList<String> JCPItems, HashTable ht){
        try {
            int numThreads = Runtime.getRuntime().availableProcessors();
            numThreads = (numThreads > 32) ? 32 : numThreads;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i<numThreads- NUMSELLERS; i++) {
                Shopper temp = new Shopper(Shopper.getShopperName(i), UPCcodes, ht);
                executor.execute(temp);
            }

            for (int i = 0; i < NUMSELLERS; i++) {
                Seller temp = new Seller(Seller.getSellerName(i), UPCcodes, JCPItems,  ht, NUMPRODUCTS);
                executor.execute(temp);
            }

            //let shoppers shop for 360 seconds
            executor.awaitTermination(30, TimeUnit.SECONDS);
            executor.shutdownNow();
            System.out.println("\nStores are now closed!");

        } catch (InterruptedException e) {
            System.out.println("process was interrupted...");
            e.printStackTrace();
        }

    }






}
