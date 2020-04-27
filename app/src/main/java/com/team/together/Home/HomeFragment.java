package com.team.together.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.team.together.R;
import com.team.together.vidyo.main_vid;
import com.team.together.Home.HomeActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button upButton;
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = view.getContext();

        upButton = (Button) view.findViewById(R.id.button2);
        upButton.setOnClickListener(this);

        return view;
    }

    @Override
    public
    void onClick ( View v ){
        switch (v.getId()) {
            case R.id.button2:
                Intent intent = new Intent( context , main_vid.class);
                startActivity(intent);
                break;
        }

    }
}
