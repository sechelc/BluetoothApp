package com.example.android.bluetoothmanager.model;

/**
 * Created by SechelC on 1/20/2015.
 */
public class Entry {
    public String truckNo ;
    public String pressure;
    public String speed;
    public String volume;
    public String slump;
    public String viscosity;
    public String yield;
    public String tempReceiver;
    public String tempProbe;
    private long id;

    public Entry() {
        truckNo ="0";
        pressure="0";
        speed="0";
        volume="0";
        slump="0";
        viscosity="0";
        yield="0";
        tempReceiver="0";
        tempProbe="0";
    }

    public Entry(String truckNo, String pressure, String speed, String volume, String slump, String viscosity, String yield, String tempReceiver, String tempProbe) {
        this.truckNo = truckNo;
        this.pressure = pressure;
        this.speed = speed;
        this.volume = volume;
        this.slump = slump;
        this.viscosity = viscosity;
        this.yield = yield;
        this.tempReceiver = tempReceiver;
        this.tempProbe = tempProbe;
    }

    public String getTruckNo() {
        return truckNo;
    }

    public String getPressure() {
        return pressure;
    }

    public String getSpeed() {
        return speed;
    }

    public String getVolume() {
        return volume;
    }

    public String getSlump() {
        return slump;
    }

    public String getViscosity() {
        return viscosity;
    }

    public String getYield() {
        return yield;
    }

    public String getTempReceiver() {
        return tempReceiver;
    }

    public String getTempProbe() {
        return tempProbe;
    }

    public void setTruckNo(String truckNo) {
        this.truckNo = truckNo;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setSlump(String slump) {
        this.slump = slump;
    }

    public void setViscosity(String viscosity) {
        this.viscosity = viscosity;
    }

    public void setYield(String yield) {
        this.yield = yield;
    }

    public void setTempReceiver(String tempReceiver) {
        this.tempReceiver = tempReceiver;
    }

    public void setTempProbe(String tempProbe) {
        this.tempProbe = tempProbe;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
