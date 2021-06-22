package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBrod;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer c;
	private LinkedList <OrderSchedule> myOrderList;
	private CountDownLatch countDownLatch;

	public APIService(CountDownLatch count, Customer customer) {
		super("API Service");

		myOrderList = customer.getOrderSchedules();
		this.c = customer;
		
		countDownLatch = count;
	}


	@Override
	protected void initialize() {

		Callback<TickBroadcast> callTick = (e) -> {

			for (OrderSchedule o: myOrderList) {
				if (o.getTick() == e.getTick()) {
					Future<OrderReceipt> f = sendEvent(new BookOrderEvent(c, o.getBook(), o.getTick()));

				}
			}
		//	System.out.println("customer: " + c.getName() + "has left: " + c.getAvailableCreditAmount() + " amount");
		};

		subscribeBroadcast(TickBroadcast.class, callTick);
		countDownLatch.countDown();

		subscribeBroadcast(TerminateBrod.class,term->{
			terminate();
		});
	}

}
