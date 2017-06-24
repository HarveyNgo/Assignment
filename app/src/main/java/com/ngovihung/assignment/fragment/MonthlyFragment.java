package com.ngovihung.assignment.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngovihung.assignment.R;

/**
 * Created by ngo.vi.hung on 6/24/2017.
 */

public class MonthlyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity_main, container, false);
        return v;
    }
}
