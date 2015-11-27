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
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Huy on 6/4/2015.
 */
public class AdsListRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<Ads> mAdsData;
    protected OnAdsClickListener mAdsClickListener;
    private Context mContext;
    private int mAdsResourceID;

    public AdsListRecycleAdapter(int adsResourceID, Context context, List<Ads> adsData) {
        mAdsResourceID = adsResourceID;
        mContext = context;
        setAdsData(adsData);
    }

    public void setAdsData(List<Ads> adsData) {
        if (adsData != null)
            mAdsData = new ArrayList<>(adsData);
        else
            mAdsData = new ArrayList<>();
    }

    public List<Ads> getmAdsData() {
        return mAdsData;
    }

    public interface OnAdsClickListener {
        public void onAdsClick(View view, int position);
    }

    public void setOnItemClickListener(final OnAdsClickListener mItemClickListener) {
        this.mAdsClickListener = mItemClickListener;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(mContext).inflate(mAdsResourceID, parent, false);
        final ViewHolder rowViewHolder = new ViewHolder(view);

        return rowViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mAdsData.isEmpty() || position > mAdsData.size())
            return;

        final int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdsClickListener != null) {
                    mAdsClickListener.onAdsClick(v, itemPosition);
                }
            }
        });

        ViewHolder viewHolder = (ViewHolder) holder;
        String adsTitle = mAdsData.get(position).getTitle();
        viewHolder.setAdsTitle(adsTitle);

        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATE_DISPLAY_PATTERN);
        DateTime startDate = mAdsData.get(position).getStartDate();
        DateTime endDate = mAdsData.get(position).getEndDate();
        String adsDate;
        if (!(startDate == null) && !(endDate == null))
            adsDate = mContext.getResources().getString(R.string.from_date, formatter.print(startDate)) + ' ' +
                    mContext.getResources().getString(R.string.to_date, formatter.print(endDate));
        else if (!(startDate == null))
            adsDate = mContext.getResources().getString(R.string.from_date, formatter.print(startDate));
        else if (!(endDate == null))
            adsDate = mContext.getResources().getString(R.string.default_no_start_date, formatter.print(endDate));
        else
            adsDate = mContext.getResources().getString(R.string.default_no_date);

        viewHolder.setAdsDate(adsDate);
        viewHolder.setAdsThumbnail(String.format(Connector.ADS_BASE_THUMBNAIL, String.valueOf(mAdsData.get(position).getAdsId())));

        if (mAdsData.get(position).is_viewed()) {
            ((ViewHolder) holder).adsTitle.setTextAppearance(mContext, R.style.normalText);
        }
    }

    @Override
    public int getItemCount() {
        return mAdsData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    //View holder for each Ads

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView adsTitle;
        public final NetworkImageView adsThumbnail;
        public final TextView adsDate;

        public ViewHolder(final View parent, TextView adTitle, TextView adsDate, NetworkImageView adThumbnail) {
            super(parent);
            this.adsTitle = adTitle;
            this.adsThumbnail = adThumbnail;
            this.adsDate = adsDate;
        }

        public ViewHolder(View parent) {
            super(parent);
            adsTitle = (TextView) parent.findViewById(R.id.adsTitle);
            adsThumbnail = (NetworkImageView) parent.findViewById(R.id.adsThumbnailImg);
            adsDate = (TextView) parent.findViewById(R.id.adsDate);
        }


        public void setAdsTitle(CharSequence text) {
            adsTitle.setText(text);
        }

        public void setAdsDate(CharSequence text) {
            adsDate.setText(text);
        }

        public void setAdsThumbnail(String url) {
            adsThumbnail.setImageUrl(url, Connector.getInstance(mContext).getImageLoader());
        }


    }
}
