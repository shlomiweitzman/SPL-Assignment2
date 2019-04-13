package bgu.spl.mics.application.passiveObjects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
    private ConcurrentHashMap<String, BookInventoryInfo> library;

    /**
     * @pre:none
     * @post:none Retrieves the single instance of this class.
     */
    private static class SingletonHolder {
        private static Inventory instance = new Inventory();
    }

    private Inventory() {
        library = new ConcurrentHashMap<>();
    }

    public static Inventory getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     * @pre: uninitialized inventory
     * @post: the Inventory has all the books from the input
     */
    public synchronized void load(BookInventoryInfo[] inventory) {
        for (BookInventoryInfo book : inventory)
            library.put(book.getBookTitle(), book);
    }

    public ConcurrentHashMap<String, BookInventoryInfo> getLibrary() {
        return library;
    }

    /**
     * @param book Name of the book to take from the store
     * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * The first should not change the state of the inventory while the
     * second should reduce by one the number of books of the desired type.
     * @pre: the Inventory is not empty
     * @post: the book has been removed from the Inventory
     * @post: return the corrent enum
     * Attempts to take one book from the store.
     * <p>
     */
    public OrderResult take(String book) {
        synchronized (library.get(book)) {
            if (checkAvailabiltyAndGetPrice(book) > 0) {
                if (delete(book)) {
                    return OrderResult.SUCCESSFULLY_TAKEN;
                }
            }
            return OrderResult.NOT_IN_STOCK;
        }
    }

    private boolean delete(String book) {
        BookInventoryInfo tmp = library.get(book);
        if (tmp.getAmountInInventory() > 0) {
            tmp.setAmount(tmp.getAmountInInventory() - 1);
            return true;
        }
        return false;
    }

    /**
     * @param book Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     * @pre: none
     * @post: if the book is available, return the correct price of the book, or -1 otherwise.
     * Checks if a certain book is available in the inventory.
     * <p>
     */
    public int checkAvailabiltyAndGetPrice(String book) {
        synchronized (library.get(book)) {
            if (library.containsKey(book) && library.get(book).getAmountInInventory() > 0)
                return library.get(book).getPrice();
            return -1;
        }
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public synchronized void printInventoryToFile(String filename) {
        HashMap<String, Integer> toPrint = new HashMap<>();
        for (BookInventoryInfo book : library.values()) {
            toPrint.put(book.getBookTitle(), book.getAmountInInventory());
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(filename));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(toPrint);
            oos.close();
            fos.close();
        } catch (IOException e) {
        }
    }
}
