package hr.fer.bioinf.utils;

public class Hasher {
  private int hashCode;

  public Hasher() {
    this.hashCode = 1337;
  }

  public void feed(int value) {
    this.hashCode *= 10007;
    this.hashCode += value;
  }

  public void feed(boolean value) {
    this.hashCode *= 10007;
    this.hashCode += (value ? 1 : 0);
  }

  public void feed(String value) {
    this.hashCode *= 10007;
    this.hashCode += value.hashCode();
  }

  @Override
  public int hashCode() {
    return hashCode;
  }
}
