package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeEvent implements Event<OrderResult> {
    String bookName;
    public TakeEvent(String bookName) {
        this.bookName=bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
