package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;


public class TickBroadcast implements Broadcast {
    private int tick;
    private int timeToEnd;
    public TickBroadcast(int tick, int ttl){
        this.tick=tick;
        this.timeToEnd =ttl;
    }

    public int getTick() {
        return tick;
    }

    public int getTTL() {
        return timeToEnd;
    }
}
