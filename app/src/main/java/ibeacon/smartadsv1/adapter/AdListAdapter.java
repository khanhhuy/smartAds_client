package ibeacon.smartadsv1.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.model.Ad;

/**
 * Created by Huy on 6/3/2015.
 */
public class AdListAdapter extends ArrayAdapter<Ad> {

    private final List<Ad> data;
    private Context context;

    public AdListAdapter(Context context, int resourceLayout,List<Ad> data) {
        super(context, resourceLayout, data );
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.card_ads_item, null);
        }

        TextView adTitle = (TextView) convertView.findViewById(R.id.adTitle);
        adTitle.setText(data.get(position).getTitle());

        TextView adDesc = (TextView) convertView.findViewById(R.id.adDesc);
        adDesc.setText(data.get(position).getDescription());

        Log.d("View", String.format("Ads Desc: %s", data.get(position).getTitle()));

        //ImageView imageView = (ImageView) convertView.findViewById(R.id.adsThumbnailImg);

        return convertView;
    }

}
