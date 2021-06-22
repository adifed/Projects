package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegisterInstance = MoneyRegister.getInstance();
	private String bookName;
	private Customer myCustomer;
	private int issuedTick;
	private CountDownLatch countDownLatch;
	private Object myLock  = new Object();
	private int recieptId=0;

	public SellingService(int i, CountDownLatch count) {
		super("Seller" + i);
	//	System.out.println(this.getName());
		countDownLatch = count;
	}

	@Override
	protected void initialize() {
		Callback <BookOrderEvent> callBookOrder = (e) -> {  // e is not a class but some AvailabilityAndPriceEvent
			bookName = e.getBookName();
			myCustomer = e.getCustomer();
			Future<Integer> myPrice = sendEvent(new AvailabilityAndPriceEvent(bookName)); // Inventory Service is going to answer that
			//System.out.println("The price is: " + myPrice.get() + " , " + " the book name is: " + bookName + " customer: " + myCustomer.getName());
			if (myPrice.get() != -1 && myCustomer.getAvailableCreditAmount() >= myPrice.get()) {
			//	Future<OrderResult> myResult = sendEvent(new TakeBookEvent(bookName));
				synchronized (myCustomer) {
					//if (myPrice.get() != -1 && myCustomer.getAvailableCreditAmount() >= myPrice.get()){
						Future<OrderResult> myResult = sendEvent(new TakeBookEvent(bookName));
						if (myResult.get().equals(OrderResult.SUCCESSFULLY_TAKEN)) {
							moneyRegisterInstance.chargeCreditCard(myCustomer, myPrice.get());
							OrderReceipt myReceipt = new OrderReceipt(recieptId, myCustomer.getId(), this.getName(), bookName, myPrice.get(), issuedTick, e.getOrderTick(), 1);
								myCustomer.addReciept(myReceipt);
							//	System.out.println("price: " + myReceipt.getPrice() + " customer: " + myReceipt.getCustomerId() +
								//		"bookname: " + myReceipt.getBookTitle() + " tick: " + myReceipt.getOrderTick());
								moneyRegisterInstance.file(myReceipt);
								recieptId++;
								sendEvent(new DeliveryEvent(myCustomer.getAddress(), myCustomer.getDistance()));
							complete(e, myReceipt);
						}
					//}
					else {
					//	System.out.println("Sorry, something went wrong");
						complete(e, null);
					}
				}
			}
			else{
				complete(e, null);
				}
		};

		Callback <TickBroadcast> callTick = (e1) -> {
			this.issuedTick = e1.getTick();
		};
		subscribeEvent(BookOrderEvent.class,callBookOrder);
		subscribeBroadcast(TickBroadcast.class, callTick);

		countDownLatch.countDown();
		subscribeBroadcast(TerminateBrod.class,term->{
			terminate();
		});
	}
}
