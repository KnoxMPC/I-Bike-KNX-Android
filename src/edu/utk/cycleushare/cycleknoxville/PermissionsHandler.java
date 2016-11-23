package edu.utk.cycleushare.cycleknoxville;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by phil on 10/3/16.
 */

class PermissionsHandler implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final Activity mActivity;
    private final String[] mPermissions;
    private List<String> toRequest;
    private int mCode;
    private final String mTAG = "PermissionsHandler";

    public PermissionsHandler(Activity activity, String[] permissions, int requestCode) {
        mActivity = activity;
        mPermissions = permissions;
        mCode = requestCode;
        toRequest = new ArrayList<String>();
    }

    public void requestPermissions() {
        for (String perm : mPermissions) {
            if (ActivityCompat.checkSelfPermission(mActivity, perm) != PackageManager.PERMISSION_GRANTED) {
                Log.d(mTAG, "Requesting permission ".concat(perm));
                toRequest.add(perm);
            }
        }

        if(!toRequest.isEmpty()) {

            String perms[] = toRequest.toArray(new String[toRequest.size()]);

            Log.d(mTAG, "Requesting permissions: ".concat(Arrays.toString(perms)));

            ActivityCompat.requestPermissions(mActivity, perms, mCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(mTAG, "onRequestPermissionsResult() callback called");

        boolean oneGranted = false;

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                Log.d(mTAG, "permission denied for ".concat(permissions[i]));
                Log.d(mTAG, "re-requesting...");

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[i])) {
                    Toast.makeText(mActivity, "This application requires ".concat(permissions[i]), Toast.LENGTH_SHORT).show();
                }

                ActivityCompat.requestPermissions(mActivity, new String[]{permissions[i]}, ++mCode);
            } else {
                Log.d(mTAG, "permission granted for ".concat(permissions[i]));
                oneGranted = true;
                //Toast.makeText(mActivity, "Please restart the application", Toast.LENGTH_SHORT).show();
            }
        }

        if(oneGranted){
            Toast.makeText(mActivity, "Please restart the application", Toast.LENGTH_SHORT).show();
        }
    }
}
