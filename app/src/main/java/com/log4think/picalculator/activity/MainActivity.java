package com.log4think.picalculator.activity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.log4think.picalculator.R;
import com.log4think.picalculator.event.CalculatorEvent;
import com.log4think.picalculator.utils.Calculator;
import com.log4think.picalculator.view.CalculatorView;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

  private FloatingActionButton fab;
  private boolean isRunning = false;
  private boolean invalidating = false;

  private CalculatorView calculatorView;
  private Calculator calculator;
  private Calculator.OnUpdateListener onUpdateListener = new Calculator.OnUpdateListener() {
    @Override
    public void onUpdate(final long time, final long progress, final BigDecimal result) {
      final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

      if (calculatorView != null && !invalidating) {
        invalidating = true;
        MainActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            calculatorView.setTime(String.format("Time: %.3f seconds", time / 1000f));
            calculatorView.setProgress(String.format("Progress: %s", numberFormat.format(progress)));
            calculatorView.setResult("Result: " + result.toString());
            invalidating = false;
          }
        });
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    EventBus.getDefault().register(this);

    initUI();
  }

  @Override
  protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override
  protected void onPause() {
    stopCalculate();
    super.onPause();
  }

  private void initUI() {
    calculatorView = (CalculatorView) findViewById(R.id.view_calculator);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fab.setVisibility(View.INVISIBLE);

        if (isRunning) {
          EventBus.getDefault().post(new CalculatorEvent.Stop());
        } else {
          EventBus.getDefault().post(new CalculatorEvent.Start());
        }
      }
    });
  }

  private void startCalculate() {
    if (calculator == null) {
      calculator = new Calculator(100);
      calculator.setOnUpdateListener(onUpdateListener);
    }
    calculator.start();
  }

  private void stopCalculate() {
    if (calculator != null) {
      calculator.stop();
    }
  }

  public void onEventMainThread(CalculatorEvent.Start event) {
    startCalculate();
  }

  public void onEventMainThread(CalculatorEvent.Started event) {
    Drawable drawable =
        ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause);
    fab.setImageDrawable(drawable);
    isRunning = true;
    fab.setVisibility(View.VISIBLE);
  }

  public void onEventMainThread(CalculatorEvent.Stop event) {
    stopCalculate();
  }

  public void onEventMainThread(CalculatorEvent.Stopped event) {
    Drawable drawable =
        ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play);
    fab.setImageDrawable(drawable);
    isRunning = false;
    fab.setVisibility(View.VISIBLE);
  }

  public void onEventMainThread(CalculatorEvent.Reset event) {
    if (calculator != null) {
      calculator.stop();
      calculator.reset();
    }
  }
}
