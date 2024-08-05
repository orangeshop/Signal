/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.ongo.signal.ui.video.webrtc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.webrtc.ThreadUtils;

@SuppressLint("MissingPermission")
public class ProximitySensor implements SensorEventListener {
    private static final String TAG = ProximitySensor.class.getSimpleName();

    private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();

    private final Runnable onSensorStateListener;
    private final SensorManager sensorManager;
    private Sensor proximitySensor = null;
    private boolean lastStateReportIsNear = false;

    private ProximitySensor(Context context, Runnable sensorStateListener) {
        onSensorStateListener = sensorStateListener;
        sensorManager = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
    }

    static ProximitySensor create(Context context, Runnable sensorStateListener) {
        return new ProximitySensor(context, sensorStateListener);
    }

    public boolean start() {
        threadChecker.checkIsOnValidThread();
        if (!initDefaultSensor()) {
            return false;
        }
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        return true;
    }

    public void stop() {
        threadChecker.checkIsOnValidThread();
        if (proximitySensor == null) {
            return;
        }
        sensorManager.unregisterListener(this, proximitySensor);
    }

    public boolean sensorReportsNearState() {
        threadChecker.checkIsOnValidThread();
        return lastStateReportIsNear;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        threadChecker.checkIsOnValidThread();

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        threadChecker.checkIsOnValidThread();

        float distanceInCentimeters = event.values[0];
        lastStateReportIsNear = distanceInCentimeters < proximitySensor.getMaximumRange();

        if (onSensorStateListener != null) {
            onSensorStateListener.run();
        }

    }

    private boolean initDefaultSensor() {
        if (proximitySensor != null) {
            return true;
        }
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            return false;
        }
        logProximitySensorInfo();
        return true;
    }

    private void logProximitySensorInfo() {
        if (proximitySensor == null) {
            return;
        }
        StringBuilder info = new StringBuilder("Proximity sensor: ");
        info.append("name=").append(proximitySensor.getName());
        info.append(", vendor: ").append(proximitySensor.getVendor());
        info.append(", power: ").append(proximitySensor.getPower());
        info.append(", resolution: ").append(proximitySensor.getResolution());
        info.append(", max range: ").append(proximitySensor.getMaximumRange());
        info.append(", min delay: ").append(proximitySensor.getMinDelay());
        // Added in API level 20.
        info.append(", type: ").append(proximitySensor.getStringType());
        // Added in API level 21.
        info.append(", max delay: ").append(proximitySensor.getMaxDelay());
        info.append(", reporting mode: ").append(proximitySensor.getReportingMode());
        info.append(", isWakeUpSensor: ").append(proximitySensor.isWakeUpSensor());
    }
}
