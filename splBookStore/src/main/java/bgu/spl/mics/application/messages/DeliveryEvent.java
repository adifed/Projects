package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class DeliveryEvent implements Event {
    String customerAddress;
    int customerDistance;
    public DeliveryEvent (String customerAddress, int customerDistance) {
        this.customerAddress = customerAddress;
        this.customerDistance = customerDistance;
    }

    public int getCustomerDistance() {
        return customerDistance;
    }
    public  String getCustomerAddress() {
        return  customerAddress;
    }

}
