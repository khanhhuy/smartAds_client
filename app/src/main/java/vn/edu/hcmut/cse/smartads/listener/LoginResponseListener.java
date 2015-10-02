package vn.edu.hcmut.cse.smartads.listener;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public interface LoginResponseListener {
    void onSuccess(String customerID,String accessToken);
    void onError(String message);
}
