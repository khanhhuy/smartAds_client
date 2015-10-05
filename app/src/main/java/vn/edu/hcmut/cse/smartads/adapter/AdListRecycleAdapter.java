package vn.edu.hcmut.cse.smartads.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.model.Ad;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Huy on 6/4/2015.
 */
public class AdListRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private List<Ad> adList;
    private int rowLayout;
    private int rowHeader;
    protected OnItemClickListener mItemClickListener;

    public AdListRecycleAdapter(List<Ad> ads, int rowLayout, int rowHeader) {
        adList = ads;
        this.rowLayout = rowLayout;
        this.rowHeader = rowHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(context).inflate(rowLayout, parent, false);
            final ViewHolder rowViewHolder = new ViewHolder(view);
            rowViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(Config.TAG, "onClick" + rowViewHolder.getAdapterPosition() + rowViewHolder.adTitle.getText());
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, rowViewHolder.getAdapterPosition() - 1); //view header.
                    }
                }
            });

            return rowViewHolder;
        } else if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(context).inflate(rowHeader, parent, false);
            return new HeaderViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types    correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isPositionHeader(position)) {
            ViewHolder viewHolder = (ViewHolder) holder;
            String adTitle = adList.get(position - 1).getTitle(); // we are taking header in to account so all of our items are correctly positioned
            viewHolder.setAdTitle(adTitle);
            String adDesc = adList.get(position - 1).getDescription();
            viewHolder.setAdDesc(adDesc);
            if (adList.get(position-1).getIcon() != null)
                viewHolder.setAdThumbnail(adList.get(position-1).getIcon());
        }
    }

    public int getBasicItemCount() {
        return adList == null ? 0 : adList.size();
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1; //header view
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnitemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView adTitle;
        public final TextView adDesc;
        public final ImageView adThumbnail;

        public ViewHolder(final View parent, TextView adTitle, TextView adDesc, ImageView adThumbnail) {
            super(parent);
            this.adTitle = adTitle;
            this.adDesc = adDesc;
            this.adThumbnail = adThumbnail;
        }

        public ViewHolder(View parent) {
            super(parent);
            adTitle = (TextView) parent.findViewById(R.id.adTitle);
            adDesc = (TextView) parent.findViewById(R.id.adDesc);
            adThumbnail = (ImageView) parent.findViewById(R.id.adThumbnailImg);
        }


        public void setAdTitle(CharSequence text) {
            adTitle.setText(text);
        }

        public void setAdDesc(CharSequence text) {
            adDesc.setText(text);
        }

        public void setAdThumbnail(Bitmap bitmap) {
            adThumbnail.setImageBitmap(bitmap);
        }


    }
}
