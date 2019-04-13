package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
    private CountDownLatch countDownLatch;
    private int timeToEnd;

    public LogisticsService(int id, CountDownLatch countDownLatch) {
        super("logistics " + id);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (tick) -> {
            timeToEnd = tick.getTTL();
        });
        subscribeEvent(DeliveryEvent.class, ev -> {
            Future<Future<DeliveryVehicle>> futureFuture = sendEvent(new AcquireVehicle());
            if (futureFuture == null || futureFuture.get(timeToEnd, TimeUnit.MILLISECONDS) == null || futureFuture.get().get(timeToEnd, TimeUnit.MILLISECONDS) == null) {
                complete(ev, null);
                return;
            }
            DeliveryVehicle vehicle = futureFuture.get().get();
            vehicle.deliver(ev.getCustomer().getAddress(), ev.getCustomer().getDistance());
            sendEvent(new ReleaseVehicle(vehicle));
            complete(ev, null);
        });
        subscribeBroadcast(TerminateBroadcast.class, tr -> {
            terminate();
        });
        countDownLatch.countDown();
    }
}
