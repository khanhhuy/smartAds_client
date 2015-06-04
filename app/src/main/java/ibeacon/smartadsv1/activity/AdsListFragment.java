package ibeacon.smartadsv1.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.adapter.AdListAdapter;
import ibeacon.smartadsv1.model.Ad;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the @link AdsListFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class AdsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AdListAdapter adapter;

    private List<Ad> mlistAd;

    public AdsListFragment() {
        // Required empty public constructor
        mlistAd = new ArrayList<>();
        createDummyAdsList();
    }

    private void createDummyAdsList() {
        mlistAd.add(new Ad(1, "Pepsi discount", "15%", new Date(), new Date()));
        mlistAd.add(new Ad(2, "Unilever discount", "All products for 30%", new Date(), new Date()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ads_list, container, false);
        adapter = new AdListAdapter(getActivity(), R.layout.card_ads_item, mlistAd);

        ListView listView = (ListView) view.findViewById(R.id.listAds);
        listView.setAdapter(adapter);

        return  view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
