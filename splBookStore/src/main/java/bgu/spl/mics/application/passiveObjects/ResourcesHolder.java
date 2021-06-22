package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.Future;

import java.io.Serializable;
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
public class ResourcesHolder implements Serializable {

	private static ResourcesHolder instance = null;
	private ConcurrentLinkedQueue<DeliveryVehicle> availableVehicles; //list of the vehicles
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> waitingForVehicles;

	private ResourcesHolder(){
		availableVehicles = new ConcurrentLinkedQueue<>();
		waitingForVehicles = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		if(instance == null)
			instance = new ResourcesHolder();
		return instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() { // check if we need synchronized
		Future<DeliveryVehicle> future = new Future<>();
		DeliveryVehicle deliveryVehicle;

		if(availableVehicles.size() > 0) {
			deliveryVehicle = availableVehicles.poll(); // delete the vehicle
			future.resolve(deliveryVehicle);
		}
		else{
			waitingForVehicles.add(future);
		}
		return future;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(waitingForVehicles.size() > 0) {
			waitingForVehicles.poll().resolve(vehicle);
			availableVehicles.add(vehicle);//
		}
		else
			availableVehicles.add(vehicle);

	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for(int i = 0; i < vehicles.length; i++){
			availableVehicles.add(vehicles[i]);

		}
	}

}