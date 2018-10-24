import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashTable {

    private Item[] items;
    private AtomicInteger itemCount;
    private ReadWriteLock lock;

    public HashTable(int size){
        items = new Item[size];
        itemCount = new AtomicInteger(0);
        lock = new ReentrantReadWriteLock();
    }


    public void put(Item item) {
        lock.writeLock().lock();

        if (itemCount.get() > (0.6f * items.length)) {
            resize();
        }

        int index = item.hash() & (items.length-1) ;

        if (this.items[index] == null) {
            itemCount.compareAndSet(itemCount.get(), itemCount.get()+1);
            items[index] = item;
        } else {
            //something must be there so add it to be the next item
            itemCount.compareAndSet(itemCount.get(), itemCount.get() + 1);
            items[index].addToEnd(item);
        }

        lock.writeLock().unlock();
    }

    public void resize(){
        Item [] newBuckets = new Item[items.length *2];
        Item [] oldBuckets = this.items;

        for (int i=0; i<oldBuckets.length; i++) {
            if (oldBuckets[i] != null) {
                // temp is used as a seeker node, while current is used to be added simpy because its easier to remove
                //next pointer that way

                Item temp = oldBuckets[i];
                Item current = new Item (temp.getUpcCode(), temp.getDescription(), temp.getPrice());
                int index = current.hash() & (newBuckets.length-1);

                if (newBuckets[index] == null) {
                    newBuckets[index] = current;
                } else {
                    newBuckets[index].addToEnd(current);
                }

                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    current = new Item (temp.getUpcCode(), temp.getDescription(), temp.getPrice());
                    index = current.hash() & (newBuckets.length-1);

                    if (newBuckets[index] == null) {
                        newBuckets[index] = current;
                    } else {
                        newBuckets[index].addToEnd(current);
                    }
                }
            }
        }
        setItems(newBuckets);

    }



    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }


    public AtomicInteger getItemCount() {
        return itemCount;
    }

    public void setItemCount(AtomicInteger itemCount) {
        this.itemCount = itemCount;
    }



}
