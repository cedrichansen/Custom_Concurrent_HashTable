import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Shopper implements Runnable{

    private String shopperName;
    private int [] availableUPCcodes;
    private HashTable ht;
    private Random r;


    Shopper (String name, int [] upcCodes, HashTable h) {
        shopperName = name;
        availableUPCcodes = upcCodes;
        ht = h;
        r = new Random();
    }


    private void buyRandomItem(){
        //pick a random item and read it
        int upc = availableUPCcodes[r.nextInt(availableUPCcodes.length)];
        Item i = ht.get(upc);
        if (i != null) {
            System.out.println(shopperName + " just purchased" + i.toString());
        } else {
            System.out.println(shopperName + " is currently browsing items ");
        }
    }

    static String getShopperName(int shopperNum) {
        try{
            BufferedReader br = new BufferedReader(new FileReader("names.csv"));
            int i = 0;
            String name;
            for (name = br.readLine(); name != null; name = br.readLine()) {
                if (i == shopperNum) {
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


    public void run() {
        while (true) {
            try {
                //basically try to buy things between 0-5 seconds randomly
                Thread.sleep(r.nextInt(5000));
            } catch (InterruptedException e) {
                System.out.println(shopperName + " has left the store");
            }

            buyRandomItem();

        }
    }





}
