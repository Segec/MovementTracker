package sk.segec.movementtracker.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.segec.movementtracker.R;

/**
 * Created by Michal on 18. 2. 2018.
 */
public class CFragmentStatistics extends AFragmentDefault
{
    public static final String TAG = CFragmentStatistics.class.getSimpleName();

    @Override
    int getFragmentLayout ()
    {
        return R.layout.fragment_statistics;
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mCallBackActivity.setToolbarTitle(getString(R.string.toolbar_title_statistics));

        return view;
    }
}
