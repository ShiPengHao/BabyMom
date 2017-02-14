package com.yimeng.babymom.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * 位置服务工具类
 */
public class LocationUtils {

    private static final LocationManager LOCATION_MANAGER = (LocationManager) MyApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);

    private static Criteria sCriteria;

    public static final String LOCATION_ACTION = "LocationReceiver";

    public static PendingIntent sLocationReceiverIntent = PendingIntent.getBroadcast(MyApp.getAppContext(), 100, new Intent(LOCATION_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);


    static {
        sCriteria = new Criteria();
        sCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        sCriteria.setAltitudeRequired(false);
        sCriteria.setBearingRequired(false);
        sCriteria.setCostAllowed(true);
        sCriteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    /**
     * 移除监听
     *
     * @param receiverIntent 广播intent
     */
    public static void removeReceiverIntent(PendingIntent receiverIntent) {
        if (ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LOCATION_MANAGER.removeUpdates(receiverIntent);
    }

    /**
     * 添加监听
     *
     * @param receiverIntent 广播intent
     */
    public static void addReceiverIntent(PendingIntent receiverIntent) {
        if (ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String provider = LOCATION_MANAGER.getBestProvider(sCriteria, true);
        Location location;
        try {
            location = LOCATION_MANAGER.getLastKnownLocation(provider);
            if (location != null)
                receiverIntent.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOCATION_MANAGER.requestLocationUpdates(provider, 5000L, 1000f, receiverIntent);
    }
}
