package com.nityant.hotel;

/**
 * com.nityant.hotel.CleaningThread — simulates asynchronous room cleaning after
 * checkout.
 * Demonstrates multithreading by extending Thread.
 */
public class CleaningThread extends Thread {
    private final Room room;
    private final Runnable onComplete; // callback to refresh UI

    public CleaningThread(Room room, Runnable onComplete) {
        this.room = room;
        this.onComplete = onComplete;
        setDaemon(true); // don't block JVM shutdown
    }

    @Override
    public void run() {
        System.out.println("[com.nityant.hotel.CleaningThread] com.nityant.hotel.Room " + room.getRoomNumber()
                + " cleaning started...");
        try {
            Thread.sleep(3000); // simulate 3-second cleaning
        } catch (InterruptedException e) {
            System.out.println("[com.nityant.hotel.CleaningThread] Interrupted!");
        }
        room.setAvailable(true);
        System.out.println("[com.nityant.hotel.CleaningThread] com.nityant.hotel.Room " + room.getRoomNumber()
                + " is now available!");
        if (onComplete != null)
            onComplete.run();
    }
}