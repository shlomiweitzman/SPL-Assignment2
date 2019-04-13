package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.services.TimeService;

public class  BookOrderEvent implements Event<OrderReceipt> {
    private OrderReceipt orderReceipt = null;
    private Customer c;
    private String book;
    private int orderId;
    private int orderTick;



    public BookOrderEvent(Customer c, String book, int orderId, int orderTick) {
        this.c = c;
        this.orderTick = orderTick;
        this.book = book;
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
    public Customer getCustomer() {
        return c;
    }

    public String getBookName() {
        return book;
    }

    public int getOrderTick() {
        return orderTick;
    }
}

