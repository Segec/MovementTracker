package sk.segec.movementtracker.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.segec.movementtracker.IActivityCallback;
import sk.segec.movementtracker.R;

/**
 * Created by Michal on 18. 2. 2018.
 */
public abstract class AActivityDefault extends AppCompatActivity implements IActivityCallback
{
    @BindView (R.id.toolbar)
    Toolbar mToolbar;
    private int layoutId;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }

    @Override
    public void setToolbarTitle (String title)
    {
        if (mToolbar != null)
        {
            mToolbar.setTitle(title);
        }
    }

    abstract public int getLayoutId ();
}
