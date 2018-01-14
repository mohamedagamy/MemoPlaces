package com.agamy.android.memoplaces.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agamy.android.memoplaces.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyViewFragment extends Fragment {


    public EmptyViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_view, container, false);
    }

}
