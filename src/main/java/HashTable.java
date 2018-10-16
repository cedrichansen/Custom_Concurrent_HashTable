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
        //lock.writeLock().lock();
        HashTable h = this;


        if (itemCount.get() > 0.6f * h.getItems().length) {
            h = h.resize();
        }

        int index = item.hash(h);

        if (this.items[index] == null) {
            itemCount.compareAndSet(itemCount.get(), itemCount.get()+1);
            items[index] = item;
        } else {
            //something must be there so add it to be the next item
            itemCount.compareAndSet(itemCount.get(), itemCount.get() + 1);
            items[index].addToEnd(item);
        }

        //lock.writeLock().unlock();
    }

    public HashTable resize(){
        HashTable newHashTable = new HashTable(this.items.length *2);
        Item[] tempStore = new Item[itemCount.get()];
        int spot=0;
        for (int i=0; i<this.getItems().length; i++) {
            if (this.getItems()[i] != null) {
                Item temp = this.getItems()[i];
                tempStore[spot] = temp;
                spot++;
                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    tempStore[spot] = temp;
                    spot++;
                }
            }
        }

        for (Item item: tempStore) {
            newHashTable.put(item);
        }

        return newHashTable;

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
