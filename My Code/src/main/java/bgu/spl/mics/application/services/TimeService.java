package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    private int duration;
    private int speed;
    private AtomicInteger tick = new AtomicInteger(0);
    private TimeUnit unit;

    public TimeService(int duration, int speed) {
        super("TimeService");
        this.duration = duration;
        this.speed = speed;
        unit = TimeUnit.MILLISECONDS;
    }

    @Override
    protected void initialize() {
        while (tick.get() < duration) {
            synchronized (this) {
                try {
                    unit.timedWait(this, speed);
                    int now=tick.incrementAndGet();
                    if(now<duration)
                        sendBroadcast(new TickBroadcast(now,(duration-tick.get())*speed));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        sendBroadcast(new TerminateBroadcast());
        terminate();
    }
}
