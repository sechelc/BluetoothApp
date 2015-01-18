package com.example.android.bluetoothmanager;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by SechelC on 1/18/2015.
 */
public class ResponseParser {
    private static final String ns = null;
    public static final String TRUCK_NO = "TruckNo";
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
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(new StringReader(response));
            parser.nextTag();
            return readFeed(parser);
        } catch (XmlPullParserException | IOException e) {
            Log.e("Parser", "Failed to parse message :" + response, e);
        }
        return null;
    }

    private Entry readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        Entry entries = null;

        parser.require(XmlPullParser.START_TAG, ns, DS_MAIN);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("TruckInfo")) {
                entries = readEntry(parser);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, TRUCK_INFO);
        String truckNo = INITIAL_VALUE;
        String pressure = INITIAL_VALUE;
        String speed = INITIAL_VALUE;
        String viscosity = INITIAL_VALUE;
        String volume = INITIAL_VALUE;
        String slump = INITIAL_VALUE;
        String yield = INITIAL_VALUE;
        String tempProbe = INITIAL_VALUE;
        String tempReceiver = INITIAL_VALUE;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TRUCK_NO)) {
                truckNo = readValue(parser, TRUCK_NO);
            } else if (name.equals(PRESSURE)) {
                pressure = readValue(parser, PRESSURE);
            } else if (name.equals(SPEED)) {
                speed = readValue(parser, SPEED);
            } else if (name.equals(VISCOSITY)) {
                viscosity = readValue(parser, VISCOSITY);
            } else if (name.equals(VOLUME)) {
                volume = readValue(parser, VOLUME);
            } else if (name.equals(SLUMP)) {
                slump = readValue(parser, SLUMP);
            } else if (name.equals(YIELD)) {
                yield = readValue(parser, YIELD);
            } else if (name.equals(TEMP_PROOBE)) {
                tempProbe = readValue(parser, TEMP_PROOBE);
            } else if (name.equals(TEMP_RECEIVER)) {
                tempReceiver = readValue(parser, TEMP_RECEIVER);
            } else {
                skip(parser);
            }
        }
        return new Entry(truckNo, pressure, speed, volume, slump, viscosity, yield, tempReceiver, tempProbe);
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
    public static class Entry {
        public final String truckNo;
        public final String pressure;
        public final String speed;
        public final String volume;
        public final String slump;
        public final String viscosity;
        public final String yield;
        public final String tempReceiver;
        public final String tempProbe;

        private Entry(String truckNo, String pressure, String speed, String volume, String slump, String viscosity, String yield, String tempReceiver, String tempProbe) {
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
    }
}
