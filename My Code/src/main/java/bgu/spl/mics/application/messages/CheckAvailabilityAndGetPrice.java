package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityAndGetPrice implements Event<Integer> {
    String bookName;
    public CheckAvailabilityAndGetPrice(String bookName) {
        this.bookName=bookName;
    }
    public String getBookName() {
        return bookName;
    }
}
