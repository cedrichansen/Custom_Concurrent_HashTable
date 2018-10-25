import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    static int NUMPRODUCTS = 1000;
    static Scanner kb = new Scanner(System.in);


    public static void main(String[] args) {

        HashTable ht = new HashTable(4);

        int [] UPCcodes = Item.generateUPCCodes(NUMPRODUCTS);
        String [] JCPItems = Item.readJCPData(NUMPRODUCTS);

        for (int i = 0; i< JCPItems.length; i++) {
            Item temp = new Item(UPCcodes[i], JCPItems[i].split(",")[0], Float.parseFloat(JCPItems[i].split(",")[1]));
            ht.put(temp);
            System.out.println(temp.toString());
        }




        System.out.println("Type upc code to change price");
        String upc = kb.nextLine();
        System.out.println("Please type in the new price");
        String newPrice = kb.nextLine();

        ht.changeItemPrice(Integer.parseInt(upc), Float.parseFloat(newPrice));
        System.out.println();

    }

}
