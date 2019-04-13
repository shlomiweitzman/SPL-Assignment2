package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable
{

	private final String bookTitle;
	private AtomicInteger amount=new AtomicInteger();
	private final int price;

	public BookInventoryInfo(String name,int amount, int price) {
		bookTitle=name;
		this.price=price;
		this.amount.set(amount);
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount.get();
	}
	public void setAmount(int num){
		amount.set(num);
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "bookTitle: "+bookTitle+"\n"+
				"amount: "+amount;
	}
}
