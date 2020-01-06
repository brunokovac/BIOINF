package hr.fer.bioinf.utils;

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
