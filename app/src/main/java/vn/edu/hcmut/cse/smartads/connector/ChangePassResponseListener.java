package vn.edu.hcmut.cse.smartads.connector;

/**
 * Created by Minh Dao Bui on 11/5/2015.
 */
public interface ChangePassResponseListener extends SimpleResponseListener {
    void onWrongCurrentPasswordError();
}
