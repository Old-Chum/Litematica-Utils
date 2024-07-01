package me.oldchum.litematicautils;

/**
 * A stopwatch.
 *
 * @author Old Chum
 * @since April 27, 2020
 */
public class Timer {
    private long time;

    public Timer() {
        this.reset();
    }

    /** @return If the given amount of time (in ms) has passed since this timer has last reset. */
    public boolean hasPassed (long time) {
        return System.currentTimeMillis() - this.time >= time;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    /** @return The amount of time (in ms) that has passed since this timer has been reset */
    public long getTime() {
        return System.currentTimeMillis() - this.time;
    }
}
