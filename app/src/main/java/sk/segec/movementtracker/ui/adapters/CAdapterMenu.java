package sk.segec.movementtracker.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.segec.movementtracker.R;
import sk.segec.movementtracker.ui.enums.ENavigationItem;
import sk.segec.movementtracker.ui.listeners.IOnMenuItemClickListener;

/**
 * Created by Michal on 17. 2. 2018.
 */
public class CAdapterMenu extends RecyclerView.Adapter<CAdapterMenu.CViewHolder>
{
    private static final int ITEM_LOGO = 0;
    private static final int ITEM_MENU_ITEM = 1;

    private static final int LOGO_COUNT = 1;

    private Context mContext;
    private IOnMenuItemClickListener mListener;

    public CAdapterMenu (Context context, IOnMenuItemClickListener listener)
    {
        mContext = context;
        mListener = listener;
    }

    @Override
    public CViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
    {
        if (viewType == ITEM_LOGO)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logo, parent, false);
            return new CViewHolderLogo(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
            return new CViewHolderItem(view, mContext, mListener);
        }
    }

    @Override
    public void onBindViewHolder (CViewHolder holder, int position)
    {
        if (holder.getItemViewType() == ITEM_MENU_ITEM)
        {
            CViewHolderItem viewHolder = (CViewHolderItem) holder;
            viewHolder.bindData(ENavigationItem.values()[position - LOGO_COUNT]);
        }
    }

    @Override
    public int getItemCount ()
    {
        return ENavigationItem.values().length + LOGO_COUNT;
    }

    public static class CViewHolder extends RecyclerView.ViewHolder
    {
        public CViewHolder (View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class CViewHolderLogo extends CViewHolder
    {
        public CViewHolderLogo (View itemView)
        {
            super(itemView);
        }
    }

    public static class CViewHolderItem extends CViewHolder
    {
        @BindView (R.id.item_menu_image_icon)
        ImageView mImageIcon;
        @BindView (R.id.item_menu_text_title)
        TextView mTextTitle;

        private Context mContext;
        private IOnMenuItemClickListener mListener;
        private ENavigationItem mNavigationItem;

        public CViewHolderItem (View itemView, Context context, IOnMenuItemClickListener listener)
        {
            super(itemView);
            mContext = context;
            mListener = listener;

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View view)
                {
                    mListener.onItemClick(mNavigationItem);
                }
            });
        }

        public void bindData (ENavigationItem navigationItem)
        {
            mNavigationItem = navigationItem;
            mImageIcon.setImageResource(navigationItem.getNavItemImageResId());
            mTextTitle.setText(mContext.getString(navigationItem.getNavItemNameResId()));
        }
    }

    @Override
    public int getItemViewType (int position)
    {
        if (position == 0)
        {
            return ITEM_LOGO;
        }
        else
        {
            return ITEM_MENU_ITEM;
        }
    }
}
