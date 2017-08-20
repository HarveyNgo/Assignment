package com.ngovihung.assignment.tools;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


public class QuarterAxisValueFormatter implements IAxisValueFormatter
{

    protected String[] mQuarter = new String[]{
            "Mar",  "Jun",  "Sep",  "Dec"
    };

    private BarLineChartBase<?> chart;

    public QuarterAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int quarter = (int) value;
        if( quarter < 0)
            return "";
        String quarterName = mQuarter[quarter % mQuarter.length];

        return quarterName;

    }
}
