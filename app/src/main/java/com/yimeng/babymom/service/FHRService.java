package com.yimeng.babymom.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jumper.data.FHRInfo;
import com.jumper.fetalheart.ADFetalHeart;
import com.jumper.fetalheart.ConnectCallback;
import com.jumper.fetalheart.Mode;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 从设备获取胎心数据的服务
 */
public class FHRService extends Service {
    /**
     * binder对象
     */
    private FHRBinder mFHRBinder;
    /**
     * 服务运行标志，给工作线程判断状态使用
     */
    private boolean isRunning;

    /**
     * 让客户端实现的数据更新接口，通过{@link FHRReceiver#onReceived(FHRInfo)}方法实现
     */
    public interface FHRReceiver {
        /**
         * 通知客户端数据更新的回调，非UI线程
         *
         * @param fhrInfo 数据
         */
        void onReceived(FHRInfo fhrInfo);
    }

    /**
     * 从设备获取胎心数据的binder类
     */
    public class FHRBinder extends Binder {
        private final String TAG = "FHRBinder";
        /**
         * 从设备获取数据的第三方核心类
         */
        private final ADFetalHeart mADFetalHeart;
        /**
         * 不断从设备获取数据的定时器
         */
        private final Timer mTimer;
        /**
         * client端通过{@link #setHeartDataIReceiver(FHRReceiver)}设置的客户端监听的引用<br/>
         * 使用弱引用避免客户端内存泄漏
         */
        private WeakReference<FHRReceiver> mHeartDataIReceiver;

        private FHRBinder() {
            mADFetalHeart = ADFetalHeart.getInstance(FHRService.this);
            mADFetalHeart.setMode(Mode.LINE);
            // 设置一个空实现，不然会闪退，原因不明
            mADFetalHeart.setConnectCallback(new ConnectCallback() {
                @Override
                public void onConnectSuccess(BluetoothDevice bluetoothDevice) {
                }

                @Override
                public void onConnectFail(boolean setting) {
                }
            });
            // 准备
            mADFetalHeart.prepare();
            // 开始工作
            mADFetalHeart.startWork();
            // 开启定时器
            mTimer = new Timer(TAG);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // 此逻辑在工作线程中，所以执行前检查服务状态和客户端引用状态
                    if (isRunning && null != mHeartDataIReceiver && null != mHeartDataIReceiver.get()) {
                        mHeartDataIReceiver.get().onReceived(mADFetalHeart.getFHRInfo());
                    }
                }
            }, 0, 1000);
        }

        public void setHeartDataIReceiver(FHRReceiver FHRReceiver) {
            mHeartDataIReceiver = new WeakReference<>(FHRReceiver);
        }

        /**
         * 停止工作，释放资源
         */
        private void stop() {
            mTimer.cancel();
            mADFetalHeart.stopWork();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isRunning = true;
        return mFHRBinder;
    }

    @Override
    public void onCreate() {
        mFHRBinder = new FHRBinder();
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        if (null != mFHRBinder) {
            mFHRBinder.stop();
        }
    }
}
