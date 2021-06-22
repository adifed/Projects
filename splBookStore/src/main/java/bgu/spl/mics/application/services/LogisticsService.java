package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
    private CountDownLatch countDownLatch;
 //   private int time;

    public LogisticsService(CountDownLatch count) {
        super("Logistics Service");
        countDownLatch = count;
    }

    @Override
    protected void initialize() {

        //subscribeBroadcast(TickBroadcast.class, (t)->{
      //      time = t.getTimeForSleeping();
       // });

        Callback<DeliveryEvent> c = (e) -> {
            //I changed it
            Future<Future<DeliveryVehicle>> myVehicle = sendEvent(new GetVehicleEvent());// ResourceService is going to answer that
            if(myVehicle == null || myVehicle.get() == null || myVehicle.get().get() == null){
                complete(e, null);
                return;
            }

            DeliveryVehicle deliveryVehicle = myVehicle.get().get();
            deliveryVehicle.deliver(e.getCustomerAddress(), e.getCustomerDistance());
          //  System.out.println("car: " + " speed: " + deliveryVehicle.getSpeed() +"address: " +  e.getCustomerAddress());
            sendEvent(new ReleaseVehicleEvent(deliveryVehicle));
           // complete(e, deliveryVehicle);

        };
        subscribeEvent(DeliveryEvent.class,c);
        countDownLatch.countDown();
     //   sendBroadcast(new TickBroadcast(,));

        subscribeBroadcast(TerminateBrod.class, term->{
            terminate();
        });
    }

}
