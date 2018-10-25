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

        int index = item.hash() & (items.length-1);

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

    public Item get(int upcCode) {
        lock.readLock().lock();
        Item seeker = new Item (upcCode, null, -1);
        int index = seeker.hash() & (items.length-1);

        if (items[index] == null) {
            lock.readLock().unlock();
            return null;
        } else if (items[index].getUpcCode() == upcCode){
            lock.readLock().unlock();
            return items[index];
        } else {
            seeker = items[index];
            while (seeker.getNext() != null) {
                seeker = seeker.getNext();
                if (seeker.getUpcCode() == upcCode) {
                    lock.readLock().unlock();
                    return seeker;
                }
            }
            //this should never be reachable, but here in case;
            lock.readLock().unlock();
            return null;
        }

    }



    public boolean changeItemPrice(int upc, float newPrice) {
        Item item = get(upc);
        lock.writeLock().lock();

        if (item == null) {
            lock.writeLock().unlock();
            return false;
        }

        //check to see if another thread has already changed the price of the item
        if (!item.setNewPrice(newPrice)) {
            System.out.println("Another seller has already changed the price of this item...");
            lock.writeLock().unlock();
            return true;
        }

        System.out.println("Item: " + item.toString() + "\nNow costs: " + newPrice);
        lock.writeLock().unlock();
        return true;



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
