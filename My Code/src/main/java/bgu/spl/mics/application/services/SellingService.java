package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private int time;
    private MoneyRegister moneyRegister = MoneyRegister.getInstance();
    private CountDownLatch countDownLatch;
    private int prcstick;
    private int timeToEnd;

    public SellingService(int id, CountDownLatch countDownLatch) {
        super("SellingService "+id);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, timer ->{
            time = timer.getTick();
        });
        subscribeBroadcast(TickBroadcast.class, (tick) -> {
            timeToEnd = tick.getTTL();
        });
        subscribeEvent(BookOrderEvent.class, order -> {
            prcstick = time;
            Customer customer = order.getCustomer();
            String bookName = order.getBookName();
            synchronized (order.getCustomer()) {
                Future<Integer> price = sendEvent(new CheckAvailabilityAndGetPrice(bookName));
                if (price != null && price.get(timeToEnd, TimeUnit.MILLISECONDS) != null) { //check if a service is handling the event, and the book exist in the inventory
                    if (price.get(timeToEnd, TimeUnit.MILLISECONDS) > -1) { //check if a service is handling the event, and the book exist in the inventory
                        if (customer.getAvailableCreditAmount() >= price.get()) { // check if the customer has enough money
                            Future<OrderResult> orderResultFuture = sendEvent(new TakeEvent(bookName));
                            if (orderResultFuture != null) { //
                                OrderResult orderResult = orderResultFuture.get(timeToEnd, TimeUnit.MILLISECONDS);
                                if (orderResult == OrderResult.SUCCESSFULLY_TAKEN) { // to verify that the book will be taken only once
                                    moneyRegister.chargeCreditCard(customer, price.get(timeToEnd, TimeUnit.MILLISECONDS));
                                    sendEvent(new DeliveryEvent(customer));
                                    OrderReceipt receipt = new OrderReceipt(moneyRegister.getTotalEarnings()
                                            , this.getName(), customer.getId(), bookName,
                                            price.get(), time, order.getOrderTick(), prcstick);
                                    complete(order, receipt);
                                    moneyRegister.file(receipt);
                                    customer.getCustomerReceiptList().add(receipt);//file receipt 2 customer
                                    return;
                                }
                            }
                        }
                    }
                    complete(order, null);
                }
            }
        });
        countDownLatch.countDown();
        subscribeBroadcast(TerminateBroadcast.class,tr->{
            terminate();
        });
    }
}
