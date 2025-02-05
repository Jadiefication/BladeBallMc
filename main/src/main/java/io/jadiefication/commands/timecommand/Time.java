package io.jadiefication.commands.timecommand;

public enum Time {

    DAY(0),
    NOON(6000),
    NIGHT(13000),
    MIDNIGHT(18000);

    private final int ticks;

    Time(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
