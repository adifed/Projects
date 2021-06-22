package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBrod;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private Timer timer;
	private int speed;
	private int duration; // ticks before termination
	private AtomicInteger currentTime;
	private TimeUnit unit;

	public TimeService(int speed, int duration) {
		super("Time Service");
		this. speed = speed;
		this.duration = duration;
		unit=TimeUnit.MILLISECONDS;
		currentTime = new AtomicInteger(0);
	}

	@Override
	protected void initialize() {

			while (duration > 0){
				synchronized (this) {
					try {
						unit.timedWait(this, speed);
						sendBroadcast(new TickBroadcast(currentTime.getAndIncrement())); // we'll send a tick broadcast and increment number of ticks
						duration--;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			sendBroadcast(new TerminateBrod());
			terminate();
	}
}
