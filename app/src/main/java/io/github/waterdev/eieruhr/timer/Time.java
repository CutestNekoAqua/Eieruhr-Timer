package io.github.waterdev.eieruhr.timer;

public class Time {

    private short hours, minutes, seconds;

    private boolean forceEnded = false;

    public Time(int hours, int minutes, int seconds) {
        this.hours = (short) hours;
        this.minutes = (short) minutes;
        this.seconds = (short) seconds;
    }

    @Override
    public String toString() {
        return (hours < 10 ? "0" : "") + hours +
                ":" + (minutes < 10 ? "0" : "") + minutes +
                ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public void forceEnd() {
        forceEnded = true;
    }

    public void count() {
        if(seconds <= 0) {
            seconds = 59;
            if(minutes <= 0) {
                hours--;
                minutes = 59;
            } else {
                minutes--;
            }
        } else {
            seconds--;
        }
    }

    public boolean ended() {
        return forceEnded || (hours <= 0 && minutes <= 0 && seconds <= 0);
    }

    public boolean isForceEnded() {
        return forceEnded;
    }

    public short getHours() {
        return hours;
    }

    public void setHours(short hours) {
        this.hours = hours;
    }

    public short getMinutes() {
        return minutes;
    }

    public void setMinutes(short minutes) {
        this.minutes = minutes;
    }

    public short getSeconds() {
        return seconds;
    }

    public void setSeconds(short seconds) {
        this.seconds = seconds;
    }

    public Time(short hours, short minutes, short seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
}
