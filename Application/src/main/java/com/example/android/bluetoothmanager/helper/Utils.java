package com.example.android.bluetoothmanager.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.example.android.bluetoothmanager.model.Entry;
import com.example.android.bluetoothmanager.model.LogEntryProtocol;

/**
 * Created by sechelc on 01.02.2015.
 */
public class Utils {
    public static void displayPromptForEnablingGPS(
            final Activity activity) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable GPS"
                + " service. Click OK to go to"
                + " location services.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static LogEntryProtocol.LogEntry build(Entry entry) {
        return LogEntryProtocol.LogEntry.newBuilder().
                setAddedWater(entry.getAddedWater()).
                setAngle(entry.getAngle()).
                setBatteryVoltage(entry.getBatteryVoltage()).
                setCalcVolume(entry.getCalcVolume()).
                setChargerVoltage(entry.getChargerVoltage()).
                setCompany(entry.getCompany()).
                setDrumState(entry.getDrumState()).
                setLatitude(entry.getLatitude()).
                setLogQty(entry.getLogQty()).
                setLongitude(entry.getLongitude()).
                setMeasurementIndex(entry.getMeasurementIndex()).
                setMeasVolume(entry.getMeasVolume()).
                setPairLinkQuality(entry.getPairLinkQuality()).
                setPressure(entry.getPressure()).
                setRatio(entry.getRatio()).
                setSlump(entry.getSlump()).
                setSpeed(entry.getSpeed()).
                setStatus(entry.getStatus()).
                setSupplyVoltage(entry.getSupplyVoltage()).
                setTempAir(entry.getTempAir()).
                setTempProbe(entry.getTempProbe()).
                setTempReceiver(entry.getTempReceiver()).
                setTimestamp(Long.valueOf(entry.getTimestamp())).
                setTruckActivity(entry.getTruckActivity()).
                setTruckNo(entry.getTruckNo()).
                setTurnCountNeg(entry.getTurnCountNeg()).
                setTurnCountPos(entry.getTurnCountPos()).
                setTurnNumber(entry.getTurnNumber()).
                setViscosity(entry.getViscosity()).
                setVolume(entry.getVolume()).
                setWaterTemperature(entry.getWaterTemperature()).
                setYield(entry.getYield()).
                setZAxis(entry.getzAxis()).
                build();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
