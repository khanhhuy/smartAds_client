package ibeacon.smartadsv1.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.model.Ad;

/**
 * Created by Huy on 6/4/2015.
 */
public class AdListRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
    private List<Ad> adList;
    private int rowLayout;
    private int rowHeader;

    public AdListRecycleAdapter(List<Ad> ads, int rowLayout, int rowHeader) {
        adList = ads;
        this.rowLayout = rowLayout;
        this.rowHeader = rowHeader;
    }

    public void clearListAds() {
        int size = adList.size();
        adList.clear();
        this.notifyItemRangeRemoved(0, size);
    }

    public void adAds(List<Ad> newAds) {
        adList.addAll(newAds);
        this.notifyItemRangeInserted(0, adList.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(context).inflate(rowLayout, parent, false);
            return ViewHolder.newInstance(view);
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
        }
    }

    public int getBasicItemCount() {
        return adList == null ? 0 : adList.size();
    }

    //our new getItemCount() that includes header View
    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1;
    }

    //added a method that returns viewType for a given position
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

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView adTitle;
        private final TextView adDesc;
        private final ImageView adThumbnail;

        public ViewHolder(final View parent, TextView adTitle, TextView adDesc, ImageView adThumbnail) {
            super(parent);
            this.adTitle = adTitle;
            this.adDesc = adDesc;
            this.adThumbnail = adThumbnail;
        }

        public static ViewHolder newInstance(View parent) {
            TextView adTitle = (TextView) parent.findViewById(R.id.adTitle);
            TextView adDesc = (TextView) parent.findViewById(R.id.adDesc);
            ImageView adThumbnail = (ImageView) parent.findViewById(R.id.adThumbnailImg);

            return new ViewHolder(parent, adTitle, adDesc, adThumbnail);
        }

        public void setAdTitle(CharSequence text) {
            adTitle.setText(text);
        }

        public void setAdDesc(CharSequence text) {
            adDesc.setText(text);
        }

        public void setAdThumbnail(int imgResource) {
            adThumbnail.setImageResource(imgResource);
        }


    }
}
