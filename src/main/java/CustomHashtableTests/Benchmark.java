package CustomHashtableTests;

import SuperMarket.Item;
import SuperMarket.Seller;
import SuperMarket.Shopper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import CustomHashTable.*;

@State(Scope.Thread)
public class Benchmark {

    static int NUMPRODUCTS = 16; //do not exceed 8000 (only 8000 items in total)
    static int  NUMSELLERS = 2; //do not exceed 12 (only 12 stores listed in text file

    static HashTable ht = new HashTable(4);
    ConcurrentHashMap<Integer, Item> jdkTable = new ConcurrentHashMap<Integer, Item>();

    static ArrayList<String> JCPItems = Item.readJCPData();
    static ArrayList<Integer> UPCcodes = Item.generateUPCCodes(JCPItems.size());
    static ArrayList <Shopper> shoppers = createShoppers();
    static ArrayList<Seller> sellers = createSellers();
    static Random r = new Random();


    @Setup
    public void setUPHashTable() {
        for (int i = 0; i< NUMPRODUCTS; i++) {
            Item temp = new Item(UPCcodes.get(i), JCPItems.get(i).split(",")[0], Float.parseFloat(JCPItems.get(i).split(",")[1]));
            ht.put(temp);
            jdkTable.put(temp.getUpcCode(), temp);
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
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testGetCustom(Blackhole bh) {

            ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());
            for (Shopper s : shoppers) {
                executor.execute(s);
                executor.execute(s);
                executor.execute(s);
            }
            executor.shutdown();

    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testGetJdk(Blackhole bh) {
        ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());
        Runnable shopper = new Runnable() {
            @Override
            public void run() {
                int upc = UPCcodes.get(r.nextInt(jdkTable.size()));
                Item i = jdkTable.get(upc);
                System.out.println("jdk just got" + i);
            }
        };

        for (Shopper s: shoppers) {
            executor.execute(shopper);
            executor.execute(shopper);
            executor.execute(shopper);
        }

        executor.shutdown();

    }



    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@OutputTimeUnit()
    public void testAddCustom(Blackhole bh) {

            ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());
            for (Seller s : sellers) {
                executor.execute(s);
                executor.execute(s);
                executor.execute(s);
            }
            executor.shutdown();
    }


    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddJDK(Blackhole bh) {
        ExecutorService executor = Executors.newFixedThreadPool(shoppers.size());

        Runnable seller = new Runnable() {
            @Override
            public void run() {
                changeRandomItemPrice(jdkTable);

                if (jdkTable.size() <7999) {
                    addItem(jdkTable);
                }
            }
        };

        for (Seller s : sellers) {
            executor.execute(seller);
            executor.execute(seller);
            executor.execute(seller);
        }

        executor.shutdown();
    }

    //A helper function to basically copy my custom hm implementation but maps it over to jdk hm
    public static void changeRandomItemPrice(ConcurrentHashMap<Integer, Item> jdkhm) {

        int upc = UPCcodes.get(r.nextInt(jdkhm.size()));
        Item item = jdkhm.get(upc);
        item.setNewPrice(r.nextFloat()*300);

    }

    //another helper function
    public static void addItem(ConcurrentHashMap<Integer, Item> ht) {
        int upc = UPCcodes.get(ht.size());
        String [] itemStuff = JCPItems.get(ht.size()).split(",");
        Item item = new Item(upc, itemStuff[0], Float.parseFloat(itemStuff[1]));
        ht.put(upc, item);
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
