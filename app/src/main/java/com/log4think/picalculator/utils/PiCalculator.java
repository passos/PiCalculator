package com.log4think.picalculator.utils;

import java.util.Arrays;

/**
 * A helper class to store the value of PI.
 * 
 * TODO: this class is not completed.
 *
 * @author liujinyu <simon.jinyu.liu@gmail.com>
 */
public class PiCalculator {
  // store the value in reverse order, data[data.length-1] is integer part
  private byte[] decimal;
  private int precision;

  public PiCalculator(int precision) {
    this.precision = precision;
    decimal = new byte[precision + 1];
  }

  /**
   * calculate the result of 1/number
   *
   * @param number the number to calculate
   * @return all the numbers after radix point in reverse order
   */
  private byte[] reciprocal(int number) {
    byte[] result = new byte[precision];
    Arrays.fill(result, (byte) 0);

    int scale = 1;
    int divisor = 10;
    while (scale <= precision) {
      result[precision - scale] = (byte) (divisor / number);
      number = 10 % number;
      // divisor *= 10;
      scale++;
    }

    return result;
  }

  /**
   * add the data to decimal parts
   */
  public void add(byte[] data) {
    if (data.length != precision) {
      throw new RuntimeException("data length is not correct");
    }

    for (int i = 0; i < precision; i++) {
      decimal[i] += data[i];
      if (decimal[i] > 10) {
        decimal[i + 1] += decimal[i] / 10;
        decimal[i] %= 10;
      }
    }

    // the integer part
    decimal[precision] += decimal[precision] / 10;
    decimal[precision] %= 10;
  }

  public void sub(byte[] data) {
    if (data.length != precision) {
      throw new RuntimeException("data length is not correct");
    }

    for (int i = 0; i < precision; i++) {
      if (decimal[i] < 0) {
        decimal[i + 1] -= 1;
        decimal[i] += 10;
      }

      if (decimal[i] > data[i]) {
        decimal[i] -= data[i];
      } else {
        decimal[i + 1] -= 1;
        decimal[i] += 10;
        decimal[i] -= data[i];
      }
    }

    // adjust the integer part
    if (decimal[precision - 1] < 0) {
      decimal[precision] -= 1;
      decimal[precision - 1] += 10;
    }
  }

  public void multi(int data) {
    for (int i = 0; i < precision; i++) {
      decimal[i] *= data;
      if (decimal[i] > 10) {
        decimal[i + 1] += decimal[i] / 10;
        decimal[i] %= 10;
      }
    }
  }

  public String toString() {
    byte[] str = new byte[precision];
    byte zero = (byte) '0';
    for (int i = 0; i < precision; i++) {
      str[i] = (byte) (zero + decimal[precision - i]);
    }

    return String.format("%d.%s", decimal[precision], new String(str));
  }

  public static void main(String[] args) {
    PiCalculator piCalculator = new PiCalculator(10);
  }
}
