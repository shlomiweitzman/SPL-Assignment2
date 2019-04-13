package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

    private final int orderid;
    private String seller;
    private final int customerid;
    private String bookTitle;
    private final int price;
    private int issueTick;
    private int orderTick;
    private int processTick;

    public OrderReceipt(int orderid,
                        String seller,
                        int customerid,
                        String bookTitle,
                        int price,
                        int issueTick,
                        int orderTick,
                        int processTick) {
        this.orderid = orderid;
        this.seller = seller;
        this.customerid = customerid;
        this.bookTitle = bookTitle;
        this.price = price;
        this.issueTick = issueTick;
        this.orderTick = orderTick;
        this.processTick = processTick;
    }

    /**
     * Retrieves the orderId of this receipt.
     */
    public int getOrderId() {
        return orderid;
    }

    /**
     * Retrieves the name of the selling service which handled the order.
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     *
     * @return the ID of the customer
     */
    public int getCustomerId() {
        return customerid;
    }

    /**
     * Retrieves the name of the book which was bought.
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the price the customer paid for the book.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Retrieves the tick in which this receipt was issued.
     */
    public int getIssuedTick() {
        return issueTick;
    }

    /**
     * Retrieves the tick in which the customer sent the purchase request.
     */
    public int getOrderTick() {
        return orderTick;
    }

    /**
     * Retrieves the tick in which the treating selling service started
     * processing the order.
     */
    public int getProcessTick() {
        return processTick;
    }
    public String toString(){
        return "{OrderId: "+ orderid+"\n"+
        "selling service: "+seller+"\n"+
        "customerid: "+customerid+"\n"+
        "bookTitle: "+bookTitle+"\n"+
        "price: "+price+"\n"+
        "issueTick: "+issueTick+"\n"+
        "orderTick: "+orderTick+"\n"+
        "processTick: "+processTick+"}";
    }
}
