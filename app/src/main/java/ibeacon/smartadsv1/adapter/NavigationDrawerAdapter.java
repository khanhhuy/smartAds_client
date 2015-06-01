package ibeacon.smartadsv1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.activity.NavigationDrawerCallback;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<String> mData;
    private NavigationDrawerCallback mNavigationDrawerCallback;
    private View mSelectedView;
    private int mSelectedPosition;

    public NavigationDrawerAdapter(List<String> data) {
        mData = data;
    }

    public NavigationDrawerCallback getNavigationDrawerCallback() {
        return mNavigationDrawerCallback;
    }

    public void setNavigationDrawerCallback(NavigationDrawerCallback navigationDrawerCallbacks) {
        mNavigationDrawerCallback = navigationDrawerCallbacks;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_row, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mSelectedView != null) {
                   mSelectedView.setSelected(false);
               }
               mSelectedPosition = viewHolder.getAdapterPosition();
               v.setSelected(true);
               mSelectedView = v;
               if (mNavigationDrawerCallback != null)
                   mNavigationDrawerCallback.onNavigationDrawerItemSelected(viewHolder.getAdapterPosition());
           }
                                               }
        );
        viewHolder.itemView.setBackgroundResource(R.drawable.row_selector);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(mData.get(i));
        if (mSelectedPosition == i) {
            if (mSelectedView != null) {
                mSelectedView.setSelected(false);
            }
            mSelectedPosition = i;
            mSelectedView = viewHolder.itemView;
            mSelectedView.setSelected(true);
        }
    }


    public void selectPosition(int position) {
        mSelectedPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
