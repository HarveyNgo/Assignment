package com.ngovihung.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.ANCHORED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.DRAGGING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.HIDDEN;

public class MainActivity extends AppCompatActivity implements  SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener {

    LineChart lineChart;

    ViewGroup activity_main_fl_options;
    ViewGroup option_daily_monthly_quarter;
    ArrayList<Portfolio>  portfolios;
    TextView activity_main_tv_option;
    SlidingUpPanelLayout activity_main_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Application.setActiveActivity(this);
        Application.setContext(this);
        lineChart = (LineChart) findViewById(R.id.activity_main_chart);
        activity_main_fl_options = (ViewGroup) findViewById(R.id.activity_main_fl_options);
        View optionLayout = LayoutInflater.from(this).inflate(R.layout.option,null);
        option_daily_monthly_quarter = (ViewGroup) optionLayout.findViewById(R.id.option_daily_monthly_quarter);
        activity_main_tv_option = (TextView) findViewById(R.id.activity_main_tv_option);
        activity_main_tv_option.setOnClickListener(this);
        activity_main_content = (SlidingUpPanelLayout) findViewById(R.id.activity_main_content);
        portfolios = new ArrayList<>();
        getData();
        //setChart();
        //setChartMonthly();
        setChartQuarterLy();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Application.setActiveActivity(this);
    }

    public void getData(){
        String data = Utils.readSampleFile("data_json.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JSONArray portfolioJsonList = null;
        try {
            portfolioJsonList = new JSONArray(data);
            if (portfolioJsonList != null && portfolioJsonList.length() > 0 ) {
                for (int i = 0; i < portfolioJsonList.length(); i++) {
                    JSONObject  portfolioJson = portfolioJsonList.getJSONObject(i);
                    Portfolio p = new Portfolio(portfolioJson.optString(Constant.PORTFOLIO_ID_TAG));
                    p.getNavsItems().clear();
                    JSONArray navsJsonList = portfolioJson.getJSONArray(Constant.NAVS_TAG);
                    if (navsJsonList != null && navsJsonList.length() > 0) {
                        for (int j = 0; j < navsJsonList.length(); ++j) {
                            NavsItem item = gson.fromJson(navsJsonList.getJSONObject(j).toString(), NavsItem.class);
                            p.getNavsItems().add(item);

                            if(j > 0 ){
                                p.getLastDateOfMonthNavs(p.getNavsItems().get(j-1),p.getNavsItems().get(j));
                                p.getQuarterMonthNavs(p.getNavsItems().get(j-1),p.getNavsItems().get(j));
                            }
                        }
                    }
                    //
                    portfolios.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setChart(){

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< portfolios.get(i).getNavsItems().size(); j++){
                entries.add(new Entry(portfolios.get(i).getNavsItems().get(j).getAmount(),
                        Utils.getDayInYear(portfolios.get(i).getNavsItems().get(j).getDate())));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> labels = new ArrayList<>();
        for(int i=0; i< 365; i++){
            labels.add(Utils.getDateString(i));
        }
        LineData data = new LineData(labels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public void setChartMonthly(){

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< 12; j++){
                entries.add(new Entry(portfolios.get(i).getLastDateOfMonthNavs().get(j)
                        .getAmount(),j));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> labels = new ArrayList<>();
        for(String month : Constant.MONTHS){
            labels.add(month);
        }
        LineData data = new LineData(labels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void setChartQuarterLy(){
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< 4; j++){
                entries.add(new Entry(portfolios.get(i).getQuarterlyNavs().get(j)
                        .getAmount(),j));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> labels = new ArrayList<>();
        for(int i=1; i<5;i++){
            String quarter = Constant.MONTHS[((Constant.MONTHS.length /4)*i) -1];
            labels.add(quarter);
        }
        LineData data = new LineData(labels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (panel.findViewById(R.id.activity_main_fl_options).equals(activity_main_fl_options)) {
            if ((previousState == ANCHORED || previousState == EXPANDED) && newState == HIDDEN)
                activity_main_fl_options.removeAllViews();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_main_tv_option:
                openTableOptions(option_daily_monthly_quarter);
        }
    }

    public void openTableOptions(View view) {
        if (activity_main_content.getPanelState() == ANCHORED || activity_main_content.getPanelState() == DRAGGING)
            return;

        activity_main_fl_options.removeAllViews();
        if (view instanceof View)
            activity_main_fl_options.addView((View) view);
        activity_main_content.setPanelState(ANCHORED);
    }

    public void closeTableOptions() {
        if (activity_main_content.getPanelState() == HIDDEN || activity_main_content.getPanelState() == COLLAPSED || activity_main_content.getPanelState() == DRAGGING)
            return;

        activity_main_content.setPanelState(HIDDEN);
    }
}
