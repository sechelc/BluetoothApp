package com.example.android.bluetoothmanager.model;

/**
 * Created by SechelC on 1/20/2015.
 */
public class Entry {
    public String truckNo;
    public String pressure;
    public String speed;
    public String volume;
    public String slump;
    public String viscosity;
    public String yield;
    public String tempReceiver;
    public String tempProbe;
    private long id;
    private String timestamp;
    private String status;
    private String measVolume;
    private String calcVolume;
    private String angle;
    private String ratio;
    private String turnNumber;
    private String pairLinkQuality;
    private String turnCountPos;
    private String turnCountNeg;
    private String tempAir;
    private String waterTemperature;
    private String batteryVoltage;
    private String supplyVoltage;
    private String chargerVoltage;
    private String zAxis;
    private String drumState;
    private String truckActivity;
    private String measurementIndex;
    private String logQty;
    private String addedWater;


    public Entry() {
        truckNo = "0";
        pressure = "0";
        speed = "0";
        volume = "0";
        slump = "0";
        viscosity = "0";
        yield = "0";
        tempReceiver = "0";
        tempProbe = "0";
        measVolume = "0";
        calcVolume = "0";
        angle = "0";
        ratio = "0";
        turnNumber = "0";
        pairLinkQuality = "0";
        turnCountPos = "0";
        turnCountNeg = "0";
        tempAir = "0";
        waterTemperature = "0";
        batteryVoltage = "0";
        supplyVoltage = "0";
        chargerVoltage = "0";
        zAxis = "0";
        drumState = "0";
        truckActivity = "0";
        measurementIndex = "0";
        logQty = "0";
        addedWater = "0";
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMeasVolume() {
        return measVolume;
    }

    public void setMeasVolume(String measVolume) {
        this.measVolume = measVolume;
    }

    public String getCalcVolume() {
        return calcVolume;
    }

    public void setCalcVolume(String calcVolume) {
        this.calcVolume = calcVolume;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(String turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getPairLinkQuality() {
        return pairLinkQuality;
    }

    public void setPairLinkQuality(String pairLinkQuality) {
        this.pairLinkQuality = pairLinkQuality;
    }

    public String getTurnCountPos() {
        return turnCountPos;
    }

    public void setTurnCountPos(String turnCountPos) {
        this.turnCountPos = turnCountPos;
    }

    public String getTurnCountNeg() {
        return turnCountNeg;
    }

    public void setTurnCountNeg(String turnCountNeg) {
        this.turnCountNeg = turnCountNeg;
    }

    public String getTempAir() {
        return tempAir;
    }

    public void setTempAir(String tempAir) {
        this.tempAir = tempAir;
    }

    public String getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(String waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getSupplyVoltage() {
        return supplyVoltage;
    }

    public void setSupplyVoltage(String supplyVoltage) {
        this.supplyVoltage = supplyVoltage;
    }

    public String getChargerVoltage() {
        return chargerVoltage;
    }

    public void setChargerVoltage(String chargerVoltage) {
        this.chargerVoltage = chargerVoltage;
    }

    public String getzAxis() {
        return zAxis;
    }

    public void setzAxis(String zAxis) {
        this.zAxis = zAxis;
    }

    public String getDrumState() {
        return drumState;
    }

    public void setDrumState(String drumState) {
        this.drumState = drumState;
    }

    public String getTruckActivity() {
        return truckActivity;
    }

    public void setTruckActivity(String truckActivity) {
        this.truckActivity = truckActivity;
    }

    public String getMeasurementIndex() {
        return measurementIndex;
    }

    public void setMeasurementIndex(String measurementIndex) {
        this.measurementIndex = measurementIndex;
    }

    public String getLogQty() {
        return logQty;
    }

    public void setLogQty(String logQty) {
        this.logQty = logQty;
    }

    public String getAddedWater() {
        return addedWater;
    }

    public void setAddedWater(String addedWater) {
        this.addedWater = addedWater;
    }
}
