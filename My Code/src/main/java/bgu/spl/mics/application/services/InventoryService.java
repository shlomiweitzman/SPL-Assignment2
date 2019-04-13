package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityAndGetPrice;
import bgu.spl.mics.application.messages.TakeEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {

    private Inventory inventory = Inventory.getInstance();
    private CountDownLatch countDownLatch;

    public InventoryService(int id, CountDownLatch countDownLatch) {
        super("Inventory "+id);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeEvent(CheckAvailabilityAndGetPrice.class, ev ->{
            synchronized (inventory.getLibrary().get(ev.getBookName())) {
                complete(ev, inventory.checkAvailabiltyAndGetPrice(ev.getBookName()));
            }});
        subscribeEvent(TakeEvent.class,take->complete(take,inventory.take(take.getBookName())));
        countDownLatch.countDown();
        subscribeBroadcast(TerminateBroadcast.class, tr->{
            terminate();
        });
    }
}
