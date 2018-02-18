package sk.segec.movementtracker.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import sk.segec.movementtracker.IActivityCallback;

/**
 * Created by Michal on 17. 2. 2018.
 */
public abstract class AFragmentDefault extends Fragment
{
    public IActivityCallback mCallBackActivity;

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    abstract int getFragmentLayout ();

    private void onAttachToContext (Context context)
    {
        if (!(context instanceof IActivityCallback))
        {
            throw new IllegalStateException("Context must implement the IActivityCallback interface.");
        }

        mCallBackActivity = (IActivityCallback) context;
    }

    /*
     * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
     * Use onAttachToContext instead
     */
    @TargetApi (23)
    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings ("deprecation")
    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            onAttachToContext(activity);
        }
    }

    @Override
    public void onDetach ()
    {
        super.onDetach();
        mCallBackActivity = null;
    }
}
