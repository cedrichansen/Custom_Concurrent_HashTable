import java.util.ArrayList;

public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database

    static int NUMPRODUCTS = 1000;



    public static void main(String[] args) {


        HashTable ht = new HashTable(NUMPRODUCTS);

        int [] UPCcodes = Item.generateUPCCodes(NUMPRODUCTS);
        String [] JCPItems = Item.readJCPData(NUMPRODUCTS);

        for (int i = 0; i< JCPItems.length; i++) {
            Item temp = new Item(UPCcodes[i], JCPItems[i].split(",")[0], Float.parseFloat(JCPItems[i].split(",")[1]));
            ht.put(temp);
            System.out.println(temp.toString());
        }


        System.out.println();


    }

}
