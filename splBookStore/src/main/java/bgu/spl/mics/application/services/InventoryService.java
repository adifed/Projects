package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AvailabilityAndPriceEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBrod;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;


/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventoryInstance = Inventory.getInstance();
	private CountDownLatch countDownLatch;


	public InventoryService(CountDownLatch count) {
		super("Inventory Service");

		countDownLatch = count;

	}

	@Override
	protected void initialize() {
		Callback <AvailabilityAndPriceEvent> checkCall = (e) -> {  // e is not a class but some AvailabilityAndPriceEvent
			int price = inventoryInstance.checkAvailabiltyAndGetPrice(e.getBookName());
			complete(e,price);
		};
		Callback <TakeBookEvent> takeCall = (e) -> {
			OrderResult o = inventoryInstance.take(e.getBookName());
			complete(e, o);
		};

		subscribeEvent(AvailabilityAndPriceEvent.class, checkCall);
		subscribeEvent(TakeBookEvent.class, takeCall);

		countDownLatch.countDown();

		subscribeBroadcast(TerminateBrod.class, term->{
			terminate();
		});
	}
}
