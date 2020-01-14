package hr.fer.bioinf.utils;

/** Util class for timing program execution. */
public class Clock {
  private long lastTime;

  public Clock() {
    lastTime = System.currentTimeMillis();
  }

  public void restart() {
    lastTime = System.currentTimeMillis();
  }

  public long elapsedTime() {
    return System.currentTimeMillis() - lastTime;
  }
}
