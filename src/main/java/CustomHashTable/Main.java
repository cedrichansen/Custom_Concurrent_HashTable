package CustomHashTable;

import SuperMarket.Item;
import SuperMarket.Seller;
import SuperMarket.Shopper;
import javafx.beans.property.IntegerProperty;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Hashtable;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    public static int NUMPRODUCTS = 16; //do not exceed 8000 (only 8000 items in total)
    static int NUMSELLERS = 2; //do not exceed 12 (only 12 stores listed in text file
    static DecimalFormat df = new DecimalFormat("#.00");

    public static void main(String[] args) {

        HashTable ht = new HashTable(4);
        //java.util.Hashtable table = new Hashtable();

        ArrayList<String> JCPItems = Item.readJCPData();
        ArrayList<Integer> UPCcodes = Item.generateUPCCodes(JCPItems.size());

        for (int i = 0; i < NUMPRODUCTS; i++) {
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
                .threads(4)
                .warmupIterations(5)
                .measurementIterations(5)
                .resultFormat(ResultFormatType.CSV)
                .result("results.csv")
                .build();


        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }


        writeHTMLFile();

    }



    public static void writeHTMLFile(){
        String total = "";

        String start = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>csc375hw02 results</title>\n" +
                "\n" +
                "    <link rel=\"stylesheet\" href=\"results.css\">\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>title 1</h1>";

        String end = "\n" +
                "</body>\n" +
                "</html>";

        total += start;


        String barGraphStart = "<div>\n" +
                "\n" +
                "    <ul class=\"bar-graph\">\n" +
                "\n" +
                "\n" +
                "        <li class=\"bar-graph-axis\">";

        total += barGraphStart;

        String barGraphEnd = "</ul>\n" +
                "\n" +
                "</div>";

        String barGraphAxisStart = "<div class=\"bar-graph-label\">";


        String barGraphAxisEnd = "</div>\n";

        File f = new File("results.csv");
        ArrayList<String> lines = new ArrayList<String>();
        try {

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();

            int count = 0;
            while (line != null) {
                if (count != 0) {
                    lines.add(line);
                }
                count++;
                line=br.readLine();
            }


            br.close();


            ArrayList<Double> scores = new ArrayList<Double>();
            Double maxScore = 0d;

            for (String s : lines) {
                    Double score = Double.parseDouble(s.split(",")[4]);
                    if (score>maxScore) {
                        maxScore = score;
                    }
                    scores.add(score);
            }

            double top = Math.ceil(maxScore);
            System.out.println(top);
            double secondDiv = (top/5) *4;
            double thirdDiv = (top/5) *3;
            double fourthDiv = (top/5) *2;
            double fifthDiv = (top/5) *1;

            String firstAxisTitle = barGraphAxisStart + top + barGraphAxisEnd;
            String secondAxisTitle = barGraphAxisStart + secondDiv + barGraphAxisEnd;
            String thirdAxisTitle = barGraphAxisStart + thirdDiv + barGraphAxisEnd;
            String fourthAxisTitle = barGraphAxisStart + fourthDiv + barGraphAxisEnd;
            String fifthAxisTitle = barGraphAxisStart + fifthDiv + barGraphAxisEnd;
            String zeroAxis = barGraphAxisStart + "0" + barGraphAxisEnd + "</li>";

            total+=firstAxisTitle;
            total +=secondAxisTitle;
            total +=thirdAxisTitle;
            total+=fourthAxisTitle;
            total+=fifthAxisTitle;
            total+=zeroAxis;


            for (String l:lines) {
                total += addBarToGraph(Double.parseDouble(l.split(",")[4]), l.split(",")[0], top, Double.parseDouble(l.split(",")[5]));
            }
            total+= barGraphEnd;
            total +=end;


            f = new File("results.html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(total);
            bw.close();


            System.out.println(total);
        } catch (IOException e ) {
            System.out.println("file doesnt exist");
        }


    }

    static String addBarToGraph (double value, String name, double max, double error) {

        String [] stuff = name.split("\\.");
        name = stuff[2].substring(0, stuff[2].length()-1);
        double perc = value/max;
        String firstLine= "<li class=\"bar primary\" style=\"height: " + perc*100 + "%;\" title =" + name + ">\n";
        String secondLine = "<div class=\"percent\">" + df.format(value) + "+/-"+  df.format(error)+"<span>ops/ms</span></div>\n";
        String thirdLine = "<div class=\"description\">" + name + "</div>\n" +
                "        </li>";

        return firstLine + secondLine + thirdLine;

    }





    public static void letShoppersIn(ArrayList<Integer> UPCcodes, ArrayList<String> JCPItems, HashTable ht) {
        try {
            int numThreads = Runtime.getRuntime().availableProcessors();
            numThreads = (numThreads > 32) ? 32 : numThreads;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i < numThreads - NUMSELLERS; i++) {
                Shopper temp = new Shopper(Shopper.getShopperName(i), UPCcodes, ht);
                executor.execute(temp);
            }

            for (int i = 0; i < NUMSELLERS; i++) {
                Seller temp = new Seller(Seller.getSellerName(i), UPCcodes, JCPItems, ht);
                executor.execute(temp);
            }

            //let shoppers shop for 360 seconds
            executor.awaitTermination(360, TimeUnit.SECONDS);
            executor.shutdownNow();
            System.out.println("\nStores are now closed!");

        } catch (InterruptedException e) {
            System.out.println("process was interrupted...");
            e.printStackTrace();
        }

    }





}
