package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicle implements Event<Object>{
    private DeliveryVehicle d;

    public ReleaseVehicle(DeliveryVehicle d){
        this.d = d;
    }

    public DeliveryVehicle getDeliveryVehicle() {
        return d;
    }
}
