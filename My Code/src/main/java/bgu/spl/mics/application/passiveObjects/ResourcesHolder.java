package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private ConcurrentLinkedQueue<DeliveryVehicle> availVehicles=new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> waitingList =new ConcurrentLinkedQueue<>();


	private ResourcesHolder(){}
	/**
     * Retrieves the single instance of this class.
     */
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */

	public synchronized Future<DeliveryVehicle> acquireVehicle() {//every Future will resolve to 1 vehicle
		Future<DeliveryVehicle> output= new Future<>();
		if(!availVehicles.isEmpty())
			output.resolve(availVehicles.poll());
		else
			waitingList.add(output);
		return output;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public synchronized void releaseVehicle(DeliveryVehicle vehicle) { // protects the case which 2 services try to release the vehicle
		if(!waitingList.isEmpty()){
			waitingList.poll().resolve(vehicle);
		}
		else
			availVehicles.add(vehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle d : vehicles) {
			releaseVehicle(d);
		}
	}
}
