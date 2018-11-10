package CustomHashtableTests;

import SuperMarket.Item;
import SuperMarket.Seller;
import SuperMarket.Shopper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import CustomHashTable.*;

@State(Scope.Thread)
public class Benchmark {

    static int NUMPRODUCTS = 16; //do not exceed 8000 (only 8000 items in total)
    static int  NUMSELLERS = 2; //do not exceed 12 (only 12 stores listed in text file

    static HashTable ht = new HashTable(4);

    static ArrayList<String> JCPItems = Item.readJCPData();
    static ArrayList<Integer> UPCcodes = Item.generateUPCCodes(JCPItems.size());
    static ArrayList <Shopper> shoppers = createShoppers();
    static ArrayList<Seller> sellers = createSellers();


    @Setup
    public void setUPHashTable() {
        for (int i = 0; i< NUMPRODUCTS; i++) {
            Item temp = new Item(UPCcodes.get(i), JCPItems.get(i).split(",")[0], Float.parseFloat(JCPItems.get(i).split(",")[1]));
            ht.put(temp);
        }
    }

    /*
    @TearDown(Level.Iteration)
    public void tearDown() {
        ht = new HashTable(4);
        for (int i = 0; i< NUMPRODUCTS; i++) {
            Item temp = new Item(UPCcodes.get(i), JCPItems.get(i).split(",")[0], Float.parseFloat(JCPItems.get(i).split(",")[1]));
            ht.put(temp);
        }
    }*/

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testGet(Blackhole bh) {

            ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());
            for (Shopper s : shoppers) {
                executor.execute(s);
                executor.execute(s);
                executor.execute(s);
            }
            executor.shutdown();



    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    //@OutputTimeUnit()
    public void testAdd(Blackhole bh) {

            ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());
            for (Seller s : sellers) {
                executor.execute(s);
                executor.execute(s);
                executor.execute(s);
            }
            executor.shutdown();


    }



    public static ArrayList<Seller> createSellers() {
        ArrayList<Seller> sellers = new ArrayList<Seller>();

        for (int i = 0; i<NUMSELLERS; i++) {
            String name = Seller.getSellerName(i);
            final Seller temp = new Seller(name , UPCcodes, JCPItems,ht);
            sellers.add(temp);
        }
        return sellers;
    }

    public static ArrayList<Shopper> createShoppers() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        numThreads = (numThreads > 32) ? 32 : numThreads;
        ArrayList <Shopper> shoppers = new ArrayList<Shopper>();

        for (int i = 0; i<numThreads- NUMSELLERS; i++) {
            final Shopper temp = new Shopper(Shopper.getShopperName(i), UPCcodes, ht);
            shoppers.add(temp);
        }
        return shoppers;
    }





}
