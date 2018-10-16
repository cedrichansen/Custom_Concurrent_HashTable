public class Main {

    //https://www.kaggle.com/rtatman/universal-product-code-database


    public static void main(String[] args) {


        HashTable h = new HashTable();

        int [] UPCcodes = Item.generateUPCCodes(99999);
        System.out.println();

        for (int upc : UPCcodes) {
            System.out.println(upc + ",");
            //put item in hashtable at correct index
        }





    }

}
