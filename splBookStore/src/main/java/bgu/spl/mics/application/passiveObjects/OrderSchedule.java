package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderSchedule implements Serializable {
    private int tickNum;
    private String bookTitle;

    public OrderSchedule (String b, int t) {
        this.bookTitle = b;
        this.tickNum = t;
    }

    public int getTick () {
        return this.tickNum;
    }

    public String getBook() {
        return this.bookTitle;
    }
}
