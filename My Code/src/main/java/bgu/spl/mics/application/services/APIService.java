package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
    private Customer customer;
    private CountDownLatch countDownLatch;
    private int orderId;
    private AtomicInteger count=new AtomicInteger(0);

    public APIService(int id, Customer customer,int o, CountDownLatch countDownLatch) {
        super("API Service "+id);
        this.customer = customer;
        this.countDownLatch = countDownLatch;
        orderId=o;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            ArrayList<Pair<Integer,String>> lst=customer.getOrderSchedule();
            for (int i = 0; i < customer.getOrderSchedule().size(); i++) {
                if (tick.getTick() == lst.get(i).getL()) {
                    sendEvent(new BookOrderEvent(customer,lst.get(i).getR(),orderId,lst.get(i).getL()));
                }
            }
            });
        countDownLatch.countDown();
        subscribeBroadcast(TerminateBroadcast.class, tr->{
            terminate();
        });
    }
}
