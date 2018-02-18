package sk.segec.movementtracker.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import sk.segec.movementtracker.R;

/**
 * Created by Michal on 18. 2. 2018.
 */
public class CActivitySplash extends AppCompatActivity
{
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRunnable = new Runnable()
        {
            @Override
            public void run ()
            {
                CActivityMain.startActivity(CActivitySplash.this);
                finish();
            }
        };

        mHandler.postDelayed(mRunnable, 2000);
    }


    @Override
    protected void onStop ()
    {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }
}
