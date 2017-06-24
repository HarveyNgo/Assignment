package com.ngovihung.assignment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ngovihung.assignment.fragment.BaseFragment;
import com.ngovihung.assignment.fragment.DailyFragment;
import com.ngovihung.assignment.fragment.MonthlyFragment;
import com.ngovihung.assignment.fragment.QuarterFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements   View.OnClickListener {


    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private LinearLayout menu_item_daily,menu_item_monthly,menu_item_quarter;
    ArrayList<Portfolio> portfolios;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Application.setActiveActivity(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationOnClickListener(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu));
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(this);
        toggle.syncState();

        menu_item_daily = (LinearLayout) findViewById(R.id.menu_item_daily);
        menu_item_daily.setOnClickListener(this);
        menu_item_monthly = (LinearLayout) findViewById(R.id.menu_item_monthly);
        menu_item_monthly.setOnClickListener(this);
        menu_item_quarter = (LinearLayout) findViewById(R.id.menu_item_quarter);
        menu_item_quarter.setOnClickListener(this);

        portfolios = new ArrayList<>();
        getData();
        fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, DailyFragment.getInstance(portfolios));
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Daily");
    }



    @Override
    protected void onResume() {
        super.onResume();
//
////        mDatabase = FirebaseDatabase.getInstance().getReference();
//        //setDataToFireBase();
////        readDataFireBase();
//        Application.setActiveActivity(this);
//        setDailyChart();

    }

    public void getData(){
        String data = Utils.readSampleFile("data_json.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JSONArray portfolioJsonList = null;
        try {
            portfolioJsonList = new JSONArray(data);
            if (portfolioJsonList.length() > 0 ) {
                for (int i = 0; i < portfolioJsonList.length(); i++) {
                    JSONObject portfolioJson = portfolioJsonList.getJSONObject(i);
                    Portfolio p = new Portfolio(portfolioJson.optString(Constant.PORTFOLIO_ID_TAG));
                    JSONArray navsJsonList = portfolioJson.getJSONArray(Constant.NAVS_TAG);
                    if (navsJsonList != null && navsJsonList.length() > 0) {
                        for (int j = 0; j < navsJsonList.length(); ++j) {
                            NavsItem  item  = gson.fromJson(navsJsonList.getJSONObject(j).toString(), NavsItem.class);
                            p.getDailyNavs().set(Utils.getDayInYear(item.getDate())-1,item);
//                            if(j > 0 ){
//                                p.setMonthlyNavs(p.getDailyNavs().get(j-1),p.getDailyNavs().get(j));
//                                p.setQuarterNavs(p.getDailyNavs().get(j-1),p.getDailyNavs().get(j));
//                            }
                        }
                    }
                    portfolios.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


//    private void setMonthlyChart(){
////        if(lineChart.getData() != null)
////            lineChart.getData().clearValues();
////        ArrayList<LineDataSet> dataSets = new ArrayList<>();
////        for(int i=0;i< portfolios.size(); i++){
////            ArrayList<Entry> entries = new ArrayList<>();
////            for(int j=0;j< 12; j++){
////                entries.add(new Entry(portfolios.get(i).getMonthlyNavs().get(j)
////                        .getAmount(),j));
////            }
////            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
////            dataset.setColor(Constant.COLORS[i]);
////            dataSets.add(dataset);
////
////        }
////        ArrayList<String> xAxisLabels = new ArrayList<>();
////        for(String month : Constant.MONTHS){
////            xAxisLabels.add(month);
////        }
////        LineData data = new LineData(xAxisLabels,dataSets);
////        lineChart.setData(data);
////        lineChart.notifyDataSetChanged();
////        lineChart.invalidate();
//    }
//    private void setQuarterChart(){
////        if(lineChart.getData() != null)
////            lineChart.getData().clearValues();
////        ArrayList<LineDataSet> dataSets = new ArrayList<>();
////        for(int i=0;i< portfolios.size(); i++){
////            ArrayList<Entry> entries = new ArrayList<>();
////            for(int j=0;j< 4; j++){
////                entries.add(new Entry(portfolios.get(i).getQuarterlyNavs().get(j)
////                        .getAmount(),j));
////            }
////            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
////            dataset.setColor(Constant.COLORS[i]);
////            dataSets.add(dataset);
////
////        }
////        ArrayList<String> xAxisLabels = new ArrayList<>();
////        for(int i=1; i<5;i++){
////            String quarter = Constant.MONTHS[((Constant.MONTHS.length /4)*i) -1];
////            xAxisLabels.add(quarter);
////        }
////        LineData data = new LineData(xAxisLabels,dataSets);
////        lineChart.setData(data);
////        lineChart.notifyDataSetChanged();
////        lineChart.invalidate();
//    }

//
//    private LineDataSet setLineDataSetStyle(ArrayList<Entry> entries,String title, int position){
//        LineDataSet dataset = new LineDataSet(entries,"");
//        dataset.setColor(Constant.COLORS[position]);
//        dataset.enableDashedLine(10f, 5f, 0f);
//        dataset.enableDashedHighlightLine(10f, 5f, 0f);
//        dataset.setCircleColor(Color.BLACK);
//        dataset.setLineWidth(1f);
//        dataset.setCircleRadius(3f);
//        dataset.setDrawCircleHole(false);
//        dataset.setValueTextSize(9f);
//        dataset.setDrawFilled(true);
//        dataset.setFormLineWidth(1f);
//        dataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//        dataset.setFormSize(15.f);
//        return dataset;
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.main_toolbar:
                if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START))
                    drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_item_daily:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new DailyFragment(), "dailyFragment");
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Daily");
                break;
            case R.id.menu_item_monthly:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, new MonthlyFragment());
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Monthly");
                break;
            case R.id.menu_item_quarter:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,new QuarterFragment());
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Quarter");
                break;

        }



//
//        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }
    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment myFragment =getVisibleFragment(); // (Fragment)getSupportFragmentManager().findFragmentByTag(R.id.main_container);
        if (myFragment != null && myFragment.isVisible()) {
            if(myFragment instanceof DailyFragment){
                ((DailyFragment) myFragment).onOptionsItemSelected(item);
            }
        }

        return true;
    }

//    private DatabaseReference mDatabase;
//    private void  setDataToFireBase(){
//
//        Portfolio p = portfolios.get(0);
//        mDatabase.child(Constant.PORTFOLIO_ID_TAG).setValue(p.getId());
//        DatabaseReference navsDBRef =  mDatabase.child(Constant.NAVS_TAG).push();
//        for(NavsItem n : p.getDailyNavs()){
//            navsDBRef.setValue(n);
//        }
//    }
//
//    private void readDataFireBase(){
//        mDatabase.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                NavsItem post = dataSnapshot.getValue(NavsItem.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
