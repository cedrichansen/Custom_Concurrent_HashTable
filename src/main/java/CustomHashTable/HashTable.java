package CustomHashTable;

import SuperMarket.Item;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashTable {

    private Item[] items;
    private AtomicInteger itemCount;
    //private ReadWriteLock lock;
    private AtomicBoolean isResizing = new AtomicBoolean(false);

    public HashTable(int size) {
        items = new Item[size];
        itemCount = new AtomicInteger(0);
    }


    public void put(Item item) {

        //keep trying until its not being resized
        for (; ; ) {

            if (!isResizing.get()) {
                //if a resize is currently not happening

                if (itemCount.get() > (0.75f * items.length)) {
                    isResizing.getAndSet(true);
                    resize();
                    isResizing.getAndSet(false);
                }


                int index = item.hash() & (items.length - 1);
                Item current = items[index];

                if (current == null) {
                    itemCount.compareAndSet(itemCount.get(), itemCount.get() + 1);
                    items[index] = item;
                    System.out.println("Successfully added: " + item.toString());

                    break;
                } else {
                    itemCount.compareAndSet(itemCount.get(), itemCount.get() + 1);
                    System.out.println("Successfully added: " + item.toString());
                    current.addToEnd(item);
                    break;
                }

            }
        }

    }

    public void resize() {

        Item[] newBuckets = new Item[items.length * 2];
        Item[] oldBuckets = this.items;

        for (int i = 0; i < oldBuckets.length; i++) {
            if (oldBuckets[i] != null) {
                // temp is used as a seeker node, while current is used to be added simpy because its easier to remove
                //next pointer that way

                Item temp = oldBuckets[i];
                Item current = new Item(temp.getUpcCode(), temp.getDescription(), temp.getPrice());
                int index = current.hash() & (newBuckets.length - 1);

                if (newBuckets[index] == null) {
                    newBuckets[index] = current;
                } else {
                    newBuckets[index].addToEnd(current);
                }

                while (temp.getNext() != null) {
                    temp = temp.getNext();
                    current = new Item(temp.getUpcCode(), temp.getDescription(), temp.getPrice());
                    index = current.hash() & (newBuckets.length - 1);

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
        //lock.readLock().lock();
        for (; ; ) {
            if (!isResizing.get()) {

                Item seeker = new Item(upcCode, null, -1);

                int index = seeker.hash() & (items.length - 1);
                Item current = items[index];

                if (current == null) {
                    return null;
                } else if (current.getUpcCode() == upcCode) {
                    return current;
                } else {
                    seeker = current;
                    while (seeker.getNext() != null) {
                        seeker = seeker.getNext();
                        if (seeker.getUpcCode() == upcCode) {
                            return seeker;
                        }
                    }
                    //this should never be reachable, but here in case;
                    return null;
                }
            }
        }

    }


    public String changeItemPrice(int upc, float newPrice) {
        Item item = get(upc);

        if (item == null) {
            return null;
        }

        float oldPrice = item.getPrice();
        //check to see if another thread has already changed the price of the item
        if (!item.setNewPrice(newPrice)) {
            System.out.println("Another seller has already changed the price of this item...");
            return "Another seller already changed the price of this item";
        }


        return "Item: " + item.getUpcCode() + " -- " + item.getDescription() + "\nUsed to cost: " + oldPrice + " Now costs: " + newPrice;

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
