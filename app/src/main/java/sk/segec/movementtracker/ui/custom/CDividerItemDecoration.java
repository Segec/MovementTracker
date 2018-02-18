package sk.segec.movementtracker.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CDividerItemDecoration extends RecyclerView.ItemDecoration
{
    private Drawable mDivider;
    private int mPaddingPx;

    public int getPadding ()
    {
        return mPaddingPx;
    }

    public interface IDiverItem
    {
        boolean canDrawDivider ();
    }

    public CDividerItemDecoration (Drawable divider)
    {
        this(divider, 0);
    }

    public CDividerItemDecoration (Drawable divider, int paddingPx)
    {
        mDivider = divider;
        mPaddingPx = paddingPx;
    }

    @Override
    public void onDrawOver (Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        if (mDivider == null)
        {
            super.onDrawOver(c, parent, state);
            return;
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL)
        {
            final int left = parent.getPaddingLeft() + getPadding();
            final int right = parent.getWidth() - parent.getPaddingRight() - getPadding();
            final int childCount = parent.getChildCount();

            for (int i = 2; i < childCount; i++)
            {
                final View child = parent.getChildAt(i);
                if (canDrawDivider(child))
                {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int size = mDivider.getIntrinsicHeight();
                    final int top = child.getTop() - params.topMargin;
                    final int bottom = top + size;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
        else
        { //horizontal
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();

            for (int i = 1; i < childCount; i++)
            {
                final View child = parent.getChildAt(i);
                if (canDrawDivider(child))
                {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int size = mDivider.getIntrinsicWidth();
                    final int left = child.getLeft() - params.leftMargin;
                    final int right = left + size;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }

    /**
     * Can draw divider.
     *
     * @param child the child
     * @return the boolean
     */
    private boolean canDrawDivider (View child)
    {
        if (child != null)
        {
            Object obj = child.getTag();

            if (obj instanceof IDiverItem)
            {
                return ((IDiverItem) obj).canDrawDivider();
            }
            return true;
        }
        else
        {
            //for null child does not draw anything
            return false;
        }
    }

    /**
     * Gets orientation.
     *
     * @param parent the parent
     * @return the orientation
     */
    private int getOrientation (RecyclerView parent)
    {
        if (parent.getLayoutManager() instanceof LinearLayoutManager)
        {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        }
        else
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
    }
}
