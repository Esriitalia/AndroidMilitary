package geot4android.geot4.esriitalia.com.geot4android.model;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giovannipalombi on 01/04/15.
 */

public class MessageParser {
    private static final String ns = null;

    public List<GeoMessage> parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            // instantiate a parser, use an InputStream as input
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            // start the parsing process
            parser.nextTag();
            // invoke readFeed() method to extract and process data
            return readGeoMessages(parser);
        } finally {
            in.close();
        }
    }

    /**
     *
     * @param parser
     * @return List containing the entries extracted from feed
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<GeoMessage> readGeoMessages(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<GeoMessage> geoMessages = new ArrayList<GeoMessage>();

        parser.require(XmlPullParser.START_TAG, ns, "geomessages");
        while (parser.next() != XmlPullParser.END_TAG) {
            String pName = parser.getName();
            // Start looking for the geomessage tag
            if (pName != null && pName.equals("geomessage")) {
                geoMessages.add(readGeoMessage(parser));
            } else {
                Log.d("DEBUG", "TAG NAME = " + pName);
                continue;
            }

        }
        return geoMessages;
    }

    private GeoMessage readGeoMessage(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "geomessage");

        String name = null;
        String type = null;
        String action = null;
        String id = null;
        String controlpoints = null;
        String wkid = null;
        String sic = null;
        String uniquedesignation = null;
        String quantity = null;
        String direction = null;
        String datetimevalid = null;
        String speed = null;
        String owningunit = null;
        String status911 = null;
        String fuel_state = null;
        String rel_info = null;
        String ttype = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String pName = parser.getName();
            if (pName.equals("_name")) {
                name = readName(parser);
            } else if (pName.equals("_type")) {
                type = readType(parser);
            } else if (pName.equals("_action")) {
                action = readAction(parser);
            } else if (pName.equals("_id")) {
                id = readID(parser);
            } else if (pName.equals("_control_points")) {
                controlpoints = readControlPoints(parser);
            } else if (pName.equals("_wkid")) {
                wkid = readWkid(parser);
            } else if (pName.equals("sic")) {
                sic = readSic(parser);
            } else if (pName.equals("uniquedesignation")) {
                uniquedesignation = readUniqueDesignation(parser);
            } else if (pName.equals("quantity")) {
                quantity = readQuantity(parser);
            } else if (pName.equals("direction")) {
                direction = readDirection(parser);
            } else if (pName.equals("datetimevalid")) {
                datetimevalid = readDatetimevalid(parser);
            } else if (pName.equals("speed")) {
                speed = readSpeed(parser);
            } else if (pName.equals("owningunit")) {
                owningunit = readOwningunit(parser);
            } else if (pName.equals("status911")) {
                status911 = readStatus911(parser);
            } else if (pName.equals("fuel_state")) {
                fuel_state = readFuelstate(parser);
            } else if (pName.equals("rel_info")) {
                rel_info = readRelinfo(parser);
            } else if (pName.equals("type")) {
                ttype = readTType(parser);
            }
        }
        return new GeoMessage(name, type, action, id, controlpoints, wkid, sic,
                uniquedesignation, quantity, direction, datetimevalid, speed,
                owningunit, status911, fuel_state, rel_info, ttype);
    }

    private String readQuantity(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "quantity");
        String quantity = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "quantity");

        return quantity;
    }

    private String readDirection(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "direction");
        String direction = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "direction");

        return direction;
    }

    private String readDatetimevalid(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "datetimevalid");
        String datetimevalid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "datetimevalid");

        return datetimevalid;
    }

    private String readSpeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "speed");
        String speed = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "speed");

        return speed;
    }

    private String readOwningunit(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "owningunit");
        String owningunit = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "owningunit");

        return owningunit;
    }

    private String readStatus911(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "status911");
        String status911 = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "status911");

        return status911;
    }

    private String readFuelstate(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "fuel_state");
        String fuel_state = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "fuel_state");

        return fuel_state;
    }

    private String readRelinfo(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "rel_info");
        String rel_info = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "rel_info");

        return rel_info;
    }

    private String readUniqueDesignation(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "uniquedesignation");
        String uniquedesignation = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "uniquedesignation");

        return uniquedesignation;
    }

    private String readSic(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "sic");
        String sic = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "sic");

        return sic;
    }

    private String readWkid(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_wkid");
        String wkid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_wkid");

        return wkid;
    }

    private String readControlPoints(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_control_points");
        String controlpoints = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_control_points");

        return controlpoints;
    }

    private String readID(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_id");

        return id;
    }

    private String readAction(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_action");
        String action = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_action");

        return action;
    }

    private String readTType(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");

        return type;
    }

    private String readType(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_type");

        return type;
    }

    private String readName(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "_name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "_name");

        return name;
    }

    // extract text values.
    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public class GeoMessage {

        public final String name;
        public final String type;
        public final String action;
        public final String id;
        public final String controlpoints;
        public final String wkid;
        public final String sic;
        public final String uniquedesignation;
        public final String quantity;
        public final String direction;
        public final String datetimevalid;
        public final String speed;
        public final String owningunit;
        public final String status911;
        public final String fuel_state;
        public final String rel_info;
        public final String ttype;

        private GeoMessage(String name, String type, String action, String id,
                           String controlpoints, String wkid, String sic,
                           String uniquedesignation, String quantity, String direction,
                           String datetimevalid, String speed, String owningunit,
                           String status911, String fuel_state, String rel_info, String ttype) {
            this.name = name;
            this.type = type;
            this.action = action;
            this.id = id;
            this.controlpoints = controlpoints;
            this.wkid = wkid;
            this.sic = sic;
            this.uniquedesignation = uniquedesignation;
            this.quantity = quantity;
            this.direction = direction;
            this.datetimevalid = datetimevalid;
            this.speed = speed;
            this.owningunit = owningunit;
            this.status911 = status911;
            this.fuel_state = fuel_state;
            this.rel_info = rel_info;
            this.ttype = ttype;
        }
    }
}
