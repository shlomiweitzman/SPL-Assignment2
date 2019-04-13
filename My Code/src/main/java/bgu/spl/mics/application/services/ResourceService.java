package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicle;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> futures=new ConcurrentLinkedQueue<>();
	private CountDownLatch countDownLatch;

	public ResourceService(int id, CountDownLatch countDownLatch) {
		super("resource"+id);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(ReleaseVehicle.class, ev->{
			resourcesHolder.releaseVehicle(ev.getDeliveryVehicle());
			complete(ev,null);
		});

		subscribeEvent(AcquireVehicle.class, ev->{
			Future<DeliveryVehicle> f = resourcesHolder.acquireVehicle();
			futures.add(f);
			complete(ev,f);
		});

		subscribeBroadcast(TerminateBroadcast.class, terminate->{
			for(Future<DeliveryVehicle> f:futures){
				f.resolve(null);
			}

		});
		countDownLatch.countDown();
		subscribeBroadcast(TerminateBroadcast.class,tr->{
			terminate();
		});
	}
}
