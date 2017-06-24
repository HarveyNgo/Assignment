package com.ngovihung.assignment.fragment;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ngovihung.assignment.Application;
import com.ngovihung.assignment.Constant;
import com.ngovihung.assignment.DayAxisValueFormatter;
import com.ngovihung.assignment.MyMarkerView;
import com.ngovihung.assignment.NavsItem;
import com.ngovihung.assignment.Portfolio;
import com.ngovihung.assignment.R;
import com.ngovihung.assignment.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngo.vi.hung on 6/24/2017.
 */

public class DailyFragment  extends BaseFragment implements OnChartGestureListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, OnChartValueSelectedListener {

    LineChart lineChart;
    CheckBox checkbox_chart_1, checkbox_chart_2, checkbox_chart_3;
    ArrayList<Portfolio> portfolios;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

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
        View v = inflater.inflate(R.layout.fragment_activity_main, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            portfolios = (ArrayList<Portfolio>) bundle.getSerializable("fortfolio");
        }
        lineChart = (LineChart) v.findViewById(R.id.activity_main_chart);
        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDrawGridBackground(false);
        // enable touch gestures
        lineChart.setTouchEnabled(true);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);

        // x-axis limit line
//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        llXAxis.setTextSize(10f); //TODO

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(lineChart);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1); //TODO
//        leftAxis.addLimitLine(ll2); //TODO
//        leftAxis.setAxisMaximum(200f); //TODO
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
        l.setForm(Legend.LegendForm.LINE);

        //lineChart.getXAxis().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);

        tvX = (TextView) v.findViewById(R.id.tvXMax);
        tvY = (TextView) v.findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) v.findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) v.findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(1);
        mSeekBarX.setMax(364);
        mSeekBarY.setMax(364);
        mSeekBarY.setProgress(364);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);
        checkbox_chart_1 = (CheckBox) v.findViewById(R.id.checkbox_chart_1);
        checkbox_chart_1.setOnClickListener(this);
        checkbox_chart_2 = (CheckBox) v.findViewById(R.id.checkbox_chart_2);
        checkbox_chart_2.setOnClickListener(this);
        checkbox_chart_3 = (CheckBox) v.findViewById(R.id.checkbox_chart_3);
        checkbox_chart_3.setOnClickListener(this);
        setDailyChart();
        return v;
    }

    private void setDailyChart() {
        if (lineChart.getData() != null)
            lineChart.getData().clearValues();

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (int i = 0; i < portfolios.size(); i++) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0; j < portfolios.get(i).getDailyNavs().size(); j++) {
//                int position = portfolios.get(i).getDailyNavs().get(j).getDate() == null ? j: Utils.getDayInYear(portfolios.get(i).getDailyNavs().get(j).getDate());
//                entries.add(new Entry(position,
//                        portfolios.get(i).getDailyNavs().get(j).getAmount(),getResources().getDrawable(R.drawable.star)));
                if (portfolios.get(i).getDailyNavs().get(j).getDate() != null &&
                        portfolios.get(i).getDailyNavs().get(j).getAmount() > 0) {
                    entries.add(new Entry(Utils.getDayInYear(portfolios.get(i).getDailyNavs().get(j).getDate()),
                            portfolios.get(i).getDailyNavs().get(j).getAmount(), getResources().getDrawable(R.drawable.star)));
                } else {
                    continue;
                }
            }
            LineDataSet dataset = setLineDataSetStyle(entries, "aa", i);
            if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), Constant.FADE_COLORS[i]);
                dataset.setFillDrawable(drawable);
            } else {
                dataset.setFillColor(Color.BLACK);
            }
            dataSets.add(dataset);
        }
        ArrayList<String> xAxisLabels = new ArrayList<>();
        for (int i = 0; i < 365; i++) {
            xAxisLabels.add(Utils.getDateString(i));
        }
        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        lineChart.setData(data);
        //lineChart.notifyDataSetChanged();
        //lineChart.invalidate();
    }

    private LineDataSet setLineDataSetStyle(ArrayList<Entry> entries, String title, int position) {
        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.setColor(Constant.COLORS[position]);
        dataset.enableDashedLine(10f, 5f, 0f);
        dataset.enableDashedHighlightLine(10f, 5f, 0f);
        dataset.setCircleColor(Color.BLACK);
        dataset.setLineWidth(1f);
        dataset.setCircleRadius(3f);
        dataset.setDrawCircleHole(false);
        dataset.setValueTextSize(9f);
        dataset.setDrawFilled(true);
        dataset.setFormLineWidth(1f);
        dataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        dataset.setFormSize(15.f);
        return dataset;
    }

        private void setData(int fromIndex, int range, int type, ArrayList<Integer> skipIndex) {
            if (lineChart.getData() != null)
                lineChart.getData().clearValues();

            if (type == 1) { //Daily
                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                for (int i = 0; i < portfolios.size(); i++) {
                    if (skipIndex.contains(i))
                        continue;
                    ArrayList<Entry> entries = new ArrayList<>();
                    List<NavsItem> navsItems = portfolios.get(i).getDailyNavs().subList(fromIndex, fromIndex + range + 1);
                    for (int j = 0; j < navsItems.size(); j++) {
                        if (navsItems.get(j).getDate() != null &&
                                navsItems.get(j).getAmount() > 0) {
                            entries.add(new Entry(Utils.getDayInYear(navsItems.get(j).getDate()),
                                    navsItems.get(j).getAmount(), getResources().getDrawable(R.drawable.star)));
                        } else {
                            continue;
                        }
                    }
                    LineDataSet dataset = setLineDataSetStyle(entries, "aa", i);
                    if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {// fill drawable only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(getContext(), Constant.FADE_COLORS[i]);
                        dataset.setFillDrawable(drawable);
                    } else {
                        dataset.setFillColor(Color.BLACK);
                    }
                    dataSets.add(dataset);
                }
                // create a data object with the datasets
                LineData data = new LineData(dataSets);
                lineChart.setData(data);
            }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawIcons(!set.isDrawIconsEnabled());
                }

                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (lineChart.getData() != null) {
                    lineChart.getData().setHighlightEnabled(!lineChart.getData().isHighlightEnabled());
                    lineChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.CUBIC_BEZIER);
                }
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.STEPPED);
                }
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                List<ILineDataSet> sets = lineChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                lineChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (lineChart.isPinchZoomEnabled())
                    lineChart.setPinchZoom(false);
                else
                    lineChart.setPinchZoom(true);

                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                lineChart.setAutoScaleMinMaxEnabled(!lineChart.isAutoScaleMinMaxEnabled());
                lineChart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                lineChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                lineChart.animateY(3000, Easing.EasingOption.EaseInCubic);
                break;
            }
            case R.id.animateXY: {
                lineChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (lineChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void OptionItemSelected(MenuItem item) {


    }
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }


    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);
        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            lineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.seekBar1:
                mSeekBarY.setMax(365-mSeekBarX.getProgress());
                mSeekBarY.setProgress(mSeekBarY.getMax());
                break;
        }
        tvX.setText("" + (mSeekBarX.getProgress()+1)); //Utils.getDateString(mSeekBarX.getProgress()));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress(), mSeekBarY.getProgress()-1,1,getSkipIndex());

        // redraw
        lineChart.invalidate();
    }
    private ArrayList<Integer> getSkipIndex(){
        ArrayList<Integer> skipIndexes = new ArrayList<>();
        if(!checkbox_chart_1.isChecked() )
            skipIndexes.add(0);
        if(!checkbox_chart_2.isChecked() )
            skipIndexes.add(1);
        if(!checkbox_chart_3.isChecked() )
            skipIndexes.add(2);
        return skipIndexes;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_chart_1:
            case R.id.checkbox_chart_2:
            case R.id.checkbox_chart_3:
                setData(mSeekBarX.getProgress(), mSeekBarY.getProgress() - 1, 1, getSkipIndex());// redraw
                lineChart.invalidate();
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
