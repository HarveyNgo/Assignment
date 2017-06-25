package com.ngovihung.assignment.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ngovihung.assignment.Application;
import com.ngovihung.assignment.Constant;
import com.ngovihung.assignment.DayAxisValueFormatter;
import com.ngovihung.assignment.MyMarkerView;
import com.ngovihung.assignment.Portfolio;
import com.ngovihung.assignment.R;
import com.ngovihung.assignment.Utils;

import java.util.ArrayList;

/**
 * Created by ngo.vi.hung on 6/24/2017.
 */

public class DailyFragment  extends BaseFragment{

    public static DailyFragment getInstance(ArrayList<Portfolio> portfolios) {
        DailyFragment screen = new DailyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("fortfolio", portfolios);
        screen.setArguments(bundle);
        return screen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            portfolios = (ArrayList<Portfolio>) bundle.getSerializable("fortfolio");
        }
        lineChart = (LineChart) v.findViewById(R.id.activity_main_chart);
        lineChart.setOnChartGestureListener(this);
        lineChart.setDrawGridBackground(false);
        // enable touch gestures
        lineChart.setTouchEnabled(true);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(lineChart);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinimum(0f); //TODO
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        xAxis.setValueFormatter(xAxisFormatter);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(Application.getContext(), R.layout.custom_marker_view, xAxisFormatter);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart

        lineChart.getAxisRight().setEnabled(false); //TODO
        lineChart.animateX(2500);
        Legend l = lineChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.SQUARE);
        lineChart.getLegend().setEnabled(false);

        //tvX = (TextView) v.findViewById(R.id.tvXMax);
        tvItems = (TextView) v.findViewById(R.id.daily_tv_seek2_end);

        mSeekBarDates = (SeekBar) v.findViewById(R.id.seekBar1);
        mSeekBarItems = (SeekBar) v.findViewById(R.id.seekBar2);

        mSeekBarDates.setProgress(0);
        mSeekBarDates.setMax(364);
        mSeekBarItems.setMax(364);
        mSeekBarItems.setProgress(364);

        mSeekBarItems.setOnSeekBarChangeListener(this);
        mSeekBarDates.setOnSeekBarChangeListener(this);
        checkbox_chart_1 = (CheckBox) v.findViewById(R.id.checkbox_chart_1);
        checkbox_chart_1.setOnClickListener(this);
        checkbox_chart_2 = (CheckBox) v.findViewById(R.id.checkbox_chart_2);
        checkbox_chart_2.setOnClickListener(this);
        checkbox_chart_3 = (CheckBox) v.findViewById(R.id.checkbox_chart_3);
        checkbox_chart_3.setOnClickListener(this);

        maxProgress = 365;
        type = Constant.DAILY;
        setData(0,364,getSkipIndex());
        return v;
    }
}
