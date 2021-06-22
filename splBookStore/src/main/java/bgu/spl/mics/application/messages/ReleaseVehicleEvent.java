package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    private DeliveryVehicle v;
    public  ReleaseVehicleEvent (DeliveryVehicle v) {
        this.v = v;
    }
    public DeliveryVehicle getV() {
        return v;
    }

}
