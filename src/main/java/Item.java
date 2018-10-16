import java.util.ArrayList;
import java.util.Random;

public class Item {

    private int upcCode;
    private final String description;
    private float price;
    private Item next;

    public Item(int upcCode, String description, float price) {
        this.upcCode = upcCode;
        this.description = description;
        this.price = price;
    }



    
    public static int[] generateUPCCodes(int size) {
        int [] codes = new int[size];
        ArrayList<Integer> codesList = new ArrayList<Integer>();
        Random r = new Random();

        for (int i = 0; i<codes.length; i++) {
            int code = r.nextInt(1000000);
            if (!codesList.contains(code)) {
                codesList.add(code);
                codes[i] = code;
            } else {
                i--;
            }
        }
        return codes;
    }


}
