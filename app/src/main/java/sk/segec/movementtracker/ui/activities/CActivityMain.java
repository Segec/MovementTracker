package sk.segec.movementtracker.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.segec.movementtracker.R;
import sk.segec.movementtracker.ui.adapters.CAdapterMenu;
import sk.segec.movementtracker.ui.custom.CDividerItemDecoration;
import sk.segec.movementtracker.ui.enums.ENavigationItem;
import sk.segec.movementtracker.ui.listeners.IOnMenuItemClickListener;

public class CActivityMain extends AActivityDefault
{
    public static final String TAG = CActivityMain.class.getSimpleName();

    @BindView (R.id.toolbar)
    Toolbar mToolbar;

    @BindView (R.id.activity_main_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView (R.id.activity_main_recycler_view)
    RecyclerView mRecyclerView;

    private ActionBarDrawerToggle mDrawerToggle;
    private CAdapterMenu mAdapter;

    public static void startActivity (Activity activity)
    {
        Intent intent = new Intent(activity, CActivityMain.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CAdapterMenu(this, new IOnMenuItemClickListener()
        {
            @Override
            public void onItemClick (ENavigationItem item)
            {
                if (item != null)
                {
                    switchContent(item);
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new CDividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.divider_decorator)));

        switchContent(ENavigationItem.MAP);
    }

    @Override
    public int getLayoutId ()
    {
        return R.layout.activity_main;
    }

    public void switchContent (ENavigationItem item)
    {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.activity_main_layout_change, ENavigationItem.changeFragmentByTag(item.getNavItemTag()), item.getNavItemTag());
        transaction.commit();
    }
}
