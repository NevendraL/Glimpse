package com.examp.glimpse.Controller;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class BackgroundLocationService extends Service {


    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;


    //Code that execute when service is started...
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getGpsLocation();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //retrieves Latitude and longitude
    public void getGpsLocation() {

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            double latitude = mLastLocation.getLatitude();
                            double longitude = mLastLocation.getLongitude();
                            getCompleteAddressString(latitude, longitude);


                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());

                        }

                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);


    }


    //converts gps coordinates into actual address
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

                Log.i("address", strReturnedAddress.toString());
            } else {
                Log.i("no address found", "No address found");


            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }

}


