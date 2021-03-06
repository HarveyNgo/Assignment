package com.ngovihung.assignment.fragment;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.ngovihung.assignment.Application;
import com.ngovihung.assignment.MyMarkerView;
import com.ngovihung.assignment.tools.Constant;
import com.ngovihung.assignment.data.NavsItem;
import com.ngovihung.assignment.data.Portfolio;
import com.ngovihung.assignment.R;
import com.ngovihung.assignment.tools.DayAxisValueFormatter;
import com.ngovihung.assignment.tools.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngo.vi.hung on 6/24/2017.
 */

public abstract class BaseFragment extends Fragment implements OnChartGestureListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    private LineChart lineChart;
    private ArrayList<Portfolio> portfolios;
    private CheckBox checkbox_chart_1, checkbox_chart_2, checkbox_chart_3;
    private SeekBar mSeekBarDates, mSeekBarItems;
    private TextView tvX, tvItems;
    //int maxProgress=365;

    //private int type=-1;
    private boolean isDrawValues = true;
    private boolean isDrawFill = true;
    private boolean isDrawCircle = true;
    private boolean isDrawCubicBezier = false;
    private boolean isDrawStepped = false;
    private boolean isDrawHorizontalBezier = false;
    private LineDataSet.Mode lineDataSetMode = LineDataSet.Mode.LINEAR;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_base, container, false);
        onBindData();
        init(v);
        setData(0,getMaxProgress(),getSkipIndex());
        return v;
    }

    private void init(View v){
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

        IAxisValueFormatter xAxisFormatter = getXAxisValueFormatter();

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
        mSeekBarDates.setMax(getMaxProgress() -1);
        mSeekBarItems.setMax(getMaxProgress() -1);
        mSeekBarItems.setProgress(getMaxProgress() -1);

        mSeekBarItems.setOnSeekBarChangeListener(this);
        mSeekBarDates.setOnSeekBarChangeListener(this);
        checkbox_chart_1 = (CheckBox) v.findViewById(R.id.checkbox_chart_1);
        checkbox_chart_1.setOnClickListener(this);
        checkbox_chart_2 = (CheckBox) v.findViewById(R.id.checkbox_chart_2);
        checkbox_chart_2.setOnClickListener(this);
        checkbox_chart_3 = (CheckBox) v.findViewById(R.id.checkbox_chart_3);
        checkbox_chart_3.setOnClickListener(this);
    }
    protected LineDataSet setLineDataSetStyle(ArrayList<Entry> entries, int position) {
        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.setColor(Constant.COLORS[position]);
        dataset.enableDashedLine(10f, 5f, 0f);
        dataset.enableDashedHighlightLine(10f, 5f, 0f);
        dataset.setCircleColor(Constant.COLORS[position]);
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

    private void setData(int fromIndex, int range, ArrayList<Integer> skipIndex) {
        if (lineChart.getData() != null)
            lineChart.getData().clearValues();
        int maxItems=0;
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (int i = 0; i < portfolios.size(); i++) {
            if (skipIndex.contains(i))
                continue;
            ArrayList<Entry> entries = new ArrayList<>();
            List<NavsItem> navsItems = new ArrayList<>();
            if(getType() == Constant.DAILY){
                navsItems = portfolios.get(i).getDailyNavs().subList(fromIndex, fromIndex + range);
            }else if(getType() == Constant.MONTHLY){
                navsItems = portfolios.get(i).getMonthlyNavs().subList(fromIndex, fromIndex + range);
            }else if(getType() == Constant.QUARTER){
                navsItems =  portfolios.get(i).getQuarterlyNavs().subList(fromIndex, fromIndex + range);
            }
            for (int j = 0; j < navsItems.size(); j++) {
                if (navsItems.get(j).getDate() != null &&
                        navsItems.get(j).getAmount() > 0) {
                    if(getType() ==Constant.DAILY) {
                        entries.add(new Entry(Utils.getDayInYear(navsItems.get(j).getDate()),
                                navsItems.get(j).getAmount()));
                    }else if(getType() ==  Constant.MONTHLY){
                        entries.add(new Entry(Utils.getMonthOfYear(navsItems.get(j).getDate()),
                                navsItems.get(j).getAmount()));
                    }else if (getType() ==  Constant.QUARTER){
                        entries.add(new Entry(Utils.getQuarterIndex(navsItems.get(j).getDate()),
                                navsItems.get(j).getAmount()));
                    }
                } else {
                    continue;
                }
            }
            if(entries.size() <=0)
                continue;
            else{
                if(maxItems <entries.size())
                    maxItems = entries.size();
            }
            LineDataSet dataset = setLineDataSetStyle(entries, i);
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
        applyStyleForChart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                isDrawValues = !isDrawValues;
                applyStyleForChart();
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
                isDrawFill = !isDrawFill;
                applyStyleForChart();
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                isDrawCircle = !isDrawCircle;
                applyStyleForChart();
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                isDrawCubicBezier = !isDrawCubicBezier;
                lineDataSetMode = isDrawCubicBezier ?  LineDataSet.Mode.CUBIC_BEZIER : LineDataSet.Mode.LINEAR;
                applyStyleForChart();
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                isDrawStepped = !isDrawStepped;
                lineDataSetMode = isDrawStepped ?  LineDataSet.Mode.STEPPED : LineDataSet.Mode.LINEAR;
                applyStyleForChart();
                lineChart.invalidate();
                break;
            }
            case R.id.actionToggleHorizontalCubic: {
                isDrawHorizontalBezier=!isDrawHorizontalBezier;
                lineDataSetMode = isDrawHorizontalBezier ?  LineDataSet.Mode.HORIZONTAL_BEZIER : LineDataSet.Mode.LINEAR;
                applyStyleForChart();
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


    protected LineChart getLineChart(){
        return this.lineChart;
    }

    private void applyStyleForChart(){
        List<ILineDataSet> sets = lineChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {
            LineDataSet set = (LineDataSet) iSet;
            set.setDrawValues(isDrawValues);
            set.setDrawFilled(isDrawFill);
            set.setDrawCircles(isDrawCircle);
            set.setMode(lineDataSetMode);
        }
    }

    public ArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(ArrayList<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    protected abstract int getType();
    protected abstract int getMaxProgress();
    protected abstract IAxisValueFormatter getXAxisValueFormatter();

    protected abstract void onBindData();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_chart_1:
            case R.id.checkbox_chart_2:
            case R.id.checkbox_chart_3:
                setData(mSeekBarDates.getProgress(), mSeekBarItems.getProgress() - 1, getSkipIndex());// redraw
                lineChart.invalidate();
                break;
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }


    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);
        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
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
        switch (seekBar.getId()) {
            case R.id.seekBar1:
                mSeekBarItems.setMax(getMaxProgress() - mSeekBarDates.getProgress());
                mSeekBarItems.setProgress(mSeekBarItems.getMax());
                break;
            case R.id.seekBar2:
                if(mSeekBarItems.getProgress() == 0 )
                    mSeekBarItems.setProgress(1);
                break;
        }
        //tvX.setText("" + (mSeekBarDates.getProgress() + 1)); //Utils.getDateString(mSeekBarDates.getProgress()));
        tvItems.setText("" + (mSeekBarItems.getProgress()));
        setData(mSeekBarDates.getProgress(), mSeekBarItems.getProgress(),  getSkipIndex());

        // redraw
        lineChart.invalidate();
    }

    protected ArrayList<Integer> getSkipIndex() {
        ArrayList<Integer> skipIndexes = new ArrayList<>();
        if (!checkbox_chart_1.isChecked())
            skipIndexes.add(0);
        if (!checkbox_chart_2.isChecked())
            skipIndexes.add(1);
        if (!checkbox_chart_3.isChecked())
            skipIndexes.add(2);
        return skipIndexes;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
