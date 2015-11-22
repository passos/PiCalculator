package com.log4think.picalculator.utils;

import static java.math.BigDecimal.ROUND_UP;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.log4think.picalculator.event.CalculatorEvent;

import de.greenrobot.event.EventBus;

/**
 * Calculate PI by SUM( 4 * (1 - 1/3 + 1/5 - 1/7 + 1/9 ...) )
 * <p/>
 * Use BigDecimal to do the calculate here but this can be improved.
 * <p/>
 * BigDecimal is not the best one for calculate because it's immutable so that there are many
 * objects are generated and cause GC during the calculation process. The better way is to make a
 * customized BigNumber class, store the numbers in byte[] and do the calculation in place instead
 * of generate a new object.
 *
 * @author liujinyu <simon.jinyu.liu@gmail.com>
 */
public class Calculator {
  private OnUpdateListener onUpdateListener;
  private int precision;
  private long elapsedTime;
  private long progress;
  private BigDecimal result;
  private boolean running;

  public interface OnUpdateListener {
    void onUpdate(long elapsedTime, long progress, BigDecimal result);
  }

  private ExecutorService executorService;

  public Calculator(int precision) {
    this.precision = precision;
    this.running = false;
    reset();
  }

  private class CalculatorService implements Runnable {
    private long startTime;
    private long elapsedTime;
    private long progress;
    private BigDecimal result;

    public CalculatorService(long elapsedTime, long progress, BigDecimal result) {
      this.progress = progress;
      this.result = result;
      this.elapsedTime = elapsedTime;
    }

    @Override
    public void run() {
      startTime = System.currentTimeMillis() - elapsedTime;
      BigDecimal four = new BigDecimal(4).setScale(precision, ROUND_UP);
      for (; running; progress++) {
        BigDecimal base = new BigDecimal((progress % 2 == 0 ? -1 : 1) * (2 * progress - 1));
        BigDecimal value = four.divide(base, ROUND_UP);
        result = result.add(value);

        // update UI every 1000 times to increase performance
        if (onUpdateListener != null && Math.abs(progress) % 1000 == 0) {
          elapsedTime = System.currentTimeMillis() - startTime;
          onUpdateListener.onUpdate(elapsedTime, progress, result);
        }
      }
    }
  }

  public void start() {
    if (running) {
      return;
    }
    running = true;
    CalculatorService calculatorService = new CalculatorService(elapsedTime, progress, result);
    executorService = Executors.newSingleThreadExecutor();
    executorService.execute(calculatorService);

    EventBus.getDefault().post(new CalculatorEvent.Started());
  }

  public void stop() {
    if (!running) {
      return;
    }

    running = false;
    executorService.shutdown();

    EventBus.getDefault().post(new CalculatorEvent.Stopped());
  }

  public void reset() {
    if (running) {
      stop();
    }
    elapsedTime = 0;
    progress = 1;
    result = new BigDecimal(0).setScale(precision, ROUND_UP);
  }

  public void setOnUpdateListener(final OnUpdateListener listener) {
    this.onUpdateListener = new OnUpdateListener() {
      @Override
      public void onUpdate(long elapsedTime, long progress, BigDecimal result) {
        listener.onUpdate(elapsedTime, progress, result);

        // save the progress for pause/resume
        Calculator.this.elapsedTime = elapsedTime;
        Calculator.this.progress = progress;
        Calculator.this.result = result;
      }
    };
  }
}
