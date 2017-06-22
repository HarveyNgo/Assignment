package com.ngovihung.assignment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Portfolio {

    private String id;

    ArrayList<NavsItem> navsItems;

    private ArrayList<NavsItem> MonthlyNavs;
    private ArrayList<NavsItem> quarterlyNavs;


    public Portfolio(String id){
        this.id = id;
        navsItems = new ArrayList<>();
        MonthlyNavs = new ArrayList<>(Collections.nCopies(12, new NavsItem()));
        quarterlyNavs = new ArrayList<>(Collections.nCopies(4, new NavsItem()));
    }

    public ArrayList<NavsItem> getNavsItems() {
        return navsItems;
    }

    public void setNavsItems(ArrayList<NavsItem> navsItems) {
        this.navsItems = navsItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<NavsItem> getQuarterlyNavs() {
        return quarterlyNavs;
    }


    public ArrayList<NavsItem> getMonthlyNavs() {
        return MonthlyNavs;
    }

    public void setMonthlyNavs(NavsItem fromNavs, NavsItem toNavs){
        if(toNavs.getDate().after(fromNavs.getDate()) && Utils.compareSameMonth(fromNavs.getDate(),toNavs.getDate())){
            this.MonthlyNavs.set(Utils.getMonthOfYear(toNavs.getDate()), toNavs);
        }
    }

    public void setQuarterNavs(NavsItem fromNavs, NavsItem toNavs){
        if(toNavs.getDate().after(fromNavs.getDate())
                && Utils.compareSameQuarter(fromNavs.getDate(),toNavs.getDate())){
            this.quarterlyNavs.set(Utils.getQuarterIndex(toNavs.getDate()), toNavs);
        }
    }
}
