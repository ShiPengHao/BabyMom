package com.yimeng.babymom.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * 位置服务工具类
 */
public class LocationUtils {
    /**
     * 设置位置变化服务的监听
     *
     * @param updateLocationListener 监听器，实现UpdateLocationListener接口的updateWithNewLocation(Location location)方法
     *                               可传入null，则位置变化时回调此工具类的updateWithNewLocation(Location location)静态方法
     */
    public static void setUpdateLocationListener(final UpdateLocationListener updateLocationListener) {
        LocationManager locationManager = (LocationManager) MyApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location;
        try {
            location = locationManager.getLastKnownLocation(provider);
            if (updateLocationListener != null && location != null)
                updateLocationListener.updateWithNewLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 5000, 100, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (updateLocationListener != null)
                    updateLocationListener.updateWithNewLocation(location);
                else LocationUtils.updateWithNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

    /**
     * 位置服务监听
     */
    public interface UpdateLocationListener {
        /**
         * 获得新的定位的监听，当前方法在主线程
         *
         * @param location 新的位置
         */
        void updateWithNewLocation(Location location);
    }

    /**
     * 解析位置，获得经纬度和行政位置，弹吐司展示
     *
     * @param location 位置
     */
    private static void updateWithNewLocation(Location location) {
        new AsyncTask<Location, Void, String>() {
            @Override
            protected String doInBackground(Location... params) {
                if (null == params || null == params[0]) {
                    return "无法获取地理信息";
                }
                Location location = params[0];
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String locationStr = "维度：" + lat + "\n经度：" + lng;
                List<Address> addList;
                Geocoder ge = new Geocoder(MyApp.getAppContext());
                try {
                    addList = ge.getFromLocation(lat, lng, 1);
                    if (addList != null && addList.size() > 0) {
                        Address ad = addList.get(0);
                        locationStr += "\n地址：" + ad.getAddressLine(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return locationStr;
            }

            @Override
            protected void onPostExecute(String s) {
                MyToast.show(s);
            }
        }.execute(location);
    }
}
