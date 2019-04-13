package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.messages.BookOrderEvent;
import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
    private final int id;
    private String name;
    private String address;
    private final int distance;
    private Vector<OrderReceipt> receipts;
    private ArrayList<Pair<Integer, String>> orderSchedule;
    private final int creditcardNumber;
    private int availableAmountInCreditCard;

    public Customer(String name, String address, int distance, int creditCard, int availableAmountInCreditCard, int id, JsonArray orderSchedule) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.orderSchedule = new ArrayList<>();
        for (int i = 0; i < orderSchedule.size(); i++) {
            Pair<Integer, String> p = new Pair<>(
                    orderSchedule.get(i).getAsJsonObject().get("tick").getAsInt(),
                    (orderSchedule.get(i).getAsJsonObject().get("bookTitle").getAsString()));
            this.orderSchedule.add(p);
        }
        this.distance = distance;
        this.receipts = new Vector<>();
        this.creditcardNumber = creditCard;
        this.availableAmountInCreditCard = (availableAmountInCreditCard);
    }

    public ArrayList<Pair<Integer, String>> getOrderSchedule() {
        return orderSchedule;
    }

    /**
     * Retrieves the name of the customer.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the ID of the customer  .
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the address of the customer.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieves the distance of the customer from the store.
     */
    public int getDistance() {
        return distance;
    }


    /**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     *
     * @return A list of receipts.
     */
    public List<OrderReceipt> getCustomerReceiptList() {
        return receipts;
    }

    /**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     *
     * @return Amount of money left.
     */
    public int getAvailableCreditAmount() {
        return availableAmountInCreditCard;
    }

    /**
     * Retrieves this customers credit card serial number.
     */
    public int getCreditNumber() {
        return creditcardNumber;
    }

    public void setAvailableAmountInCreditCard(int availableAmountInCreditCard) {
        this.availableAmountInCreditCard = (availableAmountInCreditCard);
    }

    public void addReceipts(OrderReceipt receipt) {
        this.receipts.add(receipt);
    }

    @Override
    public String toString() {
        return
        "customer name:" + getName()+"\n"+
        "id: " + getId()+"\n"+
        "address: " + this.getAddress()+"\n"+
        "distance: " + getDistance()+"\n"+
        "recipts: " + getCustomerReceiptList()+"\n"+
        "creditCard number: " + getCreditNumber()+"\n"+
        "balance: " + getAvailableCreditAmount();
    }

}

