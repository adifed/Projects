package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBrod;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolderInstance = ResourcesHolder.getInstance();
	private CountDownLatch countDownLatch;

	public ResourceService(CountDownLatch count) {
		super("Resources Service");

		countDownLatch = count;
	}

	@Override
	protected void initialize() {

		Callback<GetVehicleEvent> cTake = (e) -> {  // e is not a class but some DeliveryEvent
			Future <DeliveryVehicle> myVehicle = resourcesHolderInstance.acquireVehicle();
			//I changed it (without get)
			complete(e,myVehicle);
		};

		Callback<ReleaseVehicleEvent> cRelease = (e) -> {  // e is not a class but some DeliveryEvent
			resourcesHolderInstance.releaseVehicle(e.getV());
			//complete(e, e.getV());//
		};

		subscribeEvent(GetVehicleEvent.class, cTake);
		subscribeEvent(ReleaseVehicleEvent.class, cRelease);


		countDownLatch.countDown();
		subscribeBroadcast(TerminateBrod.class, term->{
			terminate();
		});
	}
}
