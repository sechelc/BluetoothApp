package com.example.android.bluetoothmanager.helper;

import android.util.Log;
import android.util.Xml;

import com.example.android.bluetoothmanager.model.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by SechelC on 1/18/2015.
 */
public class ResponseParser {
    private static final String ns = "";
    public static final String TRUCK_NO = "TruckNo";
    public static final String MEAS_VOLUME = "MeasVolume";
    public static final String CALC_VOLUME = "CalcVolume";
    public static final String ANGLE = "Angle";
    public static final String RATIO = "Ratio";
    public static final String TURN_NUMBER = "TurnNumber";
    public static final String PAIR_LINK_QUALITY = "PairLinkQuality";
    public static final String TURN_COUNT_POS = "TurnCountPos";
    public static final String TURN_COUNT_NEG = "TurnCountNeg";
    public static final String TEMP_AIR = "TempAir";
    public static final String WATER_TEMPERATURE = "WaterTemperature";
    public static final String BATTERY_VOLTAGE = "BatteryVoltage";
    public static final String SUPPLY_VOLTAGE = "SupplyVoltage";
    public static final String CHARGER_VOLTAGE = "ChargerVoltage";
    public static final String Z_AXIS = "ZAxis";
    public static final String DRUM_STATE = "DrumState";
    public static final String TRUCK_ACTIVITY = "TruckActivity";
    public static final String MEASUREMENT_INDEX = "MeasurementIndex";
    public static final String LOG_QTY = "LogQty";
    public static final String ADDED_WATER = "AddedWater";
    public static final String PRESSURE = "Pressure";
    public static final String SPEED = "Speed";
    public static final String VOLUME = "Volume";
    public static final String SLUMP = "Slump";
    public static final String VISCOSITY = "Viscosity";
    public static final String YIELD = "Yield";
    public static final String TEMP_RECEIVER = "TempReceiver";
    public static final String TEMP_PROOBE = "TempProbe";
    public static final String TRUCK_INFO = "TruckInfo";
    public static final String INITIAL_VALUE = "0";
    public static final String DS_MAIN = "DsMain";

    public Entry parse(String response) {
        response = response.trim();
        if(!response.startsWith("<")){
            response = "<" + response;
        }
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(new StringReader(response));
            parser.nextTag();
            Entry entry = readFeed(parser);
            entry.setRawData(response);
            return entry;
        } catch (XmlPullParserException | IOException e) {
            Log.e("Parser", "Failed to parse message :" + response, e);
        }
        return null;
    }

    private Entry readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        Entry entries = null;

   //     parser.require(XmlPullParser.START_TAG, ns, DS_MAIN);
        int eventType = parser.getEventType();
        Entry currentProduct = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals(TRUCK_INFO)){
                        currentProduct = readEntry(parser);
                        return currentProduct;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase(TRUCK_INFO) && currentProduct != null){
                        return currentProduct;
                    }
            }
            eventType = parser.next();
        }

        return null;

    }

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, TRUCK_INFO);
        Entry entry = new Entry();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TRUCK_NO)) {
                entry.setTruckNo(readValue(parser, TRUCK_NO));
            } else if (name.equals(PRESSURE)) {
                entry.setPressure(readValue(parser, PRESSURE));
            } else if (name.equals(SPEED)) {
                entry.setSpeed(readValue(parser, SPEED));
            } else if (name.equals(VISCOSITY)) {
                entry.setViscosity(readValue(parser, VISCOSITY));
            } else if (name.equals(VOLUME)) {
                entry.setVolume(readValue(parser, VOLUME));
            } else if (name.equals(SLUMP)) {
                entry.setSlump(readValue(parser, SLUMP));
            } else if (name.equals(YIELD)) {
                entry.setYield(readValue(parser, YIELD));
            } else if (name.equals(TEMP_PROOBE)) {
                entry.setTempProbe(readValue(parser, TEMP_PROOBE));
            } else if (name.equals(TEMP_RECEIVER)) {
                entry.setTempReceiver(readValue(parser, TEMP_RECEIVER));
            }else if (name.equals(MEAS_VOLUME)) {
                entry.setMeasVolume(readValue(parser, MEAS_VOLUME));
            }else if (name.equals(CALC_VOLUME)) {
                entry.setCalcVolume(readValue(parser, CALC_VOLUME));
            }else if (name.equals(ANGLE)) {
                entry.setAngle(readValue(parser, ANGLE));
            }else if (name.equals(RATIO)) {
                entry.setRatio(readValue(parser, RATIO));
            }else if (name.equals(TURN_NUMBER)) {
                entry.setTurnNumber(readValue(parser, TURN_NUMBER));
            }else if (name.equals(PAIR_LINK_QUALITY)) {
                entry.setPairLinkQuality(readValue(parser, PAIR_LINK_QUALITY));
            }else if (name.equals(TURN_COUNT_POS)) {
                entry.setTurnCountPos(readValue(parser, TURN_COUNT_POS));
            }else if (name.equals(TURN_COUNT_NEG)) {
                entry.setTurnCountNeg(readValue(parser, TURN_COUNT_NEG));
            }else if (name.equals(TEMP_AIR)) {
                entry.setTempAir(readValue(parser, TEMP_AIR));
            }else if (name.equals(WATER_TEMPERATURE)) {
                entry.setWaterTemperature(readValue(parser, WATER_TEMPERATURE));
            }else if (name.equals(BATTERY_VOLTAGE)) {
                entry.setBatteryVoltage(readValue(parser, BATTERY_VOLTAGE));
            }else if (name.equals(SUPPLY_VOLTAGE)) {
                entry.setSupplyVoltage(readValue(parser, SUPPLY_VOLTAGE));
            }else if (name.equals(CHARGER_VOLTAGE)) {
                entry.setChargerVoltage(readValue(parser, CHARGER_VOLTAGE));
            }else if (name.equals(Z_AXIS)) {
                entry.setzAxis(readValue(parser, Z_AXIS));
            }else if (name.equals(DRUM_STATE)) {
                entry.setDrumState(readValue(parser, DRUM_STATE));
            }else if (name.equals(TRUCK_ACTIVITY)) {
                entry.setTruckActivity(readValue(parser, TRUCK_ACTIVITY));
            }else if (name.equals(MEASUREMENT_INDEX)) {
                entry.setMeasurementIndex(readValue(parser, MEASUREMENT_INDEX));
            }else if (name.equals(LOG_QTY)) {
                entry.setLogQty(readValue(parser, LOG_QTY));
            } else if (name.equals(ADDED_WATER)) {
                entry.setAddedWater(readValue(parser, ADDED_WATER));
            } else {
                skip(parser);
            }
        }

        return entry;
    }

    private String readValue(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
