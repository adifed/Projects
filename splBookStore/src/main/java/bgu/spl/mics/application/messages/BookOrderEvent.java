package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private Customer myCustomer;
    private String bookName;
    private  int orderTick;

    public BookOrderEvent (Customer c, String bookName, int orderTick) {
        this.myCustomer = c;
        this.bookName = bookName;
        this.orderTick = orderTick;
    }

    public Customer getCustomer() {
        return myCustomer;
    }

    public String getBookName() {
        return bookName;
    }
    public int getOrderTick() { return orderTick;}


}
