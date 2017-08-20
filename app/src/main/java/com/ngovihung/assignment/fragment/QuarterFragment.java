package com.ngovihung.assignment.fragment;

import android.os.Bundle;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.ngovihung.assignment.Application;
import com.ngovihung.assignment.tools.Constant;
import com.ngovihung.assignment.MyMarkerView;
import com.ngovihung.assignment.data.Portfolio;
import com.ngovihung.assignment.tools.MonthAxisValueFormatter;
import com.ngovihung.assignment.tools.QuarterAxisValueFormatter;
import com.ngovihung.assignment.R;

import java.util.ArrayList;

/**
 * Created by ngo.vi.hung on 6/24/2017.
 */

public class QuarterFragment extends BaseFragment{

    public static QuarterFragment getInstance(ArrayList<Portfolio> portfolios) {
        QuarterFragment screen = new QuarterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("fortfolio", portfolios);
        screen.setArguments(bundle);
        return screen;
    }


    @Override
    protected int getType() {
        return  Constant.QUARTER;
    }

    @Override
    protected int getMaxProgress() {
        return 4;
    }


    @Override
    protected void onBindData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            setPortfolios((ArrayList<Portfolio>) bundle.getSerializable("fortfolio"));
        }
    }

    @Override
    protected IAxisValueFormatter getXAxisValueFormatter() {
        return new QuarterAxisValueFormatter(getLineChart());
    }
}
