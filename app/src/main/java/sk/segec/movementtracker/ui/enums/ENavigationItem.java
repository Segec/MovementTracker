package sk.segec.movementtracker.ui.enums;

import android.support.v4.app.Fragment;

import sk.segec.movementtracker.R;
import sk.segec.movementtracker.ui.fragments.CFragmentMap;
import sk.segec.movementtracker.ui.fragments.CFragmentSettings;
import sk.segec.movementtracker.ui.fragments.CFragmentStatistics;

/**
 * Created by Michal on 17. 2. 2018.
 */
public enum ENavigationItem
{
    MAP(CFragmentMap.TAG, R.drawable.icon_map, R.string.toolbar_title_map),
    STATISTICS(CFragmentStatistics.TAG, R.drawable.icon_statistics, R.string.toolbar_title_statistics),
    SETTINGS(CFragmentSettings.TAG, R.drawable.icon_settings, R.string.toolbar_title_settings);

    private String navItemTag;
    private int navItemImageResId;
    private int navItemNameResId;

    ENavigationItem (String navItemTag, int navItemImageResId, int navItemNameResId)
    {
        this.navItemTag = navItemTag;
        this.navItemImageResId = navItemImageResId;
        this.navItemNameResId = navItemNameResId;
    }

    public static Fragment changeFragmentByTag (String tag)
    {
        if (tag != null)
        {
            if (tag.equals(CFragmentMap.TAG))
            {
                return new CFragmentMap();
            }
            else if (tag.equals(CFragmentSettings.TAG))
            {
                return new CFragmentSettings();
            }
            else if (tag.equals(CFragmentStatistics.TAG))
            {
                return new CFragmentStatistics();
            }
        }
        return null;
    }

    public String getNavItemTag ()
    {
        return navItemTag;
    }

    public void setNavItemTag (String navItemTag)
    {
        this.navItemTag = navItemTag;
    }

    public int getNavItemImageResId ()
    {
        return navItemImageResId;
    }

    public void setNavItemImageResId (int navItemImageResId)
    {
        this.navItemImageResId = navItemImageResId;
    }

    public int getNavItemNameResId ()
    {
        return navItemNameResId;
    }

    public void setNavItemNameResId (int navItemNameResId)
    {
        this.navItemNameResId = navItemNameResId;
    }
}
