package ibeacon.smartadsv1.activity;

import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Huy on 6/4/2015.
 */
public class BaseFragment extends Fragment {

    protected Toolbar mToolbar;

    public BaseFragment() {

    }

    public void setTitle(String toolbarTitle) {
        getActivity().setTitle(toolbarTitle);
    }

    protected void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    protected void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

}
