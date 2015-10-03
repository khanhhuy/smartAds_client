package vn.edu.hcmut.cse.smartads.connector;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public interface AccountStatusResponseListener extends CustomResponseListener{
    void onSuccess(String accountStatus);
}
