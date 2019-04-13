package bgu.spl.mics.application.passiveObjects;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private Vector<OrderReceipt> receipts;
	private AtomicInteger sum = new AtomicInteger(0);

	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}
	private MoneyRegister() {
		receipts=new Vector<OrderReceipt>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		receipts.add(r);
		sum.addAndGet(r.getPrice());
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public synchronized int getTotalEarnings() {
		return sum.get();
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public synchronized void chargeCreditCard(Customer c, int amount) {
		c.setAvailableAmountInCreditCard(c.getAvailableCreditAmount()-amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(receipts);
			oos.close();
			fos.close();
		} catch (IOException e) {

		}
	}
	public String toString(){
		String s="";
		for(OrderReceipt o:receipts)
			s=s+"{"+o.toString()+"}\n";
		s=s+"Money registers sum: "+sum;
		return s;

	}
}
