package vn.edu.hcmut.cse.smartads.model.image;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Huy on 10/14/2015.
 */
public interface ImageClearCache extends ImageLoader.ImageCache {
    abstract void clearCache();
}