package com.log4think.picalculator.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.log4think.picalculator.R;

/**
 * @author liujinyu <simon.jinyu.liu@gmail.com>
 */
public class CalculatorView extends LinearLayout {
  private TextView timeView;
  private TextView progressView;
  private TextView resultView;

  public CalculatorView(Context context) {
    super(context);
    initView(context);
  }

  public CalculatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  private void initView(Context context) {
    View view = inflate(context, R.layout.view_calculator, this);
    timeView = (TextView) view.findViewById(R.id.view_time);
    resultView = (TextView) view.findViewById(R.id.view_result);
    progressView = (TextView) view.findViewById(R.id.view_progress);
  }

  public void setTime(String time) {
    timeView.setText(time);
  }

  public void setResult(String result) {
    resultView.setText(result);
  }

  public void setProgress(String progress) {
    progressView.setText(progress);
  }
}
