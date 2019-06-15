package com.example.shepherd;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shepherd.Data.GuidesData;


public class GuideDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "Guide_id";
    private GuidesData.GuideItem mItem;
    public GuideDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        try {
            if (getArguments().containsKey( ARG_ITEM_ID )) {
                mItem = GuidesData.GUIDE_MAP.get( getArguments().getString( ARG_ITEM_ID ) );
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById( R.id.toolbar_layout );
                if (appBarLayout != null) {
                    appBarLayout.setTitle( mItem.content );
                }
            }
        }
        catch (Exception e){ }

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.guide_detail, container, false );

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById( R.id.guide_detail )).setText( mItem.details );
        }

        return rootView;
    }
}
