package pl.socha23.cyberfirelocator;

import java.util.Date;

public class SynchronizationErrorEvent {

    private String message;
    private Date date = new Date();

    public SynchronizationErrorEvent(String message) {
        this.message = message;
    }

    public  Date getDate() {
        return date;
    }

    public String getMessage() {

        return message;
    }
}
