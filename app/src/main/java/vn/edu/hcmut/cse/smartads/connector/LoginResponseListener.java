package vn.edu.hcmut.cse.smartads.connector;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public interface LoginResponseListener extends CustomResponseListener {
    void onSuccess(String customerID,String accessToken);
}
