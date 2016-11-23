package edu.utk.cycleushare.cycleknoxville;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by phil on 10/3/16.
 */

public class LocationApiHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final LocationListener mListener;
    private final LocationRequest mRequest;
    private final GoogleApiClient mClient;
    private final Context mContext;

    private String mTAG = "LocationApiHandler";

    public LocationApiHandler(Context context, LocationListener listener, LocationRequest request){
        this("", context, listener, request);
    }

    public LocationApiHandler(String TAGprefix, Context context, LocationListener listener, LocationRequest request){
        mContext = context;
        mListener = listener;
        mRequest = request;

        if(TAGprefix != null && !TAGprefix.isEmpty()){
            mTAG = TAGprefix.concat(mTAG);
        }

        Log.d(mTAG, "initialized");

        mClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Log.d(mTAG, "API client ready");

    }


    public void startLocationUpdates(){
        Log.d(mTAG, "startLocationUpdates()");
        //mActive = true;
        if(!mClient.isConnected()){
            mClient.connect();
            Log.d(mTAG, "attempting API connection");
        } else {
            mClient.reconnect();
        }
    }

    public void stopLocationUpdates(){
        Log.d(mTAG, "stopLocationUpdates()");
        if(mClient.isConnected()){
            Log.d(mTAG, "removing location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(mClient, mListener);
        }
    }

    public void disconnect(){

        Log.d(mTAG, "disconnect()");

        if(mClient.isConnected()){
            stopLocationUpdates();
            mClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(mTAG, "onConnected()");

        int finePermission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(coarsePermission != PackageManager.PERMISSION_GRANTED){
            Log.d(mTAG, "missing permission ACCESS_COARSE_LOCATION");
            return;
        } else if(finePermission != PackageManager.PERMISSION_GRANTED){
            Log.d(mTAG, "missing permission ACCESS_FINE_LOCATION, accuracy is limited");
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mRequest, mListener);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(mContext, "Connection to Google Play Services failed", Toast.LENGTH_SHORT).show();
    }
}