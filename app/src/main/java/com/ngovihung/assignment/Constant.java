package com.ngovihung.assignment;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Constant {
    public static final String PORTFOLIO_ID_TAG = "portfolioId";
    public static final String NAVS_TAG = "navs";
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";

    public static final int[] COLORS = Application.getContext().getResources().getIntArray(R.array.floor_colors);
    public static final String[] MONTHS = Application.getContext().getResources().getStringArray(R.array.months);
    public static final int[] FADE_COLORS = {R.drawable.fade_red,R.drawable.fade_purple,R.drawable.fade_green};


    public static final int DAILY = 1;
    public static final int MONTHLY = 2;
    public static final int QUARTER = 3;


}
