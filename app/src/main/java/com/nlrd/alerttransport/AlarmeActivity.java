package com.nlrd.alerttransport;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmeActivity extends Fragment implements  View.OnClickListener
{
    protected Activity mActivity;
    private ImageView mCamera;
    private TextView intro;

    @Override
    public void onAttach( Activity act){
        super.onAttach(act);
        mActivity = act;
    }

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_alarme, container, false);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
