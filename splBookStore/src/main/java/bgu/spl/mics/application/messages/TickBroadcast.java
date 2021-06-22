package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int tick;

    public TickBroadcast(int n) {
        tick = n;
    }

    public int getTick() {
        return tick;
    }


}
